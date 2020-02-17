package com.linktag.linkapp.ui.cos;

public class CosList {
    private String code;
    private String name;

    /**
     *
     * @param code (코드)
     * @param name (이름)
     */
    public CosList(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getCode(){
        return code;
    }
    public String getName(){
        return name;
    }

}