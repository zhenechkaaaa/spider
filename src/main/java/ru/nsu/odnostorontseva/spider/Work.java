package ru.nsu.odnostorontseva.spider;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;


public class Work {

    public static Response get(String path) {
        URI uri = URI.create(path);
        HttpURLConnection con;
        try {
            URL url = uri.toURL();
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }

        try {
            if(con.getResponseCode() != 200) {
                System.out.println("Error: " + con.getResponseCode());
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedReader in;
        try{
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String content = readInput(in);
        return parser(content);

    }

    private static String readInput(BufferedReader in) {
        String inputLine;
        StringBuilder content = new StringBuilder();
        try {
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return content.toString();
    }

    private static Response parser(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, Response.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}