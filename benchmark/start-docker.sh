#!/usr/bin/env bash

set -o pipefail
set -o nounset
set -o errexit

docker pull tilt233/dynamic-reduce:latest

docker container run \
	--cap-add SYS_PTRACE \
	--interactive \
	--tty tilt233/dynamic-reduce:latest

