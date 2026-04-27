package com.hypo.appstoreprice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("price_snapshot")
public class PriceSnapshotEntity {
    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 应用唯一ID */
    private String appId;

    /** 地区代码 (如: CN, US) */
    private String areaCode;

    /** 货币代码 (如: CNY, USD) */
    private String currencyCode;

    /** 原始价格 */
    private Double price;

    /** 转换成人民币的价格 */
    private Double cnyPrice;

    /** 价格快照日期 */
    private String snapshotDate;

    /** 创建时间 */
    private String createdAt;
}
