package com.jd.fastbe.ext.fastc.shop.ext;

import com.jd.fastbe.framework.model.base.DomainParam;
import com.jd.fastbe.framework.model.base.DomainResult;
import com.jd.fastbe.framework.model.base.PageVO;
import com.jd.fastbe.shop.ext.sdk.manage.GoodsQueryExt;
import com.jd.fastbe.shop.ext.sdk.manage.vo.VenderSkuQueryVO;
import com.jd.fastbe.shop.ext.sdk.manage.vo.VenderSkuVO;
import com.jd.pap.priceinfo.sdk.domain.request.PriceInfoRequest;
import com.jd.pap.priceinfo.sdk.domain.response.PriceInfoResponse;
import com.jd.pap.priceinfo.sdk.domain.response.PriceResult;
import com.jd.pap.priceinfo.sdk.service.PriceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    @Override
    public DomainResult<PageVO<VenderSkuVO>> getPage(DomainParam<VenderSkuQueryVO> param) throws Exception {

        VenderSkuQueryVO vo = param.getData();
        Map<String, String> map = new HashMap<>();
        map.put("category", vo.getCategory());
        map.put("venderId", vo.getVenderId());
        map.put("currentPage", vo.getCurrentPage() + "");
        map.put("pageSize", vo.getPageSize() + "");
        String json = restTemplate.getForObject("http://spblenderlht-search.searchpaaslht.svc.tpaas.n.jd.local?" +
                "key=ShopCategoryIDS,,{category};;" +
                "vender_id,,{venderId}" +
                "&page={currentPage}" + "" +
                "&pagesize={pageSize}" +
                "&expression_key=buid,,405&sort_type=sort_default&charset=utf8&client=1634023454001", String.class, map);
        JSONObject jsonObject = new JSONObject(json);
        JSONArray paragraph = jsonObject.getJSONArray("Paragraph");
        JSONObject head = jsonObject.getJSONObject("Head");
        JSONObject summary = head.getJSONObject("Summary");
        String total = summary.getString("ResultCount");

        PageVO<VenderSkuVO> pageVO = new PageVO<>();
        pageVO.setCurrentPage(vo.getCurrentPage());
        pageVO.setPageSize(vo.getPageSize());
        pageVO.setTotal(Long.valueOf(total));
        List<VenderSkuVO> list = new ArrayList<>();
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
        Map<String, PriceResult> priceMap = realPriceInfo.getPriceMap();

        for (VenderSkuVO skuVO : list) {
            PriceResult priceResult = priceMap.get(skuVO.getSkuId());
            String jdPrice = priceResult.getJdPrice();
            skuVO.setSkuPrice(jdPrice);
        }
        pageVO.setData(list);
        return DomainResult.success(pageVO);
    }
}
