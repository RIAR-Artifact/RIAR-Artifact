import runDR

if __name__=="__main__":
    command = []
    command += ["-ro","false"]
    command += ["-coverage","true"]
    command += ["-lo","true"]
    dst=runDR.copy_folder("MFC")
    runDR.list_all(dst,command)
    runDR.run_baseline()

