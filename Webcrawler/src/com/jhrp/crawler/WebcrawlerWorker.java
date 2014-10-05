package com.jhrp.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by joaopereira on 04/10/14.
 */
public class WebcrawlerWorker implements Runnable{

    private String url;

    public WebcrawlerWorker(String s){
        this.url = s;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+" Start. url = " + url);
        processCommand();
        System.out.println(Thread.currentThread().getName()+" End.");

        /* get useful information */
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            if(doc.text().contains("research")){
                System.out.println(url);
            }

            //get all links and recursively call the processPage method
            Elements questions = doc.select("a[href]");
            System.out.println("size "+questions.size());
            int c =0;
            for(Element link: questions){
                if(link.attr("href").contains("mit.edu"))
                    //processPage(link.attr("abs:href"));
                    System.out.println(link.attr("abs:href"));
                c++;
            }
            System.out.println("filtered site "+c);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void processCommand() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){
        return this.url;
    }
}
