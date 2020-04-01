package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.DamVO;

import java.io.Serializable;
import java.util.ArrayList;

public class DAMModel implements Serializable {

    public ArrayList<DamVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
