#!/usr/bin/env bash

set -o pipefail
set -o nounset
set -o errexit

if [ $# -ne 1 ]; then
  echo "Usage: $0 <num_cpus>"
  exit 1
fi

readonly NCPU=$1

count=0

for dir in java_benchmarks/*/ ; do
    benchmark_name=$(basename "${dir}")






    ./run_benchmark.py -s "${dir}" -r creduce -o rq1_result/creduce-result/"${benchmark_name}" &
    count=$((count+1))
    if [[ ${count} == "${NCPU}" ]]; then
        wait
        count=0
    fi






done
