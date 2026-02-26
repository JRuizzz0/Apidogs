package service;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServiceDogs {

    /**
     * isntancia Gson
     * isntancia cliente
     * Url principal de la Api
     */
    private final Gson gson = new Gson();
    private final HttpClient client = HttpClient.newHttpClient();
    private final String ALL_BREEDS_URL = "https://dog.ceo/api/breeds/list/all";

    /**
     * Metodo principal que recoge la url y la respuesta, esta encapsulado
     *
     * @param url atributo de la url
     * @return devuelve la respuesta
     * @throws IOException          error Input Output
     * @throws InterruptedException error por si no carga
     */
    private JsonObject fetchApiData(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) { // error 200
            throw new IOException("HTTP error de la API Dog CEO: " + response.statusCode());
        }

        return gson.fromJson(response.body(), JsonObject.class);
    }

    /**
     * Metodo para las razas (todas)
     *
     * @param exchange the exchange containing the request from the
     *                 *                 client and used to send the response
     * @throws IOException          error Input Output
     * @throws InterruptedException error la no cargar
     */
    public void JsonPrintrazas(HttpExchange exchange) throws IOException, InterruptedException {
        JsonObject jsonRaiz = fetchApiData(ALL_BREEDS_URL);
        JsonObject message = jsonRaiz.getAsJsonObject("message");

        JsonArray resultado = new JsonArray();
        for (String raza : message.keySet()) {
            JsonArray subrazas = message.getAsJsonArray(raza);

            JsonObject obj = new JsonObject();
            obj.addProperty("nombre", raza);
            obj.add("subrazas", subrazas);
            resultado.add(obj);
        }

        sendResponse(exchange, 200, "razas");
    }

    /**
     * Metodo para las razas sin subraza
     *
     * @param exchange the exchange containing the request from the
     *                 *                 client and used to send the response
     * @throws IOException          error Input Output
     * @throws InterruptedException error la no cargar
     */
    public void JsonPrintNoSubrazas(HttpExchange exchange) throws IOException, InterruptedException {
        JsonObject jsonRaiz = fetchApiData(ALL_BREEDS_URL);
        JsonObject message = jsonRaiz.getAsJsonObject("message");

        JsonArray resultado = new JsonArray();
        for (String raza : message.keySet()) {
            JsonArray subrazas = message.getAsJsonArray(raza);
            if (subrazas.isEmpty()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("nombre", raza);
                resultado.add(obj);
            }
        }

        sendResponse(exchange, 200, "razas");
    }

    /**
     * Metodo para las razas con subraza
     *
     * @param exchange the exchange containing the request from the
     *                 *                 client and used to send the response
     * @throws IOException          error Input Output
     * @throws InterruptedException error la no cargar
     */
    public void JsonPrintSubrazas(HttpExchange exchange) throws IOException, InterruptedException {
        JsonObject jsonRaiz = fetchApiData(ALL_BREEDS_URL);
        JsonObject message = jsonRaiz.getAsJsonObject("message");

        JsonArray resultado = new JsonArray();
        for (String raza : message.keySet()) {
            JsonArray subrazas = message.getAsJsonArray(raza);
            if (!subrazas.isEmpty()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("nombre", raza);
                obj.add("subrazas", subrazas);
                resultado.add(obj);
            }
        }

        sendResponse(exchange, 200, "razas");
    }

    public void JsonPrintImagenes(HttpExchange exchange) throws IOException, InterruptedException {
        String finalPath = exchange.getRequestURI().getPath().split("/")[0];
        JsonObject jsonRaiz = fetchApiData("https://dog.ceo/api/breeds/image/random/" + finalPath);
        JsonArray imageUrls = jsonRaiz.getAsJsonArray("message");

        JsonArray resultado = new JsonArray();
        for (JsonElement element : imageUrls) {
            JsonObject obj = new JsonObject();
            obj.addProperty("imagen", element.getAsString());
            resultado.add(obj);
        }


    }

    public void sendResponse(HttpExchange exchange, int status, String body) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] bytes = body.getBytes();
        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}