package com.linktag.linkapp.ui.spinner;

public class SpinnerList {
    private String code;
    private String name;

    /**
     *
     * @param code (코드)
     * @param name (이름)
     */
    public SpinnerList(String code, String name){
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