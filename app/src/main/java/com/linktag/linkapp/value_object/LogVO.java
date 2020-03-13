package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class LogVO implements Serializable {

    public String LOG_ID;
    public String LOG_01;
    public String LOG_02;
    public String LOG_03;
    public String LOG_04;
    public String LOG_05;
    public String LOG_98;

    public String SP_NAME;


    public String getLOG_ID() {
        return LOG_ID;
    }

    public void setLOG_ID(String LOG_ID) {
        this.LOG_ID = LOG_ID;
    }

    public String getLOG_01() {
        return LOG_01;
    }

    public void setLOG_01(String LOG_01) {
        this.LOG_01 = LOG_01;
    }

    public String getLOG_02() {
        return LOG_02;
    }

    public void setLOG_02(String LOG_02) {
        this.LOG_02 = LOG_02;
    }

    public String getLOG_03() {
        return LOG_03;
    }

    public void setLOG_03(String LOG_03) {
        this.LOG_03 = LOG_03;
    }

    public String getLOG_04() {
        return LOG_04;
    }

    public void setLOG_04(String LOG_04) {
        this.LOG_04 = LOG_04;
    }

    public String getLOG_05() {
        return LOG_05;
    }

    public void setLOG_05(String LOG_05) {
        this.LOG_05 = LOG_05;
    }


    public String getLOG_98() {
        return LOG_98;
    }

    public void setLOG_98(String LOG_98) {
        this.LOG_98 = LOG_98;
    }

    public String getSP_NAME() {
        return SP_NAME;
    }

    public void setSP_NAME(String SP_NAME) {
        this.SP_NAME = SP_NAME;
    }
}
