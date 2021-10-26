package com.jd.fastc.ext.shop.rpc.impl;

import com.jd.fastc.ext.shop.rpc.GoodsCategoryQueryRpc;
import com.jd.pop.vender.center.service.shopCategory.ShopCategorySafService;
import com.jd.pop.vender.center.service.shopCategory.dto.ShopCategoryResult;
import com.jd.pop.vender.center.service.shopCategory.vo.ShopCategoryVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/***
 * @Auther: yejianjun
 * @Date: 2021/10/22
 * @Title:
 *
 */
@Service
public class GoodsCategoryQueryRpcImpl implements GoodsCategoryQueryRpc {

    @Resource
    private ShopCategorySafService shopCategorySafService;

    @Override
    public ShopCategoryResult getAllShopCategory(ShopCategoryVO vo) {
        return  shopCategorySafService.getAllShopCategory(vo);
    }

    @Override
    public ShopCategoryResult getShopCategorysByVenderId(Long venderId){
        return shopCategorySafService.getShopCategorysByVenderId(venderId);
    }
}
