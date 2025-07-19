import os
import subprocess
import csv
import json
import re
import sys
    


def extract_time_float1(text):
    match = re.search(r'effectCounter1 take time:\s*([\d.]+)\s*', text)
    if match:
        return float(match.group(1))
    else:
        return None
    
def extract_time_float2(text):
    match = re.search(r'effectCounter2 take time:\s*([\d.]+)\s*', text)
    if match:
        return float(match.group(1))
    else:
        return None
    
def extract_time_float3(text):
    match = re.search(r'effectCounter3 take time:\s*([\d.]+)\s*', text)
    if match:
        return float(match.group(1))
    else:
        return None
    
def readDR():
    dir="java_benchmarks_MFC"
    with open('record_effect.csv', mode='w', newline='') as outfile:
        writer = csv.writer(outfile)
        writer.writerow(["folders","M","F","C"])
    sum=["sum",0,0,0]
    for foldername, subfolders, filenames in sorted(os.walk(dir)):
        print(f"current directory: {foldername}")
        java_files = [f for f in os.listdir(foldername) if f.endswith('_reduced.java') and os.path.isfile(os.path.join(foldername, f))]
        if len(java_files)==0:
            continue
        lineC=[]
        lineC.append(os.path.basename(foldername))
        for filename in filenames:
            if(filename.endswith("record.txt")):
                file_path = os.path.join(foldername, filename)
                with open(file_path) as f:
                    filestring=f.read()
                    time=extract_time_float1(filestring)
                    lineC.append((float)(time))
                    sum[1]+=(float)(time)
                    time=extract_time_float2(filestring)
                    lineC.append((float)(time))
                    sum[2]+=(float)(time)
                    time=extract_time_float3(filestring)
                    lineC.append((float)(time))
                    sum[3]+=(float)(time)
        with open('record_effect.csv', mode='a', newline='') as outfile:
            writer = csv.writer(outfile)
            writer.writerow(lineC)
    
    with open('record_effect.csv', mode='a', newline='') as outfile:
        writer = csv.writer(outfile)
        writer.writerow(sum)


if __name__ == '__main__':
    readDR()
