import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import spark.Request;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;


public class GateKeeper4jApplication {

    private static final String USER_AGENT = "Mozilla/5.0";

    public static void main(String[] args) {
        startConfig();
        port(getPort());
        get("/authenticate/:code", (request, response) -> {
            return authenticate(request);
        });
        System.out.println("Server Starter on port: " + getPort());
    }

    private static String authenticate(Request request) {
        String code = request.params(":code");
        String token = "";
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost postRequest = getPostRequest();
        postRequest.setHeader("User-Agent", USER_AGENT);
        List<NameValuePair> parameters = obterParametros(code);
        return executarRequestEObterToken(token, httpClient, postRequest, parameters);
    }

    private static HttpPost getPostRequest() {
        return new HttpPost(ConfigResolver.getConfig("oauth_host") + ConfigResolver.getConfig("oauth_path"));
    }

    private static String executarRequestEObterToken(String token, HttpClient httpClient, HttpPost postRequest, List<NameValuePair> parameters) {
        try {

            postRequest.setEntity(new UrlEncodedFormEntity(parameters));
            HttpResponse response = httpClient.execute(postRequest);
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
                token = rd.lines().reduce(new String(), String::concat);
                token = paramJson(token);

        }catch (Exception e){
            e.printStackTrace();
        }
        return token;
    }

    private static List<NameValuePair> obterParametros(String code) {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("client_id", ConfigResolver.getConfig("oauth_client_id")));
        parameters.add(new BasicNameValuePair("client_secret", ConfigResolver.getConfig("oauth_client_secret")));
        parameters.add(new BasicNameValuePair("code", code));
        return parameters;
    }

    public static String paramJson(String paramIn) {
        paramIn = paramIn.replaceAll("=", "\":\"");
        paramIn = paramIn.replaceAll("&", "\",\"");
        paramIn = paramIn.replaceAll("\\+", " ");
        paramIn = paramIn.replaceAll("%", " ");
        return "{\"" + paramIn + "\"}";
    }

    private static Integer getPort() {
        return new Integer(ConfigResolver.getConfig("port"));
    }

    private static void startConfig() {
        ConfigResolver.readConfig("/config.json");
    }
}
