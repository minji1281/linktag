package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.PcdVO;
import com.linktag.linkapp.value_object.PcmVO;

import java.io.Serializable;
import java.util.ArrayList;

public class PCDModel implements Serializable {

    public ArrayList<PcdVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
