package com.jd.fastc.ext.shop.ext;

import com.google.common.collect.Lists;
import com.jd.b2b.user.sdk.domain.B2bUserResult;
import com.jd.b2b.user.sdk.domain.PaginationResult;
import com.jd.b2b.user.sdk.enums.DeliveryBizType;
import com.jd.b2b.user.sdk.enums.DeliveryTypeEnum;
import com.jd.fastbe.framework.model.base.DomainParam;
import com.jd.fastbe.framework.model.base.DomainResult;
import com.jd.fastbe.framework.model.base.PageVO;
import com.jd.fastc.biz.shop.manage.common.RestultException;
import com.jd.fastc.biz.shop.manage.enums.ResultCode;
import com.jd.fastc.ext.shop.handler.AddressConverter;
import com.jd.fastc.ext.shop.rpc.GoodsQueryRpc;
import com.jd.fastc.ext.shop.vo.AddressVO;
import com.jd.fastc.shop.ext.sdk.manage.GoodsQueryExt;
import com.jd.fastc.shop.ext.sdk.manage.vo.VenderSkuQueryVO;
import com.jd.fastc.shop.ext.sdk.manage.vo.VenderSkuVO;
import com.jd.pap.priceinfo.sdk.domain.request.PriceInfoRequest;
import com.jd.pap.priceinfo.sdk.domain.response.PriceInfoResponse;
import com.jd.pap.priceinfo.sdk.domain.response.PriceResult;
import com.jd.tp.common.masterdata.BU;
import com.jd.tp.common.masterdata.UA;
import com.yibin.b2b.user.core.query.sdk.dto.userplat.DeliveryInfoDto;
import com.yibin.b2b.user.core.query.sdk.dto.userplat.req.DeliveryQueryDto;
import com.yibin.b2b.user.core.query.sdk.dto.userplat.req.GetDeliveryByIdReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

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
@Slf4j
public class GoodsQueryExtImpl implements GoodsQueryExt {

    @Resource
    private GoodsQueryRpc goodsQueryRpc;

    @Override
    public DomainResult<PageVO<VenderSkuVO>> getPage(DomainParam<VenderSkuQueryVO> param) {

        VenderSkuQueryVO vo = param.getData();
        //组装 http调用请求参数
        Map<String, String> map = new HashMap<>();
        String category = vo.getCategory();
        if (StringUtils.isNotBlank(category)){
            map.put("category", category);
        }
        map.put("venderId", vo.getVenderId());
        map.put("currentPage", vo.getCurrentPage() + "");
        map.put("pageSize", vo.getPageSize() + "");
        //如果传了地址id,就查询地址id对应的级联组合地址id："省id,市id,区县id,乡镇id"
        Long addressId = vo.getAddressId();
        if (Objects.nonNull(addressId)){
            String address = queryAddressById(addressId);
            // 如果可以查到对应的级联组合地址id，就封装进http调用参数map中
            if (StringUtils.isNotBlank(address)) {
                map.put("address", address);
            }
        }
        //http调用搜索中台接口
        String json = search(map);
        log.debug("----------------搜索中台返回字符串:{}---------------",json);
        //对返回json解析，取出需要字段的值
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
        } catch (Exception e) {
            log.error("商品搜索中台报错:{}",e.fillInStackTrace());
            throw new RestultException(ResultCode.RPC_ERROR, "HTTP调用错误");
        }

