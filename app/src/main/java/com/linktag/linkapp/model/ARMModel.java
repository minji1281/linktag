package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.ArmVO;
import com.linktag.linkapp.value_object.JdmVO;

import java.io.Serializable;
import java.util.ArrayList;

public class ARMModel implements Serializable {

    public ArrayList<ArmVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
