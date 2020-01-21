package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.LoginVO;

import java.io.Serializable;
import java.util.ArrayList;

public class LOGIN_Model implements Serializable {
    private static final long serialVersionUID = 7858911636109804318L;

    public ArrayList<LoginVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
