package com.jd.fastc.ext.shop.rpc.impl;

import com.jd.b2b.user.sdk.domain.PaginationResult;
import com.jd.fastc.ext.shop.rpc.GoodsQueryRpc;
import com.jd.fastc.ext.shop.utils.RpcResultUtils;
import com.jd.pap.priceinfo.sdk.domain.request.PriceInfoRequest;
import com.jd.pap.priceinfo.sdk.domain.response.PriceInfoResponse;
import com.jd.pap.priceinfo.sdk.service.PriceInfoService;
import com.yibin.b2b.user.core.query.sdk.dto.userplat.DeliveryInfoDto;
import com.yibin.b2b.user.core.query.sdk.dto.userplat.req.DeliveryQueryDto;
import com.yibin.b2b.user.core.query.sdk.service.DeliveryInfoQueryService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

/***
 * @Auther: yejianjun
 * @Date: 2021/10/22
 * @Title:
 *
 */
@Service
public class GoodsQueryRpcImpl implements GoodsQueryRpc {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private PriceInfoService priceInfoService;

    @Resource
    private DeliveryInfoQueryService deliveryInfoQueryService;


    @Override
    public PaginationResult<DeliveryInfoDto> queryByPage(DeliveryQueryDto queryDto) {
        return deliveryInfoQueryService.queryByPage(queryDto,RpcResultUtils.buildYiBinClient());
    }

    @Override
    public String goodsSearch(Map<String, ?> uriVariables) {
        return restTemplate.getForObject("http://spblenderlht-search.searchpaaslht.svc.tpaas.n.jd.local?" +
                        "key=ShopCategoryIDS,,{category};;" +
                        "vender_id,,{venderId}" +
                        "&page={currentPage}" +
                        "&pagesize={pageSize}" +
                        "&area_ids={address}" +
                        "&expression_key=buid,,406&sort_type=sort_default&charset=utf8&client=1634023454002",
                String.class, uriVariables);
    }

    @Override
    public PriceInfoResponse getRealPriceInfo(PriceInfoRequest priceInfoRequest) {
        return priceInfoService.getRealPriceInfo(priceInfoRequest);
    }

}
