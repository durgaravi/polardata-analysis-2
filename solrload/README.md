This `solrload` distribution is a python program to create polardata collection in Solr and load documents from different json-metadata files

To run the entire setup on your local machine:

### Files present

1. solrload.py, setup.py
2. resources/data: JSON data files
3. resources/polarcollection

### Prerequisites:

1. Python 2.x or 3.x

### How to use:

1. `cd` to `solrload`
2. To install required python packages, run : `$ setup.py install`
3. To load data into Solr, run `$ python solrload.py <solr root directory>`
