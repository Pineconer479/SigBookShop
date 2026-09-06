#!/bin/bash
# Bookshop POS launcher for Linux/Mac.
# Run with: ./run.sh
# Requires JDK 21 and Maven to already be installed and on PATH.

cd "$(dirname "$0")"

echo "Starting Bookshop POS..."
echo "(First run may take a minute while Maven downloads dependencies.)"
echo

mvn javafx:run

if [ $? -ne 0 ]; then
    echo
    echo "Something went wrong. Common causes:"
    echo "  - Java or Maven not installed, or not on PATH"
    echo "  - No internet connection on first run (needed to download JavaFX/SQLite)"
    echo
fi
