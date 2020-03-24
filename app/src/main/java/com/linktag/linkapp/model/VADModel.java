package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.VadVO;

import java.io.Serializable;
import java.util.ArrayList;

public class VADModel implements Serializable {

    public ArrayList<VadVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
