package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.JdmVO;
import com.linktag.linkapp.value_object.RfmVO;

import java.io.Serializable;
import java.util.ArrayList;

public class RFMModel implements Serializable {

    public ArrayList<RfmVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
