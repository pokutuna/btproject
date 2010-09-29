#!/bin/bash

installs="sudo aptitude install ssh rsync sysv-rc-conf ruby wget"

#eval $installs || (export http_proxy=http://proxy.ksc.kwansei.ac.jp:8080/ && echo set proxy! && eval $installs)

ssh-keygen -P "" -f ./$USER-key

#stop services
#sudo sysv-rc-conf apport off
#sudo sysv-rc-conf avahi-daemon off
#sudo sysv-rc-conf cups off
#sudo sysv-rc-conf laptop-mode off
#sudo sysv-rc-conf speech-dispatcher off
#sudo sysv-rc-conf usplash off
#sudo sysv-rc-conf klogd off
#sudo sysv-rc-conf pulseaudio off
#sudo sysv-rc-conf sysklogd off
#sudo sysv-rc-conf nfs-common

#power manager
#ac
gconftool-2 --set --type int /apps/gnome-power-manager/timeout/sleep_computer_ac 0
gconftool-2 --set --type int /apps/gnome-power-manager/timeout/sleep_display_ac 60
gconftool-2 --set --type boolean /apps/gnome-power-manager/backlight/idle_dim_ac true

#battery
gconftool-2 --set --type int /apps/gnome-power-manager/timeout/sleep_computer_battery 0
gconftool-2 --set --type int /apps/gnome-power-manager/timeout/sleep_display_battery 60
gconftool-2 --set --type boolean /apps/gnome-power-manager/backlight/idle_dim_battery true
gconftool-2 --set --type boolean /apps/gnome-power-manager/backlight/battery_reduce true