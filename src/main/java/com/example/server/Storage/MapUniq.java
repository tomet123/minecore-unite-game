package com.example.server.Storage;

public class MapUniq {

    public static int MAP_ID =1;


    public static int getMapId(){
        MAP_ID+=1;
        return MAP_ID;
    }
}
