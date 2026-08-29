@echo off
setlocal
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0schedule-correction.ps1" %*
exit /b %ERRORLEVEL%
