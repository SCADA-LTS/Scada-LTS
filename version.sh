#!/bin/bash
if [[ ($# < 6) || ($# > 7) ]]; then
	echo "Illegal number of parameters" >&2
	exit 2
fi
versionfile="./webapp-resources/version.properties"
milestone=$1
build=$2
branch=$3
commit=$4
actor=$5
if [[ $# -eq 6 ]]; then
	pullRequestBranch=$6
fi
echo "******************************************************"
echo "**** Scada-LTS System Settings Build Information  ****"
echo "******************************************************"
echo "Current build version: $milestone.$build"
echo "Build from GitHub branch: $branch"
echo "Build from GitHub commit: $commit"
echo "Initiated by: $actor"
echo "GitHub PullRequest branch: $pullRequestBranch"

pwd
echo $versionfile

sed -i "/slts.version.number=/c\slts.version.number=$milestone" $versionfile
sed -i "/slts.version.build=/c\slts.version.build=$build" $versionfile
sed -i "/slts.version.commit=/c\slts.version.commit=$commit" $versionfile
sed -i "/slts.version.branch=/c\slts.version.branch=$branch" $versionfile
