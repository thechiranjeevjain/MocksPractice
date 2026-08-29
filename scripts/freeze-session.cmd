@echo off
setlocal
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0freeze-session.ps1" %*
exit /b %ERRORLEVEL%
