package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.VacVO;

import java.io.Serializable;
import java.util.ArrayList;

public class VACModel implements Serializable {

    public ArrayList<VacVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
