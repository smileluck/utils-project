#!/bin/bash
PID=$(ps -ef|grep ytqms-wx-1.0-SNAPSHOT.jar| grep -v grep | grep -v tail | awk '{printf $2}')

if [ $? -eq 0 ]; then
    echo "查询进程ID为 : $PID"
else
    echo "脚本执行失败,退出"
    exit
fi

if [ ! -n "$PID" ] || [ ! $PID ] || [ "$PID" = "" ]; then
    echo "未查询到PID,直接启动项目"
    java -server -Xms512m -Xmx512m -Dspring.profiles.active=prod -Duser.timezone=GMT+08 -jar ytqms-wx-1.0-SNAPSHOT.jar > ytqms-wx-1.0-SNAPSHOT.out 2>&1 &
else
    kill -9 ${PID}

    if [ $? -eq 0 ];then
        echo "杀死进程成功,启动项目"
        java -server -Xms512m -Xmx512m -Dspring.profiles.active=prod -Duser.timezone=GMT+08 -jar ytqms-wx-1.0-SNAPSHOT.jar > ytqms-wx-1.0-SNAPSHOT.out 2>&1 &
    else
        echo "杀死进程失败"
    fi

fi