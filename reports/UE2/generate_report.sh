#!/usr/bin/env bash

if [[ "$1" == "pdf" ]]; then
  pandoc report2.md -o report2.pdf --template eisvogel --pdf-engine=xelatex -V fontsize=10.5pt

elif [[ "$1" == "html" ]]; then
  pandoc report2.md -o report2.html --template GitHub --metadata title="Computational Data Analytics, UE2" --self-contained
else
  echo "Unknown parameter: $1"
fi
