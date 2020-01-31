package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.PotVO;

import java.io.Serializable;
import java.util.ArrayList;

public class POT_Model implements Serializable {
    private static final long serialVersionUID = 9088486284129217882L;

    public ArrayList<PotVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
