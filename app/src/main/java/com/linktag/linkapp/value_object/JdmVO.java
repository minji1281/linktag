package com.linktag.linkapp.value_object;

import android.content.Intent;

import com.linktag.linkapp.ui.alarm_service.Alarm_Receiver;

import java.io.Serializable;

public class JdmVO implements Serializable  {

    public String JDM_ID; // 컨테이너번호
    public String JDM_01; // 코드 일련번호
    public String JDM_02; // 명칭
    public String JDM_03; // 메모
    public String JDM_04; // 담금 일자
    public String JDM_05;    // 장독크기
    public String JDM_06;    // 청소주기일
    public String JDM_07;    // 청소주기일 단위
    public String JDM_08;    // 최근 청소일
    public String JDM_96; // 알림 지정일자
    public String JDM_97; // 작성자 아이디
    public String JDM_98; // 최종수정자 아이디

    public String ARM_ID;
    public String ARM_01;
    public String ARM_02;
    public String ARM_03;

    public int Notify_Id;
    public String CalDateTime;
    public String ContentTitle;
    public String ContentText;
    public String ClassName;

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

    public int ARM_04;

    public int getARM_04() {
        return ARM_04;
    }

    public void setARM_04(int ARM_04) {
        this.ARM_04 = ARM_04;
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

    public String getARM_98() {
        return ARM_98;
    }

    public void setARM_98(String ARM_98) {
        this.ARM_98 = ARM_98;
    }

    public String ARM_98;



    public String getJDM_ID() {
        return JDM_ID;
    }

    public void setJDM_ID(String JDM_ID) {
        this.JDM_ID = JDM_ID;
    }

    public String getJDM_01() {
        return JDM_01;
    }

    public void setJDM_01(String JDM_01) {
        this.JDM_01 = JDM_01;
    }

    public String getJDM_02() {
        return JDM_02;
    }

    public void setJDM_02(String JDM_02) {
        this.JDM_02 = JDM_02;
    }

    public String getJDM_03() {
        return JDM_03;
    }

    public void setJDM_03(String JDM_03) {
        this.JDM_03 = JDM_03;
    }

    public String getJDM_04() {
        return JDM_04;
    }

    public void setJDM_04(String JDM_04) {
        this.JDM_04 = JDM_04;
    }

    public String getJDM_05() {
        return JDM_05;
    }

    public void setJDM_05(String JDM_05) {
        this.JDM_05 = JDM_05;
    }

    public String getJDM_06() {
        return JDM_06;
    }

    public void setJDM_06(String JDM_06) {
        this.JDM_06 = JDM_06;
    }

    public String getJDM_07() {
        return JDM_07;
    }

    public void setJDM_07(String JDM_07) {
        this.JDM_07 = JDM_07;
    }

    public String getJDM_08() {
        return JDM_08;
    }

    public void setJDM_08(String JDM_08) {
        this.JDM_08 = JDM_08;
    }

    public String getJDM_96() {
        return JDM_96;
    }

    public void setJDM_96(String JDM_96) {
        this.JDM_96 = JDM_96;
    }

    public String getJDM_97() {
        return JDM_97;
    }

    public void setJDM_97(String JDM_97) {
        this.JDM_97 = JDM_97;
    }

    public String getJDM_98() {
        return JDM_98;
    }

    public void setJDM_98(String JDM_98) {
        this.JDM_98 = JDM_98;
    }
}
