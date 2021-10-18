package com.jd.fastc.ext.shop.ext;

import com.google.common.collect.Lists;
import com.jd.b2b.user.sdk.domain.PaginationResult;
import com.jd.b2b.user.sdk.enums.DeliveryBizType;
import com.jd.fastbe.framework.client.exception.RpcException;
import com.jd.fastbe.framework.model.base.DomainParam;
import com.jd.fastbe.framework.model.base.DomainResult;
import com.jd.fastbe.framework.model.base.PageVO;
import com.jd.fastc.ext.shop.handler.AddressConverter;
import com.jd.fastc.ext.shop.vo.AddressVO;
import com.jd.fastc.shop.ext.sdk.manage.GoodsQueryExt;
import com.jd.fastc.shop.ext.sdk.manage.vo.VenderSkuQueryVO;
import com.jd.fastc.shop.ext.sdk.manage.vo.VenderSkuVO;
import com.jd.m.mocker.client.ordinary.method.aop.JMock;
import com.jd.pap.priceinfo.sdk.domain.request.PriceInfoRequest;
import com.jd.pap.priceinfo.sdk.domain.response.PriceInfoResponse;
import com.jd.pap.priceinfo.sdk.domain.response.PriceResult;
import com.jd.pap.priceinfo.sdk.service.PriceInfoService;
import com.yibin.b2b.user.core.query.sdk.domain.UserCbiPageQuery;
import com.yibin.b2b.user.core.query.sdk.dto.userplat.DeliveryInfoDto;
import com.yibin.b2b.user.core.query.sdk.dto.userplat.req.DeliveryQueryDto;
import com.yibin.b2b.user.core.query.sdk.service.DeliveryInfoQueryService;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/***
 * @Auther: yejianjun
 * @Date: 2021/10/13
 * @Title:
 *
 */
@Component
public class GoodsQueryExtImpl implements GoodsQueryExt {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PriceInfoService priceInfoService;

    @Resource
    private DeliveryInfoQueryService deliveryInfoQueryService;

    @Override
    @JMock
    public DomainResult<PageVO<VenderSkuVO>> getPage(DomainParam<VenderSkuQueryVO> param) {

        VenderSkuQueryVO vo = param.getData();
        Map<String, String> map = new HashMap<>();
        map.put("category", vo.getCategory());
        map.put("venderId", vo.getVenderId());
        map.put("currentPage", vo.getCurrentPage() + "");
        map.put("pageSize", vo.getPageSize() + "");
        map.put("address",queryAddress(param.getOperator()));
        String json = restTemplate.getForObject("http://spblenderlht-search.searchpaaslht.svc.tpaas.n.jd.local?" +
                "key=ShopCategoryIDS,,{category};;" +
                "vender_id,,{venderId}" +
                "&page={currentPage}" + "" +
                "&pagesize={pageSize}" +
                "&expression_key=buid,,405&sort_type=sort_default&charset=utf8&client=1634023454001"+
                "&area_ids={address}"
                , String.class, map);
        PageVO<VenderSkuVO> pageVO = null;
        List<VenderSkuVO> list = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray paragraph = jsonObject.getJSONArray("Paragraph");
            JSONObject head = jsonObject.getJSONObject("Head");
            JSONObject summary = head.getJSONObject("Summary");
            String total = summary.getString("ResultCount");

            pageVO = new PageVO<>();
            pageVO.setCurrentPage(vo.getCurrentPage());
            pageVO.setPageSize(vo.getPageSize());
            pageVO.setTotal(Long.valueOf(total));
            list = new ArrayList<>();
            for (int i = 0; i < paragraph.length(); i++) {
                JSONObject object = paragraph.getJSONObject(i);
                JSONObject content = object.getJSONObject("Content");
                VenderSkuVO skuVO = new VenderSkuVO();
                skuVO.setSkuId(content.getString("wareid"));
                skuVO.setSkuName(content.getString("warename"));
                skuVO.setSkuImageUrl(content.getString("imageurl"));
                skuVO.setSkuStock(content.getInt("wareInStock") == 1);
                list.add(skuVO);
            }
        } catch (JSONException e) {
            return DomainResult.fail("-1005", "HTTP调用错误");
        }

        HashSet<Integer> hashSet = new HashSet<>();
        hashSet.add(1);
        PriceInfoRequest priceInfoRequest = new PriceInfoRequest();
        priceInfoRequest.setPriceInfos(hashSet);
        priceInfoRequest.setSkuIds(list.stream().map(VenderSkuVO::getSkuId).collect(Collectors.toSet()));
        priceInfoRequest.setSite(405);
        priceInfoRequest.setChannel(1);
        //todo: 需要藏经阁备案
        priceInfoRequest.setSource("jshop-act");
        PriceInfoResponse realPriceInfo = priceInfoService.getRealPriceInfo(priceInfoRequest);
        if (realPriceInfo.isSuccess()) {
            Map<String, PriceResult> priceMap = realPriceInfo.getPriceMap();

            for (VenderSkuVO skuVO : list) {
                PriceResult priceResult = priceMap.get(skuVO.getSkuId());
                String jdPrice = priceResult.getJdPrice();
                skuVO.setSkuPrice(jdPrice);
            }
            pageVO.setData(list);
            return DomainResult.success(pageVO);
        }
        return DomainResult.fail("-1003", "RPC调用错误");
    }

    public PaginationResult<DeliveryInfoDto> getAddressPageByBPin(DeliveryQueryDto queryDto, UserCbiPageQuery pageQuery) {
        PaginationResult<DeliveryInfoDto> result = deliveryInfoQueryService.queryByPage(queryDto, pageQuery);
        if (result == null) {
            throw new RpcException("RPC 返回空");
        }

        if (!result.isSuccess()) {
            throw new RpcException(String.format("接口返回异常code:%s, message:%s", result.getReturnCode(), result.getMessage()));
        }

        return result;
    }

    public String queryAddress(String operator) {

        DeliveryQueryDto deliveryQueryDto = new DeliveryQueryDto();
        deliveryQueryDto.setBPin(operator);
        deliveryQueryDto.setBizType(DeliveryBizType.DELIVERY_GOODS.getCode());
        UserCbiPageQuery pageQuery = new UserCbiPageQuery();
        pageQuery.setPageNo(1);
        pageQuery.setPageSize(1);
        PaginationResult<DeliveryInfoDto> resp = getAddressPageByBPin(deliveryQueryDto, pageQuery);
        PageVO<AddressVO> addressVOPageVO = convert2PageVO(resp);
        Optional<AddressVO> first = addressVOPageVO.getData().stream().findFirst();
        if (first.isPresent()){
            AddressVO addressVO = first.get();
            Integer provinceId = addressVO.getProvinceId();
            Integer cityId = addressVO.getCityId();
            Integer districtId = addressVO.getDistrictId();
            Integer townId = addressVO.getTownId();
            return provinceId+","+cityId+","+districtId+","+townId;
        }
        return null;
    }

    private PageVO<AddressVO> convert2PageVO(PaginationResult<DeliveryInfoDto> source) {
        if (Objects.isNull(source)) {
            return null;
        }
        PageVO<AddressVO> result = new PageVO<>();
        result.setTotal(source.getTotalCount());
        result.setCurrentPage(source.getCurrentPageNo());
        result.setPageSize(source.getPageSize());
        List<AddressVO> addressVOS = Lists.newArrayList();
        ListUtils.emptyIfNull(source.getDataList()).forEach(deliveryInfoDto -> {
            addressVOS.add(AddressConverter.convert2AddressVO(deliveryInfoDto));
        });
        result.setData(addressVOS);
        return result;
    }
}
