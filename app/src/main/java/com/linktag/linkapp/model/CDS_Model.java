package com.linktag.linkapp.model;

import com.linktag.linkapp.value_object.CDS_VO;

import java.io.Serializable;
import java.util.ArrayList;

public class CDS_Model implements Serializable {
    private static final long serialVersionUID = -4463022114704983113L;

    public ArrayList<CDS_VO> Data;
    public int Total;
    public String AggregateResults;
    public String Errors;
}
