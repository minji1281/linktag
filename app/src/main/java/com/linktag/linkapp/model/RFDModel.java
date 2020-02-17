package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.RfdVO;
import com.linktag.linkapp.value_object.RfmVO;

import java.io.Serializable;
import java.util.ArrayList;

public class RFDModel implements Serializable {

    public ArrayList<RfdVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
