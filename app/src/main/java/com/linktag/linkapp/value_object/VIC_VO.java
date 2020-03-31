package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class VIC_VO implements Serializable {
    private static final long serialVersionUID = 4635771219693349629L;

    public String VIC_ID;       // 컨테이너
    public String VIC_01;       // 투표코드(VOT_01)
    public String VIC_02;       // 내역코드(VIT_02)
    public String VIC_03;       // 사용자
    public String VIC_98;       // 최종수정자
    //public Date VIC_99;       // 최종수정일시

    public boolean Validation;
}
