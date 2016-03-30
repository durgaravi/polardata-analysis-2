import os
from setuptools import setup

def read(fname):
    return open(os.path.join(os.path.dirname(__file__), fname)).read()

setup(
    name = "solrload",
    version = "0.1",
    author = "Team 9, CSCI 599",
    description = ("A package to load data into solr"),
    keywords = "solr",
    packages=['src'],
	package_data = {"":["resources/data/*","resources/*"]},
	include_package_data = True,
    long_description=read('README.md'),
    install_requires=['pandas','solr'],
)
