package com.jhrp.crawler;

import com.jhrp.crawler.icrawler.RejectedExecutionHandlerImpl;
import com.jhrp.db.model.tPageBankModel;
import com.jhrp.object.HostInformation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.*;

/**
 * Created by joaopereira on 01/10/14.
 * - Get URL from page bank
 * - Get IP and HOSTNAME
 * - Download/parse html page and extract links
 * - Verify links and check if visited page
 * - Add links to list of links to visit
 */

public class Webcrawler {

    private static final int _MAX_NUM_WORKERS = 1; /* Thread pool size */
    private static final int _CONTRACT_NOTICE = 5; /* Time for idle threads to wait before shutting down */

    public String name = "";

    public Webcrawler(String name) {
        this.name = name;
    }

    public void start(){

        //load work
        tPageBankModel page_bank_model = new tPageBankModel();
        String url = "";
        ResultSet rs = null;
        try {
            rs = page_bank_model.getNextURL();
            rs.next();

            url = rs.getString("url");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //getting the party started
        RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        ThreadPoolExecutor executorPool = new ThreadPoolExecutor(1,
                                                                _MAX_NUM_WORKERS,
                                                                _CONTRACT_NOTICE,
                                                                TimeUnit.SECONDS,
                                                                new ArrayBlockingQueue<Runnable>(1),
                                                                threadFactory,
                                                                rejectionHandler);

        //start the monitoring thread
        WebcrawlerForeman monitor = new WebcrawlerForeman(executorPool, 5);
        Thread monitorThread = new Thread(monitor);
        monitorThread.start();
        //submit work to the thread pool
        for(int i=0; i<1; i++){ //HARDCODED 1 for the amount of threads to launch
            executorPool.execute(new WebcrawlerWorker(url));
        }

        executorPool.shutdown();
        monitor.shutdown();

    }

    private HostInformation getHostInformation( String url ){
        HostInformation hi = new HostInformation();

        return hi;
    }
}
