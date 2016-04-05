import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;


public class MetaDataScore {
	
	public static int findMatch(String inputMetaData, Pattern dcElement){
		
	    Matcher dcMatcher = dcElement.matcher(inputMetaData);
	    if(dcMatcher.find()){
	    		    	
	    	return 1;
	    }
	    else {
	    	return 0;
	    }
	}
	
	public int getScore(String metaData){
		 /*DC 
         * Title
         * Subject
         * Description
         * Type
         * Source
         * Relation
         * Coverage
         * Creator
         * Publisher
         * Rights
         * Contributor
         * Date
         * Format
         * Identifier
         * Language
         * Audience
         * Provenance
         * InstructionalMethod
         * RightsHolder
         * AccrualMethod
         * AccrualPeriodicity
         * AccrualPolicy
         */
        /*
         * Author
         */
        
        String regexTitle = "(.*)(TITILE)(.*)";
        String regexSubject = "(.*)(SUBJECT)(.*)";
        String regexDescription = "(.*)(DESCRIPTION)(.*)";
        String regexType = "(.*)(TYPE)(.*)";
        String regexSource = "(.*)(SOURCE)(.*)";
        String regexRelation = "(.*)(RELATION)(.*)";
        String regexCoverage = "(.*)(COVERAGE)(.*)";
        String regexCreator = "(.*)(CREATOR)(.*)";
        String regexPublisher = "(.*)(PUBLISHER)(.*)";
        String regexRights = "(.*)(RIGHTS)(.*)";
        String regexContributor = "(.*)(CONTRIBUTOR)(.*)";
        String regexDate = "(.*)(DATE)(.*)";
        String regexFormat = "(.*)(FORMAT)(.*)";
        String regexIdentifier = "(.*)(IDENTIFIER)(.*)";
        String regexAudience = "(.*)(AUDIENCE)(.*)";
        String regexProvenance = "(.*)(PROVENANCE)(.*)";
        String regexInstructionalMethod = "(.*)(INSTRUCTIONALMETHOD)(.*)";
        String regexAccrualMethod = "(.*)(ACCRUALMETHOD)(.*)";
        String regexAccrualPeriodicity = "(.*)(ACCURALPERIODICITY)(.*)";
        String regexAccrualPolicy = "(.*)(ACCRUALPOLICY)(.*)";
        String regexAuthor = "(.*)(AUTHOR)(.*)";
        
        Pattern pat[] = new Pattern[21];
        
        pat[0] = Pattern.compile(regexTitle);
        pat[1] = Pattern.compile(regexSubject);
         pat[2] = Pattern.compile(regexDescription);
         pat[3] = Pattern.compile(regexType);
         pat[4] = Pattern.compile(regexSource);
         pat[5] = Pattern.compile(regexRelation);
         pat[6] = Pattern.compile(regexCoverage);
         pat[7] = Pattern.compile(regexCreator);
         pat[8] = Pattern.compile(regexPublisher);
         pat[9] = Pattern.compile(regexRights);
         pat[10] = Pattern.compile(regexContributor);
         pat[11] = Pattern.compile(regexDate);
         pat[12] = Pattern.compile(regexFormat);
         pat[13] = Pattern.compile(regexIdentifier);
         pat[14] = Pattern.compile(regexIdentifier);
         pat[15] = Pattern.compile(regexProvenance);
         pat[16] = Pattern.compile(regexInstructionalMethod);
         pat[17] = Pattern.compile(regexAccrualMethod);
         pat[18] = Pattern.compile(regexAccrualPeriodicity);
         pat[19] = Pattern.compile(regexAccrualPolicy);
         pat[20] = Pattern.compile(regexAuthor);
         

         String upMetaData = metaData.toUpperCase();

         int metaDataScore = 0; 
         
         for(int i = 0 ; i < 21;i++){
           metaDataScore = metaDataScore + findMatch(upMetaData,pat[i]);   
       }

         return metaDataScore;
	}
	
	public String parseDoc(String FilePath, String FileName) throws IOException, SAXException, TikaException {
	    AutoDetectParser parser = new AutoDetectParser();
	    BodyContentHandler handler = new BodyContentHandler();
	    Metadata metadata = new Metadata();
	    
	    
	    try (InputStream stream = new FileInputStream(FilePath + FileName)) {
	        parser.parse(stream, handler, metadata);
	        return metadata.toString();
	    }
	}
	
	public static void main(String[] args){
		
			MetaDataScore m = new MetaDataScore();
			
			String metadata = null ;
		
			//String myDirectoryPath = "C:\\PolarDumpTrainData\\video\\mp4\\metascore\\";
			String myDirectoryPath = args[0]; 
		
			HashMap<String, String> metaDataMap = new HashMap<String, String>();
		
			File dir = new File(myDirectoryPath);
			
			File[] directoryListing = dir.listFiles();
			
			if (directoryListing != null) {
			  System.out.println("Processing in progress");
			  for (File child : directoryListing) {
				  System.out.print('.');
				  try{
				  metadata = m.parseDoc(myDirectoryPath, child.getName());
				  metaDataMap.put(child.getName(), metadata);
				  }
				  catch (Exception ex) {
					  System.out.println(ex.getMessage());
					  System.out.println(child.getName() + "can not be opened" );
					   continue;
					}

			  }
			}
			
			int noOfMetaDataEntry = metaDataMap.size();
			
			int metaDataScore[] = new int[noOfMetaDataEntry];
			 /* Display content using Iterator*/
		      Set set = metaDataMap.entrySet();
		      Iterator iterator = set.iterator();
		      int index = 0 ;
		      
		      while(iterator.hasNext()) {
		         Map.Entry mentry = (Map.Entry)iterator.next();
		         
		         
		         System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
		         System.out.println(mentry.getValue());
		         String data = mentry.getValue().toString();
		         metaDataScore[index] = m.getScore(data);
		         System.out.println("MetadataScore : " + metaDataScore[index]);
		         index = index + 1; 
		      }
		      
		      int temp[] = new int[metaDataScore.length];
		      
		      
		      System.arraycopy( metaDataScore, 0, temp, 0,  metaDataScore.length );
		      Arrays.sort(temp);
		      
		      float max = temp[temp.length-1];
		      
		      System.out.println("Max : " + max);
		      
		      float normMetaDataScore[] = new float[metaDataScore.length];
		      
		      for (int  i = 0 ; i < metaDataScore.length;i++){
		    	  System.out.println(metaDataScore[i]);
		    	  normMetaDataScore[i] = metaDataScore[i]/max; 
		    	  
		    	  System.out.println("metaDataScore: " + metaDataScore[i]);
		    	  System.out.println("Normalized Data Score : " + normMetaDataScore[i]);
		      }
		      float sum = 0;
		      
		      for(float i : normMetaDataScore){
		    	  sum += i; 
		      }
		      
		      float avgScore = sum / normMetaDataScore.length;
		      System.out.println(sum);
		      System.out.println("Norm Avg Score : " + avgScore);
	}  
}
