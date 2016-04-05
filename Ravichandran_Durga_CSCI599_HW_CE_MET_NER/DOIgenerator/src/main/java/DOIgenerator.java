import org.apache.solr.client.solrj.SolrServerException;
import org.json.simple.JSONObject;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class DOIgenerator {

    private HashMap<String, String> keyMap;
    private HashMap<String, String> valueMap;
    private JSONObject jsonObject = new JSONObject();
    private String domain; // Use this attribute to generate urls for a custom
    private char myChars[]; // This array is used for character to number
    private Random myRand; // Random object used to generate random integers
    private int length;
    public String dir = "";

    DOIgenerator(String domain, int length){
        keyMap = new HashMap<String, String>();
        valueMap = new HashMap<String, String>();
        this.domain = domain;
        this.length = length;
        myChars = new char[62];
        myRand = new Random();
        for (int i = 0; i < 62; i++) {
            int j;
            if (i < 10) {
                j = i + 48;
            } else if (i > 9 && i <= 35) {
                j = i + 55;
            } else {
                j = i + 61;
            }
            myChars[i] = (char) j;
        }
    }

    public String genKey(){
        String key = "";
        Boolean flag = true;
        while(flag) {
            for (int i = 0; i < length; i++) {
                key += myChars[myRand.nextInt(62)];
            }
            if(!keyMap.containsKey(key)){
                flag = false;
            }else{
                key = "";
            }
        }
        return key;
    }

    public void writeJSON(File file)throws Exception{
        for(Map.Entry<String, String> entry: valueMap.entrySet()){
            jsonObject.put(entry.getKey(), domain+"/"+entry.getValue());
            
        }
        FileWriter jsonFile = new FileWriter(file);
        jsonFile.write(jsonObject.toJSONString());
        jsonFile.close();
    }

    public void callGenerator(File file) throws IOException, SolrServerException{
        if(file.isDirectory()){
            File[] fileList = file.listFiles();
            for(File temp : fileList){
                callGenerator(temp);
            }
        }else{
            String fileName = file.getName();
            System.out.println(file.getAbsolutePath());
            if(!valueMap.containsKey(fileName)) {
                String key = genKey();
                valueMap.put(fileName, key);
                keyMap.put(key, fileName);
                SolrPost.indexFilesSolrCell(file.getAbsolutePath(), "polar.usc.edu/"+key);
            }
        }
    }

    public static void main(String[] args) throws Exception{
        if(args.length < 2 ){
            System.out.println("Usage: DOIgenerator.java <data directory> <solr directory>");
        }
        else {
        	Runtime rt = Runtime.getRuntime();
        	Process pr = rt.exec(args[1]+"/bin/solr create -c polarcollection2");
        	Thread.sleep(10000);
        	pr = rt.exec(args[1]+"/bin/solr restart");
        	Thread.sleep(15000);
            DOIgenerator doi = new DOIgenerator("polar.usc.edu", 10);
            File file = new File(args[0]+"/"+"DOI.json");
            file.createNewFile();
            doi.callGenerator(new File(args[0]));
            doi.writeJSON(file);
        }
    }

}
