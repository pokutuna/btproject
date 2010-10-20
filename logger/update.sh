#!/bin/sh
wget http://ns.hcilab.jp/~tokuami/logger.zip || http_proxy="http://proxy.ksc.kwansei.ac.jp:8080" wget http://ns.hcilab.jp/~tokuami/logger.zip
unzip -o ./logger.zip -d ./tmp
cp -r ./tmp/logger/* ./
rm -r ./logger.zip ./tmp
