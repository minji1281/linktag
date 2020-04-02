package com.linktag.linkapp.ui.rmm;

import java.util.ArrayList;

public class ReserveList implements Comparable<ReserveList> {
    private String code;
    private String time;

    public ReserveList(String code, String time){
        this.code = code;
        this.time = time;
    }

    public String getCode(){
        return code;
    }
    public String getTime(){
        return time;
    }

    @Override
    public int compareTo(ReserveList r) {
        if (Integer.valueOf(this.time) < Integer.valueOf(r.getTime())) {
            return -1;
        } else if (Integer.valueOf(this.time) > Integer.valueOf(r.getTime())) {
            return 1;
        }
        return 0;
    }
}