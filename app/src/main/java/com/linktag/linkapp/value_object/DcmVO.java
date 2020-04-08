package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class DcmVO implements Serializable  {

    public String DCM_ID; // 컨테이너번호
    public String DCM_01; // 코드 일련번호
    public String DCM_02; // 일련번호
    public String DCM_03; // 아이콘
    public String DCM_98; // 최종수정자 아이디

    public String getDCM_ID() {
        return DCM_ID;
    }

    public void setDCM_ID(String DCM_ID) {
        this.DCM_ID = DCM_ID;
    }

    public String getDCM_01() {
        return DCM_01;
    }

    public void setDCM_01(String DCM_01) {
        this.DCM_01 = DCM_01;
    }

    public String getDCM_02() {
        return DCM_02;
    }

    public void setDCM_02(String DCM_02) {
        this.DCM_02 = DCM_02;
    }

    public String getDCM_03() {
        return DCM_03;
    }

    public void setDCM_03(String DCM_03) {
        this.DCM_03 = DCM_03;
    }

    public String getDCM_98() {
        return DCM_98;
    }

    public void setDCM_98(String DCM_98) {
        this.DCM_98 = DCM_98;
    }

    public DcmVO init(){
        this.DCM_ID = "";
        this.DCM_01 = "";
        this.DCM_02 = "";
        this.DCM_03 = "";
        this.DCM_98 = "";

        return this;
    }
}
