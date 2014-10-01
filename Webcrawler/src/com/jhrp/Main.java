package com.jhrp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String url = "http://stackoverflow.com/questions/16794913/data-scraping-with-scrapy";
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String question = document.select(".question .postcell").text();

        System.out.println(question);


        //Testing
    }
}
