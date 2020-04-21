package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.ICM_VO;

import java.io.Serializable;
import java.util.ArrayList;

public class ICMModel implements Serializable {
    private static final long serialVersionUID = 9088486284129217882L;

    public ArrayList<ICM_VO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
