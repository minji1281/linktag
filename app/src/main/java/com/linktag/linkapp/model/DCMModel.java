package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.DamVO;
import com.linktag.linkapp.value_object.DcmVO;

import java.io.Serializable;
import java.util.ArrayList;

public class DCMModel implements Serializable {

    public ArrayList<DcmVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
