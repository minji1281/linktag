package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.RMM_VO;

import java.io.Serializable;
import java.util.ArrayList;

public class RMMModel implements Serializable {
    private static final long serialVersionUID = 9088486284129217882L;

    public ArrayList<RMM_VO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
