package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.TrdVO;

import java.io.Serializable;
import java.util.ArrayList;

public class TRDModel implements Serializable {

    public ArrayList<TrdVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
