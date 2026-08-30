param(
    [Parameter(Mandatory = $true)][string] $Id,
    [Parameter(Mandatory = $true)][string] $Title,
    [Parameter(Mandatory = $true)][string] $Area,
    [ValidateSet('again', 'hard', 'good', 'easy')][string] $Rating = 'again',
    [ValidateSet('EASY', 'MEDIUM', 'HARD')][string] $Difficulty = 'MEDIUM',
    [string] $SessionPath = '',
    [int] $SolveTimeSeconds = 0,
    [int] $Hints = 0,
    [bool] $CompileSuccess = $true,
    [string[]] $Mistake = @()
)

$ErrorActionPreference = 'Stop'
$PracticeRoot = Split-Path -Parent $PSScriptRoot
$DeckPath = Join-Path $PracticeRoot 'review\review.json'
$ReviewOsRoot = if ($env:REVIEW_OS_ROOT) {
    [System.IO.Path]::GetFullPath($env:REVIEW_OS_ROOT)
} else {
    [System.IO.Path]::GetFullPath((Join-Path (Split-Path -Parent $PracticeRoot) 'review-os'))
}
if (-not (Test-Path -LiteralPath $ReviewOsRoot)) { throw "Review OS not found. Set REVIEW_OS_ROOT." }
$Deck = Get-Content -Raw -LiteralPath $DeckPath | ConvertFrom-Json
$NormalizedId = ($Id.ToUpperInvariant() -replace '[^A-Z0-9]+', '-').Trim('-')
if (-not $NormalizedId.StartsWith('MOCK-')) { $NormalizedId = "MOCK-$NormalizedId" }
$Existing = @($Deck.problems | Where-Object { $_.id -eq $NormalizedId })
if ($Existing.Count -eq 0) {
    $sourceRefs = @()
    if ($SessionPath) { $sourceRefs += $SessionPath }
    $problem = [pscustomobject][ordered]@{
        id = $NormalizedId
        title = $Title
        contentType = 'mock-correction'
        pattern = $Area
        difficulty = $Difficulty
        tags = @('mock-correction', ($Area.ToLowerInvariant() -replace '[^a-z0-9]+', '-').Trim('-'))
        codePath = $SessionPath
        notesPath = '.interviewer/PROGRESS.md'
        githubUrl = ''
        prompt = "Cold-reconstruct $Title before opening sources or using AI. Produce Trigger, Pattern, Invariant, Template, Fallback, and Optimization; then perform it, defend it, and solve one changed-constraint variation."
        answer = ''
        sourceRefs = $sourceRefs
        repetitions = 0
        interval = 0
        easeFactor = 2.5
        stability = 0.0
        difficultyScore = 0.0
        fsrsState = 'LEARNING'
        fsrsStep = 0
        lastReviewed = $null
        nextReview = (Get-Date).ToString('yyyy-MM-dd')
        attempts = 0
        averageSolveTimeSeconds = 0
        hintUsedCount = 0
        compileFailures = 0
        mistakes = @()
    }
    $Deck.problems = @($Deck.problems) + $problem
}
$Deck.generatedAt = (Get-Date).ToUniversalTime().ToString('o')
$TemporaryDeck = "$DeckPath.tmp"
$Deck | ConvertTo-Json -Depth 10 | Set-Content -LiteralPath $TemporaryDeck -Encoding UTF8
Move-Item -Force -LiteralPath $TemporaryDeck -Destination $DeckPath

$ReviewArguments = @($PracticeRoot, 'done', $NormalizedId, $Rating, '--solve-time', $SolveTimeSeconds, '--hints', $Hints, '--compile-success', $CompileSuccess.ToString().ToLowerInvariant(), '--no-sync')
foreach ($value in $Mistake) { $ReviewArguments += @('--mistake', $value) }
$reviewCommand = Join-Path $ReviewOsRoot 'scripts\review-repo.cmd'
& $reviewCommand @ReviewArguments
if ($LASTEXITCODE -ne 0) { throw "Review OS failed with exit code $LASTEXITCODE" }
