param()
$ErrorActionPreference = 'Stop'
$PracticeRoot = Split-Path -Parent $PSScriptRoot
$ReviewOsRoot = if ($env:REVIEW_OS_ROOT) { [IO.Path]::GetFullPath($env:REVIEW_OS_ROOT) } else { [IO.Path]::GetFullPath((Join-Path (Split-Path -Parent $PracticeRoot) 'review-os')) }
if (-not (Test-Path -LiteralPath $ReviewOsRoot)) { throw 'Review OS not found. Set REVIEW_OS_ROOT.' }
& (Join-Path $ReviewOsRoot 'scripts\review-repo.cmd') $PracticeRoot due
exit $LASTEXITCODE
