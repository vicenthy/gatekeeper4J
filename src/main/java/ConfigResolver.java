import org.json.JSONObject;
import spark.resource.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static spark.Spark.port;

public class ConfigResolver {

    private static String jsonConfig = "";
    private static JSONObject jsonObject;

    private ConfigResolver(){

    }



    public static String getConfig(String item){
        return jsonObject.get(item).toString();
    }

    public static void readConfig(String file){
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource(file).getInputStream()));
            jsonConfig = reader.lines().reduce(new String(), String::concat);
            jsonObject  = new JSONObject(jsonConfig);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
