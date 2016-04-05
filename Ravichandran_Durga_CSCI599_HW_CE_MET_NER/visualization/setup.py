import os
from setuptools import setup

def read(fname):
    return open(os.path.join(os.path.dirname(__file__), fname)).read()

setup(
    name = "visualization",
    version = "0.1",
    author = "Team 9, CSCI 599",
    description = ("A package to visulaize results using Python server"),
    keywords = "D3 visualization",
    packages=['src'],
	package_data = {"":["resources/public/*"]},
	include_package_data = True,
    long_description=read('README.md'),
    install_requires=['bottle'],
	entry_points = {'console_scripts': ['visualization = src.mainserver:main_fn']},
)
