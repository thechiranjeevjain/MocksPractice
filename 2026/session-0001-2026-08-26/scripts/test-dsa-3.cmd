@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0run-test.ps1" -TestClass Dsa3Test -Round DSA-3
exit /b %ERRORLEVEL%
