import solr
import json
from pandas import DataFrame
import os
import sys
import time

def get_merged_data(solrdata):
	
	with open("ner.json") as f:
		ner = DataFrame(json.load(f))
	with open("publications.json") as f:
		publications = DataFrame(json.load(f))
	with open("geotopic.json") as f:
		sweet_features = DataFrame(json.load(f))

	return (ner.merge(publications)).merge(sweet_features).T.to_dict().values()

def solr_restart(solrroot):
	os.system(solrroot+"/bin/solr restart")
	time.sleep(10)

def copy_collection_core(solrroot):
	os.system("cp -r resources/polarcollection "+solrroot+"/server/solr")
	print("Copied polarcollection...")
	solr_restart(solrroot)
	
if __name__ == "__main__":
	if len(sys.argv) != 2:
		print("Please enter your solr root directory argument")
	else:
		#copy_collection_core(sys.argv[1])
		#data = get_merged_data()
		s = solr.Solr('http://localhost:8983/solr/polarcollection2')
		#[s.add(row,commit='true') for row in data]
		res = solr.SearchHandler(s, '/select?q=*:*')
		data = get_merged_data(res().__dict__['results'])
		print data[0]
		[s.add(row,commit='true') for row in data]
		print "Done :)"
