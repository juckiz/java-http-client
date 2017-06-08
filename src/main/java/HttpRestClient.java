import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;

public class HttpRestClient {

    public static void main(String[] args) throws Exception {

        final String BASE_URL = "http://localhost:3000";

        HttpRestClient http = new HttpRestClient();

        // Scheduled retrieval of resources
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            try {
                System.out.println("Request all resources : " + new java.util.Date());
                String resourcesJSON = http.sendGet(BASE_URL.concat("/resources"));

                if (!resourcesJSON.isEmpty()) {
                    // Get individual resources and generate object from them
                    JSONArray arr = new JSONArray(resourcesJSON);
                    for (int i = 0; i < arr.length(); i++) {
                        System.out.println("Request single resource");
                        String entityJSON = http.sendGet(BASE_URL.concat(arr.getJSONObject(i).getString("ref")));
                        http.generateObject(entityJSON);
                    }
                } else {
                    System.out.println("Resources list empty");
                }
            }
            catch (InterruptedException e) {
                System.err.println("Thread interrupted : " + e);
            }
            catch (Exception e) {
                System.err.println("No valid response : " + e);
            }
        };

        // Schedule "Request all resources" to run every 5 seconds
        // scheduleWithFixedDelay because we cannot predict duration of scheduled task
        executor.scheduleWithFixedDelay(task, 0, 5, TimeUnit.SECONDS);

        // Run thread for 30 seconds
        Thread.sleep(30000);

        executor.shutdown();

        while (!executor.isTerminated()) {
            //wait for all tasks to finish
            System.out.println("terminating threads....");
        }
        System.out.println("Finished all threads");
    }

    /**
     * Generate object based on ResourceEntity POJO using Jackson
     * @param entityJSON Single entity JSON object as string
     */
    public void generateObject(String entityJSON) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Convert JSON string to Object
            ResourceEntity entity = mapper.readValue(entityJSON, ResourceEntity.class);

            //Pretty print object
            //String prettyEntity = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity);
            //System.out.println(prettyEntity);

            // "Play" sound from generated object
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
        String jsonResponse = "";

        try {
            String url = requestUrl;

            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = connection.getResponseCode();
            System.out.println("GET : " + url);
            System.out.println("Response code: " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Print response
            System.out.println(response.toString());

            jsonResponse = response.toString();
        } catch (Exception e) {
            System.out.println("URL call failed");
            e.printStackTrace();
        }
        return jsonResponse;
    }

}
