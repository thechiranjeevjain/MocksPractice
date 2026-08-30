param()

$ErrorActionPreference = 'Stop'
$PracticeRoot = Split-Path -Parent $PSScriptRoot
$ReviewOsRoot = if ($env:REVIEW_OS_ROOT) {
    [System.IO.Path]::GetFullPath($env:REVIEW_OS_ROOT)
} else {
    [System.IO.Path]::GetFullPath((Join-Path (Split-Path -Parent $PracticeRoot) 'review-os'))
}
$JavaEnvironment = Join-Path $ReviewOsRoot 'scripts\java-env.ps1'
if (Test-Path -LiteralPath $JavaEnvironment) {
    . $JavaEnvironment
    Use-DsaReviewJava
}
& java (Join-Path $PracticeRoot 'tools\RepoSnapshot.java') $PracticeRoot
if ($LASTEXITCODE -ne 0) { throw "Repository snapshot failed with exit code $LASTEXITCODE" }
