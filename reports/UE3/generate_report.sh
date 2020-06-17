#!/usr/bin/env bash

if [[ "$1" == "pdf" ]]; then
  pandoc report3.md -o report3.pdf --template eisvogel --pdf-engine=xelatex -V fontsize=10.5pt

elif [[ "$1" == "html" ]]; then
  pandoc report3.md -o report3.html --template GitHub --metadata title="Computational Data Analytics, UE3" --self-contained
else
  echo "Unknown parameter: $1"
fi
