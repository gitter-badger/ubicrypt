#!/bin/bash

set -e

version=$1
jar=$2
mainClass=$3

javapackager \
    -deploy \
    -BappVersion=$version \
    -Bmac.CFBundleIdentifier=hellofx \
    -Bmac.CFBundleName=HelloFX \
    -Bruntime="$JAVA_HOME/../../" \
    -native dmg \
    -name HelloFX \
    -title HelloFX \
    -vendor cbeams \
    -outdir build \
    -srcfiles $jar \
    -appclass $mainClass \
    -outfile HelloFX

#-Bicon=client/icons/mac.icns \
