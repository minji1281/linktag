package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.CtdVO;

import java.io.Serializable;
import java.util.ArrayList;

public class CTD_Model implements Serializable {
    private static final long serialVersionUID = 1842009996973346496L;

    public ArrayList<CtdVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
