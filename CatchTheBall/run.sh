#!/bin/bash
# Compile and run Catch the Ball
echo "Compiling..."
javac *.java
if [ $? -eq 0 ]; then
    echo "Running..."
    java Main
else
    echo "Compilation failed. Make sure JDK 11+ is installed."
fi
