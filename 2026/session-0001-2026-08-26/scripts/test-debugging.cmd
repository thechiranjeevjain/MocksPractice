@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0run-test.ps1" -TestClass DebuggingTest -Round DEBUGGING
exit /b %ERRORLEVEL%
