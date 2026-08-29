param()

$ErrorActionPreference = 'Stop'
$PracticeRoot = Split-Path -Parent $PSScriptRoot
$required = @(
    '.ai\INTERVIEW_AGENT.md', 'LEARNING_METHOD.md', 'FAILURE_LOG.md', 'PROGRESS.md', 'MASTERY_MATRIX.md',
    '.interviewer\REPO_INDEX.md', '.interviewer\CANDIDATE_MODEL.md',
    '.interviewer\INTERVIEWER_STATE.md', '.interviewer\SESSION_HISTORY.md',
    '.interviewer\EVIDENCE_LOG.md', 'review\review.json', 'config\repositories.json'
)
foreach ($relative in $required) {
    $path = Join-Path $PracticeRoot $relative
    if (-not (Test-Path -LiteralPath $path)) { throw "Missing required file: $relative" }
}
Get-Content -Raw -LiteralPath (Join-Path $PracticeRoot 'review\review.json') | ConvertFrom-Json | Out-Null
Get-Content -Raw -LiteralPath (Join-Path $PracticeRoot 'config\repositories.json') | ConvertFrom-Json | Out-Null
& (Join-Path $PSScriptRoot 'rebuild-profile.ps1')
$ReviewOsRoot = if ($env:REVIEW_OS_ROOT) { [System.IO.Path]::GetFullPath($env:REVIEW_OS_ROOT) } else { [System.IO.Path]::GetFullPath((Join-Path (Split-Path -Parent $PracticeRoot) 'review-os')) }
& (Join-Path $ReviewOsRoot 'scripts\review-repo.cmd') $PracticeRoot stats
if ($LASTEXITCODE -ne 0) { throw 'Review OS could not read the mock correction deck.' }
Write-Host 'MocksPractice validation passed.'
