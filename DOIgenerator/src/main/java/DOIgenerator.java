import org.json.simple.JSONObject;
import java.io.*;
import java.util.ArrayList;



public class DOIgenerator {

    public static final String prefix = "polar.usc.edu/";
    public static JSONObject jsonObject;

    public static void DOI_generator(File file){
        if(file.isDirectory()){
            File[] fileList = file.listFiles();
            for(File temp : fileList){
                DOI_generator(temp);
            }
        }else{
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                ArrayList<String> str = new ArrayList<String>();
                String string;
                while ((string = br.readLine()) != null) {
                    str.add(string);
                }
                String[] keyString = str.get(str.size() - 3).split(":");
                jsonObject.put(file.getName(), prefix+keyString[1].substring(2,keyString[1].length()-2)+file.getName().substring(0,file.getName().indexOf('.')));
            }
            catch(FileNotFoundException e){
                e.printStackTrace();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception{
        if(args.length < 1 ){
            System.out.println("Usage: DOIgenerator.java <directory>");
        }else {
            FileWriter jsonFile = new FileWriter(args[0]+"DOI_generation.json");
            jsonObject = new JSONObject();
            DOI_generator(new File(args[0]));
            jsonFile.write(jsonObject.toJSONString());
        }
    }

}
