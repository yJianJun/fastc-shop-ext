package com.jd.fastc.ext.shop.rpc;

import com.jd.pop.vender.center.service.shopCategory.dto.ShopCategoryResult;
import com.jd.pop.vender.center.service.shopCategory.vo.ShopCategoryVO;

/***
 * @Auther: yejianjun
 * @Date: 2021/10/22
 * @Title:
 *
 */
public interface GoodsCategoryQueryRpc {

    ShopCategoryResult getAllShopCategory(ShopCategoryVO vo);

    ShopCategoryResult getShopCategorysByVenderId(Long venderId);
}
