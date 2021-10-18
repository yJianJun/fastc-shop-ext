package com.jd.fastc.ext.shop.vo;

import com.jd.fastbe.framework.model.base.BaseExtModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/***
 * @Auther: yejianjun
 * @Date: 2021/10/18
 * @Title:
 *
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class AddressVO extends BaseExtModel {
    /**
     * 地址id
     */
    private Long addressId;

    /**
     * 联系人姓名
     */
    private String name;

    /**
     * 联系人电话
     */
    private String mobile;

    /**
     * 是否默认地址
     */
    private Boolean defAddress;

    /**
     * 完整地址（包含四级地址）
     */
    private String fullAddress;

    /**
     * 只是详细地址
     */
    private String addressDetail;

    /**
     * 省id
     */
    private Integer provinceId;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 市id
     */
    private Integer cityId;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 区id
     */
    private Integer districtId;

    /**
     * 区名称
     */
    private String districtName;

    /**
     * 镇id
     */
    private Integer townId;

    /**
     * 镇名称
     */
    private String townName;


}

