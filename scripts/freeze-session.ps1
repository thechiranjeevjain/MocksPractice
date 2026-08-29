param([Parameter(Mandatory = $true)][string] $SessionPath)

$ErrorActionPreference = 'Stop'
$PracticeRoot = Split-Path -Parent $PSScriptRoot
& java (Join-Path $PracticeRoot 'tools\FreezeSession.java') $PracticeRoot $SessionPath
if ($LASTEXITCODE -ne 0) { throw "Freeze failed with exit code $LASTEXITCODE" }
