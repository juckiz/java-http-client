import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

public class HttpRestClient extends TimerTask {
    public void run() {

    }

    public static void main(String[] args) throws Exception {

        final String BASE_URL = "http://localhost:3000";

        HttpRestClient http = new HttpRestClient();


        // Get list of resources
        System.out.println("Request all resources");
        //String url = BASE_URL.concat("/resources");
        String resourcesJSON = http.sendGet(BASE_URL.concat("/resources"));

        /*
        * Parse values from JSONArray
        */
        //http.jsonToArray(resourcesJSON);

        // Get individual resources and generate object from them
        JSONArray arr = new JSONArray(resourcesJSON);
        //Map<String, String> list = new HashMap<String, String>();
        for (int i = 0; i < arr.length(); i++) {
            //list.put(arr.getJSONObject(i).getString("type"), arr.getJSONObject(i).getString("ref"));
            System.out.println("Request single resource");
            String entityJSON = http.sendGet(BASE_URL.concat(arr.getJSONObject(i).getString("ref")));
            http.generateObject(entityJSON);
        }

    }

    /**
     * Generate object based on ResourceEntity POJO using Jackson
     * @param entityJSON Single entity JSON object as string
     */
    public void generateObject(String entityJSON) {
       // JSONObject jsonObject = new JSONObject(entityJSON);
        System.out.println("GENERATE OBJECT: ");

        ObjectMapper mapper = new ObjectMapper();

        try {
            // Convert JSON string to Object
            ResourceEntity entity = mapper.readValue(entityJSON, ResourceEntity.class);
            //System.out.println(entity);

            //Pretty print
            String prettyEntity = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity);
            System.out.println(prettyEntity);
/*
            System.out.println(entity.getType());
            System.out.println(entity.getName());
            System.out.println(entity.getSound());*/
            entity.Sound();

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send GET request to specified resources url
     * @param requestUrl
     * @return
     * @throws Exception
     */
    private String sendGet(String requestUrl) throws Exception {
        final String USER_AGENT = "Mozilla/5.0";

        String url = requestUrl;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("GET : " + url);
        System.out.println("Response Code: " + responseCode);

        BufferedReader in = new BufferedReader(
                                new InputStreamReader(con.getInputStream())
                            );
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Print response
        System.out.println(response.toString());

        return response.toString();
    }

    /**
     * Originally to parse all resources json array to Java Map
     * @param jsonString JSON array
     */
    public void jsonToArray(String jsonString) {
        JSONArray arr = new JSONArray(jsonString);
        /*
        List<String> list = new ArrayList<String>();
        for(int i = 0; i < arr.length(); i++){
            list.add(arr.getJSONObject(i).getString("type"));
        }
        */
        Map<String, String> list = new HashMap<String, String>();
        for (int i = 0; i < arr.length(); i++) {
            list.put(arr.getJSONObject(i).getString("type"), arr.getJSONObject(i).getString("ref"));
        }

        System.out.println(list);
    }

}
