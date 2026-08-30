@echo off
setlocal
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0build-cache.ps1" %*
exit /b %ERRORLEVEL%
