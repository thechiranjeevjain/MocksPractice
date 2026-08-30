param()

$ErrorActionPreference = 'Stop'
$PracticeRoot = Split-Path -Parent $PSScriptRoot
$venvPython = Join-Path $PracticeRoot 'cache\.venv\Scripts\python.exe'
if (Test-Path -LiteralPath $venvPython) {
    & $venvPython (Join-Path $PracticeRoot 'cache\build_cache.py') sync
} elseif (Get-Command py -ErrorAction SilentlyContinue) {
    & py -3 (Join-Path $PracticeRoot 'cache\build_cache.py') sync
} else {
    & python (Join-Path $PracticeRoot 'cache\build_cache.py') sync
}
if ($LASTEXITCODE -ne 0) { throw "Source sync command failed with exit code $LASTEXITCODE" }
