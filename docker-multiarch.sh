#!/bin/bash
#/ Usage docker-multiarch.sh <NAME> <ARCH>
#/
#/ Setup docker buildx and build image for any platform.

if [ "$#" -ne 2 ]; then
    echo "Usage docker-multiarch.sh <NAME> <ARCH>"
    exit 1
fi

BUILDX_NAME="scadalts-multi-arch-builder"

if (! docker buildx inspect $BUILDX_NAME) > /dev/null ; then docker buildx create --use --name $BUILDX_NAME; fi
docker buildx build --output type=docker,name=$1  --platform=$2 .