@echo off
echo Compiling...
dir /s /b src\*.java > sources.txt
javac -d out -cp out @sources.txt
if %errorlevel% neq 0 (
    echo.
    echo COMPILE ERROR! Check above for details.
    pause
    exit
)
echo Compile successful! Starting game...
java -cp out OOP_PROJECT.CatchTheBall.src.main.Main
pause