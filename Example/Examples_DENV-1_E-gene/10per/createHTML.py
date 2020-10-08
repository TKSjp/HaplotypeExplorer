import re
import pprint
import subprocess
import itertools
import pandas


class inputPrep:

	def makeTCSresultDir(self):

		subprocess.run(["mkdir", "TCSresult"])

	def moveToParentDir(self):

		print("move to parent dir [./] - start.")
		subprocess.run(["cd", ".."])
		print("move to parent dir [./] - done.")

	def skip(self):
		
		print("Do you have input.SNV.fasta already? [Y/N]")
		select = input()
		if select == 'Y' or select == 'N':
			print("i see")
		else:
			print("input [Y/N]")
		return select

	def seqkit(self):

		print("curation of sequences using seqkit - start.")

		# replace space in headers and sequences into underscore
		with open('input1.fasta', 'w') as input1:
			state = "s/ /_/g"
			subprocess.run(["sed", state, "input.fasta"], stdout = input1)

		# run seqkit and remove DNA sequences with ambiguous codes
		with open('input2.fasta', 'w') as input2:
			state = 'N|n|_|R|r|M|m|W|w|S|s|Y|y|K|k|H|h|B|b|D|d|V|v'
			subprocess.run(["seqkit", "grep", "-vsirp", state, "-w", "0", "input1.fasta"], stdout = input2)

		print("curation of sequences using seqkit - done.")

	def mafft(self):

		print("alignment of sequences using mafft - start.")

		print("If make alignment alongwith reference sequence, put reference in ./Source/ and input its name here. If not, press Return with no input.")

		refpath = input()
		if refpath != '':
			reference = "./Source/" + refpath
			with open('input.mafft.fasta', 'w') as input3:
				subprocess.run(["mafft", "--auto", "--thread", "-1", "--keeplength", "--addfragments", "input2.fasta", reference], stdout = input3)
		else: 
			with open('input.mafft.fasta', 'w') as input3:
				subprocess.run(["mafft", "--thread", "-1" ,"input2.fasta"], stdout = input3)

		print("alignment of sequences using mafft - done.")

	def trimal(self):

		print("trimming of sequences using trimal. all sites with gaps will be removed. - start.")
		subprocess.run(["trimal", "-in", "input.mafft.fasta", "-out", "input.trimal.fasta", "-nogaps"])
		print("trimming of sequences using trimal - end.")

	def snpsites(self):

		print("SNV sites extraction using snp-sites - start.")
		subprocess.run(["snp-sites", "-o", "input.SNV.fasta", "input.trimal.fasta"])
		print("SNV sites extraction using snp-sites - done.")

	def cdhit(self):

		print("sequence clustering using ch-hit-est - start.")
		subprocess.run(["cd-hit-est", "-i", "input.SNV.fasta", "-o", "input.cdhit.txt", "-c", "1", "-d", "200"])
		print("sequence clustering using ch-hit-est - done.")

	def parseCdhit(self):
		print("metadata construction - start.")		
		with open('input.metadata.txt', 'w') as metadata:
			subprocess.run(["python3", "./Source/parseCdhit.py"], stdout = metadata)
		print("metadata construction - done.")

	def makePhy(self):

		print("conversion of fasta into phylip - start.")
		with open('input.SNV.phylip', 'w') as tcsinput:
			subprocess.run(["python3", "./Source/makePhy.py"], stdout = tcsinput)
		subprocess.run(["cp", "input.SNV.phylip", "./TCSresult/"])
		print("conversion of fasta into phylip - done.")

	# def deleteFiles(self):
	# 	subprocess.run(["rm", "input1.fasta"])
	# 	subprocess.run(["rm", "input2.fasta"])
	# 	subprocess.run(["rm", "input2.fasta.seqkit.fai"])
	# 	subprocess.run(["rm", "input2.fasta"])
	# 	subprocess.run(["rm", "input3.fasta"])
	# 	subprocess.run(["rm", "input5.bedtools.fasta"])
	# 	subprocess.run(["rm", "input5.blastn.txt"])
	# 	subprocess.run(["rm", "input5.cut.txt"])
	# 	subprocess.run(["rm", "input5.fasta"])
	# 	subprocess.run(["rm", "input5.fasta.fai"])
	# 	subprocess.run(["rm", "input5.sed.fasta"])
	# 	subprocess.run(["rm", "input5.seq.fasta"])
	# 	subprocess.run(["rm", "input5.SNV.cdhit.txt"])
	# 	subprocess.run(["rm", "input5.SNV.cdhit.txt.clstr"])
	# 	subprocess.run(["rm", "input5.SNV.fasta"])
	# 	subprocess.run(["rm", "input5.stats.txt"])

	def whichProtocol(self):
		# print("select method from below.")		
		# print("1. TCS - Haplotype network analysis")
		# print("2. FastTree () - Fast phylogenetic analysis")		
		# print("3. IQ-TREE () - Accurate phylogenetic analysis")
		# choice = int(input())
		choice = 1 #default is TCS
		if choice == 1 or choice == 2 or choice == 3:
			return choice
		else:
			print("select 1~3.")

