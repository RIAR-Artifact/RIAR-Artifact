import os
import subprocess
import time
import xml.etree.ElementTree as ET
import shutil

def list_all(path):
    i=0
    for entry in os.listdir(path):
        full_path = os.path.join(path, entry)
        if os.path.isdir(full_path) and entry!="FuzzerUtils":
            tree = ET.parse(full_path+"/classinfo.xml")
            root = tree.getroot()
            className=root.find("classname").text
            print(" "+full_path+" start ")
            command = ["bash","/tmp/cenv/sanity_test.sh","test.sh",className+".java"]
            result = subprocess.run(command, text=True,cwd=full_path,check=False)
            if result.returncode==0:
                with open(path+'/useful.txt', 'a') as f:
                    f.write(entry+"\n")
                    f.close()
                shutil.copytree(full_path, os.path.join("/tmp/javaenv/java_benchmarks_useful", entry))


list_all("/tmp/javaenv/java_benchmarks_tmp")
