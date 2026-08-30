param()

$ErrorActionPreference = 'Stop'
$PracticeRoot = Split-Path -Parent $PSScriptRoot
$required = @(
    '.ai\INTERVIEW_AGENT.md', '.ai\OPERATOR_QUICKCARD.md', '.ai\FILE_MAP.md', '.ai\DIAGRAMS.md', 'LEARNING_METHOD.md',
    '.interviewer\FAILURE_LOG.md', '.interviewer\PROGRESS.md', '.interviewer\MASTERY_MATRIX.md',
    '.interviewer\REPO_INDEX.md', '.interviewer\CANDIDATE_MODEL.md',
    '.interviewer\INTERVIEWER_STATE.md', '.interviewer\SESSION_HISTORY.md',
    '.interviewer\EVIDENCE_LOG.md', '.interviewer\SELF_MODEL.md', '.interviewer\TOPIC_COVERAGE.md',
    '.interviewer\GAMIFICATION.md', '.interviewer\PARAMETERS.json',
    'review\review.json', 'config\repositories.json', 'cache\build_cache.py', 'tools\seal.py'
)
foreach ($relative in $required) {
    $path = Join-Path $PracticeRoot $relative
    if (-not (Test-Path -LiteralPath $path)) { throw "Missing required file: $relative" }
}
Get-Content -Raw -LiteralPath (Join-Path $PracticeRoot 'review\review.json') | ConvertFrom-Json | Out-Null
Get-Content -Raw -LiteralPath (Join-Path $PracticeRoot 'config\repositories.json') | ConvertFrom-Json | Out-Null
Get-Content -Raw -LiteralPath (Join-Path $PracticeRoot '.interviewer\PARAMETERS.json') | ConvertFrom-Json | Out-Null
$legacyYearDirectories = @(Get-ChildItem -LiteralPath $PracticeRoot -Directory | Where-Object { $_.Name -match '^20\d{2}$' })
if ($legacyYearDirectories.Count -gt 0) { throw "Legacy year-based session directories found: $($legacyYearDirectories.Name -join ', ')" }
$SessionsRoot = Join-Path $PracticeRoot 'sessions'
if (Test-Path -LiteralPath $SessionsRoot) {
    $invalidSessions = @(Get-ChildItem -LiteralPath $SessionsRoot -Directory | Where-Object { $_.Name -notmatch '^session-\d{4}$' })
    if ($invalidSessions.Count -gt 0) { throw "Invalid flat session name: $($invalidSessions.Name -join ', ')" }
}
& (Join-Path $PSScriptRoot 'rebuild-profile.ps1')
$ReviewOsRoot = if ($env:REVIEW_OS_ROOT) { [System.IO.Path]::GetFullPath($env:REVIEW_OS_ROOT) } else { [System.IO.Path]::GetFullPath((Join-Path (Split-Path -Parent $PracticeRoot) 'review-os')) }
& (Join-Path $ReviewOsRoot 'scripts\review-repo.cmd') $PracticeRoot stats
if ($LASTEXITCODE -ne 0) { throw 'Review OS could not read the mock correction deck.' }
Write-Host 'MocksPractice validation passed.'
