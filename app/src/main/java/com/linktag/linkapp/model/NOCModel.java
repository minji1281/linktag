package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.NOC_VO;

import java.io.Serializable;
import java.util.ArrayList;

public class NOCModel implements Serializable {
    private static final long serialVersionUID = 3282021142550940994L;

    public ArrayList<NOC_VO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;

}
