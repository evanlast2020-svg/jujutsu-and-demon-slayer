#!/bin/sh

##############################################################################
## Gradle start up script for UN*X
##############################################################################

# Attempt to find JAVA_HOME
if [ -z "$JAVA_HOME" ]; then
    if [ -x "/usr/libexec/java_home" ]; then
        export JAVA_HOME=`/usr/libexec/java_home`
    fi
fi

APP_HOME=$(cd "$(dirname "$0")" && pwd)
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

exec "$JAVA_HOME/bin/java" \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
