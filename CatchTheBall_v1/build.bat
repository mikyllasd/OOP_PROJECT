@echo off
echo ============================================
echo  Catch the Ball - Farm Edition
echo  Build Script
echo ============================================

:: Check for Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java not found. Please install JDK 17 or later.
    echo Download from: https://adoptium.net/
    pause
    exit /b 1
)

:: Check for javac
javac -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java compiler (javac) not found.
    echo Make sure you have JDK installed, not just JRE.
    echo Download JDK from: https://adoptium.net/
    pause
    exit /b 1
)

echo Compiling...
if not exist out mkdir out
javac -encoding UTF-8 -d out src\*.java

if errorlevel 1 (
    echo Compilation FAILED. Check error messages above.
    pause
    exit /b 1
)

echo Compilation successful!
echo.

:: Create executable JAR
echo Packaging JAR...
echo Main-Class: Main > manifest.txt
jar cfm CatchTheBall.jar manifest.txt -C out .
del manifest.txt

echo.
echo ============================================
echo  BUILD SUCCESSFUL!
echo  Run: java -jar CatchTheBall.jar
echo  Or double-click: run.bat
echo ============================================

:: Create run.bat
echo @echo off > run.bat
echo java -jar CatchTheBall.jar >> run.bat

echo.
echo Created run.bat - double-click it to play!
pause
