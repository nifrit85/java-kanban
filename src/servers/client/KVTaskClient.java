package servers.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final String url;
    private final String apiToken;

    private final HttpClient client;

    public KVTaskClient(String url) throws IOException, InterruptedException {
        this.url = url;
        client = HttpClient.newHttpClient();
        apiToken = register();
    }

    private String register() throws IOException, InterruptedException {
        String body = null;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/register"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            body = response.body();
        }
        return body;
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public String load(String key) throws IOException, InterruptedException {
        String body = null;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/load/" + key + "?API_TOKEN=" + apiToken))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            body = response.body();
        }
        return body;


    }

}
