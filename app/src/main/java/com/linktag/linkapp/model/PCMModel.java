package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.JdmVO;

import java.io.Serializable;
import java.util.ArrayList;

public class JDMModel implements Serializable {

    public ArrayList<JdmVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
