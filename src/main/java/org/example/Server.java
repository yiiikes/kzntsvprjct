package org.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Server {

    private static int targetNumber = new Random().nextInt(500) + 1;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/currentTime", new CurrentTimeHandler());
        server.createContext("/api", new NumberListHandler());
        server.createContext("/random_number", new RandomNumberHandler());
        server.createContext("/fib", new FibonacciHandler());
        server.createContext("/game", new GameHandler());
        server.createContext("/", new ReverseStringHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("завелся");
    }

    static class CurrentTimeHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = LocalDateTime.now().toString();
            sendResponse(exchange, response);
        }
    }

    static class NumberListHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            int n = Integer.parseInt(query.split("=")[1]);
            List<Integer> numbers = IntStream.rangeClosed(1, n).boxed().collect(Collectors.toList());
            String response = numbers.toString();
            sendResponse(exchange, response);
        }
    }

    static class RandomNumberHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            int randomNumber = new Random().nextInt(500) + 1;
            sendResponse(exchange, String.valueOf(randomNumber));
        }
    }

    static class FibonacciHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            int n = Integer.parseInt(query.split("=")[1]);
            int fibNumber = fibonacci(n);
            sendResponse(exchange, String.valueOf(fibNumber));
        }

        private int fibonacci(int n) {
            if (n <= 1) return n;
            return fibonacci(n - 1) + fibonacci(n - 2);
        }
    }

    static class GameHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            int guess = Integer.parseInt(query.split("=")[1]);
            String response;

            if (guess < targetNumber) {
                response = "ваше число меньше загаданного числа";
            } else if (guess > targetNumber) {
                response = "ваше число больше загаданного числа";
            } else {
                targetNumber = new Random().nextInt(500) + 1; // сброс
                response = "вы угадали число";
            }
            sendResponse(exchange, response);
        }
    }

    static class ReverseStringHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String stringToReverse = path.substring(1);
            String response = new StringBuilder(stringToReverse).reverse().toString();
            sendResponse(exchange, response);
        }
    }

    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}