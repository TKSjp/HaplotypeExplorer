# Haplotype Explorer

HaplotypeExplorer is an interactive haplotype network viewer for spatiotemporal dissection of multimodal epidemic spectra.

## Example 

- Example networks, JSON files, and a movie are available in `Example`.
  - html files: Example outputs of Haplotype Explorer.
  - json files: Users can share and resume the network by input JSON into Haplotype Explorer.
  - movie: The movie depicting the eruption of SARS-CoV-2 in the world from 2019 DEC 31 to 2020 MAR 21. 

## How to visualize your data

The way to visualize users data is also described in Figure 2. 

- Install dependencies with conda: `conda install -c bioconda seqkit mafft cd-hit snp-sites`
- Download TCS and put it in 'Source' directory. (e.g. `./Downloads/HX/Source/TCS/`)
- Put multi-FASTA in `./Downloads/HX/` as `input0.fasta`.
- Run `python Step1.py` and `python Step2.py`
- Run TCS and save data in `./Downloads/HX/` as `TCS.gml`.
- Run `python Step3.py`
- Open `Result_in-house.html` then you can explore your network.
- Please see also `manual.pdf`.

## Citation

```
Haplotype Explorer: infection cluster visualization tool toward spatiotemporal dissection of the COVID-19 pandemic
https://www.biorxiv.org/content/10.1101/2020.07.19.179101v1

Tetsuro Kawano-Sugaya1*, Koji Yatsu1, Tsuyoshi Sekizuka1, Kentaro Itokawa1, Masanori Hashino1, Rina Tanaka1, Makoto Kuroda1

1 Pathogen Genomics Center, National Institute of Infectious Diseases, Toyama 1-23-1, Shinjuku, Tokyo, Japan

* Corresponding author: Tetsuro Kawano-Sugaya (tks_jp@seikai.org)
```
