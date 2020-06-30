inputfilename="input4.SNV.cdhit.txt"

if __name__ == '__main__':

	phylipId = []
	phylipSeq = []

	with open(inputfilename) as fasta:
		lines = fasta.read().split('\n')

		for index, line in enumerate(lines):

			if index % 2 == 0:

				# exit if EOF
				if line == "": 
					numberOfSequence = str(len(phylipId))
					lengthOfSequence = str(len(phylipSeq[0]))
					print(numberOfSequence + " " + lengthOfSequence)
					break

				# make list if not EOF
				else:
					phylipId.append(str(int(index/2)).zfill(9))
					phylipSeq.append(lines[index + 1])

	for index in range(len(phylipId)):
		print(phylipId[index] + " " + phylipSeq[index])



