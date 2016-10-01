#!/bin/bash

set -e

version=$1
jar=$2
mainClass=$3

javapackager -deploy \
    -BappVersion=$version \
    -Bcategory=Finance \
    -BlicenseType=GPLv3 \
    -Bemail=chris@beams.io \
    -native deb \
    -name HelloFX \
    -title HelloFX \
    -vendor cbeams \
    -outdir build \
    -appclass $mainClass \
    -srcfiles $jar \
    -outfile HelloFX

# -Bicon=client/icons/icon.png \
