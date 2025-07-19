import os
import subprocess
import csv
import json
import sys

from clang import cindex

def count_tokens(source_file_path: str) -> int:
    try:
        token_counter_sh = os.path.join("/tmp/cenv", "binaries", "run_token_counter.sh")
        count = subprocess.check_output(
            [token_counter_sh, source_file_path],
            stderr=subprocess.STDOUT)
        return int(count)
    except Exception as err:
        print("Error counting token for " + source_file_path)
        raise err

cindex.Config.set_library_file(r"/usr/lib/llvm-7/lib/libclang.so")  
def format_c(file_path):
    tokens=count_tokens(file_path)
    index = cindex.Index.create()
    tu = index.parse(file_path)
    with open('temp.c', 'w', encoding='utf-8') as f:
        for token in tu.get_tokens(extent=tu.cursor.extent):
            f.write(token.spelling+"\n")
    cmd=["clang-format",'/tmp/temp0123.c']
    result = subprocess.run(cmd, capture_output=True, text=True)
    text=result.stdout
    print(text)
    os.remove('/tmp/temp0123.c')
    return tokens,sum(1 for line in text.splitlines() if line.strip())

def read():
    folders=["creduce","perses","vulcan"]
    begin=True
    for folder in folders:
        print(f"{folder}-result")
        times=[]
        lines=[]
        tokens=[]
        folders=[["folders"]]
        for foldername, subfolders, filenames in sorted(os.walk(os.path.join("rq1_result",f"{folder}-result"))):
            print(f"current directory: {foldername}")
            cfile = os.path.join(foldername, "small.c")
            if not os.path.exists(cfile):
                continue
            folders.append([os.path.basename(foldername)])
            token,line=format_c(cfile)
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
        writer.writerow(["folders","rtr_time","rtr_tokens","rtr_lines"])
    sum=["sum",0,0,0]
    for foldername, subfolders, filenames in sorted(os.walk(dir)):
        print(f"current directory: {foldername}")
        cfile = os.path.join(foldername, "small_reduced.c")
        if not os.path.exists(cfile):
            continue
        lineC=[]
        lineC.append(os.path.basename(foldername))
        token,line=format_c(cfile)
        for filename in filenames:
            if(filename.endswith(".json")):
                file_path = os.path.join(foldername, filename)
                with open(file_path) as f:
                    record = json.load(f)
                    lineC.append((int)(record["TOTAL"]["TotalTime"]))
                    sum[1]+=(int)(record["TOTAL"]["TotalTime"])
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

if __name__ == '__main__':
    if len(sys.argv) > 1:
        readDR(sys.argv[1])
    else:
        read()