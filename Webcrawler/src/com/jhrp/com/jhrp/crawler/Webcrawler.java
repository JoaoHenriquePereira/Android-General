package com.jhrp.com.jhrp.crawler;

import com.jhrp.com.jhrp.db.DBClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by joaopereira on 01/10/14.
 */
public class Webcrawler {

    public static DBClient db = new DBClient();

    public String name = "";

    public Webcrawler(String name) {
        this.name = name;
    }

    /*public static void main(String[] args) throws SQLException, IOException {
        db.execute("TRUNCATE Record;");
        processPage("http://www.mit.edu");
    }*/

    public static void processPage(String URL) throws SQLException, IOException{
        //check if the given URL is already in database
        String sql = "select * from Record where URL = '"+URL+"'";
        ResultSet rs = db.executeFetch(sql);
        if(rs.next()){

        }else{
            //store the URL to database to avoid parsing again
            sql = "INSERT INTO  `Crawler`.`Record` " + "(`URL`) VALUES " + "(?);";
            PreparedStatement stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, URL);
            stmt.execute();

            //get useful information
            Document doc = Jsoup.connect("http://www.mit.edu/").get();

            if(doc.text().contains("research")){
                System.out.println(URL);
            }

            //get all links and recursively call the processPage method
            Elements questions = doc.select("a[href]");
            for(Element link: questions){
                if(link.attr("href").contains("mit.edu"))
                    processPage(link.attr("abs:href"));
            }
        }
    }
}
