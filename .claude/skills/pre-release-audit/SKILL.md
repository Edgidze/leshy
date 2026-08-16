---
name: pre-release-audit
description: Supply-chain dependency audit to run right before publishing to Google Play / App Store — checks all versions in gradle/libs.versions.toml against OSV.dev, verifies settings.gradle.kts still restricts repos to google()/mavenCentral(), and spot-checks new/less-established dependencies. Use when the user is preparing a release or asks about pre-publish checks.
---

# Перед публикацией

Перед финальным шагом публикации (Google Play / App Store) — обязательна широкая проверка
зависимостей на предмет supply-chain рисков:

1. Прогнать все версии из `gradle/libs.versions.toml` через OSV.dev
   (`https://api.osv.dev/v1/querybatch`, ecosystem `Maven`) на предмет известных CVE.
2. Убедиться, что `settings.gradle.kts` по-прежнему ограничивает разрешение зависимостей только
   `google()`/`mavenCentral()` — никаких сторонних/приватных репозиториев не добавилось.
3. Точечно проверить репутацию/происхождение любых новых или менее устоявшихся зависимостей
   (не от `androidx.*`/`org.jetbrains.*`/крупных организаций) — кто мейнтейнер, публикуется ли
   через официальный канал (Maven Central/Gradle Plugin Portal), нет ли признаков компрометации.

Это осознанная альтернатива постоянно поддерживаемому Gradle dependency locking/verification
(`gradle/verification-metadata.xml`) — для этого проекта решили не тащить их ongoing maintenance
cost, а вместо этого делать разовую широкую проверку прямо перед релизом. Причина и когда стоит
пересмотреть это решение — см. память `feedback-dependency-locking-decision` (общая, не
привязанная к этому проекту).
