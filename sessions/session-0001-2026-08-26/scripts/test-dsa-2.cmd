@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0run-test.ps1" -TestClass Dsa2Test -Round DSA-2
exit /b %ERRORLEVEL%
