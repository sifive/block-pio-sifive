#/usr/bin/env bash

set -euvo pipefail


## Environment variable checking

if [[ -z "$WIT_WORKSPACE" ]]; then
  >&2 echo Environment variable WIT_WORKSPACE should be set to the path of the Wit workspace.
  exit 1
fi

if [[ -z "$BLOCKCI_DOCKER_IMAGE" ]]; then
  >&2 echo Environment variable BLOCKCI_DOCKER_IMAGE should be set to the name of the Docker image to run.
  exit 1
fi


## Function definitions

_base_docker_run() {
  allow_internet="$1"
  shift

  if [[ "$allow_internet" == "true" ]]; then
    network_option=""
  else
    network_option="--network none"
  fi

  docker run --rm \
    $network_option \
    -v "$WIT_WORKSPACE:/mnt/workspace" \
    --workdir /mnt/workspace \
    "$BLOCKCI_DOCKER_IMAGE" \
    "$@"
}

# Run docker run with common options
docker_run() {
  _base_docker_run true "$@"
}

# Run docker run with common options but with internet disabled
docker_run_no_internet() {
  _base_docker_run false "$@"
}


## Main script

docker_run_no_internet wake --init .
# Run preinstall step with internet so that we can install Python and Ruby
# dependencies
docker_run wake -v --no-tty preinstall Unit

# Tail output to avoid filling up Travis CI maximum stdout.
docker_run_no_internet wake -v --no-tty runSim pioDUT 2>&1 | (head -n 10000; tail -n 1000)
docker_run_no_internet wake -v --no-tty runSim pio16DUT 2>&1 | (head -n 10000; tail -n 1000)

docker_run_no_internet wake -v --no-tty makeOnboardingDocument pioDUT
docker_run_no_internet wake -v --no-tty makeOnboardingDocument pio16DUT
