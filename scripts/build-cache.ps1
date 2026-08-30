param([Parameter(ValueFromRemainingArguments = $true)][string[]] $CacheArgs)

$ErrorActionPreference = 'Stop'
$PracticeRoot = Split-Path -Parent $PSScriptRoot
$venvPython = Join-Path $PracticeRoot 'cache\.venv\Scripts\python.exe'
if (Test-Path -LiteralPath $venvPython) {
    & $venvPython (Join-Path $PracticeRoot 'cache\build_cache.py') build @CacheArgs
} elseif (Get-Command py -ErrorAction SilentlyContinue) {
    & py -3 (Join-Path $PracticeRoot 'cache\build_cache.py') build @CacheArgs
} else {
    & python (Join-Path $PracticeRoot 'cache\build_cache.py') build @CacheArgs
}
if ($LASTEXITCODE -ne 0) { throw "Cache build failed with exit code $LASTEXITCODE" }
