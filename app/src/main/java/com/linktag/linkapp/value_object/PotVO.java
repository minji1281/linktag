package com.linktag.linkapp.value_object;

import java.io.Serializable;
import okhttp3.RequestBody;

public class PotVO implements Serializable {
    private static final long serialVersionUID = 4635771219693349629L;

    public String POT_ID;       // 컨테이너
    public String POT_01;       // 코드번호
    public String POT_02;       // 명칭
    //public Date POT_03;       // 최근 물주기 시각
    public int POT_04;       // 주기
    public String POT_05;       // 주기구분
    public String POT_06;       // 메모
    public String POT_81;       // 이미지
    public String POT_96;       // 알림시각
    public String POT_97;       // 작성자
    public String POT_98;       // 최종수정자
    //public Date POT_99;       // 최종수정일시

    public String POT_03_T;       // 최근 물주기 시각(YYYY-MM-DD HH:mm)
    public String DDAY;       // 다음 알림시간까지 D-day
    public String ARM_03;       // 해당 사용자의 알림여부
    public int ARM_04;       // 알림ID

    public boolean Validation;
//    public RequestBody POT_81_F;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getPOT_ID() {
        return POT_ID;
    }

    public void setPOT_ID(String POT_ID) {
        this.POT_ID = POT_ID;
    }

    public String getPOT_01() {
        return POT_01;
    }

    public void setPOT_01(String POT_01) {
        this.POT_01 = POT_01;
    }

    public String getPOT_02() {
        return POT_02;
    }

    public void setPOT_02(String POT_02) {
        this.POT_02 = POT_02;
    }

    public int getPOT_04() {
        return POT_04;
    }

    public void setPOT_04(int POT_04) {
        this.POT_04 = POT_04;
    }

    public String getPOT_05() {
        return POT_05;
    }

    public void setPOT_05(String POT_05) {
        this.POT_05 = POT_05;
    }

    public String getPOT_06() {
        return POT_06;
    }

    public void setPOT_06(String POT_06) {
        this.POT_06 = POT_06;
    }

    public String getPOT_81() {
        return POT_81;
    }

    public void setPOT_81(String POT_81) {
        this.POT_81 = POT_81;
    }

    public String getPOT_96() {
        return POT_96;
    }

    public void setPOT_96(String POT_96) {
        this.POT_96 = POT_96;
    }

    public String getPOT_97() {
        return POT_97;
    }

    public void setPOT_97(String POT_97) {
        this.POT_97 = POT_97;
    }

    public String getPOT_98() {
        return POT_98;
    }

    public void setPOT_98(String POT_98) {
        this.POT_98 = POT_98;
    }

    public String getPOT_03_T() {
        return POT_03_T;
    }

    public void setPOT_03_T(String POT_03_T) {
        this.POT_03_T = POT_03_T;
    }

    public String getDDAY() {
        return DDAY;
    }

    public void setDDAY(String DDAY) {
        this.DDAY = DDAY;
    }

    public String getARM_03() {
        return ARM_03;
    }

    public void setARM_03(String ARM_03) {
        this.ARM_03 = ARM_03;
    }

    public int getARM_04() {
        return ARM_04;
    }

    public void setARM_04(int ARM_04) {
        this.ARM_04 = ARM_04;
    }

    public boolean isValidation() {
        return Validation;
    }

    public void setValidation(boolean validation) {
        Validation = validation;
    }
}
