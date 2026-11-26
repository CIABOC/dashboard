[CmdletBinding()]
param (
    [Parameter(Mandatory)]
    [string]$AtlasCredentialsFile
)

$ErrorActionPreference = 'Stop'

$uriLine = Get-Content -LiteralPath $AtlasCredentialsFile |
    Where-Object { $_ -match '^\s*MONGODB_URI\s*=' } |
    Select-Object -First 1
if (-not $uriLine) {
    throw 'The Atlas credentials file must contain MONGODB_URI.'
}

$atlasUri = ($uriLine -split '=', 2)[1].Trim().Trim([char]34, [char]39)
if ($atlasUri -notmatch '^mongodb(?:\+srv)?://\S+$') {
    throw 'MONGODB_URI must contain a MongoDB connection string.'
}

$previousAtlasUri = $env:VMR_ATLAS_URI
Push-Location -LiteralPath $PSScriptRoot
try {
    $env:VMR_ATLAS_URI = $atlasUri
    & mvn spring-boot:run
    if ($LASTEXITCODE -ne 0) {
        throw "The dashboard backend exited with code $LASTEXITCODE. Check the Spring Boot output above."
    }
} finally {
    $env:VMR_ATLAS_URI = $previousAtlasUri
    Pop-Location
}
