#!/bin/sh

export PATH=$PATH:/opt/checker-0.147

scan-build -k -V xcodebuild -configuration Debug
