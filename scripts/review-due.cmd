@echo off
setlocal
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0review-due.ps1"
exit /b %ERRORLEVEL%
