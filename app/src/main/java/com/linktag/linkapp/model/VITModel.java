package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.VIT_VO;

import java.io.Serializable;
import java.util.ArrayList;

public class VITModel implements Serializable {
    private static final long serialVersionUID = 9088486284129217882L;

    public ArrayList<VIT_VO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
