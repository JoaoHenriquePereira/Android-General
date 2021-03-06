package com.jhrp.test.crawler;

import com.jhrp.crawler.Webcrawler;
import com.jhrp.db.DBClient;
import com.jhrp.db.model.tPageBankModel;
import com.jhrp.object.HostInformation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by joaopereira on 01/10/14.
 */
public class WebcrawlerTest {

    @Test
    public void checkDBConnection(){
        DBClient t = new DBClient();
        assertNotNull("DB connect test failed", t.conn);
    }

    @Test
    public void checkJSOUP(){
        Document d = null;
        try {
            d = Jsoup.connect("http://www.google.com").get();
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionError();
        }

        assertNotNull("JSoup connect test failed", d);
    }

    @Test
    public void checkGetHostInformation(){
        Webcrawler t = new Webcrawler();
        HostInformation tt = t.getHostInformation("http://www.imgur.com");

    }

}
