import argparse
import textwrap
import json
import csv
import os
import glob
import subprocess

def processFile(bench, result):
    stats = {"ans":"", "time":"", "mem":"", "ins":""}
    for r in result.splitlines():
        if 'no errors detected' in r:
            stats.update({"ans":"SAFE"})
        if 'elapsed time' in r:
            t = r.split()
            time = t[len(t)-1]
            stats.update({"time":str(time)})
        if 'max memory' in r:
            t = r.split()
            mem = t[len(t)-1]
            stats.update({"mem":str(mem)})
        if 'instructions' in r:
            t = r.split()
            ins = t[len(t)-1]
            stats.update({"ins":str(ins)})
        if 'java.lang.AssertionError' in r:
            stats.update({"ans":"CEX"})
    return {bench:stats}


def runBench(args):
    dr = args.directory
    viz_html = ""
    all_dir = [os.path.join(dr, name)for name in os.listdir(dr) if os.path.isdir(os.path.join(dr, name)) ]
    all_results = {}
    for d in all_dir:
        tmp = d.split("/")
        bench = tmp[len(tmp)-1]
        jpf = glob.glob(os.path.abspath(d) + os.sep + "*.jpf")
        if len(jpf) == 1:
            cmd = ['java', "-jar", "/Users/teme/Documents/GitHub/jpf-core/build/RunJPF.jar", "+shell.port=4242", jpf[0]]
            p = subprocess.Popen(cmd, shell=False, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
            result, _ = p.communicate()
            ans = processFile(bench, result)
            all_results.update(ans)
    print all_results


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        prog='Bench Analysis Utils',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        description=textwrap.dedent('''\
                   Bench Analysis Utils
            --------------------------------
            '''))
    #parser.add_argument ('file', metavar='BENCHMARK', help='Benchmark file')
    parser.add_argument ('directory', metavar='DIR', help='Benchmark dirs')
    #parser.add_argument('-fc', '--fc', required=False, dest="fc", action="store_true")
    #parser.add_argument('-err', '--err', required=False, dest="err", action="store_true")

    args = parser.parse_args()
    try:
        runBench(args)
    except Exception as e:
        print str(e)
