package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.OcmVO;

import java.io.Serializable;
import java.util.ArrayList;

public class OCM_Model implements Serializable {
    private static final long serialVersionUID = 3064858979684075796L;

    public ArrayList<OcmVO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
