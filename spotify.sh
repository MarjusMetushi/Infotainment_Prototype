#!/bin/bash

# function to check if command exists
command_exists(){
    command -v "$1" >/dev/null 2>&1
}

OS=$(grep -Ei 'debian|buntu|min' /etc/*release)

if[[ -n "$OS"]]; then
sudo apt update && sudo apt install -y spotify
elif command_exists dnf; then
sudo dnf install -y spotify
elif command_exists pacman; then
sudo pacman -S --noconfirm spotify
elif command_exists zypper; then
sudo zypper install -y spotify


# make it executable