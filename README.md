# Haplotype Explorer

HaplotypeExplorer is an interactive haplotype network viewer for spatiotemporal dissection of multimodal epidemic spectra.

## How to start

- Click "↓Code" button and download files.
- To see example results, open `Example`.
- To visualize your data in Haplotype Explorer, run `python createHTML.py` after preparing `input.fasta` and installing dependencies.

## Contents of `Example` 

- Example networks, JSON files, and a movie are available in `Example`.
  - html files: Example outputs of Haplotype Explorer. Click the "Raw" button to download. Please open it in a browser after saving as a HTML file.
  - json files: Users can share and resume the network by input JSON into Haplotype Explorer. Please see `manual.pdf`.
  - movie: The movie depicting the eruption of the SARS-CoV-2 in the world from 2019 DEC 31 to 2020 MAR 21. 

## How to visualize your data

The way to visualize users data is also described in Figure 2. Please see also `manual.pdf`.

- Install dependencies with conda: `conda install -c bioconda seqkit mafft cd-hit snp-sites trimal`
- Put multi-FASTA as `input.fasta` and run `python createHTML.py`
- Open `result.html` then you can explore your network.

## Citation

```
Haplotype Explorer: infection cluster visualization tool toward spatiotemporal dissection of the COVID-19 pandemic
https://www.biorxiv.org/content/10.1101/2020.07.19.179101v1

Tetsuro Kawano-Sugaya1*, Koji Yatsu1, Tsuyoshi Sekizuka1, Kentaro Itokawa1, Masanori Hashino1, Rina Tanaka1, Makoto Kuroda1

1 Pathogen Genomics Center, National Institute of Infectious Diseases, Toyama 1-23-1, Shinjuku, Tokyo, Japan

* Corresponding author: Tetsuro Kawano-Sugaya (tks_jp@seikai.org)
```

