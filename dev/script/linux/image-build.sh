#!/bin/bash

IMG_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
IMG_NAME=golenski/fibonacci-msg-relay

if [ -n "$(docker images -q $IMG_NAME:$IMG_VERSION 2> /dev/null)" ]; then
  echo "Cannot build, image exists: $IMG_NAME:$IMG_VERSION"
  exit 1
fi

docker build -t $IMG_NAME:$IMG_VERSION .