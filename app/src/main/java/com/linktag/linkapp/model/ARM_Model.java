package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.ARM_VO;

import java.io.Serializable;
import java.util.ArrayList;

public class ARM_Model implements Serializable {
    private static final long serialVersionUID = 9088486284129217882L;

    public ArrayList<ARM_VO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
