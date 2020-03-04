package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class TrpVO implements Serializable {


    public String TRP_ID; // 컨테이너 번호
    public String TRP_01; // 코드 일련번호
    public String TRP_02; // 명칭
    public String TRP_03; // 메모
    public String TRP_04; // 복용빈도
    public String TRP_05; // 복약 일 횟수
    public String TRP_06; // 식전후 구분
    public String TRP_07; // 대상자
    public String TRP_97; // 작성자 아이디
    public String TRP_98; // 최종수정자 아이디

    public String TRD_ID;  //컨테이너번호
    public String TRD_01;  //코드 일련번호
    public String TRD_02;  //일련번호
    public String TRD_96;  //알림시각 (알림지정일자)
    public String TRD_98;  // 최종수정자 아이디

    public String ARM_ID;
    public String ARM_01;
    public String ARM_02;

    public String ARM_03;
    public int ARM_04;

    public int Notify_Id;
    public String CalDateTime;
    public String ContentTitle;
    public String ContentText;

    public String getTRP_ID() {
        return TRP_ID;
    }

    public void setTRP_ID(String TRP_ID) {
        this.TRP_ID = TRP_ID;
    }

    public String getTRP_01() {
        return TRP_01;
    }

    public void setTRP_01(String TRP_01) {
        this.TRP_01 = TRP_01;
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

    public String getTRP_05() {
        return TRP_05;
    }

    public void setTRP_05(String TRP_05) {
        this.TRP_05 = TRP_05;
    }

    public String getTRP_06() {
        return TRP_06;
    }

    public void setTRP_06(String TRP_06) {
        this.TRP_06 = TRP_06;
    }

    public String getTRP_07() {
        return TRP_07;
    }

    public void setTRP_07(String TRP_07) {
        this.TRP_07 = TRP_07;
    }

    public String getTRP_97() {
        return TRP_97;
    }

    public void setTRP_97(String TRP_97) {
        this.TRP_97 = TRP_97;
    }

    public String getTRP_98() {
        return TRP_98;
    }

    public void setTRP_98(String TRP_98) {
        this.TRP_98 = TRP_98;
    }


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

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public String ClassName;

}
