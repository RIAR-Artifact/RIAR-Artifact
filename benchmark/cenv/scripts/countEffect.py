import os
import subprocess
import csv
import json
import sys

def readDR():
    dir="c_benchmarks_MFC"
    with open('record_effect.csv', mode='w', newline='') as outfile:
        writer = csv.writer(outfile)
        writer.writerow(["folders","M","F","C"])
    sum=["sum",0,0,0]
    for foldername, subfolders, filenames in sorted(os.walk(dir)):
        print(f"current directory: {foldername}")
        cfile = os.path.join(foldername, "small_reduced.c")
        if not os.path.exists(cfile):
            continue
        lineC=[]
        lineC.append(os.path.basename(foldername))
        for filename in filenames:
            if(filename.endswith(".json")):
                file_path = os.path.join(foldername, filename)
                with open(file_path) as f:
                    record = json.load(f)
                    lineC.append((float)(record["effectRecord0"]["Time"]))
                    sum[1]+=((float)(record["effectRecord0"]["Time"]))
                    lineC.append((float)(record["effectRecord1"]["Time"]))
                    sum[2]+=((float)(record["effectRecord1"]["Time"]))
                    lineC.append((float)(record["effectRecord2"]["Time"]))
                    sum[3]+=((float)(record["effectRecord2"]["Time"]))
        with open('record_effect.csv', mode='a', newline='') as outfile:
            writer = csv.writer(outfile)
            writer.writerow(lineC)
    
    with open('record_effect.csv', mode='a', newline='') as outfile:
        writer = csv.writer(outfile)
        writer.writerow(sum)

if __name__ == '__main__':
    readDR()
