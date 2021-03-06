import json

ner = [
	{"id":"polar.usc.edu/b29LnhyzYY","locations":['Bangalore','Los Angeles'],"measurements":['15 cm','20 C']},
	{"id":"polar.usc.edu/abdf","locations":['Mumbai','New York'],"measurements":['151 km','20 meter']},
	{"id":"polar.usc.edu/kgjhg","locations":['Chennai','Houston'],"measurements":['445 ft','200 amps']},
	{"id":"polar.usc.edu/fkjghr","locations":['Hyderabad','Irvine'],"measurements":['15 cm','20 mm']}
]

publications = [
	{"id":"polar.usc.edu/b29LnhyzYY","citations":["aaa","bbb","ccc"]},
	{"id":"polar.usc.edu/abdf","citations":["aaaa","bbbb","cccc"]},
	{"id":"polar.usc.edu/kgjhg","citations":["1aaa","2bbb","3ccc"]},
	{"id":"polar.usc.edu/fkjghr","citations":["aaa1","bbb2","ccc3"]}
]

sweet_features = [
	{"id":"polar.usc.edu/b29LnhyzYY","concepts":['Phenomena','Climate change']},
	{"id":"polar.usc.edu/abdf","concepts":['Natural phenomena','Geology']},
	{"id":"polar.usc.edu/kgjhg","concepts":['Ocenaography','Earth Science']},
	{"id":"polar.usc.edu/fkjghr","concepts":['Phenomena','Substance']}
]

with open("ner.json","wb") as f:
	json.dump(ner,f)

with open("publications.json","wb") as f:
	json.dump(publications,f)

with open("sweet.json","wb") as f:
	json.dump(sweet_features,f)
