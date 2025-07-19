import runDR

if __name__=="__main__":
    command = []
    command += ["-coverage","false"]
    command += ["-ct","false"]
    command += ["-lo","false"]
    dst=runDR.copy_folder("Blank")
    runDR.list_all(dst,command)
    command = []
    command += ["-coverage","false"]
    command += ["-lo","false"]
    dst=runDR.copy_folder("M")
    runDR.list_all(dst,command)
    command = []
    command += ["-coverage","false"]
    command += ["-lo","true"]
    dst=runDR.copy_folder("MF")
    runDR.list_all(dst,command)
    command = []
    command += ["-coverage","true"]
    command += ["-lo","true"]
    dst=runDR.copy_folder("MFC")
    runDR.list_all(dst,command)

