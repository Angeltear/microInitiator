package com.angeltear.microinitiator.Model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaymentRequest {
    private long clientId;
    private long paymentId;
    private double paymentSum;

}
