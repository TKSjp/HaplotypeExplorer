# Haplotype Explorer

HaplotypeExplorer is an interactive haplotype network viewer for spatiotemporal dissection of multimodal epidemic spectra.

## How to start

- Click "↓Code" button and download files.
- To see example results, open `Example`.
- To visualize your data in Haplotype Explorer, run `python createHTML.py` after preparing `input.fasta` and installing dependencies.

## Contents of `Example` 

- Example networks, JSON files, and a movie are available in `Example`. Please look after download.
  - html files: Example outputs of Haplotype Explorer. Click the "Raw" button to download. Please open it in a browser after saving as a HTML file.
  - json files: Users can share and resume the network by input JSON into Haplotype Explorer. Please see `manual.pdf`.
  - movie: The movie depicting the eruption of the SARS-CoV-2 in the world from 2019 DEC 31 to 2020 MAR 21. 

## How to visualize your data

The way to visualize users data is also described in Figure 2. Please see also `manual.pdf`.

- Install dependencies with conda: `conda install -c bioconda seqkit mafft cd-hit snp-sites trimal`
- Put multi-FASTA as `input.fasta` and run `python createHTML.py`
- `result.html` will automatically open after calcuration.

## Limitation

There are upper limits for sequence number, SNV length, and genetic distance for inputs. They affect calculation time and result. 

- The TCS requires several hours for a few thousands sequences/SNVs (in standard iMac). Alignment also takes long time for a large input.
- It also split the network in case inputs have too long genetic distance.
- Using over thousands of sequences make it difficult to visualize the result clearly due to multiple edges.
- Also, it does not consider sequence gaps, recombination, and reassortment.

Therefore, we recommend to start with a small set and gradually scale up until you can obtain better result.

## Citation

```
Tetsuro Kawano-Sugaya1*, Koji Yatsu1, Tsuyoshi Sekizuka1, Kentaro Itokawa1, Masanori Hashino1, Rina Tanaka1, Makoto Kuroda1 (2020)
Haplotype Explorer: infection cluster visualization tool toward spatiotemporal dissection of the COVID-19 pandemic.
bioRxiv. doi: https://www.biorxiv.org/content/10.1101/2020.07.19.179101v1

1 Pathogen Genomics Center, National Institute of Infectious Diseases, Toyama 1-23-1, Shinjuku, Tokyo, Japan
* Corresponding author: Tetsuro Kawano-Sugaya (tks_jp@seikai.org)
```

