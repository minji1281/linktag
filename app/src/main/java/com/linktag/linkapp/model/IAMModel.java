package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.IamVO;
import com.linktag.linkapp.value_object.JdmVO;

import java.io.Serializable;
import java.util.ArrayList;

public class IAMModel implements Serializable {

    public ArrayList<IamVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
