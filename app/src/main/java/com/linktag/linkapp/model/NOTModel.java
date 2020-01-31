package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.NOT_VO;

import java.io.Serializable;
import java.util.ArrayList;

public class NOTModel implements Serializable {
    private static final long serialVersionUID = 3282021142550940994L;

    public ArrayList<NOT_VO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;

}
