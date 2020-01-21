package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.CDO_VO;

import java.io.Serializable;
import java.util.ArrayList;

public class CDOModel implements Serializable {
    private static final long serialVersionUID = 2609229925413629973L;

    public ArrayList<CDO_VO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;

}
