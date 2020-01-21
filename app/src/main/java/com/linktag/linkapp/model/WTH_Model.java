package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.WthVO;

import java.io.Serializable;
import java.util.ArrayList;

public class WTH_Model implements Serializable {
    private static final long serialVersionUID = 7166551616403243971L;

    public ArrayList<WthVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
