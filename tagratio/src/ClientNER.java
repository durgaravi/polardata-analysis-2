import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ner.NamedEntityParser;
import org.apache.tika.parser.ner.corenlp.CoreNLPNERecogniser;
import org.apache.tika.parser.ner.opennlp.OpenNLPNERecogniser;
import org.apache.tika.parser.ner.regex.RegexNERecogniser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.json.simple.JSONObject;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;

public class ClientNER 
{
	public static final String CONFIG_FILE = "tika-config.xml";
	public static Map location_measurements = new HashMap(); 
	
	public static void main(String args[]) throws IOException, TikaException, SAXException
	{
		if(args.length == 1)
		{
			File rootdir = new File(args[0]);
			if(rootdir.isDirectory())
			{
				ner(rootdir);
				writeVizJSON(location_measurements, args[0]);
			}
			else
			{
				System.out.println("Invalid directory argument");
			}
		}
		else
		{
			System.out.println("Please enter a directory path as your first argument");
		}
	}
	
	public static void writeVizJSON(Map location_measurements, String filepath) throws IOException
	{
		File file = new File(filepath+"/"+"MeasurementNER.json");
        file.createNewFile();
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("name","location-measurements");
        ArrayList<HashMap> location_details = new ArrayList<HashMap>();
        for(Object location:location_measurements.keySet())
        {
        	HashMap location_measures = new HashMap();
        	location_measures.put("name", location);
        	location_measures.put("children", new ArrayList<HashMap>());
        	for(String measure:(HashSet<String>)location_measurements.get(location))
        	{
        		HashMap h = new HashMap();
        		h.put("name", measure);
        		h.put("size",1);
        		((ArrayList<HashMap>) location_measures.get("children")).add(h);    															 
        	}
        	location_details.add(location_measures);
        }
        FileWriter jsonFile = new FileWriter(file);
        jsonObj.put("children", location_details);
        jsonFile.write(jsonObj.toJSONString());
        jsonFile.close();
	}
	public static void addResults(String location, String measurement)
	{
		if(location_measurements.containsKey(location))
		{
			((HashSet<String>) location_measurements.get(location)).add(measurement);
		}
		else
		{
			location_measurements.put(location, new HashSet<String>(Arrays.asList(measurement)));
		}
	}
	public static void ner(File file) throws FileNotFoundException, TikaException, IOException, SAXException
	{
		
		if(!file.isDirectory())
		{
			String[] results = getLocationMeasurement(file).split("\\t");
			if(results.length==1 && results[0]!="")
			{
				String [] location_measurement = results[0].split(":");
				String location = location_measurement[0];
				String measurement = location_measurement[1];
				addResults(location, measurement);
			}
			else if(results[0]!="")
			{
				for(String res:results)
				{
					System.out.println(res);
					String [] location_measurement = res.split(":");
					String location = location_measurement[0];
					String measurement = location_measurement[1];
					addResults(location, measurement);
				}
			}
		}
		else
		{
			System.out.println(file.getName());
			for(File f:file.listFiles())
				ner(f);
		}
	}
	
	// extracts content from string recursively. Handles tags within tags cases
		public static String getContent(String line)
		{
			if(line.indexOf("<") == -1)
			{
				return line;
			}
			else
			{
				Pattern pattern = Pattern.compile("<([A-Za-z][A-Za-z0-9]*)\\b[^>]*>(.*?)</\\1>");
		        Matcher  matcher = pattern.matcher(line);
		        if(matcher.find())
		        	return getContent(matcher.group(2).trim());
		        else
		        	return "";
			}
		}
		
		// removes script, style, and comments
		public static String removeMiscTags(String content)
		{
			String cleaned_content = content.replaceAll("(?s)<script.*?</script>", "");
			cleaned_content = cleaned_content.replaceAll("(?s)<!--.*?-->", "");
			cleaned_content = cleaned_content.replaceAll("(?s)<style.*?</style>", "");
			return cleaned_content;
		}
		
		public static int getTagCount(String line)
		{
			int count = 0;
			Pattern pattern = Pattern.compile("<");
	        Matcher  matcher = pattern.matcher(line);
	        while(matcher.find())
	        {
	        	++count;
	        }
	        return count!=0?count:1;
		}
		
		public static int getCharCount(String line)
		{
			return getContent(line).length();
		}
		
		public static String getTagRatios(String content) throws IOException
		{
			String contentlines[] = removeMiscTags(content).split("\n");
			String cleanedcontent = "";
			for(int i=0;i<contentlines.length;++i)
			{
				String line = contentlines[i];
				if(line.length() != 0)
				{
					int tagcount = getTagCount(line);
					int charCount = getCharCount(line);
					float tagratio = (float)charCount/tagcount;
					if(tagratio > 1)
					{
						cleanedcontent += getContent(line);
					}
				}
			}
			return cleanedcontent;
		}
	public static String getLocationMeasurement(File document) throws FileNotFoundException, TikaException, IOException, SAXException
	{
		String classNames = RegexNERecogniser.class.getName();
        System.setProperty(NamedEntityParser.SYS_PROP_NER_IMPL, classNames);
		TikaConfig config = new TikaConfig(TikaInputStream.get(new File(CONFIG_FILE)));
        Tika tika = new Tika(config);
        Parser parser = new AutoDetectParser();
		ContentHandler handler = new ToXMLContentHandler();
        Metadata metadata = new Metadata();
        InputStream stream = new FileInputStream(document);
        try
        {
        	parser.parse(stream, handler, metadata, new ParseContext());
        }
        catch(Exception e)
        {
        	System.out.println("Unable to parse file");
        }
        String content = handler.toString();
        content = getTagRatios(content);
		 
        Metadata md = new Metadata();
        tika.parse(new ByteArrayInputStream(content.getBytes(Charset.defaultCharset())), md);
        String measurement  = md.get("NER_MEASUREMENT");
        CoreNLPNERecogniser corenlp= new CoreNLPNERecogniser();
        Map nermap = corenlp.recognise(content);
        if(measurement!=null)
        {
        	HashSet locations = (HashSet) nermap.get("LOCATION");
        	String location_measurement = "";
	        if(locations != null)
	        {
	        	for(Object location:locations)
		        	location_measurement += location.toString().trim()+":"+measurement.trim()+"\t";
	       
	        	return location_measurement.trim();
	        }
	        return "Other:"+measurement;
        }
        else
        	return "";
	}
}