class useTCS():

	def __init__(self, procedures):
		self.procedures = procedures

	def runTCS(self):

		print("haplotype network calculation using TCS - start.")		
		subprocess.run(["java", "-jar", "./Source/TCS/TCS1.21.jar", "./TCSresult/"])
		print("haplotype network calculation using TCS - done.")		

	def parseGraphML(self):

		with open("./TCSresult/input.SNV.phylip.graph") as fopen:

			lines = fopen.read().split('\n')

			# node extraction
			nodes = []
			edges = []		
			for index,currentline in enumerate(lines):
				if re.match(r'      id',currentline) != None:
					if re.match(r'      label \"(\d| ).*\"',lines[index+1]) != None:

						nodeid = lines[index].strip()[3:]

						if re.match(r'.*label \"\d',lines[index + 1]) != None:
							nodelabel = lines[index + 1].strip()[7:-1].strip()
						else:
							nodelabel = "NA"

						nodename = lines[index-6].strip() + lines[index-5].strip()
						if re.match(r'Frequency \"frequency=1', nodename) != None:
							nodename = nodename.strip("Frequency \"frequency=1")
						if nodename[-1] == '\"':
							nodename = nodename[:-1]

						if nodelabel == "NA":
							blank = 1
						else:
							blank = 0

						nodes.append({ 
							"name" : nodeid,
							"TCS_ID" : nodelabel,
							# "name" : nodename,
							"blank": blank
						} )

				# edge extraction
				if re.match(r'   edge',currentline) != None:
					if re.match(r'      source ',lines[index+6]) != None:
						source = lines[index+6][13:].strip()
					if re.match(r'      target ',lines[index+7]) != None:
						target = lines[index+7][13:].strip()			
					edges.append({ 
						"source" : source,
						"target" : target,
						"SNV" : 1
	    			})
		
			# sort node data same to TCS result
			nodes_sorted = sorted(nodes, key=lambda x:x['TCS_ID'])	

		return nodes_sorted, edges

	def parseMetadata(self, nodes_sorted):

		with open("./input.metadata.txt") as fopen2:

			lines = fopen2.read().split('\n')
			
			lineno = len(lines)
			datano = len(nodes_sorted)
			definedNodeNo = 0

			for index, line in enumerate(lines):
				if re.match(r'.*location',line) != None:
					metadata = {
						"location": lines[index][14:-2],
						"collection_date": lines[index+1][21:-2],
						"identical_name": lines[index+2][20:-2]
					}
					nodes_sorted[definedNodeNo].update(metadata)
					definedNodeNo += 1

			for index in range(definedNodeNo,datano):
				metadata = {
					"location": "Undefined",
					"collection_date": "Undefined",
					"identical_name": "Undefined"
				}
				nodes_sorted[index].update(metadata)

		return nodes_sorted

	def createHTML(self, nodes_sorted, edges):

		jsonline = '{\"elements\" : {\n\t\"nodes\":' + str(nodes_sorted) + ',\t\"edges\" : ' + str(edges) + '}}'
		subprocess.run(["cp", "./Source/empty.html", "result.html"])

		with open("result.html") as inhouse:
			lines = inhouse.readlines()
			index = 1
			for line in lines:
				if (re.match(r'var jsondata =',line) != None):
					lines.insert(index, jsonline)
					with open("result.html", mode='w') as inhouseWrite:
						inhouseWrite.writelines(lines)
					break
				index += 1

