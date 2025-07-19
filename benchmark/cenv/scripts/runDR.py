import os
import subprocess
import time
import xml.etree.ElementTree as ET
import shutil

def list_all(path,additional_command):
    for entry in os.listdir(path):
        full_path = os.path.join(path, entry)
        if os.path.isdir(full_path):
            if not "small_reduced.c" in os.listdir(full_path):
                command = ["/JVMS/jdk-17.0.12/bin/java","-jar","DynamicReduce-C.jar"]
                command += ["-bd",full_path]
                command += ["-f","small.c"]
                command += ["-b","r.sh"]
                command += additional_command

                print(command)
                print(full_path+" start")
                with open(path+'/time.txt', 'a') as f:
                    f.write(full_path+" RT start\n")
                    f.close()
                result = subprocess.run(command, text=True,cwd="/tmp/cenv",check=True)
                print(" "+full_path+" end "+str(result))
                with open(path+'/time.txt', 'a') as f:
                    f.write(" "+full_path+" RT end\n")
                    f.close()
                for name in os.listdir(full_path):
                    to_delete_path = os.path.join(full_path, name)
                    if os.path.isdir(to_delete_path) and name.startswith("DR_test_"):
                        shutil.rmtree(to_delete_path)


def copy_folder_with_increment(suffix=""):
    src_path="/tmp/cenv/c_benchmarks"
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
    src_path="/tmp/cenv/c_benchmarks"
    base_dst = src_path +"_"+ suffix
    if os.path.exists(base_dst):
        shutil.rmtree(base_dst)
    shutil.copytree(src_path, base_dst)
    return base_dst

def run_baseline():
    command = ["./run_rq1.sh","1"]
    result = subprocess.run(command, text=True,cwd="/tmp/cenv")
