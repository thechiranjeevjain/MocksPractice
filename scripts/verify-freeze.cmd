@echo off
setlocal
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0verify-freeze.ps1" %*
exit /b %ERRORLEVEL%
