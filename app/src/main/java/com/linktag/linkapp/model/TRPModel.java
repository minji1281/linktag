package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.TrpVO;

import java.io.Serializable;
import java.util.ArrayList;

public class TRPModel implements Serializable {

    public ArrayList<TrpVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
