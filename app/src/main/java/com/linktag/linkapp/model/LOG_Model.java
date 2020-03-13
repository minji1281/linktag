package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.LogVO;

import java.io.Serializable;
import java.util.ArrayList;

public class LOG_Model implements Serializable {

    public ArrayList<LogVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
