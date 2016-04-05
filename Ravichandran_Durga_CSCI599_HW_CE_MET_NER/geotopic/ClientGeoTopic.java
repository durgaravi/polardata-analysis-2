import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.BodyContentHandler;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.xml.sax.SAXException;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.geo.topic.GeoParser;
import org.apache.tika.parser.geo.topic.GeoParserConfig;

public class ClientGeoTopic 
{
	//public static final String CONFIG_FILE = "tika-config.xml";
	//public static Map location_measurements = new HashMap(); 
	public static JSONArray solrGeoTopic = new JSONArray();
	public static void main(String args[]) throws IOException, TikaException, SAXException
	{
		if(args.length == 1)
		{
			FileWriter fw = new FileWriter(args[0]+"/geotopic.csv"); 
		    fw.write("city,lat,lon\n");
		    fw.close();
			File rootdir = new File(args[0]);
			if(rootdir.isDirectory())
			{
				JSONObject doiMap = getDOIMap(args[0]);
				geotopicParsing(rootdir,args[0],doiMap);
				FileWriter jsonFile = new FileWriter(new File(args[0]+"/geotopic.json"));
		        jsonFile.write(solrGeoTopic.toString());
		        jsonFile.close();
		        System.out.println("Done :)");
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
	public static JSONObject getDOIMap(String filepath)
	{
		try {
			JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(filepath+"/DOI.json"));
 
            return (JSONObject) obj;

        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}
	public static void geotopicParsing(File file, String filepath,JSONObject doiMap) throws FileNotFoundException, TikaException, IOException, SAXException
	{
		
		if(!file.isDirectory())
		{
			String result = getLocationDetails(file,doiMap);
			System.out.println(file.getName());
			if(result!="")
			{
				FileWriter fw = new FileWriter(filepath+"/geotopic.csv",true); 
			    fw.write(result+"\n");
			    fw.close();
			}
		}
		else
		{
			System.out.println(file.getName());
			for(File f:file.listFiles())
				geotopicParsing(f,filepath,doiMap);
		}
	}
	
	public static String getLocationDetails(File document,JSONObject doiMap) throws FileNotFoundException, TikaException, IOException, SAXException
	{
		Parser parser = new AutoDetectParser();
		BodyContentHandler handler = new BodyContentHandler(10000000);
        Metadata metadata = new Metadata();
        TikaInputStream tikafile = TikaInputStream.get(document);
		parser.parse(tikafile, handler, metadata, new ParseContext());
        String content = handler.toString();
       
        Parser geoparser = new GeoParser();
        ParseContext context = new ParseContext();
        metadata = new Metadata();
		GeoParserConfig config = new GeoParserConfig();
		context.set(GeoParserConfig.class, config);
		if (!((GeoParser) geoparser).isAvailable())
			System.out.println("Not available");;
		
		InputStream contentstream = new ByteArrayInputStream(content.getBytes());
        geoparser.parse(contentstream, new BodyContentHandler(), metadata, context);
        //System.out.println(metadata.get("Geographic_NAME")+"\t"+metadata.get("Geographic_LATITUDE")+"\t"+metadata.get("Geographic_LONGITUDE"));
        
        String location = metadata.get("Geographic_NAME");
        String latitude = metadata.get("Geographic_LATITUDE");
        String longitude = metadata.get("Geographic_LONGITUDE");
        
        
        if(location!=null && latitude!= null && longitude!=null)
        {
        	JSONObject jsonObj = new JSONObject();
            jsonObj.put("id",doiMap.get(document.getName()));
            jsonObj.put("location_name", location);
            jsonObj.put("latitude", latitude);
            jsonObj.put("longitude", longitude);
            solrGeoTopic.put(jsonObj);
        	return location+","+latitude+","+longitude;
        }
        return "";
        
	}
}