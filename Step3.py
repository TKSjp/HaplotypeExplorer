inputFileName="TCS.gml" 
cdhit="input4.metadata.txt" 

import re
import pprint
import subprocess

if __name__ == '__main__':

	with open(inputFileName) as fopen:

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

	with open(cdhit) as fopen2:

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
				"location": "undefined",
				"collection_date": "undefined",
				"identical_name": "undefined"
			}
			nodes_sorted[index].update(metadata)

	# open empty.html and write in in-house.html

	jsonline = '{\"elements\" : {\n\t\"nodes\":' + str(nodes_sorted) + ',\t\"edges\" : ' + str(edges) + '}}'

	subprocess.run(["cp", "./Source/empty.html", "Result_in-house.html"])
	with open("Result_in-house.html") as inhouse:
		lines = inhouse.readlines()
		index = 1
		for line in lines:
			if (re.match(r'var jsondata =',line) != None):
				lines.insert(index, jsonline)
				with open("Result_in-house.html", mode='w') as inhouseWrite:
					inhouseWrite.writelines(lines)
				break
			index += 1

		# lines = inhouse.readlines()
		# index = 1
		# for line in lines:
		# 	if (re.match(r'var jsondata =',line) != None):
		# 		# print(index)
		# 		with open("Result_in-house.html", mode='w') as inhouseWrite:
		# 			inhouseWrite.writelines(lines)
		# 		break
		# 	index += 1

	# subprocess.run(["rm", "input5.metadata.txt"])
	# subprocess.run(["rm", "input5.SNV.phy"])
	# subprocess.run(["rm", "TCS.gml"])

	# print JSON
	# print('{\"elements\" : {\n\t\"nodes\":')
	# pprint.pprint(nodes_sorted)
	# print(',\t\"edges\" : ')
	# pprint.pprint(edges)
	# print('}}')