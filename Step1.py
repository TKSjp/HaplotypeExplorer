import re
import subprocess

class inputPrep:

	def __init__(self):

		self.inputFileName = "input0.fasta"
		self.referencePath = "./Source/MN908947.3_trim.fasta"

	def seqkit(self):

		# replace space in header and seq to underscore
		with open('input1.fasta', 'w') as input1:
			state = "s/ /_/g"
			subprocess.run(["sed", state, self.inputFileName], stdout = input1)

		# run seqkit
		with open('input2.fasta', 'w') as input2:
			state = 'N|n|_|R|r|M|m|W|w|S|s|Y|y|K|k|H|h|B|b|D|d|V|v'
			subprocess.run(["seqkit", "grep","-vsirp", state, "-w", "0", "input1.fasta"], stdout = input2)

		# delete fasta with uncomplete meta-data (delete records with only YYYY or YYYY-MM)
		with open('input3.fasta', 'w') as input3:
			state = '/([0-9][0-9][0-9][0-9]$)|([0-9][0-9][0-9][0-9]-[0-9][0-9]$)/{d;d;}'
			subprocess.run(["sed", "-E", state, "input2.fasta"], stdout = input3)

	def mafft(self):
		with open('input4.fasta', 'w') as input4:
			subprocess.run(["mafft", "--auto", "--thread", "-1", "--keeplength", "--addfragments", "input3.fasta", self.referencePath], stdout = input4)

	def snpsites(self):
		subprocess.run(["snp-sites", "-o", "input4.SNV.fasta", "input4.fasta"])

	def cdhit(self):
		subprocess.run(["cd-hit-est", "-i", "input4.SNV.fasta", "-o", "input4.SNV.cdhit.txt", "-c", "1", "-d", "200"])

	def parseCdhit(self):
		with open('input4.metadata.txt', 'w') as metadata:
			subprocess.run(["python3", "./Source/parseCdhit.py"], stdout = metadata)

	def makePhy(self):
		with open('input4.SNV.phy', 'w') as tcsinput:
			subprocess.run(["python3", "./Source/makePhy.py"], stdout = tcsinput)

	def deleteFiles(self):
		subprocess.run(["rm", "input1.fasta"])
		subprocess.run(["rm", "input2.fasta"])
		subprocess.run(["rm", "input2.fasta.seqkit.fai"])
		subprocess.run(["rm", "input2.fasta"])
		subprocess.run(["rm", "input3.fasta"])
		subprocess.run(["rm", "input4.bedtools.fasta"])
		subprocess.run(["rm", "input4.blastn.txt"])
		subprocess.run(["rm", "input4.cut.txt"])
		subprocess.run(["rm", "input4.fasta"])
		subprocess.run(["rm", "input4.fasta.fai"])
		subprocess.run(["rm", "input4.sed.fasta"])
		subprocess.run(["rm", "input4.seq.fasta"])
		subprocess.run(["rm", "input4.SNV.cdhit.txt"])
		subprocess.run(["rm", "input4.SNV.cdhit.txt.clstr"])
		subprocess.run(["rm", "input4.SNV.fasta"])
		subprocess.run(["rm", "input4.stats.txt"])

if __name__ == '__main__':

	procedures = inputPrep()

	procedures.seqkit()
	procedures.mafft()
	procedures.snpsites()
	procedures.cdhit()
	procedures.parseCdhit()
	procedures.makePhy()
	# procedures.deleteFiles()

