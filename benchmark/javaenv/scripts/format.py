import os
import xml.etree.ElementTree as ET
import shutil
import re

def list_all(path):
    for entry in os.listdir(path):
        full_path = os.path.join(path, entry)
        if os.path.isdir(full_path) and entry!="FuzzerUtils":
            shutil.copytree(full_path, os.path.join("/tmp/javaenv/java_benchmarks", unify(entry)))

def unify(entry):
    match=re.match(r'^(.*)_(\d+)$',entry)
    if match:
        num=int(match.group(2))
        return f"{num:02d}"+match.group(1)
    else:
        return "00"+entry

list_all("/tmp/javaenv/java_benchmarks_useful")
