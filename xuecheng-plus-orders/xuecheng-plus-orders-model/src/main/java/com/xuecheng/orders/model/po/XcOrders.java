package com.xuecheng.orders.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *订单表
 * @author author
 * @since 2024-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("xc_orders")
public class XcOrders implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 总价
     */
    private Float totalPrice;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 交易状态
     */
    private String status;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 订单名称
     */
    private String orderName;

    /**
     * 订单描述
     */
    private String orderDescrip;

    /**
     * 订单明细json
     */
    private String orderDetail;

    /**
     * 外部系统业务id
     */
    private String outBusinessId;


}
