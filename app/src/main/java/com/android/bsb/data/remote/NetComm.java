package com.android.bsb.data.remote;

public class NetComm {

    //test
    //public static final String HOST_NAME = "http://mock-api.com/4jzArZgk.mock/";

    //public static final String HOST_NAME = "http://26097d23.all123.net:8888/sectem/";

    public static final String KEY_IP = "IP_CONFIG";

    private static  String IP = "47.104.77.108" ;

    private static  String PORT = "8080";

    public static void setIpConfig(String ipPort){
        String[] ip = ipPort.split(":");
        IP = ip[0];
        PORT = ip[1];
    }

    public static String getHost(){
        return "http://"+IP+":"+PORT+"/sectem/";
    }

    public static String getImageHost(){
        return "http://"+IP+":"+PORT+"/images/";
    }


}
