package com.jhrp.crawler;

import com.jhrp.db.model.tPageBankModel;
import com.jhrp.object.HostInformation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by joaopereira on 01/10/14.
 * - Get URL from page bank
 * - Get IP and HOSTNAME
 * - Download/parse html page and extract links
 * - Verify links and check if visited page
 * - Add links to list of links to visit
 */

public class Webcrawler {

    //TODO No multi-thread for now
    private static final int _NUM_WORKERS = 1;

    public String name = "";

    public Webcrawler(String name) {
        this.name = name;
    }

    public void start(){
        //TODO

    }

    private HostInformation getHostInformation( String url ){
        HostInformation hi = new HostInformation();

        return hi;
    }
}
