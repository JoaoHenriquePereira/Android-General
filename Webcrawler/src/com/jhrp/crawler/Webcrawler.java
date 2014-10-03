package com.jhrp.crawler;

import com.jhrp.db.model.PageBankModel;
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
    private static final int _NUM_THREADS = 1;

    public String name = "";

    public Webcrawler(String name) {
        this.name = name;
    }

    public void start(){

        PageBankModel page_bank_model = new PageBankModel();
        String url = "";

        try{
            ResultSet rs = page_bank_model.getNextURL();
            url = rs.getString("url");


            processPage(url);
            HostInformation hi = getHostInformation(url);


        } catch (SQLException e){
            e.printStackTrace();
            return;
        } finally {
            page_bank_model = null;
            url = "";
        }

    }

    public static void processPage(String url) throws SQLException, IOException{

        //get useful information
        Document doc = Jsoup.connect(url).get();

        if(doc.text().contains("research")){
            System.out.println(url);
        }

        //get all links and recursively call the processPage method
        Elements questions = doc.select("a[href]");
        for(Element link: questions){
            if(link.attr("href").contains("mit.edu"))
                processPage(link.attr("abs:href"));
        }

    }

    private HostInformation getHostInformation( String url ){
        HostInformation hi = new HostInformation();

        return hi;
    }
}
