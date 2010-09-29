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