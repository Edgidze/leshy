import Foundation
import MetricKit
import os

/// Подписчик MetricKit, складывающий диагностику в контейнер приложения.
///
/// Зачем это здесь. Три инцидента с зависанием на iPhone SE 1 не удалось разобрать
/// до конца по crash-репортам: в двух случаях система вообще не успела записать
/// репорт самого приложения — watchdog перезагрузил устройство целиком, и остались
/// только чужие `.ips` системных демонов со стекшотом внутри. MetricKit приходит
/// и в таких случаях, потому что собирается системой отдельно от crash reporter.
///
/// Нас интересуют ровно два класса из `MXDiagnosticPayload`:
/// `MXHangDiagnostic` (главный поток не отвечает — это инцидент №1) и
/// `MXCPUExceptionDiagnostic` (процесс выжрал CPU — это инциденты №2 и №3),
/// у обоих есть `callStackTree`. Плюс `MXMetricPayload`: там
/// `cpuMetrics.cumulativeCPUTime` за сутки реального пользования — то самое
/// измерение расхода CPU, которое иначе снимается только вручную в Instruments.
///
/// Фактура и план — `.claude/investigations/ios-maplibre-background-watchdog/README.md`,
/// шаг 4. Как забрать файлы — см. `iosApp/CLAUDE.md`.
///
/// **Расписание доставки не наше:** система отдаёт payload'ы не чаще раза в сутки
/// и обычно на следующем запуске после события, так что сразу после зависания
/// файла не будет. Для проверки самого кода есть Xcode → Debug → Simulate MetricKit
/// Payloads (на подключённом устройстве).
final class DiagnosticsArchive: NSObject, MXMetricManagerSubscriber {

    static let shared = DiagnosticsArchive()

    private let log = Logger(
        subsystem: Bundle.main.bundleIdentifier ?? "leshy",
        category: "diagnostics"
    )

    /// Payload'ы приходят раз в сутки, так что это примерно полтора месяца истории.
    private let keepFiles = 50

    private lazy var isoFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd'T'HH-mm-ss"
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
        formatter.locale = Locale(identifier: "en_US_POSIX")
        return formatter
    }()

    func start() {
        MXMetricManager.shared.add(self)
        // Система хранит до 7 суток уже собранных payload'ов — если что-то произошло
        // до установки этой сборки (или подписка не успела получить доставку),
        // заберём сразу, не дожидаясь следующего окна доставки.
        DispatchQueue.global(qos: .utility).async { [weak self] in
            guard let self else { return }
            self.didReceive(MXMetricManager.shared.pastDiagnosticPayloads)
            self.didReceive(MXMetricManager.shared.pastPayloads)
        }
    }

    // MARK: - MXMetricManagerSubscriber

    func didReceive(_ payloads: [MXDiagnosticPayload]) {
        for payload in payloads {
            let hangs = payload.hangDiagnostics?.count ?? 0
            let cpuExceptions = payload.cpuExceptionDiagnostics?.count ?? 0
            let crashes = payload.crashDiagnostics?.count ?? 0
            let diskWrites = payload.diskWriteExceptionDiagnostics?.count ?? 0

            // Длительности зависаний — в syslog: это то, что видно сразу через
            // `idevicesyslog`, не дожидаясь выгрузки контейнера.
            for hang in payload.hangDiagnostics ?? [] {
                log.error("MetricKit hang: \(hang.hangDuration.description, privacy: .public)")
            }
            for cpuException in payload.cpuExceptionDiagnostics ?? [] {
                log.error("""
                    MetricKit CPU exception: \
                    total \(cpuException.totalCPUTime.description, privacy: .public), \
                    sampled \(cpuException.totalSampledTime.description, privacy: .public)
                    """)
            }

            let tag = "hang\(hangs)-cpu\(cpuExceptions)-crash\(crashes)-disk\(diskWrites)"
            write(payload.jsonRepresentation(), kind: "diagnostic-\(tag)", stamp: payload.timeStampEnd)
        }
    }

    func didReceive(_ payloads: [MXMetricPayload]) {
        for payload in payloads {
            if let cpuTime = payload.cpuMetrics?.cumulativeCPUTime {
                log.notice("MetricKit cumulative CPU: \(cpuTime.description, privacy: .public)")
            }
            write(payload.jsonRepresentation(), kind: "metrics", stamp: payload.timeStampEnd)
        }
    }

    // MARK: - Запись

    /// `Application Support`, а не `Documents`: содержимое `Documents` пришлось бы
    /// открывать наружу через `UIFileSharingEnabled`, а там лежат данные пользователя.
    /// Забирается выгрузкой контейнера из Xcode → Devices and Simulators.
    private var directory: URL? {
        let manager = FileManager.default
        guard let base = try? manager.url(
            for: .applicationSupportDirectory,
            in: .userDomainMask,
            appropriateFor: nil,
            create: true
        ) else {
            log.error("MetricKit: не удалось получить Application Support")
            return nil
        }
        let directory = base.appendingPathComponent("diagnostics", isDirectory: true)
        do {
            try manager.createDirectory(at: directory, withIntermediateDirectories: true)
        } catch {
            log.error("MetricKit: не удалось создать \(directory.path, privacy: .public): \(error.localizedDescription, privacy: .public)")
            return nil
        }
        return directory
    }

    private func write(_ data: Data, kind: String, stamp: Date) {
        guard let directory else { return }
        // Метка времени payload'а плюс порядковый суффикс: за одно окно доставки
        // может прийти несколько payload'ов с одинаковым `timeStampEnd`.
        let base = "\(isoFormatter.string(from: stamp))-\(kind)"
        var url = directory.appendingPathComponent("\(base).json")
        var index = 1
        while FileManager.default.fileExists(atPath: url.path) {
            url = directory.appendingPathComponent("\(base)-\(index).json")
            index += 1
        }
        do {
            try data.write(to: url, options: .atomic)
            log.notice("MetricKit: записан \(url.lastPathComponent, privacy: .public) (\(data.count) байт)")
        } catch {
            log.error("MetricKit: не удалось записать \(url.lastPathComponent, privacy: .public): \(error.localizedDescription, privacy: .public)")
            return
        }
        prune(in: directory)
    }

    private func prune(in directory: URL) {
        guard let files = try? FileManager.default.contentsOfDirectory(
            at: directory,
            includingPropertiesForKeys: nil
        ) else { return }
        // Имена начинаются с метки времени в UTC, поэтому лексикографическая
        // сортировка совпадает с хронологической.
        let sorted = files.filter { $0.pathExtension == "json" }.sorted { $0.lastPathComponent < $1.lastPathComponent }
        guard sorted.count > keepFiles else { return }
        for file in sorted.prefix(sorted.count - keepFiles) {
            try? FileManager.default.removeItem(at: file)
        }
    }
}
