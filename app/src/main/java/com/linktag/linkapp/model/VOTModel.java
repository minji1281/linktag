package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.VOT_VO;

import java.io.Serializable;
import java.util.ArrayList;

public class VOTModel implements Serializable {
    private static final long serialVersionUID = 9088486284129217882L;

    public ArrayList<VOT_VO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
