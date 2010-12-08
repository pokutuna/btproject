#!/bin/sh
#OUT_DIR="~/Dropbox/LifeLog\ Project/"
rsync -avz --stats tokuami@ns.hcilab.jp:~/human_network/ ${HOME}/log/
