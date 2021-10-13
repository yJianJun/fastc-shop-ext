package com.jd.fastbe.ext.fastc.shop.ext;

import com.jd.fastbe.framework.model.base.DomainParam;
import com.jd.fastbe.framework.model.base.DomainResult;
import com.jd.fastbe.shop.ext.sdk.manage.GoodsCategoryQueryExt;
import com.jd.fastbe.shop.ext.sdk.sayhello.vo.VenderGoodsCategoryVO;
import com.jd.pop.vender.center.service.shopCategory.ShopCategorySafService;
import com.jd.pop.vender.center.service.shopCategory.dto.ShopCategory;
import com.jd.pop.vender.center.service.shopCategory.dto.ShopCategoryResult;
import com.jd.pop.vender.center.service.shopCategory.vo.ShopCategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/***
 * @Auther: yejianjun
 * @Date: 2021/10/12
 * @Title:
 *
 */
@Component
public class GoodsCategoryQueryExtImpl implements GoodsCategoryQueryExt {

    @Autowired
    private ShopCategorySafService shopCategorySafService;

    @Override
    public DomainResult<List<VenderGoodsCategoryVO>> getList(DomainParam param) {

        ShopCategoryVO vo = new ShopCategoryVO();
        vo.setVenderId(Long.parseLong(param.getVenderId()));
        vo.setSource(1);
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
            }
        }
        return DomainResult.success(list);
    }
}
