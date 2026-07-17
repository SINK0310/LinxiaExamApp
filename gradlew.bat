@echo off
REM Gradle wrapper using locally installed Gradle
set JAVA_HOME=F:\jdk17\jdk-17.0.2
set "GRADLE_HOME=F:\gradle\gradle-8.6"
set "PATH=%JAVA_HOME%\bin;%GRADLE_HOME%\bin;%PATH%"

if "%1"=="" (
    echo Usage: gradlew.bat ^<task^>
    echo Example: gradlew.bat assembleDebug
    exit /b 1
)

call "%GRADLE_HOME%\bin\gradle.bat" %*
