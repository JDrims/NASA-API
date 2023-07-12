package http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static final String API_KEY = "TpN03fFyS0w97ie735Pn9mgXdMAX4gpgUdHUihOG";
    public static final String URL = "https://api.nasa.gov/planetary/apod?api_key=" + API_KEY;
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();
        HttpGet request = new HttpGet(URL);
        CloseableHttpResponse response = httpClient.execute(request);
        NasaApi nasaApi = mapper.readValue(response.getEntity().getContent(), NasaApi.class);
        CloseableHttpResponse responseImage = httpClient.execute(new HttpGet(nasaApi.getUrl()));
        byte[] imageBytes = responseImage.getEntity().getContent().readAllBytes();
        try (FileOutputStream fileOutputStream = new FileOutputStream(getNameFileFromURL(nasaApi.getUrl()))) {
            fileOutputStream.write(imageBytes);
        } catch (Exception e) {
            System.out.println(e);
        }
        response.close();
        httpClient.close();
    }

    public static String getNameFileFromURL(String url) {
        int i = url.lastIndexOf("/");
        return url.substring(i + 1);
    }
}