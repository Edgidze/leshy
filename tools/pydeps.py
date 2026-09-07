#!/usr/bin/env python3
"""Project-local third-party python for the scripts in `tools/`.

`build_catalog.py` needs `Babel` (CLDR country names), and it is not in the
system python. Twice now it was unpacked into a temp directory and handed over
through `PYTHONPATH`; both times the directory was gone by the next batch of
countries and had to be downloaded again. This module makes the dependency
live inside the project instead:

    tools/.pydeps/           unpacked wheels, added to `sys.path`
    tools/.pydeps/_wheels/   the downloaded wheels themselves (cache)

Both are gitignored. The directory belongs to the **main worktree** (resolved
through `git rev-parse --git-common-dir`), so linked worktrees share one copy
instead of downloading their own — see `docs/catalog/CLAUDE.md`.

Usage:
    import pydeps; pydeps.ensure_on_path()   # from a tools/ script
    python3 tools/pydeps.py                  # check, print where things are
    python3 tools/pydeps.py --fetch          # download what is missing

Versions and their sha256 are pinned below: this downloads code that then runs
on the maintainer's machine, so the digest is checked against the pin, not just
against whatever PyPI happens to serve. Bumping a version means replacing both
fields (`--fetch` prints the digest it got when it does not match).

DNS on this machine resolves nothing (`docs/catalog/CLAUDE.md`), so `pip
install` cannot reach PyPI at all. `--fetch` therefore resolves through
Cloudflare DoH (`1.1.1.1`, an IP, so it needs no resolver) and downloads with
`curl --resolve`, resuming — the link is slow enough that 10 MB does not
arrive in one attempt.
"""

import argparse
import hashlib
import json
import shutil
import subprocess
import sys
import zipfile
from pathlib import Path

PYPI_HOST = "pypi.org"
FILES_HOST = "files.pythonhosted.org"
DOH_URL = "https://1.1.1.1/dns-query"

# Pure-python wheels only (`py3-none-any`): a compiled wheel would need tag
# matching, and nothing here needs one. `Pillow` is deliberately absent — it
# lives in the user site-packages (`~/Library/Python/3.9`) and has not gone
# anywhere; if it ever does, it cannot simply be added here, see above.
PINNED = {
    # import name: (distribution on PyPI, version, sha256 of the wheel)
    "babel": (
        "babel",
        "2.18.0",
        "e2b422b277c2b9a9630c1d7903c2a00d0830c409c59ac8cae9081c92f1aeba35",
    ),
}


def lib_dir() -> Path:
    """`tools/.pydeps` of the main worktree — shared by every linked worktree."""
    here = Path(__file__).resolve()
    try:
        common = subprocess.run(
            ["git", "rev-parse", "--git-common-dir"],
            cwd=here.parent,
            capture_output=True,
            text=True,
            check=True,
        ).stdout.strip()
    except (OSError, subprocess.CalledProcessError):
        return here.parent / ".pydeps"
    root = (here.parent / common).resolve().parent if common else here.parent.parent
    return root / "tools" / ".pydeps"


def wheel_cache() -> Path:
    return lib_dir() / "_wheels"


def missing(directory: Path) -> list[str]:
    """Pinned packages that are not unpacked into `directory`."""
    return [name for name in PINNED if not (directory / name).is_dir()]


def ensure_on_path() -> Path:
    """Put the project-local libs on `sys.path`; explain the fix if they are absent."""
    directory = lib_dir()
    if str(directory) not in sys.path:
        sys.path.insert(0, str(directory))
    absent = [name for name in PINNED if not _importable(name)]
    if absent:
        raise SystemExit(
            f"Нет зависимостей: {', '.join(absent)}.\n"
            f"Поставить в проект: python3 {Path(__file__).name} --fetch\n"
            f"(положит их в {directory}, скачивать при следующем запуске не придётся)"
        )
    return directory


def _importable(name: str) -> bool:
    import importlib.util

    try:
        return importlib.util.find_spec(name) is not None
    except (ImportError, ValueError):
        return False


def _resolve(host: str) -> list[str]:
    """Addresses of `host`: the system resolver if it works, else DoH."""
    import socket

    try:
        return sorted({info[4][0] for info in socket.getaddrinfo(host, 443)})
    except OSError:
        pass
    out = subprocess.run(
        [
            "curl", "-sS", "-m", "20",
            "-H", "accept: application/dns-json",
            f"{DOH_URL}?name={host}&type=A",
        ],
        capture_output=True,
        text=True,
    )
    answers = json.loads(out.stdout or "{}").get("Answer", [])
    return [a["data"] for a in answers if a.get("type") == 1]


