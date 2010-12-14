#!/bin/sh
rsync -avz -e "ssh -i ${HOME}/Dropbox/auth/synckey" tokuami@ns.hcilab.jp:~/human_network/ ${HOME}'/Dropbox/LifeLog Project/LogServerMirror/'
