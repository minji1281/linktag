package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class IamVO implements Serializable {
    public String IAM_ID; // 컨테이너번호
    public String IAM_01; // 코드 일련번호
    public int IAM_02; // 알림번호
    public String IAM_03; // 명칭
    public String IAM_96; // 알림 지정일자
    public String IAM_97; // 작성자 아이디
    public String IAM_98; // 최종수정자 아이디


    public String getIAM_ID() {
        return IAM_ID;
    }

    public void setIAM_ID(String IAM_ID) {
        this.IAM_ID = IAM_ID;
    }

    public String getIAM_01() {
        return IAM_01;
    }

    public void setIAM_01(String IAM_01) {
        this.IAM_01 = IAM_01;
    }

    public int getIAM_02() {
        return IAM_02;
    }

    public void setIAM_02(int IAM_02) {
        this.IAM_02 = IAM_02;
    }

    public String getIAM_03() {
        return IAM_03;
    }

    public void setIAM_03(String IAM_03) {
        this.IAM_03 = IAM_03;
    }

    public String getIAM_96() {
        return IAM_96;
    }

    public void setIAM_96(String IAM_96) {
        this.IAM_96 = IAM_96;
    }

    public String getIAM_97() {
        return IAM_97;
    }

    public void setIAM_97(String IAM_97) {
        this.IAM_97 = IAM_97;
    }

    public String getIAM_98() {
        return IAM_98;
    }

    public void setIAM_98(String IAM_98) {
        this.IAM_98 = IAM_98;
    }
}
