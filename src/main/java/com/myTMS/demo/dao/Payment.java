package com.myTMS.demo.dao;

import com.myTMS.demo.dao.abstractclass.createdAt;
import com.myTMS.demo.dao.typeconst.PaymentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 결제 수단 데이터를 의미하는 객체
 */
@Getter
@Setter
@Entity
public class Payment extends createdAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(mappedBy = "payment")
    private Orders order;
    private Long userId;
    @NotNull(message = "{NotEmpty.payment.type}")
    @Enumerated(EnumType.STRING)
    private PaymentType type;
    @NotEmpty(message = "{NotEmpty.payment.number}")
    @Pattern(regexp = "^\\d{16}$", message = "{Pattern.payment.number}")
    private String number;
    @NotEmpty(message = "{NotEmpty.payment.CVV}")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "{Pattern.payment.CVV}")
    @Size(min = 3 , max = 4, message = "{Size.payment.CVV}")
    private String CVV;
    @NotEmpty(message = "{NotEmpty.payment.expireDate}")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/([0-9]{2})$" , message = "{Pattern.payment.expireDate}")
    private String expireDate;

    public Payment(PaymentBuilder pb) {
        this.userId = pb.userId;
        this.type = pb.type;
        this.number =  pb.number;
        this.CVV = pb.CVV;
        this.expireDate = pb.expireDate;
    }

    public Payment() {

    }

    @Override
    public String toString() {
        return "Payment{" +
                "userId=" + userId +
                ", type=" + type +
                ", number='" + number + '\'' +
                ", CVV=" + CVV +
                ", expireDate='" + expireDate + '\'' +
                '}';
    }

    public static class PaymentBuilder {
        private Long userId;
        private PaymentType type;
        private String number;
        private String CVV;
        private String expireDate;

        public PaymentBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }



        public PaymentBuilder type(PaymentType type) {
            this.type = type;
            return this;
        }
        public PaymentBuilder number(String number) {
            this.number = number;
            return this;
        }

        public PaymentBuilder CVV(String CVV) {
            this.CVV = CVV;
            return this;
        }

        public PaymentBuilder expireDate(String expireDate) {
            this.expireDate = expireDate;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }
}