class tree2network:

	def __init__(self, procedure, choice):

		self.duplicatenodesfile = "phylotree.newick.edge-list.txt"
		self.choice = choice
		self.data = self.makeList(choice)
		self.duplicatenodes = self.data[0]
		self.networks = self.data[1]
		self.connectDuplicateNode(self.duplicatenodes)
		self.outputNetworks(self.networks)

	# https://dlrecord.hatenablog.com/entry/2020/07/30/230234
	def atoi(self, text):
	    return int(text) if text.isdigit() else text

	def natural_keys(self, text):
	    return [ self.atoi(c) for c in re.split(r'(\d+)', text) ]

	def makeList(self, choice):

		duplicatenodes = []
		networks = []

		if choice == 2: # in case use fasttree
			threshold = 0.00001
		elif choice == 3: # in case use IQ-TREE
			threshold = 0.0001
		else:
			print("Exception has occurred.")

		with open(self.duplicatenodesfile) as file:

			for index, eachrow in enumerate(file):

				eachrow = eachrow.split(' ')
				branchlength = float(eachrow[2])

				if branchlength > 1.0:
					print("sequences are too divergent. exit.")
					break

				elif branchlength == 1.0 or branchlength < threshold:
					#identical nodes
					eachrow = [eachrow[0], eachrow[1]]
					duplicatenodes.append(eachrow)

				elif threshold < branchlength and branchlength < 1.0:
					#network file
					snv = float(eachrow[2])*1000
					eachrow = [eachrow[0], snv, eachrow[1]]
					# if re.search('^\d', eachrow[0]):
					# 	eachrow[0] = "Node" + eachrow[0]
					# if re.search('^\d', eachrow[2]):
					# 	eachrow[2] = "Node" + eachrow[2]
					networks.append(eachrow)

		# print(duplicatenodes)
		return duplicatenodes, networks

	def connectDuplicateNode(self, duplicatenodes):

		# print(duplicatenodes)
		connectednode = []
		mergecells = []

		for i in duplicatenodes:
			mergecells.append([i[0],i[1]])

		for i, line in enumerate(mergecells):
			for j, cell in enumerate(line):
				for k, row in enumerate(duplicatenodes):
					if k + 1 <= len(duplicatenodes):
						if mergecells[i][j] in duplicatenodes[k]:
							mergecells[i].extend(duplicatenodes[k])
							duplicatenodes[k] = ['','']

		for a in duplicatenodes:
			print(*a, sep=',')

		for i, row in enumerate(mergecells):
			if len(row) > 2:
				connectednode.append(sorted(list(set(row)), key = self.natural_keys))

		with open("dupnode.txt", mode='w') as f:
			for a in connectednode:
				f.writelines(','.join(a))
				f.write('\n')

	def extractEmptyNodes(self, duplicatenodes):

		emptynode = []

		for index, eachrow in enumerate(duplicatenodes):

			left = duplicatenodes[index][0]
			right = duplicatenodes[index][1]

			if left.find('Node') == 0 and right.find('Node') == 0:

				emptynode.append([left, right])

		# print(emptynode)
		return emptynode

	def connectEmptyNode(self, emptynode):

		connectednode = []

		leftcells = [x[0] for x in emptynode]
		rightcells = [x[1] for x in emptynode]
		mergecells = []

		for i in emptynode:
			mergecells.append(i)

		for i, line in enumerate(mergecells):
			for j, cell in enumerate(line):
				for k, row in enumerate(emptynode):
					if k + 1 < len(emptynode):
						if mergecells[i][j] in emptynode[k]:
							mergecells[i].extend(emptynode[k])
							emptynode[k] = ['','']
		
		for i, row in enumerate(mergecells):
			if len(row) > 2:
				connectednode.append(sorted(list(set(row)), key = self.natural_keys))

			for a in connectednode:
				print(*a, sep=',')

	def outputNetworks(self, networks):

		with open("network.txt", mode='w') as f:
			for a in networks:
				a = str(a[0]) + "	" + str(a[1]) + "	" + str(a[2])
				f.writelines(''.join(a))
				f.write('\n')

if __name__ == '__main__':

	print("------------- Haplotype Explorer -------------")
	print("Usage: put \"input.fasta\" here and run createHTML.py")
	print("Starting HTML generation.")

	procedures = inputPrep()
	select = procedures.skip()

	if select == 'Y':
		
		choice = procedures.whichProtocol()

	else:

		choice = procedures.whichProtocol()

		if choice == 1 or choice == 2 or choice == 3:

			procedures.seqkit()
			procedures.mafft()
			procedures.trimal()
			procedures.snpsites()

		else:
			print("Exception was occurred. exit.")

	if choice == 1: # use TCS

		print("use TCS")

		procedures.cdhit()
		procedures.parseCdhit()
		procedures.makeTCSresultDir()
		procedures.makePhy()
		
		tcsrun = useTCS(procedures)
		tcsrun.runTCS()
		nodes_sorted = tcsrun.parseGraphML()[0]
		nodes_sorted = tcsrun.parseMetadata(nodes_sorted)
		edges = tcsrun.parseGraphML()[1]
		tcsrun.createHTML(nodes_sorted, edges)
		subprocess.run(["open", "result.html"])

	elif choice == 2:

		print("use FastTree")
		print("Have you prepared phylotree.newick.edge-list.txt? [Y/N]")
		select = input()

		if select == 'Y':

			fasttreerun = tree2network(procedures, choice)

		else:

			print("i see")
			print("phylogenetic analysis using FastTree - start.")
			with open('input.fasttree', 'w') as output:
				subprocess.run(["fasttree", "-gtr", "-nt", "input.SNV.fasta"], stdout = output)
			print("phylogenetic analysis using FastTree - done.")
			#parse
			fasttreerun = tree2network(procedures, choice)

	elif choice == 3:

		print("use IQ-TREE")
		print("Have you prepared phylotree.newick.edge-list.txt? [Y/N]")
		select = input()

		if select == 'Y':

			iqtreerun = tree2network(procedures, choice)

		else:

			print("phylogenetic analysis using IQ-TREE - start.")
			subprocess.run(["./iqtree2", "-nt", "AUTO", "-s", "input.SNV.fasta"])
			print("phylogenetic analysis using IQ-TREE - done.")
			#parse
			iqtreerun = tree2network(procedures, choice)

	else:
		print("Exception was occurred. exit.")

	print("HTML generation was completed. Opening \"result.html\"......")
	print("Thank you for using Haplotype Explorer.")
