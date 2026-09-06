@echo off
REM Bookshop POS launcher for Windows.
REM Double-click this file to build and run the app.
REM Requires JDK 21 and Maven to already be installed and on PATH.

cd /d "%~dp0"

echo Starting Bookshop POS...
echo (First run may take a minute while Maven downloads dependencies.)
echo.

call mvn javafx:run

if errorlevel 1 (
    echo.
    echo Something went wrong. Common causes:
    echo   - Java or Maven not installed, or not added to PATH
    echo   - No internet connection on first run ^(needed to download JavaFX/SQLite^)
    echo.
    pause
)
