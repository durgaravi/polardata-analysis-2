#! /usr/bin/env python
import os
import subprocess
import json
import time 

#file = '/home/nandan/git_repo/grobid/papers/shafer_ispass10.pdf'
#1E7CD574F6AE8DC8E59F306125E906B55CBBB8FB1F0D5F40E5D4CA0E4A55D2DE
#file = '/home/nandan/git_repo/grobid/papers/1E7CD574F6AE8DC8E59F306125E906B55CBBB8FB1F0D5F40E5D4CA0E4A55D2DE'
#7DFF9DAEA6AD75863E470FB7B9286B8F4A8A9AF4631A8D3A9EC334AD0CAADD74
#'/home/nandan/git_repo/grobid/papers/7CC887C82F22A5AE4EE870D918D7247F988C16615097E36B710750858B1B450F'
#a5-vavilapalli

#/home/nandan/Documents/CSCI599/Assignment2/pdf/docs/1E7CD574F6AE8DC8E59F306125E906B55CBBB8FB1F0D5F40E5D4CA0E4A55D2DE

#'/home/nandan/Documents/CSCI599/Assignment2/pdf/testdata/3C8E5CC7FD8D52BBE215858AF7CFA32E740488F7211A9F9C4987155DED0CBE19'

#'/home/nandan/git_repo/grobid/papers/7DFF9DAEA6AD75863E470FB7B9286B8F4A8A9AF4631A8D3A9EC334AD0CAADD74'

file_directory = '/home/nandan/Documents/CSCI599/Assignment2/pdf/docs/'

#file = '/home/nandan/git_repo/grobid/papers/a5-vavilapalli.pdf'

#file = '/home/nandan/Documents/CSCI599/Assignment2/pdf/testdata/0D6DD46C88F56F29539BFDE3090827D8EE06A28FD6BF77FA6560C0727B93822C'

#file = '/home/nandan/git_repo/grobid/papers/7CC887C82F22A5AE4EE870D918D7247F988C16615097E36B710750858B1B450F'

#file = '/home/nandan/Documents/CSCI599/Assignment2/pdf/testdata/3C8E5CC7FD8D52BBE215858AF7CFA32E740488F7211A9F9C4987155DED0CBE19'


file_scholar_mapping = [];

count = 0 

for file in os.listdir(file_directory):
        
 
       #count = count + 1 
       #if count == 50:
       #     break                    

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
              
            file_scholar_info ={ 'File : ' : file,
                               'Scholar Info : ' : scholar_info}
            print str(file_scholar_info['File : ']) + " : " + str(file_scholar_info['Scholar Info : '])
            
            if file_scholar_info != []:
                file_scholar_mapping.append(file_scholar_info)

json_output = json.dumps(file_scholar_mapping)

#print file_scholar_mapping
#print json_output               

#json_string = json_output.toString()

json_out_file = open("scholar_output.json","w")

json_out_file.write(json_output)

json_out_file.close()

#print file_scholar_mapping['File : '] + " : " + file_scholar_mapping['Scholar Info : ']                     



#./scholar.py -c 20 --author "Mahmoodilari'

#scholar.py -c 1 --author "albert einstein" --phrase "quantum theory"
#scholar_output = subprocess.Popen(['python','scholar.py','-c',1,'--author','\"albert einstein\"','--phrase','\"quantum theory\"'],stdout=subprocess.PIPE, stderr=subprocess.PIPE)


