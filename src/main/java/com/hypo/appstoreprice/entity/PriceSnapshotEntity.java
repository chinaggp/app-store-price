package com.hypo.appstoreprice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("price_snapshot")
public class PriceSnapshotEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String appId;
    private String areaCode;
    private String currencyCode;
    private Double price;
    private Double cnyPrice;
    private String snapshotDate;
    private String createdAt;
}
