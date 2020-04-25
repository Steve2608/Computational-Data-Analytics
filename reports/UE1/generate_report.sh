if [[ "$1" == "pdf" ]]; then
  pandoc report1.md -o report1.pdf --template eisvogel --pdf-engine=xelatex -V fontsize=11pt -V geometry:landscape
elif [[ "$1" == "html" ]]; then
  pandoc report1.md -o report1.html --template GitHub.html5 --metadata title="Computational Data Analytics, UE1" --self-contained
else
  echo "Unknown parameter: $1"
fi
