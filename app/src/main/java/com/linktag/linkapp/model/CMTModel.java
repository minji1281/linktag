package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.CMT_VO;

import java.io.Serializable;
import java.util.ArrayList;

public class CMTModel implements Serializable {
    private static final long serialVersionUID = 3282021142550940994L;

    public ArrayList<CMT_VO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;

}
