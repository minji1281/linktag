package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.FRM_VO;

import java.io.Serializable;
import java.util.ArrayList;

public class FRMModel implements Serializable {
    private static final long serialVersionUID = 9088486284129217882L;

    public ArrayList<FRM_VO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
