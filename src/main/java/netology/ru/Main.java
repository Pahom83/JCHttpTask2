package netology.ru;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main {
    public static final String REMOTE_SERVICE_URI = "https://api.nasa.gov/planetary/apod?api_key=";
    public static final String key = "e9iJjMd7YysJ8yyU5TSk1Bmb4gw70x6siFsTSEIE";

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать перенаправлению в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet(REMOTE_SERVICE_URI + key);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
// отправка запроса
        CloseableHttpResponse response = httpClient.execute(request);
// вывод полученных заголовков
        ObjectMapper mapper = new ObjectMapper();
        Post post = mapper.readValue(
                response.getEntity().getContent(), new
                        TypeReference<>() {
                        });
        String nameHdFile = post.getHdurl();
        String nameSdFile = post.getUrl();
        System.out.println("Найдены графические файлы:");
        System.out.println(nameSdFile);
        System.out.println(nameHdFile);
        System.out.println("Сохраняем...");
        File fileHd = new File("./src/" + new File(post.getHdurl()).getName());
        File fileSd = new File("./src/" + new File(post.getUrl()).getName());
        createImgOnDisk("jpg", fileSd, post.getUrl());
        createImgOnDisk("jpg", fileHd, post.getHdurl());
    }

    private static void createImgOnDisk(String format, File file, String url) {
        try {
            BufferedImage img = ImageIO.read(new URL(url));
            ImageIO.write(img, format, file);
            if (file.exists()) {
                System.out.println("Файл " + file.getName() + " сохранен по адресу \"" + file.getPath() + "\".");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}