        //为每个商品通过价格中台找到价格，并设值
        setPriceForSku(list);
        pageVO.setData(list);
        return DomainResult.success(pageVO);
    }

    private void setPriceForSku(List<VenderSkuVO> list) {
        HashSet<Integer> hashSet = new HashSet<>();
        hashSet.add(1);
        PriceInfoRequest priceInfoRequest = new PriceInfoRequest();
        priceInfoRequest.setPriceInfos(hashSet);
        priceInfoRequest.setSkuIds(list.stream().map(VenderSkuVO::getSkuId).collect(Collectors.toSet()));
        priceInfoRequest.setSite(BU.YB_B2B.getId());
        priceInfoRequest.setChannel(UA.PC.ordinal() + 1);
        //todo: 需要藏经阁备案 配置文件注入
        priceInfoRequest.setSource("jshop-act");
        Map<String, PriceResult> priceMap = getRealPrice(priceInfoRequest);
        for (VenderSkuVO skuVO : list) {
            PriceResult priceResult = priceMap.get(skuVO.getSkuId());
            String jdPrice = "";
            if (Objects.nonNull(priceResult)) {
                jdPrice = priceResult.getJdPrice();
            }
            skuVO.setSkuPrice(jdPrice);
        }
    }


    private Map<String, PriceResult> getRealPrice(PriceInfoRequest priceInfoRequest) {
        PriceInfoResponse realPriceInfo = goodsQueryRpc.getRealPriceInfo(priceInfoRequest);
        if (realPriceInfo.isSuccess()) {
            Map<String, PriceResult> priceMap = realPriceInfo.getPriceMap();
            if (MapUtils.isNotEmpty(priceMap)) {
                return priceMap;
            }
            throw new RestultException(ResultCode.DATA_ERROR);
        }
        throw new RestultException(ResultCode.RPC_ERROR);
    }

    private String search(Map<String, String> map) {
        String json = goodsQueryRpc.goodsSearch(map);
        if (StringUtils.isNotBlank(json)) {
            return json;
        }
        throw new RestultException(ResultCode.DATA_ERROR);
    }

    public PaginationResult<DeliveryInfoDto> getAddressPageByBPin(DeliveryQueryDto queryDto) {
        PaginationResult<DeliveryInfoDto> result = goodsQueryRpc.queryByPage(queryDto);
        if (result.isSuccess()) {
            return result;
        }
        throw new RestultException(ResultCode.RPC_ERROR);
    }

    public String queryAddress(String operator) {

        DeliveryQueryDto deliveryQueryDto = new DeliveryQueryDto();
        deliveryQueryDto.setBPin(operator);
        deliveryQueryDto.setTenant(BU.YB_B2B.getId()+"");
        deliveryQueryDto.setBizType(DeliveryBizType.DELIVERY_GOODS.getCode());
        deliveryQueryDto.setDeliveryType(DeliveryTypeEnum.DEF_ADDR.getCode());
        deliveryQueryDto.setPageNo(1);
        deliveryQueryDto.setPageSize(1);
        PaginationResult<DeliveryInfoDto> resp = getAddressPageByBPin(deliveryQueryDto);
        PageVO<AddressVO> addressVOPageVO = convert2PageVO(resp);
        Optional<AddressVO> first = addressVOPageVO.getData().stream().findFirst();
        if (first.isPresent()) {
            AddressVO addressVO = first.get();
            Integer provinceId = addressVO.getProvinceId();
            Integer cityId = addressVO.getCityId();
            Integer districtId = addressVO.getDistrictId();
            Integer townId = addressVO.getTownId();
            return provinceId + "," + cityId + "," + districtId + "," + townId;
        }
        return null;
    }

    public String queryAddressById(Long addressId) {

        GetDeliveryByIdReq req = new GetDeliveryByIdReq();
        req.setTenant(BU.YB_B2B.getId()+"");
        req.setDeliveryId(addressId);
        B2bUserResult<DeliveryInfoDto> result = goodsQueryRpc.getByDeliveryId(req);
        if (result.isSuccess()) {
            DeliveryInfoDto data = result.getData();
            if (Objects.nonNull(data)) {
                AddressVO addressVO = AddressConverter.convert2AddressVO(data);
                Integer provinceId = addressVO.getProvinceId();
                Integer cityId = addressVO.getCityId();
                Integer districtId = addressVO.getDistrictId();
                Integer townId = addressVO.getTownId();
                return provinceId + "," + cityId + "," + districtId + "," + townId;
            }
            return null;
        }
        throw new RestultException(ResultCode.RPC_ERROR);
    }

    private PageVO<AddressVO> convert2PageVO(PaginationResult<DeliveryInfoDto> source) {

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
