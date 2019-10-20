package com.daeseong.intent1service;

public class gameinfo {
    private String packagename;
    private String starttm;
    private String endtm;

    public gameinfo(String packagename, String starttm, String endtm){
        this.packagename = packagename;
        this.starttm = starttm;
        this.endtm = endtm;
    }

    public String getPackagename(){
        return packagename;
    }

    public String getStarttm(){
        return starttm;
    }

    public String getEndtm(){
        return endtm;
    }
}
