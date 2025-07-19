import runDR

if __name__=="__main__":
    command = []
    command += ["-cov","true"]
    command += ["-lo","true"]
    dst=runDR.copy_folder("MFC")
    runDR.list_all(dst,command)
    runDR.run_baseline()

