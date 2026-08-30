@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0run-test.ps1" -TestClass LldTest -Round LLD
exit /b %ERRORLEVEL%
