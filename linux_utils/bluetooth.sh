#!/bin/bash

if [ $# -lt 1 -o $# -gt 3 ]; then
	echo 'Not valid args'
	exit
fi	

cmd=$1
dev='/dev/rfcomm0'
mac='20:15:05:22:03:44'

if [ $cmd != 'bind' -a $cmd != 'release' -a $cmd != 'monitor' ]; then
	echo 'Not valid args'
	exit
fi	

if [ $cmd = 'monitor' -o $cmd = 'release' ]; then
	if [ $# -gt 2 ]; then
		echo 'Not valid args'
		exit
	fi
fi

if [ $# -eq 2 ]; then
	dev=$2
fi

if [ $# -eq 3 ]; then
	mac=$3
fi

if [ $cmd = 'monitor' ]; then
	echo 'Executing: cat' $dev
	exec cat $dev	
fi

if [ $cmd = 'bind' ]; then
	echo 'Executing: rfcomm' $cmd $dev $mac
	exec rfcomm $cmd $dev $mac
fi

if [ $cmd = 'release' ]; then
	echo 'Executing: rfcomm' $cmd $dev
	exec rfcomm $cmd $dev
fi
