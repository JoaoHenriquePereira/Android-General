package com.jhrp.test;

import com.jhrp.com.jhrp.db.DBClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by joaopereira on 01/10/14.
 */
public class WebcrawlerTest {

    @Test
    public void checkDBConnection(){
        DBClient t = new DBClient();
        if (null == t.conn) {
            throw new AssertionError();
        }
    }

    @Test
    public void checkJSOUP(){
        Document document = null;
        try {
            document = Jsoup.connect("http://www.google.com").get();
        } catch (IOException e) {
            throw new AssertionError();
        }

        if (null == document) {
            throw new AssertionError();
        }
    }

    @Test
    public void computeOPIC(){
        Document document = null;
        try {
            document = Jsoup.connect("http://www.google.com").get();
        } catch (IOException e) {
            throw new AssertionError();
        }

        if (null == document) {
            throw new AssertionError();
        }
    }
}
