package com.lpy.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单消息实体
 * @author demo
 * @date 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 商品ID
     */
    private String productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 订单数量
     */
    private Integer quantity;

    /**
     * 订单状态
     * 1-待支付 2-已支付 3-已发货 4-已收货 5-已完成 6-已取消
     */
    private Integer status;

    /**
     * 订单状态描述
     */
    private String statusDesc;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 获取状态描述
     */
    public String getStatusDesc() {
        if (status == null) {
            return "未知状态";
        }
        switch (status) {
            case 1:
                return "待支付";
            case 2:
                return "已支付";
            case 3:
                return "已发货";
            case 4:
                return "已收货";
            case 5:
                return "已完成";
            case 6:
                return "已取消";
            default:
                return "未知状态";
        }
    }
}