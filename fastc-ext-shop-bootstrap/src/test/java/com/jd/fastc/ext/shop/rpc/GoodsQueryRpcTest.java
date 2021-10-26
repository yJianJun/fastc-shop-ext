package com.jd.fastc.ext.shop.rpc;

import com.jd.b2b.user.sdk.domain.PaginationResult;
import com.jd.b2b.user.sdk.enums.DeliveryBizType;
import com.jd.b2b.user.sdk.enums.DeliveryTypeEnum;
import com.jd.fastc.ext.shop.utils.RpcResultUtils;
import com.yibin.b2b.user.core.query.sdk.dto.userplat.DeliveryInfoDto;
import com.yibin.b2b.user.core.query.sdk.dto.userplat.req.DeliveryQueryDto;
import com.yibin.b2b.user.core.query.sdk.service.DeliveryInfoQueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/***
 * @Auther: yejianjun
 * @Date: 2021/10/26
 * @Title:
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsQueryRpcTest {

    @Resource
    private DeliveryInfoQueryService deliveryInfoQueryService;

    @Test
    public void queryByPage() {

        //todo 查询收货地址 联调
        String operator = "";
        DeliveryQueryDto deliveryQueryDto = new DeliveryQueryDto();
        deliveryQueryDto.setBPin(operator);
        deliveryQueryDto.setBizType(DeliveryBizType.DELIVERY_GOODS.getCode());
        deliveryQueryDto.setDeliveryType(DeliveryTypeEnum.DEF_ADDR.getCode());
        deliveryQueryDto.setPageNo(1);
        deliveryQueryDto.setPageSize(1);
        PaginationResult<DeliveryInfoDto> result = deliveryInfoQueryService.queryByPage(deliveryQueryDto, RpcResultUtils.buildYiBinClient());
        assertTrue(result.isSuccess());
    }
}