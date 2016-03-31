import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.BodyContentHandler;
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
				geotopicParsing(rootdir,args[0]);
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
	
	public static void geotopicParsing(File file, String filepath) throws FileNotFoundException, TikaException, IOException, SAXException
	{
		
		if(!file.isDirectory())
		{
			String result = getLocationDetails(file);
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
				geotopicParsing(f,filepath);
		}
	}
	
	public static String getLocationDetails(File document) throws FileNotFoundException, TikaException, IOException, SAXException
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
        	return location+","+latitude+","+longitude;
        }
        return "";
        
	}
}