#!/bin/bash

# News Feed Mock API Server Startup Script

echo "================================"
echo "News Feed Mock API Server"
echo "================================"
echo ""

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "❌ Error: Node.js is not installed"
    echo "Please install Node.js from https://nodejs.org/"
    exit 1
fi

echo "✓ Node.js version: $(node --version)"
echo ""

# Check if npm is installed
if ! command -v npm &> /dev/null; then
    echo "❌ Error: npm is not installed"
    exit 1
fi

echo "✓ npm version: $(npm --version)"
echo ""

# Install dependencies if node_modules doesn't exist
if [ ! -d "node_modules" ]; then
    echo "📦 Installing dependencies..."
    npm install
    echo ""
fi

# Get local IP address
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    LOCAL_IP=$(ifconfig | grep "inet " | grep -v 127.0.0.1 | awk '{print $2}' | head -n 1)
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    LOCAL_IP=$(hostname -I | awk '{print $1}')
else
    LOCAL_IP="localhost"
fi

echo "🚀 Starting mock API server..."
echo ""
echo "Server will be available at:"
echo "  - Local:   http://localhost:3000"
echo "  - Network: http://$LOCAL_IP:3000"
echo ""
echo "API Endpoints:"
echo "  - GET  /api/feed         - Get news feed"
echo "  - GET  /api/posts/:id    - Get post detail"
echo "  - POST /api/posts        - Create new post"
echo "  - POST /api/posts/:id/interact - Like/share post"
echo ""
echo "For Android Emulator, use: http://10.0.2.2:3000"
echo "For Physical Device, use: http://$LOCAL_IP:3000"
echo ""
echo "Press Ctrl+C to stop the server"
echo "================================"
echo ""

npm start
