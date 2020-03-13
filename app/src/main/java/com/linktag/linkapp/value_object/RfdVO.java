package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class RfdVO implements Serializable {

    public String GUBUN;
    public String RFD_ID;  //컨테이너번호
    public String RFD_01;  //코드 일련번호
    public String RFD_02;  //일련번호
    public String RFD_03;  //명칭
    public String RFD_04;  //메모
    public String RFD_05;  //일자 (유통기한 제조)
    public String RFD_06;  //보관위치
    public String RFD_07;  //사용종료일자
    public String RFD_96;  //알림 지정일자
    public String RFD_98;  // 최종수정자 아이디

    public String ARM_ID;
    public String ARM_01;
    public String ARM_02;

    public String ARM_03;
    public int ARM_04;

    public int Notify_Id;
    public String CalDateTime;
    public String ContentTitle;
    public String ContentText;

    public String getGUBUN() {
        return GUBUN;
    }

    public void setGUBUN(String GUBUN) {
        this.GUBUN = GUBUN;
    }

    public String getRFD_ID() {
        return RFD_ID;
    }

    public void setRFD_ID(String RFD_ID) {
        this.RFD_ID = RFD_ID;
    }

    public String getRFD_01() {
        return RFD_01;
    }

    public void setRFD_01(String RFD_01) {
        this.RFD_01 = RFD_01;
    }

    public String getRFD_02() {
        return RFD_02;
    }

    public void setRFD_02(String RFD_02) {
        this.RFD_02 = RFD_02;
    }

    public String getRFD_03() {
        return RFD_03;
    }

    public void setRFD_03(String RFD_03) {
        this.RFD_03 = RFD_03;
    }

    public String getRFD_04() {
        return RFD_04;
    }

    public void setRFD_04(String RFD_04) {
        this.RFD_04 = RFD_04;
    }

    public String getRFD_05() {
        return RFD_05;
    }

    public void setRFD_05(String RFD_05) {
        this.RFD_05 = RFD_05;
    }

    public String getRFD_06() {
        return RFD_06;
    }

    public void setRFD_06(String RFD_06) {
        this.RFD_06 = RFD_06;
    }

    public String getRFD_07() {
        return RFD_07;
    }

    public void setRFD_07(String RFD_07) {
        this.RFD_07 = RFD_07;
    }

    public String getRFD_96() {
        return RFD_96;
    }

    public void setRFD_96(String RFD_96) {
        this.RFD_96 = RFD_96;
    }

    public String getRFD_98() {
        return RFD_98;
    }

    public void setRFD_98(String RFD_98) {
        this.RFD_98 = RFD_98;
    }

    public String getARM_ID() {
        return ARM_ID;
    }

    public void setARM_ID(String ARM_ID) {
        this.ARM_ID = ARM_ID;
    }

    public String getARM_01() {
        return ARM_01;
    }

    public void setARM_01(String ARM_01) {
        this.ARM_01 = ARM_01;
    }

    public String getARM_02() {
        return ARM_02;
    }

    public void setARM_02(String ARM_02) {
        this.ARM_02 = ARM_02;
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

    public int getNotify_Id() {
        return Notify_Id;
    }

    public void setNotify_Id(int notify_Id) {
        Notify_Id = notify_Id;
    }

    public String getCalDateTime() {
        return CalDateTime;
    }

    public void setCalDateTime(String calDateTime) {
        CalDateTime = calDateTime;
    }

    public String getContentTitle() {
        return ContentTitle;
    }

    public void setContentTitle(String contentTitle) {
        ContentTitle = contentTitle;
    }

    public String getContentText() {
        return ContentText;
    }

    public void setContentText(String contentText) {
        ContentText = contentText;
    }
}
