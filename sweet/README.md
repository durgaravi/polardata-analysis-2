Steps to run the sweet parser
1) Copy the folder structure to tika project
2) Compile the tika project
3) The model for sweet ontology ner-model.sweet.gz is present under org.apache.tika.parser.ner.sweet
3) The steps to run the SweetOntologyParser is similar to the steps for Stanform Core NLP in https://wiki.apache.org/tika/TikaAndNER
  
   To run the SweetOntologyParser run the following command-
    java  -Dner.corenlp.model=<path_to_file/ner-model.sweet.gz> \
       -Dner.impl.class=org.apache.tika.parser.ner.sweet.SweetOntologyParser \
       -classpath $TIKA_APP:$CORE_NLP_JAR org.apache.tika.cli.TikaCLI \
       --config=tika-config.xml -m http://www.hawking.org.uk
       
