#!/bin/sh
rsync -avz -e "ssh -i '${HOME}/Dropbox/LifeLog Project/LogServerMirror/mirrorkey'" tokuami@ns.hcilab.jp:~/human_network/ ${HOME}'/Dropbox/LifeLog Project/LogServerMirror/'
