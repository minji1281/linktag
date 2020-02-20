package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class RfmVO implements Serializable {

    public String RFM_ID; // 컨테이너번호
    public String RFM_01; // 코드 일련번호
    public String RFM_02; // 명칭
    public String RFM_03; // 메모
    public String RFM_97; // 작성자 아이디
    public String RFM_98; // 최종수정자 아이디


    public String getRFM_ID() {
        return RFM_ID;
    }

    public void setRFM_ID(String RFM_ID) {
        this.RFM_ID = RFM_ID;
    }

    public String getRFM_01() {
        return RFM_01;
    }

    public void setRFM_01(String RFM_01) {
        this.RFM_01 = RFM_01;
    }

    public String getRFM_02() {
        return RFM_02;
    }

    public void setRFM_02(String RFM_02) {
        this.RFM_02 = RFM_02;
    }

    public String getRFM_03() {
        return RFM_03;
    }

    public void setRFM_03(String RFM_03) {
        this.RFM_03 = RFM_03;
    }

    public String getRFM_97() {
        return RFM_97;
    }

    public void setRFM_97(String RFM_97) {
        this.RFM_97 = RFM_97;
    }

    public String getRFM_98() {
        return RFM_98;
    }

    public void setRFM_98(String RFM_98) {
        this.RFM_98 = RFM_98;
    }
}
