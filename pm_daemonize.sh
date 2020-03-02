#!/bin/sh

if [ $# = 0 ]
then
    echo "Usage : $0 command"
else
    $* > server.log&
    PID=$!
    echo $PID
fi
