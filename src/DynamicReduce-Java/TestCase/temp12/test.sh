#!/usr/bin/env bash

script_dir=$(dirname "$(realpath "$0")")

cd "$script_dir" || exit



class="a.b.Test"
classpath="/home/tilt/tilt233/cases/FuzzerUtils/.:."
javaMender="/home/tilt/tilt233/cases/FuzzerUtils/JavaMender.py"
javac="/home/tilt/tilt233/JVMS/ibm-semeru-open-jdk_x64_linux_11.0.16_8_openj9-0.33.0/jdk-11.0.16+8/bin/javac"
java1="/home/tilt/tilt233/JVMS/ibm-semeru-open-jdk_x64_linux_11.0.16_8_openj9-0.33.0/jdk-11.0.16+8/bin/java"
java2="/home/tilt/tilt233/JVMS/OpenJDK11U-jdk_x64_linux_hotspot_2022-07-13-05-49/jdk-11.0.16+7/bin/java"
mkdir -p $script_dir/a/b/
cp $script_dir/Test.java $script_dir/a/b/Test.java
> out1.txt
> out2.txt
python3 $javaMender $script_dir/Test.java
if ! $javac -cp $classpath "a/b/Test.java"
then
  exit 1
fi


timeout -s KILL 10s $java1 -Xmx1G -cp $classpath $class >out1.txt 2>&1
if [ $? -ne 0 ]; then
    exit 1
fi

timeout -s KILL 10s $java2 -Xmx1G -cp $classpath $class >out2.txt 2>&1
if [ $? -ne 0 ]; then
    exit 1
fi

if diff -q out1.txt out2.txt > /dev/null; then
    exit 1
else
    exit 0
fi
