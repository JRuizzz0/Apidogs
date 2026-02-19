package service;


import Clases.Raza;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
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


    public void JsonPrint() throws IOException, InterruptedException {
        List<String> breeds = getBreedNames();

        JsonArray resultado = new JsonArray();

        for (String raza : breeds) {


            JsonArray subraza = new JsonArray();
            resultado.add(subraza);
            subraza.add(raza);
            if (subraza.size() != 1) {
                JsonObject subrazaJson = new JsonObject();
                subrazaJson.addProperty("nombre", raza);
            }

            JsonObject obj = new JsonObject();
            obj.addProperty("nombre", raza);
            resultado.add(obj);
        }

        JsonObject raiz = new JsonObject();
        raiz.add("razas", resultado);
        System.out.println(raiz.toString());
    }

}