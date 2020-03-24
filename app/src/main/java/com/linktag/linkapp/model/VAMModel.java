package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.VamVO;

import java.io.Serializable;
import java.util.ArrayList;

public class VAMModel implements Serializable {

    public ArrayList<VamVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
