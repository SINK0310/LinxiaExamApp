#!/bin/sh

# Gradle wrapper startup script
# Uses locally installed Gradle or downloads it

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

# Find Gradle - use local installation or download wrapper
if command -v gradle >/dev/null 2>&1; then
    exec gradle "$@"
else
    echo "ERROR: Gradle is not installed."
    exit 1
fi
