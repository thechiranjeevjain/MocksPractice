param()

$ErrorActionPreference = 'Stop'
$PracticeRoot = Split-Path -Parent $PSScriptRoot
$ConfigPath = Join-Path $PracticeRoot 'config\repositories.json'
$SnapshotRoot = Join-Path $PracticeRoot '.interviewer\snapshots'
$SnapshotPath = Join-Path $SnapshotRoot 'repo-snapshot.json'
$ChangesPath = Join-Path $SnapshotRoot 'REPO_CHANGES.md'
$InventoryPath = Join-Path $SnapshotRoot 'REPO_INVENTORY.md'

$Config = Get-Content -Raw -LiteralPath $ConfigPath | ConvertFrom-Json
$AllowedExtensions = @{}
foreach ($extension in $Config.extensions) { $AllowedExtensions[$extension.ToLowerInvariant()] = $true }
$ExcludedNames = @{}
foreach ($name in $Config.excludedDirectoryNames) { $ExcludedNames[$name.ToLowerInvariant()] = $true }

function Test-IncludedFile([System.IO.FileInfo] $File, [string] $Root) {
    if (-not $AllowedExtensions.ContainsKey($File.Extension.ToLowerInvariant())) { return $false }
    $relative = $File.FullName.Substring($Root.Length).TrimStart('\')
    foreach ($segment in ($relative -split '\\')) {
        if ($ExcludedNames.ContainsKey($segment.ToLowerInvariant())) { return $false }
    }
    return $true
}

function Get-Sha256([string] $Path) {
    $stream = [System.IO.File]::OpenRead($Path)
    try {
        $algorithm = [System.Security.Cryptography.SHA256]::Create()
        try {
            return ([System.BitConverter]::ToString($algorithm.ComputeHash($stream))).Replace('-', '').ToLowerInvariant()
        } finally {
            $algorithm.Dispose()
        }
    } finally {
        $stream.Dispose()
    }
}

$Previous = $null
if (Test-Path -LiteralPath $SnapshotPath) {
    $Previous = Get-Content -Raw -LiteralPath $SnapshotPath | ConvertFrom-Json
}

$Records = New-Object System.Collections.Generic.List[object]
$RepoSummaries = New-Object System.Collections.Generic.List[object]
foreach ($repository in $Config.repositories) {
    $environmentRoot = [Environment]::GetEnvironmentVariable([string]$repository.environmentVariable)
    $relativeRoot = Join-Path (Split-Path -Parent $PracticeRoot) ([string]$repository.relativeToPracticeParent)
    $candidates = @($environmentRoot, $relativeRoot, [string]$repository.windowsFallback) |
        Where-Object { -not [string]::IsNullOrWhiteSpace($_) }
    $selectedRoot = $candidates | Where-Object { Test-Path -LiteralPath $_ } | Select-Object -First 1
    if (-not $selectedRoot) {
        throw "Repository $($repository.name) was not found. Keep it beside MocksPractice or set $($repository.environmentVariable)."
    }
    $root = [System.IO.Path]::GetFullPath($selectedRoot).TrimEnd('\', '/')
    $files = Get-ChildItem -LiteralPath $root -Recurse -File -ErrorAction Stop |
        Where-Object { Test-IncludedFile $_ $root }
    $extensionCounts = @{}
    foreach ($file in $files) {
        $relative = $file.FullName.Substring($root.Length).TrimStart('\').Replace('\', '/')
        $hash = Get-Sha256 $file.FullName
        $Records.Add([pscustomobject]@{
            repository = [string]$repository.name
            relativePath = $relative
            length = $file.Length
            lastWriteUtc = $file.LastWriteTimeUtc.ToString('o')
            sha256 = $hash
        })
        $extension = if ($file.Extension) { $file.Extension.ToLowerInvariant() } else { '[none]' }
        if (-not $extensionCounts.ContainsKey($extension)) { $extensionCounts[$extension] = 0 }
        $extensionCounts[$extension]++
    }
    $RepoSummaries.Add([pscustomobject]@{
        name = [string]$repository.name
        root = $root
        fileCount = $files.Count
        extensions = $extensionCounts
    })
}

$OrderedRecords = @($Records.ToArray() | Sort-Object repository, relativePath)
$Snapshot = [ordered]@{
    schemaVersion = 1
    scannedAt = (Get-Date).ToUniversalTime().ToString('o')
    repositories = @($RepoSummaries.ToArray())
    files = $OrderedRecords
}

$OldMap = @{}
if ($Previous) {
    foreach ($item in $Previous.files) { $OldMap["$($item.repository)|$($item.relativePath)"] = [string]$item.sha256 }
}
$NewMap = @{}
foreach ($item in $OrderedRecords) { $NewMap["$($item.repository)|$($item.relativePath)"] = [string]$item.sha256 }

$Added = @($NewMap.Keys | Where-Object { -not $OldMap.ContainsKey($_) } | Sort-Object)
$Removed = @($OldMap.Keys | Where-Object { -not $NewMap.ContainsKey($_) } | Sort-Object)
$Modified = @($NewMap.Keys | Where-Object { $OldMap.ContainsKey($_) -and $OldMap[$_] -ne $NewMap[$_] } | Sort-Object)

New-Item -ItemType Directory -Force -Path $SnapshotRoot | Out-Null
$TemporarySnapshot = "$SnapshotPath.tmp"
$Snapshot | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $TemporarySnapshot -Encoding UTF8
Move-Item -Force -LiteralPath $TemporarySnapshot -Destination $SnapshotPath

$changeLines = New-Object System.Collections.Generic.List[string]
$changeLines.Add('# Repository Changes')
$changeLines.Add('')
$changeLines.Add("Scan: $($Snapshot.scannedAt)")
$changeLines.Add('')
if (-not $Previous) {
    $changeLines.Add('Initial baseline created. Repository contents establish exposure only.')
} else {
    $changeLines.Add("- Added: $($Added.Count)")
    $changeLines.Add("- Modified: $($Modified.Count)")
    $changeLines.Add("- Removed: $($Removed.Count)")
    foreach ($section in @(@('Added', $Added), @('Modified', $Modified), @('Removed', $Removed))) {
        $changeLines.Add('')
        $changeLines.Add("## $($section[0])")
        $values = @($section[1])
        if ($values.Count -eq 0) { $changeLines.Add('- None') }
        else { foreach ($value in ($values | Select-Object -First 250)) { $changeLines.Add('- ' + $value) } }
        if ($values.Count -gt 250) { $changeLines.Add("- ... $($values.Count - 250) more in snapshot JSON") }
    }
}
$changeLines | Set-Content -LiteralPath $ChangesPath -Encoding UTF8

$inventoryLines = New-Object System.Collections.Generic.List[string]
$inventoryLines.Add('# Repository Inventory')
$inventoryLines.Add('')
$inventoryLines.Add("Scan: $($Snapshot.scannedAt)")
foreach ($summary in $RepoSummaries) {
    $inventoryLines.Add('')
    $inventoryLines.Add("## $($summary.name)")
    $inventoryLines.Add('')
    $inventoryLines.Add("- Root: $($summary.root)")
    $inventoryLines.Add("- Included source files: $($summary.fileCount)")
    $inventoryLines.Add('- Extensions:')
    foreach ($entry in ($summary.extensions.GetEnumerator() | Sort-Object Name)) {
        $inventoryLines.Add("  - $($entry.Name): $($entry.Value)")
    }
}
$inventoryLines | Set-Content -LiteralPath $InventoryPath -Encoding UTF8

Write-Host "Snapshot: $SnapshotPath"
Write-Host "Included files: $($OrderedRecords.Count)"
Write-Host "Changes: +$($Added.Count) ~$($Modified.Count) -$($Removed.Count)"
