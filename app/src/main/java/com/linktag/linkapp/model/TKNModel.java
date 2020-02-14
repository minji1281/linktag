package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.TKN_VO;

import java.io.Serializable;
import java.util.ArrayList;

public class TKNModel implements Serializable {
    private static final long serialVersionUID = 3282021142550940994L;

    public ArrayList<TKN_VO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;

}
