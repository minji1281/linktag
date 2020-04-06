package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class RMD_VO implements Serializable {
    private static final long serialVersionUID = 4635771219693349629L;

    public String RMD_ID;       // 컨테이너
    public String RMD_01;       // Master일련번호(RMM_01)
    public String RMD_02;       // 일련번호(스캔코드)
    public String RMD_03;       // 명칭
    public String RMD_04;       // 장비
    public String RMD_97;       // 작성자
    public String RMD_98;       // 최종수정자
    //public Date RMD_99;       // 최종수정일시

    public boolean Validation;
    public String RMM_03;
    public String RMM_04;
    public String RMM_98;

}
