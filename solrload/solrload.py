import solr
import json
from pandas import DataFrame
import os
import sys
import time

def get_merged_data():
	
	with open("resources/data/ner.json") as f:
		ner = DataFrame(json.load(f))
	with open("resources/data/publications.json") as f:
		publications = DataFrame(json.load(f))
	with open("resources/data/sweet.json") as f:
		sweet_features = DataFrame(json.load(f))

	return (ner.merge(publications,on=["id"])).merge(sweet_features,on=["id"]).T.to_dict().values()

def solr_restart(solrroot):
	os.system(solrroot+"/bin/solr restart")
	time.sleep(10)

def copy_collection_core(solrroot):
	os.system("cp -r resources/polarcollection "+solrroot+"/example/solr")
	print("Copied polarcollection...")
	solr_restart(solrroot)
	
if __name__ == "__main__":
	if len(sys.argv) != 2:
		print("Please enter your solr root directory argument")
	else:
		copy_collection_core(sys.argv[1])
		data = get_merged_data()
		s = solr.Solr('http://localhost:8983/solr/polarcollection')
		[s.add(row,commit='true') for row in data]
		res = solr.SearchHandler(s, '/select?q=*:*')
		print res().__dict__
