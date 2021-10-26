package com.jd.fastc.ext.shop.rpc;

import com.jd.b2b.user.sdk.domain.PaginationResult;
import com.jd.fastc.ext.shop.utils.RpcResultUtils;
import com.yibin.b2b.user.core.query.sdk.dto.purchaserelation.PurchaseRelationQueryDto;
import com.yibin.b2b.user.core.query.sdk.dto.purchaserelation.RelationDetailDto;
import com.yibin.b2b.user.core.query.sdk.service.UserRelationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertTrue;

/***
 * @Auther: yejianjun
 * @Date: 2021/10/26
 * @Title:
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class ShopManageRpcTest {

    @Resource
    private UserRelationService userRelationService;

    @Test
    void queryUserRelationPage() {

        //todo 查询合作关系 联调
        String venderId= "";
        String pin = "";
        PurchaseRelationQueryDto purchaseRelationDto = new PurchaseRelationQueryDto();
        purchaseRelationDto.setTenant("buId:406");
        purchaseRelationDto.setVenderId(Long.parseLong(venderId));
        purchaseRelationDto.setBPin(pin);
        purchaseRelationDto.setPageNo(1);
        purchaseRelationDto.setPageSize(1);
        PaginationResult<RelationDetailDto> result = userRelationService.queryUserRelationPage(purchaseRelationDto, RpcResultUtils.buildYiBinClient());
        assertTrue(result.isSuccess());
    }
}