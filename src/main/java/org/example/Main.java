package org.example;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import router.RouterHandler;
import service.ServiceDogs;


public class Main {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);


        server.createContext("/", new RouterHandler());

        try {
            ServiceDogs service = new ServiceDogs();
            service.JsonPrint();
        } catch (Exception e) {
            e.printStackTrace();
        }


        server.setExecutor(null);
        server.start();

        System.out.println("Servidor iniciado en http://localhost:8080");
    }
}