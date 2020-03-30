package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.RMD_VO;

import java.io.Serializable;
import java.util.ArrayList;

public class RMDModel implements Serializable {
    private static final long serialVersionUID = 9088486284129217882L;

    public ArrayList<RMD_VO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
