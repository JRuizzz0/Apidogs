package controllers;

import com.sun.net.httpserver.HttpExchange;
import service.ServiceDogs;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DogsController {

    private final HttpClient client = HttpClient.newHttpClient();

    public void handle(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();

        try {

            String apiUrl = "";


            if (path.equals("/dogs/list")) {
                try {
                    ServiceDogs service = new ServiceDogs();
                    service.JsonPrint();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else if (path.equals("/dogs/random")) {
                apiUrl = "https://dog.ceo/api/berfwereeds/image/random";
            }
            else {
                sendResponse(exchange, 404, "Endpoint dogs no válido");
                return;
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            sendResponse(exchange, 200, response.body());

        } catch (Exception e) {
            sendResponse(exchange, 500, "Error llamando a la API dogs");
        }
    }

    private void sendResponse(HttpExchange exchange, int status, String body) throws IOException {

        exchange.getResponseHeaders().add("Content-Type", "application/json");

        byte[] bytes = body.getBytes();

        exchange.sendResponseHeaders(status, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}