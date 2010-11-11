#!/bin/sh
SELF_DIR=`echo $(cd $(dirname $0);pwd)`
rsync -avz --stats tokuami@ns.hcilab.jp:~/human_network/ ${SELF_DIR}/log/
