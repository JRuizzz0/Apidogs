package service;


import Clases.Raza;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;


public class ServiceDogs {
    private final Gson gson = new Gson();
    private final HttpClient client = HttpClient.newHttpClient();
    private final String url = "https://dog.ceo/api/breeds/list/all";





    public List<String> getBreedNames() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("HTTP error: " + response.statusCode());
        }

        Raza breedData = gson.fromJson(response.body(), Raza.class);

        if ("success".equals(breedData.status)) {
            return List.copyOf(breedData.message.keySet());
        } else {
            throw new IOException("API error: " + breedData.status);
        }
    }

    public void JsonPrintrazas(HttpExchange exchange) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("HTTP error: " + response.statusCode());
        }

        JsonObject jsonRaiz = gson.fromJson(response.body(), JsonObject.class);
        JsonObject message = jsonRaiz.getAsJsonObject("message");

        JsonArray resultado = new JsonArray();
        for (String raza : message.keySet()) {
            JsonArray subrazas = message.getAsJsonArray(raza);
            if (subrazas.size() >= 0) {
                JsonObject obj = new JsonObject();
                obj.addProperty("nombre", raza);
                obj.addProperty("subrazas", String.valueOf(subrazas));
                resultado.add(obj);

            }

        }

        JsonObject principal = new JsonObject();
        principal.add("razas", resultado);
        System.out.println(principal);

        sendResponse(exchange, 200, principal.toString());
    }

    public void JsonPrintNoSubrazas(HttpExchange exchange) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("HTTP error: " + response.statusCode());
        }

        JsonObject jsonRaiz = gson.fromJson(response.body(), JsonObject.class);
        JsonObject message = jsonRaiz.getAsJsonObject("message");

        JsonArray resultado = new JsonArray();
        for (String raza : message.keySet()) {
            JsonArray subrazas = message.getAsJsonArray(raza);
            if ( subrazas.isEmpty()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("nombre", raza);
                resultado.add(obj);
            }

        }
        System.out.println(resultado);

        JsonObject principal = new JsonObject();
        principal.add("razas", resultado);
        System.out.println(principal);
        sendResponse(exchange, 200, principal.toString());
    }

    public void JsonPrintSubrazas(HttpExchange exchange) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("HTTP error: " + response.statusCode());
        }

        JsonObject jsonRaiz = gson.fromJson(response.body(), JsonObject.class);
        JsonObject message = jsonRaiz.getAsJsonObject("message");

        JsonArray resultado = new JsonArray();
        for (String raza : message.keySet()) {
            JsonArray subrazas = message.getAsJsonArray(raza);
            if ( subrazas.size() > 0 ) {
                JsonObject obj = new JsonObject();
                obj.addProperty("nombre", raza);
                obj.addProperty("subrazas", String.valueOf(subrazas));
                resultado.add(obj);
            }

        }

        JsonObject principal = new JsonObject();
        principal.add("razas", resultado);
        System.out.println(principal);

        sendResponse(exchange, 200, principal.toString());
    }

    public void JsonPrintImagenes(HttpExchange exchange) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dog.ceo/api/breeds/image/random/5"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("HTTP error: " + response.statusCode());
        }


        JsonObject jsonRaiz = gson.fromJson(response.body(), JsonObject.class);
        JsonArray imageUrls = jsonRaiz.getAsJsonArray("message");

        JsonArray resultado = new JsonArray();
        for (JsonElement element : imageUrls) {
            JsonObject obj = new JsonObject();
            obj.addProperty("imagen", element.getAsString());
            resultado.add(obj);
        }

        JsonObject principal = new JsonObject();
        principal.add("Imagenes", resultado);
        sendResponse(exchange, 200, principal.toString());
    }

    public void sendResponse(HttpExchange exchange, int status, String body) throws IOException {

        exchange.getResponseHeaders().add("Content-Type", "application/json");

        byte[] bytes = body.getBytes();

        exchange.sendResponseHeaders(status, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}