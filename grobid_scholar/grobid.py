#! /usr/bin/env python
import sys
import argparse
import os
import subprocess
import json
import time 

def extract(directory):
    #file_directory = '/home/nandan/Documents/CSCI599/Assignment2/pdf/docs/'
    file_directory = directory

    file_scholar_mapping = [];

    count = 0 
    
    tei_file = open("tei_extract.txt","a+")
    

    for file in os.listdir(file_directory):
            
            fileName = file_directory + file
            args = ['java','-classpath','/home/nandan/git_repo/grobidparser-resources/tika-app-1.12.jar','org.apache.tika.cli.TikaCLI','--config=/home/nandan/git_repo/grobidparser-resources/tika-config.xml','-J',fileName]
            
            print fileName
            
            
            proc = subprocess.Popen(args,stdout=subprocess.PIPE, stderr=subprocess.PIPE)

            (out, err) = proc.communicate()

            print len(out)
            
            
            if len(out) > 1:
                time.sleep(5)

                json_out = json.loads(out)

                data = json_out[0]
                
                print 'metadata extracted ' 
                
                scholar_info =[]  
                
                tei_data = data['grobid:header_TEIXMLSource']
                
                tei_data = tei_data.encode('ascii','ignore')
                
                tei_file.write(tei_data)
                tei_file.write("\n")

                if 'Author' in data.keys():
                    
                    author = data['Author']
                    print author
                    author = ''.join([i for i in author if i is not i.isdigit()])    
                    
                    
                    proc1 = subprocess.Popen(['/home/nandan/Documents/CSCI599/Assignment2/scholar.py','-c','20','--author','\"'+author+'\"'],stdout=subprocess.PIPE, stderr=subprocess.PIPE)       
                    (authorout, err) = proc1.communicate()
                
                    if len(authorout)> 1:
                        details_list = authorout.split('\n')
                        for detail in details_list:
                            if 'Title' in detail:
                                scholar_info.append(detail)
                            elif 'URL' in detail:
                                scholar_info.append(detail)
                            elif 'Cluster ID' in detail:
                                scholar_info.append(detail)
                            elif 'Citation List' in detail:
                                scholar_info.append(detail)    
                    
                elif 'grobid:header_Authors' in data.keys():       
                    authors = data['grobid:header_Authors']
                    authors = ''.join([i for i in authors if i is not i.isdigit()])
                    authors = authors.split()
                    
                    print authors

                    for author in authors:
                        proc1 = subprocess.Popen(['/home/nandan/Documents/CSCI599/Assignment2/scholar.py','-c','20','--author','\"'+author+'\"'],stdout=subprocess.PIPE, stderr=subprocess.PIPE)       
                        (authorout, err) = proc1.communicate()
                        
                        if len(authorout) > 1:
                            print authorout
                            details_list = authorout.split('\n')
                            

                            for detail in details_list:
                                
                                if 'Title' in detail:
                                    scholar_info.append(detail)                                    
                                elif 'URL' in detail:
                                    scholar_info.append(detail)
                                elif 'Cluster ID' in detail:
                                    scholar_info.append(detail)
                                elif 'Citation List' in detail:
                                    scholar_info.append(detail) 
                file_scholar_info = {}
                
                if scholar_info != []: 
                    file_scholar_info['id'] = file             
                    for info in scholar_info:
                        print 'info' + str(info)
                        
                        if 'Title' in info:
                            file_scholar_info['Title'] = info.split("Title",1)[1]
                        elif 'URL' in info:
                            file_scholar_info['URL'] = info.split("URL",1)[1]
                        elif 'Cluster ID' in info:
                            file_scholar_info['Cluster ID'] = info.split("Cluster ID",1)[1]
                        elif 'Citation List' in detail:
                            file_scholar_info['Citation List'] = info.split("Citation List",1)[1]
                                    
                    #print str(file_scholar_info['File : ']) + " : " + str(file_scholar_info['Scholar Info : '])
                    

                    file_scholar_mapping.append(file_scholar_info)
    
    tei_file.close()
    
    json_output = json.dumps(file_scholar_mapping)


    json_out_file = open("scholar_output.json","w")
    json_out_file.write(json_output)

    json_out_file.close()

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('InputDirectory', help='Enter PDF directory for which citation needs to queried thorugh Google Scholar API')
    args = parser.parse_args()
    
    extract(args.InputDirectory)
