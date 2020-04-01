package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class DamVO implements Serializable  {

    public String DAM_ID; // 컨테이너번호
    public String DAM_01; // 코드 일련번호
    public String DAM_02; // 명칭
    public String DAM_03; // 아이콘
    public String DAM_04; // 계산방법
    public String DAM_96; // 날짜(알림 지정일자)
    public String DAM_97; // 작성자 아이디
    public String DAM_98; // 최종수정자 아이디
    public String ARM_03;


    public String getDAM_ID() {
        return DAM_ID;
    }

    public void setDAM_ID(String DAM_ID) {
        this.DAM_ID = DAM_ID;
    }

    public String getDAM_01() {
        return DAM_01;
    }

    public void setDAM_01(String DAM_01) {
        this.DAM_01 = DAM_01;
    }

    public String getDAM_02() {
        return DAM_02;
    }

    public void setDAM_02(String DAM_02) {
        this.DAM_02 = DAM_02;
    }

    public String getDAM_03() {
        return DAM_03;
    }

    public void setDAM_03(String DAM_03) {
        this.DAM_03 = DAM_03;
    }

    public String getDAM_04() {
        return DAM_04;
    }

    public void setDAM_04(String DAM_04) {
        this.DAM_04 = DAM_04;
    }

    public String getDAM_96() {
        return DAM_96;
    }

    public void setDAM_96(String DAM_96) {
        this.DAM_96 = DAM_96;
    }

    public String getDAM_97() {
        return DAM_97;
    }

    public void setDAM_97(String DAM_97) {
        this.DAM_97 = DAM_97;
    }

    public String getDAM_98() {
        return DAM_98;
    }

    public void setDAM_98(String DAM_98) {
        this.DAM_98 = DAM_98;
    }

    public String getARM_03() {
        return ARM_03;
    }

    public void setARM_03(String ARM_03) {
        this.ARM_03 = ARM_03;
    }
}
