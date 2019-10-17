package com.daeseong.gameservice;

import java.util.ArrayList;
import java.util.List;

public class GameInfo {

    private static final String TAG = GameInfo.class.getSimpleName();

    private static GameInfo instance;
    public static GameInfo getInstance(){
        if( instance == null){
            instance = new GameInfo();
        }
        return instance;
    }

    private List<GameItem> gameList = new ArrayList<>();

    public void setGameItem(String packageName){
        GameItem item = new GameItem();
        item.packageName = packageName;
        gameList.add(item);
    }

    public boolean isGameItem(String packageName){

        for (int i = 0; i < gameList.size(); i++) {

            if(gameList.get(i).packageName.equals(packageName)){
                return true;
            }

        }

        return false;
    }

    public static class GameItem{
        public String packageName = "";
    }

}
