package com.jhrp.object;

/**
 * Created by joaopereira on 03/10/14.
 */
public class HostInformation {

    private String hostname;
    private String ip;

    public HostInformation() {}

    public HostInformation(String hostname, String ip) {
        this.hostname = hostname;
        this.ip = ip;
    }

    public String getHostname(){
        return this.hostname;
    }

    public void setHostname(String hostname){
        this.hostname = hostname;
    }

    public String getIp(){
        return this.ip;
    }

    public void setIp(String ip){
        this.ip = ip;
    }

}
