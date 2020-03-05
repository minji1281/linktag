package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class TrdVO  implements Serializable {

    public String TRD_ID;  //컨테이너번호
    public String TRD_01;  //코드 일련번호
    public String TRD_02;  //일련번호
    public String TRD_96;  //알림시각 (알림지정일자)
    public String TRD_98;  // 최종수정자 아이디

    public String TRP_02; // 명칭
    public String TRP_03; // 메모
    public String TRP_04; // 요일
    public String ARM_03; // 알림여부

    public String getTRD_ID() {
        return TRD_ID;
    }

    public void setTRD_ID(String TRD_ID) {
        this.TRD_ID = TRD_ID;
    }

    public String getTRD_01() {
        return TRD_01;
    }

    public void setTRD_01(String TRD_01) {
        this.TRD_01 = TRD_01;
    }

    public String getTRD_02() {
        return TRD_02;
    }

    public void setTRD_02(String TRD_02) {
        this.TRD_02 = TRD_02;
    }

    public String getTRD_96() {
        return TRD_96;
    }

    public void setTRD_96(String TRD_96) {
        this.TRD_96 = TRD_96;
    }

    public String getTRD_98() {
        return TRD_98;
    }

    public void setTRD_98(String TRD_98) {
        this.TRD_98 = TRD_98;
    }


    public String getTRP_02() {
        return TRP_02;
    }

    public void setTRP_02(String TRP_02) {
        this.TRP_02 = TRP_02;
    }

    public String getTRP_03() {
        return TRP_03;
    }

    public void setTRP_03(String TRP_03) {
        this.TRP_03 = TRP_03;
    }

    public String getTRP_04() {
        return TRP_04;
    }

    public void setTRP_04(String TRP_04) {
        this.TRP_04 = TRP_04;
    }

    public String getARM_03() {
        return ARM_03;
    }

    public void setARM_03(String ARM_03) {
        this.ARM_03 = ARM_03;
    }
}
