package com.jd.fastc.ext.shop.ext;

import com.jd.fastbe.framework.model.base.DomainParam;
import com.jd.fastbe.framework.model.base.DomainResult;
import com.jd.fastc.biz.shop.manage.common.RestultException;
import com.jd.fastc.biz.shop.manage.enums.ResultCode;
import com.jd.fastc.ext.shop.rpc.GoodsCategoryQueryRpc;
import com.jd.fastc.shop.ext.sdk.manage.GoodsCategoryQueryExt;
import com.jd.fastc.shop.ext.sdk.manage.vo.VenderGoodsCategoryVO;
import com.jd.pop.vender.center.service.shopCategory.dto.ShopCategory;
import com.jd.pop.vender.center.service.shopCategory.dto.ShopCategoryResult;
import com.jd.pop.vender.center.service.shopCategory.vo.ShopCategoryVO;
import org.apache.commons.lang3.math.NumberUtils;
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
    private GoodsCategoryQueryRpc categoryQueryRpc;

    @Override
    public DomainResult<List<VenderGoodsCategoryVO>> getList(String venderId) {

        ShopCategoryVO vo = new ShopCategoryVO();
        vo.setVenderId(Long.parseLong(venderId));
        vo.setSource(NumberUtils.INTEGER_ONE);
        List<ShopCategory> resultList = getCategoryList(vo);
        //ShopCategoryResult categoryResult = categoryQueryRpc.getShopCategorysByVenderId(vo.getVenderId());
        List<VenderGoodsCategoryVO> list = new ArrayList<>();
        for (ShopCategory category : resultList) {
            VenderGoodsCategoryVO goodsCategoryVO = new VenderGoodsCategoryVO();
            goodsCategoryVO.setId(category.getId());
            goodsCategoryVO.setTitle(category.getTitle());
            list.add(goodsCategoryVO);
        }
        return DomainResult.success(list);
    }

    private List<ShopCategory> getCategoryList(ShopCategoryVO vo) {
        ShopCategoryResult categoryResult = categoryQueryRpc.getAllShopCategory(vo);
        if (categoryResult.isSuccess()) {
            List<ShopCategory> resultList = categoryResult.getList();
            if (!CollectionUtils.isEmpty(resultList)) {
                return resultList;
            }
            throw new RestultException(ResultCode.DATA_ERROR);
        }
        throw new RestultException(ResultCode.RPC_ERROR);
    }
}
