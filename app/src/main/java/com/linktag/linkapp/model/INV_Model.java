package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.INV_VO;

import java.io.Serializable;
import java.util.ArrayList;

public class INV_Model implements Serializable {
    private static final long serialVersionUID = 1605181983596535695L;

    public ArrayList<INV_VO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
