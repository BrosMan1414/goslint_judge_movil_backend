# Inicia el backend Spring Boot y abre un túnel ngrok HTTP al puerto 8080.
# Uso: ./start-backend-ngrok.ps1 [-Profile prod|dev] [-NgrokSubdomain <sub>] [-SkipBuild]
# Requisitos: ngrok autenticado (ngrok config add-authtoken <token>), Java, Maven wrapper.

param(
    [string]$Profile = "prod",
    [string]$NgrokSubdomain = "",
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"

Write-Host "[1/4] Verificando prerequisitos..." -ForegroundColor Cyan
if (-not (Get-Command ngrok -ErrorAction SilentlyContinue)) { throw "ngrok no está en PATH" }
if (-not (Test-Path .\mvnw.cmd)) { throw "No se encuentra mvnw.cmd en el directorio actual" }

Write-Host "[2/4] Iniciando backend (perfil=$Profile)..." -ForegroundColor Cyan
# Construcción opcional
if (-not $SkipBuild) {
    & .\mvnw.cmd -q -DskipTests package
}

# Lanzar backend en segundo plano
$backendLogOut = Join-Path $PWD "backend-out.log"
$backendLogErr = Join-Path $PWD "backend-err.log"
Write-Host "   Log backend (stdout): $backendLogOut" -ForegroundColor DarkGray
Write-Host "   Log backend (stderr): $backendLogErr" -ForegroundColor DarkGray
Start-Process -FilePath .\mvnw.cmd -ArgumentList "spring-boot:run","-Dspring-boot.run.profiles=$Profile" -RedirectStandardOutput $backendLogOut -RedirectStandardError $backendLogErr

# Espera simple para que arranque
Write-Host "   Esperando a que el backend escuche en 8080..." -ForegroundColor DarkGray
$maxWait = 40; $elapsed = 0
while ($elapsed -lt $maxWait) {
    try {
        $resp = Invoke-WebRequest -Uri http://localhost:8080/actuator/health -UseBasicParsing -TimeoutSec 3
        if ($resp.StatusCode -eq 200) { break }
    } catch {}
    Start-Sleep -Seconds 2
    $elapsed += 2
}
if ($elapsed -ge $maxWait) { Write-Warning "El backend no respondió aún, continuando con ngrok." }

Write-Host "[3/4] Abriendo túnel ngrok HTTP -> localhost:8080" -ForegroundColor Cyan
$ngrokArgs = @("http","8080")
if ($NgrokSubdomain -ne "") { $ngrokArgs += ("--subdomain=$NgrokSubdomain") }
Write-Host "   Comando: ngrok $($ngrokArgs -join ' ')" -ForegroundColor DarkGray

# Abrimos ngrok en la misma consola (bloquea hasta Ctrl+C)
ngrok @ngrokArgs

Write-Host "[4/4] Cerrando..." -ForegroundColor Cyan
# Al cerrar ngrok, opcional: detener backend
Get-Process | Where-Object { $_.Path -like "*java*" -and $_.StartInfo.Arguments -match "spring-boot" } | ForEach-Object { try { $_.Kill() } catch {} }
