package com.jd.fastc.ext.shop.handler;

import com.jd.b2b.user.sdk.enums.DeliveryTypeEnum;
import com.jd.fastc.ext.shop.vo.AddressVO;
import com.yibin.b2b.user.core.query.sdk.dto.userplat.DeliveryInfoDto;

import java.util.Objects;

/***
 * @Auther: yejianjun
 * @Date: 2021/10/18
 * @Title:
 *
 */

public class AddressConverter {

    public static AddressVO convert2AddressVO(DeliveryInfoDto deliveryInfoDto) {
        return new AddressVO()
                .setAddressId(deliveryInfoDto.getDeliveryId())
                .setName(deliveryInfoDto.getContact())
                .setMobile(deliveryInfoDto.getPhone())
                .setDefAddress(Objects.equals(deliveryInfoDto.getDeliveryType(), DeliveryTypeEnum.DEF_ADDR.getCode()))
                .setFullAddress(deliveryInfoDto.getFullAddress())
                .setAddressDetail(deliveryInfoDto.getAddress())
                .setProvinceId(deliveryInfoDto.getProvinceId())
                .setProvinceName(deliveryInfoDto.getProvinceName())
                .setCityId(deliveryInfoDto.getCityId())
                .setCityName(deliveryInfoDto.getCityName())
                .setDistrictId(deliveryInfoDto.getCountyId())
                .setDistrictName(deliveryInfoDto.getCountyName())
                .setTownId(deliveryInfoDto.getTownId())
                .setTownName(deliveryInfoDto.getTownName());
    }
}
