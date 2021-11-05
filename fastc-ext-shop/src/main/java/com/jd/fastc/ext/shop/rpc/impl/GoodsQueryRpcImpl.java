package com.jd.fastc.ext.shop.rpc.impl;

import com.jd.b2b.user.sdk.domain.B2bUserResult;
import com.jd.b2b.user.sdk.domain.PaginationResult;
import com.jd.fastc.ext.shop.rpc.GoodsQueryRpc;
import com.jd.fastc.ext.shop.utils.RpcResultUtils;
import com.jd.m.mocker.client.ordinary.method.aop.JMock;
import com.jd.pap.priceinfo.sdk.domain.request.PriceInfoRequest;
import com.jd.pap.priceinfo.sdk.domain.response.PriceInfoResponse;
import com.jd.pap.priceinfo.sdk.service.PriceInfoService;
import com.yibin.b2b.user.core.query.sdk.dto.userplat.DeliveryInfoDto;
import com.yibin.b2b.user.core.query.sdk.dto.userplat.req.DeliveryQueryDto;
import com.yibin.b2b.user.core.query.sdk.dto.userplat.req.GetDeliveryByIdReq;
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
        return deliveryInfoQueryService.queryByPage(queryDto, RpcResultUtils.buildYiBinClient());
    }

    @Override
    public B2bUserResult<DeliveryInfoDto> getByDeliveryId(GetDeliveryByIdReq queryDto) {
        return deliveryInfoQueryService.getByDeliveryId(queryDto, RpcResultUtils.buildYiBinClient());
    }

    @Override
    @JMock
    public String goodsSearch(Map<String, ?> uriVariables) {

        String url = "http://spblenderlht-search.searchpaaslht.svc.tpaas.n.jd.local?";
        String queryWithCategory = "key=ShopCategoryIDS,,{category};;" +
                "vender_id,,{venderId}" +
                "&page={currentPage}" +
                "&pagesize={pageSize}" +
                "&expression_key=buid,,406&sort_type=sort_default&charset=utf8&client=1634023454002";

        String queryNotWithCategory="key=vender_id,,{venderId}" +
                "&page={currentPage}" +
                "&pagesize={pageSize}" +
                "&expression_key=buid,,406&sort_type=sort_default&charset=utf8&client=1634023454002";

        String query;
        if (uriVariables.containsKey("category")){
            query = queryWithCategory;
        }else {
            query = queryNotWithCategory;
        }

        if (uriVariables.containsKey("address")) {
            query = query + "&area_ids={address}";
        }
        return restTemplate.getForObject(url + query,
                String.class, uriVariables);
    }

    @Override
    public PriceInfoResponse getRealPriceInfo(PriceInfoRequest priceInfoRequest) {
        return priceInfoService.getRealPriceInfo(priceInfoRequest);
    }

}
