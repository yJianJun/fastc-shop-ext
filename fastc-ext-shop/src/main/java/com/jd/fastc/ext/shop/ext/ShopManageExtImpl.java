package com.jd.fastc.ext.shop.ext;

import com.jd.b2b.user.sdk.domain.PaginationResult;
import com.jd.fastbe.framework.model.base.DomainParam;
import com.jd.fastbe.framework.model.base.DomainResult;
import com.jd.fastc.shop.ext.sdk.manage.ShopManagetExt;
import com.jd.fastc.shop.ext.sdk.manage.vo.VenderShopVO;
import com.jd.pop.vender.center.service.shop.ShopSafService;
import com.jd.pop.vender.center.service.shop.dto.BasicShop;
import com.jd.pop.vender.center.service.shop.dto.BasicShopResult;
import com.jd.pop.vender.center.service.vbinfo.VenderBasicSafService;
import com.jd.pop.vender.center.service.vbinfo.dto.VenderBasicResult;
import com.jd.pop.vender.center.service.vbinfo.dto.VenderBasicVO;
import com.yibin.b2b.user.core.query.sdk.domain.UserCbiPageQuery;
import com.yibin.b2b.user.core.query.sdk.dto.purchaserelation.PurchaseRelationDto;
import com.yibin.b2b.user.core.query.sdk.dto.purchaserelation.PurchaseRelationQueryDto;
import com.yibin.b2b.user.core.query.sdk.dto.purchaserelation.RelationDetailDto;
import com.yibin.b2b.user.core.query.sdk.service.UserRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/***
 * @Auther: yejianjun
 * @Date: 2021/10/12
 * @Title:
 *
 */
@Component
public class ShopManageExtImpl implements ShopManagetExt {

    @Autowired
    private ShopSafService shopSafService;

    @Autowired
    private VenderBasicSafService venderBasicSafService;

    @Autowired
    private UserRelationService userRelationService;

    @Override
    public DomainResult<VenderShopVO> detail(DomainParam paramData) {

        VenderShopVO venderShopVO = new VenderShopVO();
        String venderId = paramData.getVenderId();

        Integer status = queryRlation(venderId, paramData.getOperator());
        BasicShop shop = queryShop(venderId);
        VenderBasicVO vender = queryVender(venderId);

        if (shop != null && vender != null && status !=null ) {
            venderShopVO.setShopId(shop.getId()+"");
            venderShopVO.setVenderId(shop.getVenderId()+"");
            venderShopVO.setShopName(shop.getTitle());
            Integer shopStatus = shop.getStatus();
            venderShopVO.setShopStatus(shopStatus ==0?1:(shopStatus==1?2:3));
            venderShopVO.setLogo(shop.getFullLogoUri());
            venderShopVO.setContact(shop.getCsNo());
            venderShopVO.setCompanyName(vender.getCompanyName());
            venderShopVO.setCooperationStatus(status);
            return DomainResult.success(venderShopVO);
        } else {
            return DomainResult.fail("-1003", "RPC调用错误");
        }
    }

    private VenderBasicVO queryVender(String venderId) {
        VenderBasicResult venderResult = venderBasicSafService.getBasicVenderInfoByVenderId(Long.parseLong(venderId), null, 1);
        if (venderResult.isSuccess()) {
            return venderResult.getVenderBasicVO();
        }
        return null;
    }

    private BasicShop queryShop(String venderId) {
        BasicShopResult shopResult = shopSafService.getBasicShopByVenderId(Long.parseLong(venderId), null, 1);
        if (shopResult.isSuccess()) {
            return shopResult.getBasicShop();
        }
        return null;
    }

    private Integer queryRlation(String venderId, String pin) {

        PurchaseRelationQueryDto purchaseRelationDto = new PurchaseRelationQueryDto();
        purchaseRelationDto.setTenant("buId:406");
        purchaseRelationDto.setVenderId(Long.parseLong(venderId));
        purchaseRelationDto.setBPin(pin);
        PaginationResult<RelationDetailDto> relationPage = userRelationService.queryUserRelationPage(purchaseRelationDto, new UserCbiPageQuery());
        if (relationPage.isSuccess()) {
            List<RelationDetailDto> dataList = relationPage.getDataList();
            if (!CollectionUtils.isEmpty(dataList)) {
                PurchaseRelationDto relationCbiDto = dataList.get(0).getPurchaseRelationCbiDto();
                return relationCbiDto.getAuthStatus();
            }
            return null;
        }
        return null;
    }
}
