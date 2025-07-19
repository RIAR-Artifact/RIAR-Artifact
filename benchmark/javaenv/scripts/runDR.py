import os
import subprocess
import time
import xml.etree.ElementTree as ET
import shutil
def list_all(path,additional_command):
    for entry in sorted(os.listdir(path)):
        full_path = os.path.join(path, entry)
        if os.path.isdir(full_path):
            if "test.sh" in os.listdir(full_path):
                tree = ET.parse(full_path+"/classinfo.xml")
                root = tree.getroot()
                folder=root.find("folder").text
                if (folder==None):
                    folder=""
                className=root.find("classname").text
                if(className==None):
                    continue
                classPath=root.find("classpath").text
                if(classPath==None):
                    continue
                command = ["/JVMS/jdk-17.0.12/bin/java","-jar","/tmp/javaenv/DynamicReduce.jar"]
                command += ["-cn",className]
                command += ["-fcn",classPath]
                if (not folder==""):
                    command += ["-sd",folder+"/"]
                command += ["-j","/01JVMS/linux64/openjdk11/openj9_11"]
                command += ["-cp",".:/HotspotTests-Java/lib/junit-4.13.1.jar:/HotspotTests-Java/lib/testng-6.14.3.jar:/HotspotTests-Java/lib/tools.jar:/HotspotTests-Java/HotspotTests-Java/."]
                command += ["-d",entry+"/"]
                command += additional_command
                print(command)
                timestamp = time.time()
                print(time.strftime('%Y-%m-%d %H:%M:%S', time.localtime())+" "+full_path+" start")
                with open(path+'/time.txt', 'a') as f:
                    f.write(time.strftime('%Y-%m-%d %H:%M:%S', time.localtime())+" "+full_path+" RT start\n")
                    f.close()
                result = subprocess.run(command, text=True,cwd=path)
                print(time.strftime('%Y-%m-%d %H:%M:%S', time.localtime())+" "+full_path+" end")
                with open(path+'/time.txt', 'a') as f:
                    f.write(time.strftime('%Y-%m-%d %H:%M:%S', time.localtime())+" "+full_path+" RT end\n")
                    f.close()
                for name in os.listdir(full_path):
                    to_delete_path = os.path.join(full_path, name)
                    if os.path.isdir(to_delete_path) and name.startswith("temp"):
                        shutil.rmtree(to_delete_path)

def copy_folder_with_increment(suffix=""):
    src_path="/tmp/javaenv/java_benchmarks"
    base_dst = src_path + suffix+"_"
    i = 1
    while True:
        dst_path = f"{base_dst}{i}"
        if not os.path.exists(dst_path):
            break
        i += 1
    shutil.copytree(src_path, dst_path)
    return dst_path

def copy_folder(suffix):
    src_path="/tmp/javaenv/java_benchmarks"
    base_dst = src_path +"_"+ suffix
    if os.path.exists(base_dst):
        shutil.rmtree(base_dst)
    shutil.copytree(src_path, base_dst)
    return base_dst

def run_baseline():
    command = ["./run_rq1.sh","1"]
    result = subprocess.run(command, text=True,cwd="/tmp/javaenv/java_benchmarks")
