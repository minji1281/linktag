package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.RutcVO;

import java.io.Serializable;
import java.util.ArrayList;

public class RUTC_Model implements Serializable {
    private static final long serialVersionUID = -1124454060264607288L;

    public ArrayList<RutcVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
