package com.jd.fastc.ext.shop.ext;

import com.jd.b2b.user.sdk.domain.PaginationResult;
import com.jd.fastbe.framework.model.base.DomainParam;
import com.jd.fastbe.framework.model.base.DomainResult;
import com.jd.fastc.biz.shop.manage.common.RestultException;
import com.jd.fastc.biz.shop.manage.enums.ResultCode;
import com.jd.fastc.ext.shop.enums.ShopStatus;
import com.jd.fastc.ext.shop.rpc.ShopManageRpc;
import com.jd.fastc.shop.ext.sdk.manage.ShopManagetExt;
import com.jd.fastc.shop.ext.sdk.manage.vo.VenderShopVO;
import com.jd.pop.vender.center.service.shop.dto.BasicShop;
import com.jd.pop.vender.center.service.shop.dto.BasicShopResult;
import com.jd.pop.vender.center.service.vbinfo.dto.VenderBasicResult;
import com.jd.pop.vender.center.service.vbinfo.dto.VenderBasicVO;
import com.yibin.b2b.user.core.query.sdk.dto.purchaserelation.PurchaseRelationDto;
import com.yibin.b2b.user.core.query.sdk.dto.purchaserelation.PurchaseRelationQueryDto;
import com.yibin.b2b.user.core.query.sdk.dto.purchaserelation.RelationDetailDto;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/***
 * @Auther: yejianjun
 * @Date: 2021/10/12
 * @Title:
 *
 */
@Component
public class ShopManageExtImpl implements ShopManagetExt {

    @Resource
    private ShopManageRpc shopManageRpc;

    @Override
    public DomainResult<VenderShopVO> detail(DomainParam paramData) {

        VenderShopVO venderShopVO = new VenderShopVO();
        String venderId = paramData.getVenderId();

        Integer status = queryRlation(venderId, paramData.getOperator());
        BasicShop shop = queryShop(venderId);
        VenderBasicVO vender = queryVender(venderId);

        if (shop != null && vender != null && status != null) {
            venderShopVO.setShopId(shop.getId() + "");
            venderShopVO.setVenderId(shop.getVenderId() + "");
            venderShopVO.setShopName(shop.getTitle());
            Integer shopStatus = shop.getStatus();
            venderShopVO.setShopStatus(shopStatus == 0 ? ShopStatus.Disable.getValue() : (shopStatus == 1 ? ShopStatus.Enable.getValue() : ShopStatus.Await.getValue()));
            venderShopVO.setLogo(shop.getFullLogoUri());
            venderShopVO.setContact(shop.getCsNo());
            venderShopVO.setCompanyName(vender.getCompanyName());
            venderShopVO.setCooperationStatus(status);
            return DomainResult.success(venderShopVO);
        }
        throw new RestultException(ResultCode.RPC_ERROR);
    }

    private VenderBasicVO queryVender(String venderId) {
        VenderBasicResult venderResult = shopManageRpc.getBasicVenderInfoByVenderId(Long.parseLong(venderId));
        if (venderResult.isSuccess()) {
            return venderResult.getVenderBasicVO();
        }
        throw new RestultException(ResultCode.RPC_ERROR);
    }

    private BasicShop queryShop(String venderId) {
        BasicShopResult shopResult = shopManageRpc.getBasicShopByVenderId(Long.parseLong(venderId));
        if (shopResult.isSuccess()) {
            return shopResult.getBasicShop();
        }
        throw new RestultException(ResultCode.RPC_ERROR);
    }

    private Integer queryRlation(String venderId, String pin) {

        PurchaseRelationQueryDto purchaseRelationDto = new PurchaseRelationQueryDto();
        purchaseRelationDto.setTenant("buId:406");
        purchaseRelationDto.setVenderId(Long.parseLong(venderId));
        purchaseRelationDto.setBPin(pin);
        purchaseRelationDto.setPageNo(1);
        purchaseRelationDto.setPageSize(1);

        PaginationResult<RelationDetailDto> relationPage = shopManageRpc.queryUserRelationPage(purchaseRelationDto);
        if (relationPage.isSuccess()) {
            List<RelationDetailDto> dataList = relationPage.getDataList();
            if (!CollectionUtils.isEmpty(dataList)) {
                PurchaseRelationDto relationCbiDto = dataList.get(0).getPurchaseRelationCbiDto();
                return relationCbiDto.getAuthStatus();
            }
            throw new RestultException(ResultCode.DATA_ERROR);
        }
        throw new RestultException(ResultCode.RPC_ERROR);
    }
}
