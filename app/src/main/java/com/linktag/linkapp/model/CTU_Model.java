package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.CtuVO;

import java.io.Serializable;
import java.util.ArrayList;

public class CTU_Model implements Serializable {
    private static final long serialVersionUID = 2345501053406964758L;

    public ArrayList<CtuVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
