package com.jd.fastc.ext.shop.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ShopStatus implements BaseEnum {

    /**
     * 0:停用（合同到期后会停用，运营可以手动停用，停用的店铺可以再启用）,
     *
     * 1:启用(店铺有效状态),
     *
     * 5:待启用（刚入驻未完成开店任务，为待启用）
     */
    Disable(1), //禁用
    Enable(2), //启用
    Await(3); //待启用


    @JsonValue
    private int code;

    ShopStatus(int code) {
        this.code = code;
    }


    @JsonCreator
    public static ShopStatus fromText(String text) {

        int code = Integer.parseInt(text);
        for (ShopStatus value : ShopStatus.values()) {
            if (value.getValue() == code) {

                return value;
            }
        }
        throw new RuntimeException("unknown code");
    }


    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code + "";
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
