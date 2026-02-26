package router;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.DogsController;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Clase Router Handler que implementa HTTPHANDLER
 */
public class RouterHandler implements HttpHandler {

    /**
     * @param Instancia dogsController
     * @param atributo boolean para ver si el server esta listo
     */
    private final DogsController dogsController = new DogsController();

    private boolean isServerReady = true;


    /**
     *
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException excepcion input output
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        //si el server no esta listo error 503
        if (!isServerReady) {
            error503(exchange);
            return;
        }
        try {
            //si la ruta empieza por dogs llama al dogsController y le manda el exchange
            if (path.startsWith("/dogs")) {
                dogsController.handle(exchange);
            }
            else {
                //si no error q no lo encuentra
                error404(exchange);
            }

        } catch (Exception e) {
            System.err.println("Error en el servidor: " + e.getMessage());
            error500(exchange);
            //no carga el server
        }
    }

    /**
     * Metodo error404
     * @param exchange the exchange containing the request from the
     *      *                 client and used to send the response
     * @throws IOException error input output
     */
    public static void error404(HttpExchange exchange) throws IOException {
        String response404 = "404 - Ruta no encontrada";
        exchange.sendResponseHeaders(404, response404.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response404.getBytes());
        os.close();
    }

    /**
     * metodo error 500 server not init
     * @param exchange the exchange containing the request from the
     *      *                 client and used to send the response
     * @throws IOException error input output
     */
    public static void error500(HttpExchange exchange) throws IOException {
        String response500 = "500 - Error interno del servidor";
        exchange.sendResponseHeaders(500, response500.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response500.getBytes());
        os.close();
    }

    /**
     * Metodo error 503
     * @param exchange the exchange containing the request from the
     *      *                 client and used to send the response
     * @throws IOException error input output
     */
    public static void error503(HttpExchange exchange) throws IOException {
        String response503 = "503 - Servidor no disponible o no iniciado";
        exchange.sendResponseHeaders(503, response503.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response503.getBytes());
        os.close();
    }
}