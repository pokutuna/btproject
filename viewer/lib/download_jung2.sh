#!/bin/sh
SELF_DIR=`echo $(cd $(dirname $0);pwd)`
cd ${SELF_DIR}
wget "http://downloads.sourceforge.net/project/jung/jung/jung-2.0.1/jung2-2_0_1.zip" && unzip -o jung2-2_0_1.zip && rm jung2-2_0_1.zip
