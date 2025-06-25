package com.myTMS.demo.dao.delivery;

import com.myTMS.demo.dao.typeconst.WeightCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


/**
 * Delivery 의 자식 클래스이며 해외직구타입의 배송을 지원함
 */
@Entity
@Getter
@Setter
@DiscriminatorValue("PFS")
public class OverseasDelivery extends Delivery {
    private Long customsFee;
    private String intlCarrier; // DHL, FedEx, UPS, etc.
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    private WeightCategory weightCategory;
}
