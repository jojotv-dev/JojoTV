$ErrorActionPreference = 'Stop'

$versionFile = Join-Path $PSScriptRoot '..\app\version.properties'
$versionFile = [System.IO.Path]::GetFullPath($versionFile)
$properties = [System.IO.File]::ReadAllLines($versionFile)
$versionNameLine = $properties | Where-Object { $_ -match '^versionName=' } | Select-Object -First 1
$versionCodeLine = $properties | Where-Object { $_ -match '^versionCode=' } | Select-Object -First 1

if (-not $versionNameLine -or -not $versionCodeLine) {
    throw 'app/version.properties must contain versionName and versionCode.'
}

$match = [regex]::Match($versionNameLine, '^versionName=(\d+)\.(\d+)\.(\d+)$')
if (-not $match.Success) {
    throw "Unsupported versionName: $($versionNameLine.Substring('versionName='.Length))"
}

$major = [int]$match.Groups[1].Value
$minor = [int]$match.Groups[2].Value
$patch = [int]$match.Groups[3].Value
$versionCode = 0
if (-not [int]::TryParse($versionCodeLine.Substring('versionCode='.Length), [ref]$versionCode)) {
    throw 'versionCode must be an integer.'
}

if ($patch -lt 100) {
    $patch++
} else {
    $patch = 1
    if ($minor -lt 100) {
        $minor++
    } else {
        $major++
        $minor = 1
    }
}

$nextVersionName = "$major.$minor.$patch"
$nextVersionCode = $versionCode + 1
$updatedProperties = @(
    "versionName=$nextVersionName"
    "versionCode=$nextVersionCode"
)
[System.IO.File]::WriteAllLines($versionFile, $updatedProperties, [System.Text.UTF8Encoding]::new($false))
Write-Output $nextVersionName
