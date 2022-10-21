package edu.escuela;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static spark.Spark.*;

public class SparkWebServer {

    public static void main(String... args){
        port(getPort());
        staticFiles.location("/public");
        post("log", (request, response) -> roundRobin(request.body()));
    }

    public static int server = 0;

    public static String roundRobin(String request) throws IOException {
        if (server > 2){
            server = 0;
        }
        String url = "http://ec2-3-88-222-210.compute-1.amazonaws.com:3500"+server+"/save";
        server++;
        System.out.println("conectando a "+ url);
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "text/plain");
        con.setRequestProperty("Accept", "text/plain");
        con.setDoOutput(true);
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = request.getBytes("utf-8");
            os.write(input, 0, input.length);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
            return response.toString();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }

}