#!/bin/sh

# Gradle wrapper startup script
# Downloads and uses Gradle 8.6 specifically

APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

# Use JAVA_HOME if set, otherwise find java
if [ -z "$JAVA_HOME" ]; then
    if command -v java >/dev/null 2>&1; then
        JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
    else
        echo "ERROR: JAVA_HOME is not set and no 'java' command could be found."
        exit 1
    fi
fi

# Determine the Gradle version and download URL
GRADLE_VERSION="8.6"
GRADLE_HOME="$HOME/.gradle/wrapper/dists/gradle-$GRADLE_VERSION"
GRADLE_BIN="$GRADLE_HOME/bin/gradle"

if [ ! -f "$GRADLE_BIN" ]; then
    echo "Downloading Gradle $GRADLE_VERSION..."
    URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"
    # Try alternate mirrors if needed
    ZIP_FILE="/tmp/gradle-${GRADLE_VERSION}-bin.zip"
    if command -v curl >/dev/null 2>&1; then
        curl -fsSL -o "$ZIP_FILE" "$URL"
    elif command -v wget >/dev/null 2>&1; then
        wget -q -O "$ZIP_FILE" "$URL"
    else
        echo "ERROR: Neither curl nor wget found"
        exit 1
    fi
    
    if [ ! -f "$ZIP_FILE" ] || [ ! -s "$ZIP_FILE" ]; then
        echo "ERROR: Failed to download Gradle"
        exit 1
    fi
    
    mkdir -p "$GRADLE_HOME"
    unzip -oq "$ZIP_FILE" -d "$GRADLE_HOME/.."
    rm "$ZIP_FILE"
fi

exec "$GRADLE_BIN" "$@"
