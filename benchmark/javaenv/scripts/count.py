import os
import subprocess
import csv
import json
import re
import sys


def count_tokens(source_file_path: str) -> int:
    try:
        token_counter_sh = os.path.join("/tmp/javaenv", "binaries", "run_token_counter.sh")
        count = subprocess.check_output(
            [token_counter_sh, source_file_path],
            stderr=subprocess.STDOUT)
        return int(count)
    except Exception as err:
        print("Error counting token for " + source_file_path)
        raise err
    

def format_java(javafile):
    token=count_tokens(javafile)
    cmd=["/JVMS/jdk-17.0.12/bin/java","-jar","/tmp/javaenv/scripts/google-java-format-1.26.0-all-deps.jar",javafile]
    result = subprocess.run(cmd, capture_output=True, text=True)
    text=result.stdout
    return token,sum(1 for line in text.splitlines() if line.strip())

def extract_time_float(text):
    match = re.search(r'time:\s*([\d.]+)\s*', text)
    if match:
        return float(match.group(1))
    else:
        return None
    
def read():
    folder_names=["creduce","perses","vulcan"]
    begin=True
    for folder in folder_names:
        print(f"{folder}-result")
        times=[]
        lines=[]
        tokens=[]
        folders=[["folders"]]
        for foldername, subfolders, filenames in sorted(os.walk(os.path.join("rq1_result",f"{folder}-result"))):
            print(f"current directory: {foldername}")
            java_files = [f for f in os.listdir(foldername) if f.endswith('.java') and os.path.isfile(os.path.join(foldername, f))]
            if len(java_files)==0:
                continue
            javafile = os.path.join(foldername, java_files[0])
            if not os.path.exists(javafile):
                continue
            folders.append([os.path.basename(foldername)])
            token,line=format_java(javafile)
            tokens.append(token)
            lines.append(line)
            for filename in filenames:
                if(filename.endswith(".json")):
                    file_path = os.path.join(foldername, filename)
                    with open(file_path) as f:
                        record = json.load(f)
                        times.append(record["Time"])
        if begin:
            with open('record.csv', mode='w', newline='') as outfile:
                writer = csv.writer(outfile)
                writer.writerows(folders)
            begin=False
        with open('record.csv', mode='r', newline='') as infile:
            reader = csv.reader(infile)
            rows = list(reader)
            header = rows[0]
            header.append(f"{folder}_time")  
            header.append(f"{folder}_tokens")
            header.append(f"{folder}_lines") 
            index=0
            for row in rows[1:]:
                if index<len(times):
                    row.append(times[index])
                    row.append(tokens[index])
                    row.append(lines[index])
                    index+=1

            with open('record.csv', mode='w', newline='') as outfile:
                writer = csv.writer(outfile)
                writer.writerows([header] + rows[1:])


def readDR(dir):
    with open('record_'+dir+'.csv', mode='w', newline='') as outfile:
        writer = csv.writer(outfile)
        writer.writerow(["folders","dr_time","dr_tokens","dr_lines"])
    sum=["sum",0,0,0]
    for foldername, subfolders, filenames in sorted(os.walk(dir)):
        print(f"current directory: {foldername}")
        java_files = [f for f in os.listdir(foldername) if f.endswith('_reduced.java') and os.path.isfile(os.path.join(foldername, f))]
        if len(java_files)==0:
            continue
        javafile = os.path.join(foldername, java_files[0])
        lineC=[]
        lineC.append(os.path.basename(foldername))
        token,line=format_java(javafile)
        for filename in filenames:
            if(filename.endswith("record.txt")):
                file_path = os.path.join(foldername, filename)
                with open(file_path) as f:
                    time=extract_time_float(f.read())
                    lineC.append((int)(time))
                    sum[1]+=(int)(time)
        lineC.append(token)
        lineC.append(line)
        sum[2]+=token
        sum[3]+=line
        with open('record_'+dir+'.csv', mode='a', newline='') as outfile:
            writer = csv.writer(outfile)
            writer.writerow(lineC)
    
    with open('record_'+dir+'.csv', mode='a', newline='') as outfile:
        writer = csv.writer(outfile)
        writer.writerow(sum)

def readOrigin():
    with open('record_origin.csv', mode='w', newline='') as outfile:
        writer = csv.writer(outfile)
        writer.writerow(["folders","origin_tokens","origin_lines"])
    sum=["sum",0,0]
    for foldername, subfolders, filenames in sorted(os.walk("java_benchmarks")):
        print(f"current directory: {foldername}")
        java_files = [f for f in os.listdir(foldername) if f.endswith('.java') and os.path.isfile(os.path.join(foldername, f))]
        if len(java_files)==0:
            continue
        javafile = os.path.join(foldername, java_files[0])
        lineC=[]
        lineC.append(os.path.basename(foldername))
        token,line=format_java(javafile)
        lineC.append(token)
        lineC.append(line)
        sum[1]+=token
        sum[2]+=line
        with open('record_origin.csv', mode='a', newline='') as outfile:
            writer = csv.writer(outfile)
            writer.writerow(lineC)
    
    with open('record_origin.csv', mode='a', newline='') as outfile:
        writer = csv.writer(outfile)
        writer.writerow(sum)

if __name__ == '__main__':
    if len(sys.argv) > 1:
        if sys.argv[1]=="java_benchmarks":
            readOrigin()
        else:
            readDR(sys.argv[1])
    else:
        read()
