#!/usr/bin/env bash
script_dir=$(dirname "$(realpath "$0")")

class="compiler.exceptions.TestRecursiveReplacedException"
classpath=".:/HotspotTests-Java/lib/junit-4.13.1.jar:/HotspotTests-Java/lib/testng-6.14.3.jar:/HotspotTests-Java/lib/tools.jar:/HotspotTests-Java/HotspotTests-Java/."
javaMender="/FuzzerUtils/JavaMender.py"
mkdir -p compiler/exceptions
cp TestRecursiveReplacedException.java compiler/exceptions/TestRecursiveReplacedException.java
python3 $javaMender $script_dir/compiler/exceptions/TestRecursiveReplacedException.java
if ! /01JVMS/linux64/openjdk11/openj9_11/bin/javac -cp $classpath compiler/exceptions/TestRecursiveReplacedException.java
then
        exit 1
fi
> out1.txt
> out2.txt
timeout -s 9 10s /01JVMS/linux64/openjdk11/openj9_11/bin/java -cp $classpath $class > out1.txt 2>&1
exit_code=$?
if [ $exit_code -eq 124 ]; then
    exit 1
elif [ $exit_code -eq 137 ]; then
    exit 1
fi
timeout -s 9 10s /01JVMS/linux64/openjdk11/hotspot_11/bin/java -cp $classpath $class > out2.txt 2>&1
exit_code=$?
if [ $exit_code -eq 124 ]; then
    exit 1
elif [ $exit_code -eq 137 ]; then
    exit 1
fi
if grep -E -q "Exception|Error" out1.txt; then
    exit 1
fi
if grep -E -q "Exception|Error" out2.txt; then
    exit 1
fi
if diff -q out1.txt out2.txt > /dev/null; then
    exit 1
else
    exit 0
fi
        