def _download(url: str, host: str, target: Path, attempts: int = 12) -> None:
    """`curl --resolve` with resume — the link is slow, one attempt is not enough."""
    addresses = _resolve(host)
    if not addresses:
        raise SystemExit(f"Не удалось разрешить {host} (ни резолвером, ни через DoH)")
    target.parent.mkdir(parents=True, exist_ok=True)
    for attempt in range(attempts):
        address = addresses[attempt % len(addresses)]
        subprocess.run(
            [
                "curl", "-sS", "-L", "--http1.1", "-C", "-",
                "-m", "120",
                "--resolve", f"{host}:443:{address}",
                url, "-o", str(target),
            ]
        )
        if target.exists() and zipfile.is_zipfile(target):
            return
        print(
            f"  ... {target.stat().st_size if target.exists() else 0} байт, "
            f"продолжаю (попытка {attempt + 1}/{attempts})"
        )
    raise SystemExit(f"Не удалось скачать {url}")


def fetch() -> None:
    directory = lib_dir()
    cache = wheel_cache()
    for name, (dist, version, digest) in PINNED.items():
        if (directory / name).is_dir():
            print(f"{name} {version}: уже на месте ({directory})")
            continue
        wheel = cache / f"{dist}-{version}-py3-none-any.whl"
        if not (wheel.exists() and zipfile.is_zipfile(wheel)):
            url = _wheel_url(dist, version)
            print(f"{name} {version}: качаю {url}")
            _download(url, FILES_HOST, wheel)
        got = hashlib.sha256(wheel.read_bytes()).hexdigest()
        if got != digest:
            wheel.unlink()
            raise SystemExit(
                f"{name} {version}: sha256 не совпал с закреплённым в PINNED.\n"
                f"  ожидалось {digest}\n  получено  {got}\n"
                "Колесо удалено. Если версия менялась намеренно — впиши новый "
                "digest в PINNED и запусти снова."
            )
        print(f"{name} {version}: sha256 сошёлся, распаковываю в {directory}")
        with zipfile.ZipFile(wheel) as archive:
            archive.extractall(directory)
    print(f"\nГотово. {directory}")
    print("Скрипты tools/ подхватывают это сами (`import pydeps; pydeps.ensure_on_path()`).")


def _wheel_url(dist: str, version: str) -> str:
    """The `py3-none-any` wheel of an exact version, from the PyPI JSON API."""
    addresses = _resolve(PYPI_HOST)
    if not addresses:
        raise SystemExit(f"Не удалось разрешить {PYPI_HOST}")
    out = subprocess.run(
        [
            "curl", "-sS", "-m", "60",
            "--resolve", f"{PYPI_HOST}:443:{addresses[0]}",
            f"https://{PYPI_HOST}/pypi/{dist}/{version}/json",
        ],
        capture_output=True,
        text=True,
    )
    payload = json.loads(out.stdout or "{}")
    for entry in payload.get("urls", []):
        if entry["filename"].endswith("-py3-none-any.whl"):
            return entry["url"]
    raise SystemExit(f"У {dist} {version} нет чистого py3-none-any колеса")


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Зависимости python для скриптов tools/, хранятся в проекте"
    )
    parser.add_argument("--fetch", action="store_true", help="скачать недостающее")
    parser.add_argument(
        "--clean", action="store_true", help="удалить .pydeps целиком (вместе с кешем колёс)"
    )
    args = parser.parse_args()

    directory = lib_dir()
    if args.clean:
        shutil.rmtree(directory, ignore_errors=True)
        print(f"Удалено: {directory}")
        return
    if args.fetch:
        fetch()
        return

    absent = missing(directory) if directory.exists() else list(PINNED)
    print(f"Директория: {directory}{'' if directory.exists() else ' (нет)'}")
    for name, (_, version, _digest) in PINNED.items():
        print(f"  {name} {version}: {'НЕТ' if name in absent else 'на месте'}")
    if absent:
        print(f"\nПоставить: python3 {Path(__file__).name} --fetch")


if __name__ == "__main__":
    main()
