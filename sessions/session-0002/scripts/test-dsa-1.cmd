@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0run-test.ps1" -TestClass Dsa1Test -Round DSA-1
exit /b %ERRORLEVEL%
