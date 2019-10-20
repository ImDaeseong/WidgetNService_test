package com.daeseong.intent1service;

import java.util.HashMap;

public class iteminfo {

    private static final String TAG = iteminfo.class.getSimpleName();

    private static iteminfo instance;
    public static iteminfo getInstance(){
        if( instance == null){
            instance = new iteminfo();
        }
        return instance;
    }

    private HashMap<String, String> gameMap = new HashMap<>();

    public iteminfo(){
        gameMap.clear();
    }

    public void setGameItem(String packageName){

        if(!gameMap.containsKey(packageName)){
            gameMap.put(packageName, packageName);
        }
    }

    public boolean isGameItem(String packageName){

        boolean bfind = false;
        if(gameMap.containsKey(packageName)){
            bfind = true;
        }
        return bfind;
    }

    public void clearGameItem(){
        gameMap.clear();
    }
}
