package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class PcmVO implements Serializable {


    public String PCM_ID; // 컨테이너 번호
    public String PCM_01; // 코드 일련번호
    public String PCM_02; // 명칭
    public String PCM_03; // 메모
    public String PCM_96; // 알림 지정일자
    public String PCM_97; // 작성자 아이디
    public String PCM_98; // 최종수정자 아이디

    public String PCD_ID;  //컨테이너번호
    public String PCD_01;  //코드 일련번호
    public String PCD_02;  //일련번호
    public String PCD_03;  //구분 (H/W, S/W)
    public String PCD_04;  //분류 (CPU, RAM ...)
    public String PCD_05;  //분류 세부설명
    public String PCD_98;  // 최종수정자 아이디

    public String ARM_ID;
    public String ARM_01;
    public String ARM_02;

    public String ARM_03;
    public int ARM_04;

    public int Notify_Id;
    public String CalDateTime;
    public String ContentTitle;
    public String ContentText;


    public String getPCM_ID() {
        return PCM_ID;
    }

    public void setPCM_ID(String PCM_ID) {
        this.PCM_ID = PCM_ID;
    }

    public String getPCM_01() {
        return PCM_01;
    }

    public void setPCM_01(String PCM_01) {
        this.PCM_01 = PCM_01;
    }

    public String getPCM_02() {
        return PCM_02;
    }

    public void setPCM_02(String PCM_02) {
        this.PCM_02 = PCM_02;
    }

    public String getPCM_03() {
        return PCM_03;
    }

    public void setPCM_03(String PCM_03) {
        this.PCM_03 = PCM_03;
    }

    public String getPCM_96() {
        return PCM_96;
    }

    public void setPCM_96(String PCM_96) {
        this.PCM_96 = PCM_96;
    }

    public String getPCM_97() {
        return PCM_97;
    }

    public void setPCM_97(String PCM_97) {
        this.PCM_97 = PCM_97;
    }

    public String getPCM_98() {
        return PCM_98;
    }

    public void setPCM_98(String PCM_98) {
        this.PCM_98 = PCM_98;
    }

    public String getPCD_ID() {
        return PCD_ID;
    }

    public void setPCD_ID(String PCD_ID) {
        this.PCD_ID = PCD_ID;
    }

    public String getPCD_01() {
        return PCD_01;
    }

    public void setPCD_01(String PCD_01) {
        this.PCD_01 = PCD_01;
    }

    public String getPCD_02() {
        return PCD_02;
    }

    public void setPCD_02(String PCD_02) {
        this.PCD_02 = PCD_02;
    }

    public String getPCD_03() {
        return PCD_03;
    }

    public void setPCD_03(String PCD_03) {
        this.PCD_03 = PCD_03;
    }

    public String getPCD_04() {
        return PCD_04;
    }

    public void setPCD_04(String PCD_04) {
        this.PCD_04 = PCD_04;
    }

    public String getPCD_05() {
        return PCD_05;
    }

    public void setPCD_05(String PCD_05) {
        this.PCD_05 = PCD_05;
    }

    public String getPCD_98() {
        return PCD_98;
    }

    public void setPCD_98(String PCD_98) {
        this.PCD_98 = PCD_98;
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
