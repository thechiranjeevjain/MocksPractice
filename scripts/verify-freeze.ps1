param([Parameter(Mandatory = $true)][string] $SessionPath)

$ErrorActionPreference = 'Stop'
$PracticeRoot = Split-Path -Parent $PSScriptRoot
& java (Join-Path $PracticeRoot 'tools\FreezeSession.java') $PracticeRoot $SessionPath --verify
if ($LASTEXITCODE -ne 0) { throw "Freeze verification failed with exit code $LASTEXITCODE" }
