@echo off
@echo ------------------------------------------------
@echo 请使用管理员权限打开
@echo enter the version you want to change:
@echo options   version
@echo 8      	JDK8
@echo 20     	JDK20
@echo 19     	JDK19
@echo 17     	JDK17
@echo ------------------------------------------------
set /P choose=please enter your choice:
IF "%choose%" EQU "8" (
    REG "JAVA_HOME" 修改为自己的jdk安装路径
    setx "JAVA_HOME" "E:\devlop\Java\jdk1.8.0_291" /m
    echo "JAVA_HOME" has been modified E:\devlop\Java\jdk1.8.0_291
) ELSE IF "%choose%" EQU "20" (
    setx "JAVA_HOME" "E:\devlop\Java\jdk-20.0.1" /m
    echo "JAVA_HOME" has been modified E:\devlop\Java\jdk-20.0.1
) ELSE IF "%choose%" EQU "19" (
    setx "JAVA_HOME" "E:\devlop\Java\jdk-19.0.2" /m
    echo "JAVA_HOME" has been modified E:\devlop\Java\jdk-19.0.2
) ELSE IF "%choose%" EQU "17" (
    setx "JAVA_HOME" "E:\devlop\Java\jdk-17" /m
    echo "JAVA_HOME" has been modified E:\devlop\Java\jdk-17
)
pause
