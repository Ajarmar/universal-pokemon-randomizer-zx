@echo off
pushd "%~dp0"
cd /d "%~dp0"

where java >nul 2>nul
if %errorlevel%==1 (
    echo Java is not installed.

    goto press_key_to_exit
)

java -version 2>&1 | findstr /i "64-Bit" >nul
if %errorlevel%==1 (
    echo Wrong Java version: Java 32-Bit instead of 64-Bit is installed.

    goto press_key_to_exit
)

java -Xmx4608M -jar PokeRandoZX.jar please-use-the-launcher

:press_key_to_exit
echo Press any key to exit...
pause >nul
