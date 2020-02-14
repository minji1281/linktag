package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.JdmVO;
import com.linktag.linkapp.value_object.PcmVO;

import java.io.Serializable;
import java.util.ArrayList;

public class PCMModel implements Serializable {

    public ArrayList<PcmVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
