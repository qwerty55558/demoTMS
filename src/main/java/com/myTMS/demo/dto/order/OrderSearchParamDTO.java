package com.myTMS.demo.dto.order;

import com.myTMS.demo.dao.typeconst.Keyword;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class OrderSearchParamDTO {
    private String value;
    private Keyword keyword;

    public OrderSearchParamDTO() {
    }

    public OrderSearchParamDTO(String value, Keyword keyword) {
        this.value = value;
        this.keyword = keyword;
    }
}
