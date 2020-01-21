package com.linktag.linkapp.value_object;

import java.io.Serializable;
import java.util.Date;

public class CfbVO implements Serializable {
    private static final long serialVersionUID = 5791423340531816319L;

    public String CFB_ID;       // 컨테이너
    public String CFB_01;       // 코드
    public String CFB_02;       // 이름
    public String CFB_03;       // 비밀번호
    public int CFB_04;
    public String CFB_05;
    public String CFB_06;
    public float CFB_07;
    public float CFB_08;
    public String CFB_98;


    public boolean Validation;
    public String ErrorCode;
}
