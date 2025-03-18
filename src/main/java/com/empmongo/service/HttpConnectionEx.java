package com.empmongo.service;

import com.fasterxml.jackson.core.util.BufferRecycler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpConnectionEx {

    URL url;

    {
        try {
            url = new URL("");
            HttpURLConnection connection =(HttpURLConnection) url.openConnection();
            connection.setRequestMethod("");
            connection.setDoInput(true);
            String data = "";
            connection.getOutputStream().write(data.getBytes());
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
