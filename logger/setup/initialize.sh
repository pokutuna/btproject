#!/bin/bash

sudo aptitude install ssh rsync sysv-rc-conf ruby wget

#device up
sudo hciconfig hci0 up
sudo hciconfig hci0 piscan
sudo ifconfig eth0 up

#ntpdate
sudo ln -s /etc/network/if-up.d/ntpdate /etc/cron.hourly/
sudo /etc/init.d/cron restart