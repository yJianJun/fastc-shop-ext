package com.jd.fastc.ext.shop.rpc.impl;

import com.jd.b2b.user.sdk.domain.PaginationResult;
import com.jd.fastc.ext.shop.rpc.ShopManageRpc;
import com.jd.fastc.ext.shop.utils.RpcResultUtils;
import com.jd.m.mocker.client.ordinary.method.aop.JMock;
import com.jd.pop.vender.center.service.shop.ShopSafService;
import com.jd.pop.vender.center.service.shop.dto.BasicShopResult;
import com.jd.pop.vender.center.service.vbinfo.VenderBasicSafService;
import com.jd.pop.vender.center.service.vbinfo.dto.VenderBasicResult;
import com.yibin.b2b.user.core.base.sdk.domain.ClientInfoReq;
import com.yibin.b2b.user.core.query.sdk.dto.purchaserelation.PurchaseRelationQueryDto;
import com.yibin.b2b.user.core.query.sdk.dto.purchaserelation.RelationDetailDto;
import com.yibin.b2b.user.core.query.sdk.service.UserRelationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/***
 * @Auther: yejianjun
 * @Date: 2021/10/22
 * @Title:
 *
 */
@Service
public class ShopManageRpcImpl implements ShopManageRpc {

    @Resource
    private ShopSafService shopSafService;

    @Resource
    private VenderBasicSafService venderBasicSafService;

    @Resource
    private UserRelationService userRelationService;


    @Override
    @JMock
    public BasicShopResult getBasicShopByVenderId(Long venderId) {
        return shopSafService.getBasicShopByVenderId(venderId, null, 1);
    }

    @Override
    @JMock
    public VenderBasicResult getBasicVenderInfoByVenderId(Long venderId) {
        return venderBasicSafService.getBasicVenderInfoByVenderId(venderId, null, 1);
    }

    @Override
    @JMock
    public PaginationResult<RelationDetailDto> queryUserRelationPage(PurchaseRelationQueryDto var1) {
        return userRelationService.queryUserRelationPage(var1,RpcResultUtils.buildYiBinClient());
    }
}
