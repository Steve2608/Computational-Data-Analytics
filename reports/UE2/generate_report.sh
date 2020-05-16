#!/usr/bin/env bash

if [[ "$1" == "pdf" ]]; then
  pandoc report2.md -o report2.pdf --template eisvogel --pdf-engine=xelatex -V fontsize=10pt -V geometry:landscape
elif [[ "$1" == "html" ]]; then
  pandoc report2.md -o report2.html --template GitHub.html5 --metadata title="Computational Data Analytics, UE2" --self-contained
else
  echo "Unknown parameter: $1"
fi
