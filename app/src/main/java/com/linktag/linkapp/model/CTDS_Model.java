package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.CtdsVO;

import java.io.Serializable;
import java.util.ArrayList;

public class CTDS_Model implements Serializable {
    private static final long serialVersionUID = 6050646109681262015L;

    public ArrayList<CtdsVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
