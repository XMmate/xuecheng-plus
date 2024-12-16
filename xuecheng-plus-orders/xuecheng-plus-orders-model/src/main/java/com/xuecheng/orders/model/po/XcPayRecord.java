package com.xuecheng.orders.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 支付记录表
 * </p>
 *
 * @author author
 * @since 2024-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("xc_pay_record")
public class XcPayRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 本系统支付交易号
     */
    private Long payNo;

    /**
     * 第三方支付交易流水号
     */
    private String outPayNo;

    /**
     * 第三方支付渠道编号
     */
    private String outPayChannel;

    /**
     * 商品订单号
     */
    private Long orderId;

    /**
     * 订单名称
     */
    private String orderName;

    /**
     * 订单总价单位元
     */
    private Float totalPrice;

    /**
     * 币种CNY
     */
    private String currency;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 支付状态
     */
    private String status;

    /**
     * 支付成功时间
     */
    private LocalDateTime paySuccessTime;

    /**
     * 用户id
     */
    private String userId;


}
