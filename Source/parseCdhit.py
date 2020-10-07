inputfilename="input.cdhit.txt.clstr" # line 1 should be reference ID

import re

class Block:

	def __init__(self):
		self.block = []
		self.location = []
		self.collectionDate = []
		self.gisaid = []

	def __del__(self):
		print('\n')

	def parseRecord(self, record):
		print("parseRecord")

	def addBlockData(self, location, collectionDate, gisaid):
		self.block['location'].append(location)

	def addLineToBlock(self, line):
		line = re.split('[\t\s\|]',line)
		self.block.append(line)

	def printBlock(self):
		print(" \"location\": \"" + (','.join(self.location) + "\",").replace('-',''))
		print(" \"collection_date\": \"" + (','.join(self.collectionDate) + "\",").replace('-',''))
		print(" \"identical_name\": \"" + (','.join(self.gisaid) + "\",").replace('-',''))

	def parseLine(self, line):
		line = re.split('[\t\s\|]',line)
		return line

class Cluster:

	def __init__(self):
		self.printedtimes = 0		
		self.clusterID = ""

	def inputClusterID(self, clusterID):
		self.clusterID = int(clusterID)

	def parseLine(self, line):
		line = re.split('[\t\s\|]',line)
		return line

	def printClusterID(self):
		if self.printedtimes == 0:
			print("Cluster ID: " + str(self.clusterID))
			self.printedtimes = 1

with open(inputfilename) as fopen:

	lines = fopen.readlines()
	lines.append('>Cluster 0\n0 EOF')

	for line in lines:

		# cluster = undefined, block = undefined, and at header
		if ('cluster' in locals()) == False and ('block' in locals()) == False and line[0] == '>':
			block = Block()
			cluster = Cluster()
			line = cluster.parseLine(line)
			cluster.inputClusterID(line[1])
			continue

		# cluster = defined, block = defined, not header
		if ('cluster' in locals()) == True and ('block' in locals()) == True and line[0] != '>':
			line = block.parseLine(line)
			block.location.append(line[3])
			block.collectionDate.append(line[4][:-3])
			block.gisaid.append(line[2])
			continue

		# cluster = defined, block = defined, at next header
		if ('cluster' in locals()) == True and ('block' in locals()) == True and line[0] == '>':
			print(cluster.clusterID)
			block.printBlock()
			del cluster
			del block
			block = Block()
			cluster = Cluster()
			line = cluster.parseLine(line)
			cluster.inputClusterID(line[1])
			continue

