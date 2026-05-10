@echo off
echo Compiling...
javac *.java
if %errorlevel% == 0 (
    echo Running...
    java Main
) else (
    echo Compilation failed. Make sure JDK 11+ is installed.
    pause
)
