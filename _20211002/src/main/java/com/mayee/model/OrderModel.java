package com.mayee.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderModel {
    // 主键
    private Integer id;
    // 订单流水号
    private String serialNumber;
    // 用户 id
    private String uuid;
    // 支付金额
    private BigDecimal payAmount;
}
