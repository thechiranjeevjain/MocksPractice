param(
    [Parameter(Mandatory = $true)][string] $TestClass,
    [Parameter(Mandatory = $true)][string] $Round
)

$ErrorActionPreference = 'Continue'
$SessionRoot = Split-Path -Parent $PSScriptRoot
$cursor = [System.IO.DirectoryInfo]$SessionRoot
while ($null -ne $cursor -and -not (Test-Path -LiteralPath (Join-Path $cursor.FullName '.ai\INTERVIEW_AGENT.md'))) {
    $cursor = $cursor.Parent
}
if ($null -eq $cursor) { throw 'MocksPractice root was not found above the session directory.' }
$PracticeRoot = $cursor.FullName
$EvidenceLog = Join-Path $PracticeRoot '.interviewer\EVIDENCE_LOG.md'
$ReviewOsRoot = if ($env:REVIEW_OS_ROOT) { [IO.Path]::GetFullPath($env:REVIEW_OS_ROOT) } else { [IO.Path]::GetFullPath((Join-Path (Split-Path -Parent $PracticeRoot) 'review-os')) }
$Maven = Join-Path $ReviewOsRoot 'mvnw.cmd'
. (Join-Path $ReviewOsRoot 'scripts\java-env.ps1')
Use-DsaReviewJava

Push-Location $SessionRoot
try {
    & $Maven -q -f (Join-Path $SessionRoot 'pom.xml') "-Dtest=$TestClass" test
    $exitCode = $LASTEXITCODE
} finally {
    Pop-Location
}

$tests = 0; $failures = 0; $errors = 0
$reports = Get-ChildItem -LiteralPath (Join-Path $SessionRoot 'target\surefire-reports') -Filter 'TEST-*.xml' -ErrorAction SilentlyContinue
foreach ($report in $reports) {
    [xml]$xml = Get-Content -Raw -LiteralPath $report.FullName
    $tests += [int]$xml.testsuite.tests
    $failures += [int]$xml.testsuite.failures
    $errors += [int]$xml.testsuite.errors
}
$timestamp = Get-Date -Format 'yyyy-MM-dd HH:mm:ss K'
Add-Content -LiteralPath $EvidenceLog -Encoding UTF8 -Value "`n- $timestamp | $Round | exit=$exitCode | tests=$tests failures=$failures errors=$errors"
exit $exitCode
