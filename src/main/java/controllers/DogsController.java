package controllers;

import com.sun.net.httpserver.HttpExchange;
import service.ServiceDogs;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DogsController {


    //Instancia HttpClient
    private final HttpClient client = HttpClient.newHttpClient();
    /**
     * Metodo para manejar los endpoints
     * @param exchange encapsula la petición del cliente y la respuesta del servidor.
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
            if (path.startsWith("/dogs/list/imagenes/")) {//endpoint mostrar n imagenes random
                service.JsonPrintImagenes(exchange);
            }
            if (path.startsWith("/dogs/raza/imagenes/")) {//endpoint mostrar todas las imagenes de una raza
                service.JsonPrintImagenesPorRaza(exchange);
            }
            if (path.startsWith("/dogs/compare/imagenes/")) {//endpoint para mostra 2 fotos de 2 perros dif
                service.JsonPrintDosRazas(exchange);
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
     * Envía una respuesta HTTP al cliente especificando que el contenido es de tipo JSON.
     *
     * @param exchange  encapsula la petición del cliente y la respuesta del servidor.
     * @param status   El código de estado HTTP que se enviará al cliente.
     * @param body     El cuerpo de la respuesta en formato de texto que será enviado.
     * @throws IOException Si ocurre un error de entrada/salida al configurar las cabeceras o al escribir en el flujo de respuesta.
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