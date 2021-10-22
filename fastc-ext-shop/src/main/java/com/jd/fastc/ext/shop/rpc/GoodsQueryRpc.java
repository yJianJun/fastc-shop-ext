package com.jd.fastc.ext.shop.rpc;

import com.jd.b2b.user.sdk.domain.PaginationResult;
import com.jd.pap.priceinfo.sdk.domain.request.PriceInfoRequest;
import com.jd.pap.priceinfo.sdk.domain.response.PriceInfoResponse;
import com.yibin.b2b.user.core.base.sdk.domain.ClientInfoReq;
import com.yibin.b2b.user.core.query.sdk.dto.userplat.DeliveryInfoDto;
import com.yibin.b2b.user.core.query.sdk.dto.userplat.req.DeliveryQueryDto;

import java.util.Map;

/***
 * @Auther: yejianjun
 * @Date: 2021/10/22
 * @Title:
 *
 */
public interface GoodsQueryRpc {

    PaginationResult<DeliveryInfoDto> queryByPage(DeliveryQueryDto var1);

    public String goodsSearch(Map<String, ?> uriVariables);

    PriceInfoResponse getRealPriceInfo(PriceInfoRequest var1);
}
