package com.jd.fastc.ext.shop.ext;

import com.jd.fastbe.framework.model.base.DomainParam;
import com.jd.fastbe.framework.model.base.DomainResult;
import com.jd.fastc.biz.shop.manage.common.RestultException;
import com.jd.fastc.biz.shop.manage.enums.ResultCode;
import com.jd.fastc.shop.ext.sdk.manage.GoodsCategoryQueryExt;
import com.jd.fastc.shop.ext.sdk.manage.vo.VenderGoodsCategoryVO;
import com.jd.m.mocker.client.ordinary.method.aop.JMock;
import com.jd.pop.vender.center.service.shopCategory.ShopCategorySafService;
import com.jd.pop.vender.center.service.shopCategory.dto.ShopCategory;
import com.jd.pop.vender.center.service.shopCategory.dto.ShopCategoryResult;
import com.jd.pop.vender.center.service.shopCategory.vo.ShopCategoryVO;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/***
 * @Auther: yejianjun
 * @Date: 2021/10/12
 * @Title:
 *
 */
@Component
public class GoodsCategoryQueryExtImpl implements GoodsCategoryQueryExt {

    @Resource
    private ShopCategorySafService shopCategorySafService;

    @Override
    @JMock
    public DomainResult<List<VenderGoodsCategoryVO>> getList(DomainParam param) {

        ShopCategoryVO vo = new ShopCategoryVO();
        vo.setVenderId(Long.parseLong(param.getVenderId()));
        vo.setSource(NumberUtils.INTEGER_ONE);
        ShopCategoryResult categoryResult = shopCategorySafService.getAllShopCategory(vo);
        List<VenderGoodsCategoryVO> list = new ArrayList<>();

        if (categoryResult.isSuccess()){
            List<ShopCategory> resultList = categoryResult.getList();
            if (!CollectionUtils.isEmpty(resultList)){
                for (ShopCategory category : resultList) {
                    VenderGoodsCategoryVO goodsCategoryVO = new VenderGoodsCategoryVO();
                    goodsCategoryVO.setId(category.getId());
                    goodsCategoryVO.setTitle(category.getTitle());
                    list.add(goodsCategoryVO);
                }
                return DomainResult.success(list);
            }
            throw new RestultException(ResultCode.DATA_ERROR);
        }
        throw new RestultException(ResultCode.RPC_ERROR);
    }
}
