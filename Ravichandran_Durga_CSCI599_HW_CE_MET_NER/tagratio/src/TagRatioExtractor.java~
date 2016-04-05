import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ner.corenlp.CoreNLPNERecogniser;
import org.apache.tika.parser.ner.opennlp.OpenNLPNameFinder;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import com.uwyn.jhighlight.tools.FileUtils;

public class TagRatioExtractor {

	public static void main(String[] args) throws IOException, TikaException, SAXException {
		File document = new File("/home/durga/similarity_data1/AEC2CCFBCABC767F3C9BF141AED8D8930858334322506F456E1C4E3CB0E2F7FA_html");
        //This directly parses the doc using tika parser and gets content and does ner
        /**
        Parser parser = new AutoDetectParser();
		BodyContentHandler handler = new BodyContentHandler(10000000);
        Metadata metadata = new Metadata();
        TikaInputStream tikafile = TikaInputStream.get(document);
		System.out.println("Parsing....");
		parser.parse(tikafile, handler, metadata, new ParseContext());
        String content = handler.toString();
        System.out.println(content);
        
        CoreNLPNERecogniser corenlp= new CoreNLPNERecogniser();
        Map m = corenlp.recognise(content);
        System.out.println(m);
        **/
		
		// This uses TagRatioParser implemented below
		ArrayList<Map> line_tr = getTagRatios(document);
        getNERMap(line_tr);
        System.out.println("Done :)");
	}
	
	// Named entity recognition: I/p: {"lineContent":<line>,"tagRatio":<tag ratio>} O/p: {Entity: [list of terms]}
	
	public static Map getNERMap(ArrayList<Map> line_tr)
	{
		String content = "";
		for(Map m: line_tr)
		{
			content += ((String) m.get("lineContent")).trim()+" ";
		}
		System.out.println(content);
		CoreNLPNERecogniser corenlp= new CoreNLPNERecogniser();
        Map ner = corenlp.recognise(content);
        System.out.println(ner);
        return ner;
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
	
	public static ArrayList<Map> getTagRatios(File f) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line,content="";
		while((line = br.readLine()) != null)
		{
			line = line.trim();
			if(line.length() != 0)
			{
				content += line+"\n";
			}
		}
		String contentlines[] = removeMiscTags(content).split("\n");
		ArrayList<Map> tagratios = new ArrayList<Map>();
		for(int i=0;i<contentlines.length;++i)
		{
			line = contentlines[i];
			if(line.length() != 0)
			{
				int tagcount = getTagCount(line);
				int charCount = getCharCount(line);
				Map m = new HashMap();
				String lineContent = getContent(line);
				m.put("lineContent",lineContent);
				m.put("tagRatio", (float)charCount/tagcount);
				tagratios.add(m);
				//System.out.println(i+" "+tagratios.get(tagratios.size()-1));
			}
		}
		// directly returning tagratios- check how to smooth values using gaussian kernel
		return tagratios;
	}
}
