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

    //Instancia HttpClient
    private final HttpClient client = HttpClient.newHttpClient();

    /**
     * Metodo para manejar los endpoints
     * @param exchange the exchange containing the request from the
     *      *                 client and used to send the response
     * @throws IOException error Input Output
     */
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        try {
            String apiUrl = "";
            ServiceDogs service = new ServiceDogs();
            if (path.equals("/dogs/list/razas")) { //endpoint mostra todas las razas

                service.JsonPrintrazas(exchange);
                return;
            }
            if (path.equals("/dogs/list/nosubrazas")) { //endpoint mostrar las razas sin subrazas
                service.JsonPrintNoSubrazas(exchange);
                return;
            }
            if (path.equals("/dogs/list/subrazas")) { //endpoint mostrar las razas con subraza
                service.JsonPrintSubrazas(exchange);
                return;
            }
            if (path.equals("/dogs/list/imagenes")) { //endpoint mostrar las imagenes random
                service.JsonPrintImagenes(exchange);
            }
            else {
                service.sendResponse(exchange, 404, "Endpoint dogs no válido"); //error server not found
                return;
            }

            /**
             * request para recorger la url y manejarla con el router y el control
             */
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            sendResponse(exchange, 200, response.body());
        } catch (Exception e) {
            sendResponse(exchange, 500, "Error llamando a la API dogs"); //error 500
        }
    }

    /**
     *
     * @param exchange the exchange containing the request from the
     *                client and used to send the response
     * @param status  status error ej:200
     * @param body    atributo string que guarda el "cuerpo" al enviar la respuesta
     * @throws IOException error input output
     */
    public void sendResponse(HttpExchange exchange, int status, String body) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] bytes = body.getBytes();
        exchange.sendResponseHeaders(status, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

}