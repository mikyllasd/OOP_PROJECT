#!/bin/bash
echo "============================================"
echo " Catch the Ball - Farm Edition"
echo " Build Script"
echo "============================================"

# Check for javac
if ! command -v javac &> /dev/null; then
    echo "ERROR: javac not found. Please install JDK 17+."
    echo "Ubuntu/Debian: sudo apt install openjdk-21-jdk"
    echo "Mac: brew install openjdk"
    echo "Or download from: https://adoptium.net/"
    exit 1
fi

echo "Compiling..."
mkdir -p out
javac -encoding UTF-8 -d out src/*.java

if [ $? -ne 0 ]; then
    echo "Compilation FAILED."
    exit 1
fi

echo "Packaging JAR..."
echo "Main-Class: Main" > manifest.txt
jar cfm CatchTheBall.jar manifest.txt -C out .
rm manifest.txt

echo ""
echo "============================================"
echo " BUILD SUCCESSFUL!"
echo " Run: java -jar CatchTheBall.jar"
echo "============================================"
chmod +x run.sh
cat > run.sh << 'EOF'
#!/bin/bash
java -jar CatchTheBall.jar
EOF
chmod +x run.sh
echo "Created run.sh - execute it to play!"
