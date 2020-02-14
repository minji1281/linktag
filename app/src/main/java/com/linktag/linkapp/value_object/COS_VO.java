package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class COS_VO implements Serializable {
    private static final long serialVersionUID = 4635771219693349629L;

    public String COS_ID;       // 컨테이너
    public String COS_01;       // 코드번호
    public String COS_02;       // 명칭
    public String COS_03;       // 메모
    public String COS_97;       // 작성자
    public String COS_98;       // 최종수정자
    //public Date COS_99;       // 최종수정일시

    public String ARM_03;       //알람여부

    public boolean Validation;

}
