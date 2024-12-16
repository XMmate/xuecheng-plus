package com.xuecheng.media.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class QueryFileParamsDto {

   public String filename;
   public String type;
   public String auditStatus;

}
