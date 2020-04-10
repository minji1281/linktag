package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class INV_VO implements Serializable {
    private static final long serialVersionUID = 8995492762495280092L;

    public String INV_01;       // 계약번호
    public String INV_02;       // 서비스 일련번호
    public String INV_03;       // 이용자
    public String INV_04;       // 초대자
    public String INV_05;       // 초대일자
    public String INV_06;       // 확인여부

    public String INV_01_NM;        //
    public String INV_02_NM;        //
    public String INV_03_NM;        //
    public String INV_04_NM;

    public String SVC_01;
    public String CTD_08;

    public boolean Validation;
    public String ErrorCode;
}
