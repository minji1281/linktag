package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.SIF_VO;

import java.io.Serializable;
import java.util.ArrayList;

public class SIFModel implements Serializable {

    public ArrayList<SIF_VO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
