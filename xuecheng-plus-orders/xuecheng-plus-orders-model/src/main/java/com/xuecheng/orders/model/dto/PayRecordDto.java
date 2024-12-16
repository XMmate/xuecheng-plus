package com.xuecheng.orders.model.dto;

import com.xuecheng.orders.model.po.XcPayRecord;
import lombok.Data;

@Data
public class PayRecordDto extends XcPayRecord {
    //二维码
    private String qrcode;

}
