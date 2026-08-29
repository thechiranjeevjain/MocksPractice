@echo off
setlocal
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0rebuild-profile.ps1"
exit /b %ERRORLEVEL%
