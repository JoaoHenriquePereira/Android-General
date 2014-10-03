package com.jhrp;

import com.jhrp.crawler.Webcrawler;

public class Main {

    public static void main(String[] args) {
        Webcrawler w = new Webcrawler("Fluffy");
        w.start();
    }
}
