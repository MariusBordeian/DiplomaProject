#!/bin/sh

cd /home/pi/Desktop/DiplomaProject
git stash
git reset
git checkout .
git clean -fdx
git pull https://github.com/MariusBordeian/DiplomaProject.git