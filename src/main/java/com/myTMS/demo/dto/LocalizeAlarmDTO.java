package com.myTMS.demo.dto;

import com.myTMS.demo.dao.typeconst.MessageType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalizeAlarmDTO {
    private String msData;
    private String[] params;
    private MessageType messageType;

    public LocalizeAlarmDTO(String msData, String[] params) {
        this.msData = msData;
        this.params = params;
    }

    public LocalizeAlarmDTO(String msData) {
        this.msData = msData;
        this.params = null;
        this.messageType = null;
    }
}
