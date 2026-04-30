package com.hypo.appstoreprice.pojo.enums;

import cn.hutool.core.util.StrUtil;
import com.hypo.appstoreprice.common.BizException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * area enum
 * https://www.apple.com/choose-country-region/
 *
 * @author hypo
 * @date 2025-09-16
 */
@Getter
@AllArgsConstructor
public enum AreaEnum {

    USA("us", "美国", "$", "USD", StrUtil.COMMA, "In-App Purchases", "en-US"),
    CHINA("cn", "中国", "¥", "CNY", StrUtil.COMMA, "App 内购买项目", "zh-CN"),
    TAIWAN("tw", "台湾", "NT$", "TWD", StrUtil.COMMA, "App 內購買", "zh-TW"),
    HONGKONG("hk", "香港", "HK$", "HKD", StrUtil.COMMA, "App 內購買", "zh-HK"),
    JAPAN("jp", "日本", "¥", "JPY", StrUtil.COMMA, "アプリ内課金", "ja-JP"),
    KOREA("kr", "韩国", "₩", "KRW", StrUtil.COMMA, "앱 내 구입", "ko-KR"),
    TURKEY("tr", "土耳其", "₺", "TRY", StrUtil.DOT, "In-App Purchases", "tr-TR"),
    ARGENTINA("ar", "阿根廷", "$", "ARS", StrUtil.COMMA, "Compras dentro de la app", "es-AR"),
    NIGERIA("ng", "尼日利亚", "₦", "NGN", StrUtil.COMMA, "In-App Purchases", "en-NG"),
    INDIA("in", "印度", "₹", "INR", StrUtil.COMMA, "In-App Purchases", "en-IN"),
    PAKISTAN("pk", "巴基斯坦", "₨", "PKR", StrUtil.COMMA, "In-App Purchases", "en-PK"),
    BRAZIL("br", "巴西", "R$", "BRL", StrUtil.DOT, "Compras dentro do app", "pt-BR"),
    EGYPT("eg", "埃及", "E£", "EGP", StrUtil.COMMA, "In-App Purchases", "ar-EG-u-nu-latn");

    private final String code;
    private final String name;
    private final String currency;
    private final String currencyCode;
    private final String thousandsSeparator;
    private final String inAppPurchaseStr;
    private final String locale;

    public static AreaEnum getByCurrencyCode(String currencyCode) {
        for (AreaEnum areaEnum : values()) {
            if (StrUtil.equals(areaEnum.getCurrencyCode(), currencyCode)) {
                return areaEnum;
            }
        }
        throw new BizException("area not found, currencyCode: {}", currencyCode);
    }

    public static AreaEnum getByCode(String code) {
        for (AreaEnum areaEnum : values()) {
            if (StrUtil.equals(areaEnum.getCode(), code)) {
                return areaEnum;
            }
        }
        throw new BizException("area not found, code: {}", code);
    }
}
