package com.tke.island.controller;

import lombok.Getter;

public class IslandNPCController {

    @Getter
    private static IslandNPCController instance;



    public IslandNPCController(){
        instance = this;
    }


}
