package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.RtscVO;

import java.io.Serializable;
import java.util.ArrayList;

public class RTSC_Model implements Serializable {
    private static final long serialVersionUID = -1124454060264607288L;

    public ArrayList<RtscVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
