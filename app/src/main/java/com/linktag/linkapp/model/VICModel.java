package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.VIC_VO;

import java.io.Serializable;
import java.util.ArrayList;

public class VICModel implements Serializable {
    private static final long serialVersionUID = 9088486284129217882L;

    public ArrayList<VIC_VO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
