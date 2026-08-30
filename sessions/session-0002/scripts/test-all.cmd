@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0run-test.ps1" -TestClass *Test -Round ALL-CODING
exit /b %ERRORLEVEL%
