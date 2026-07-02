#!/bin/bash

# Render Build Script for Spring Boot Application
# This script is optional and can be used if not using Docker

set -e  # Exit on error

echo "======================================"
echo "Building EventZone Backend Application"
echo "======================================"

# Install dependencies and build
echo "Running Maven build..."
./mvnw clean package -DskipTests

echo "======================================"
echo "Build completed successfully!"
echo "======================================"
