@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0run-test.ps1" -TestClass ConcurrencyTest -Round CONCURRENCY
exit /b %ERRORLEVEL%
