@echo off
setlocal
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0validate-workspace.ps1"
exit /b %ERRORLEVEL%
