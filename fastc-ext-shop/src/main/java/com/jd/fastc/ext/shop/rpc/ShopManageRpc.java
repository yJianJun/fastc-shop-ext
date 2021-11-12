package com.jd.fastc.ext.shop.rpc;

import com.jd.b2b.user.sdk.domain.B2bUserResult;
import com.jd.b2b.user.sdk.domain.PaginationResult;
import com.jd.pop.vender.center.service.shop.dto.BasicShopResult;
import com.jd.pop.vender.center.service.vbinfo.dto.VenderBasicResult;
import com.yibin.b2b.user.core.base.sdk.domain.ClientInfoReq;
import com.yibin.b2b.user.core.query.sdk.dto.purchaserelation.GetPurchaseRelReq;
import com.yibin.b2b.user.core.query.sdk.dto.purchaserelation.PurchaseRelationQueryDto;
import com.yibin.b2b.user.core.query.sdk.dto.purchaserelation.RelationDetailDto;

import java.util.List;

/***
 * @Auther: yejianjun
 * @Date: 2021/10/22
 * @Title:
 *
 */
public interface ShopManageRpc {

    BasicShopResult getBasicShopByVenderId(Long var1);

    VenderBasicResult getBasicVenderInfoByVenderId(Long var1);

    B2bUserResult<List<RelationDetailDto>> getPurchaseRelByPurCode(GetPurchaseRelReq var1);
}
