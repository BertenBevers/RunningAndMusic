package com.example.project_ict_4;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Created by nick on 7/05/2015.
 */
public class EchoNestHandler {
    private String ApiKey="";
    String SetServerString;

    public double SendToNest(final String url)
    {
        new Thread() {
            public void run() {
                try {


                    HttpClient Client = new DefaultHttpClient();
                    HttpGet httpget = new HttpGet(url);
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    SetServerString = Client.execute(httpget, responseHandler);
                    SetServerString = Jsoup.parse(SetServerString).text();


                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }.start();
        double value = Double.parseDouble(SetServerString);
        return value;

    }

    public String FormatUrl(String artist , String title)
    {

        artist = artist.replaceAll("\\s+","@");
        title =title.replaceAll("\\s+","@");
        String url ="http://bertenbevers.be/ProjectICT4/echonest.php?title="+title+"&artist="+artist;
        return url;
    }
}
