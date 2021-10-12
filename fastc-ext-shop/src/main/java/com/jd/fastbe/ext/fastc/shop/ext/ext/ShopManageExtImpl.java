package com.jd.fastbe.ext.fastc.shop.ext.ext;

import com.jd.b2b.user.sdk.domain.PaginationResult;
import com.jd.fastbe.framework.model.base.DomainParam;
import com.jd.fastbe.framework.model.base.DomainResult;
import com.jd.fastbe.shop.ext.sdk.manage.ShopManagetExt;
import com.jd.fastbe.shop.ext.sdk.sayhello.vo.VenderShopQueryVO;
import com.jd.fastbe.shop.ext.sdk.sayhello.vo.VenderShopVO;
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
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/***
 * @Auther: yejianjun
 * @Date: 2021/10/12
 * @Title:
 *
 */

public class ShopManageExtImpl implements ShopManagetExt {

    @Autowired
    private ShopSafService shopSafService;

    @Autowired
    private VenderBasicSafService venderBasicSafService;

    @Autowired
    private UserRelationService userRelationService;

    @Override
    public DomainResult<VenderShopVO> detail(DomainParam paramData) {
        try {


//加载配置文件
//            ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
//                    "/jsf-consumer.xml");
//
//            shopSafService = (ShopSafService) appContext
//                    .getBean("shopSafService");
//            log.info("得到调用端代理：{}", shopSafService);
            VenderShopVO venderShopVO = new VenderShopVO();
             Map ext = paramData.getExt();
            String pin = (String)ext.get("pin");
            Integer status = queryRlation(paramData.getVenderId(),pin);
            venderShopVO.setCooperation(status);

            BasicShopResult shopResult = shopSafService.getBasicShopByVenderId(Long.parseLong(paramData.getVenderId()),null, 1);
            VenderBasicResult venderResult = venderBasicSafService.getBasicVenderInfoByVenderId(Long.parseLong(paramData.getVenderId()), null, 1);
            if (shopResult.isSuccess() && venderResult.isSuccess()) {
                BasicShop shop = shopResult.getBasicShop();
                VenderBasicVO vender = venderResult.getVenderBasicVO();
                if (shop != null && vender!=null) {
                    venderShopVO.setShopId(shop.getId());
                    venderShopVO.setVenderId(shop.getVenderId());
                    venderShopVO.setShopName(shop.getTitle());
                    venderShopVO.setStatus(shop.getStatus());
                    venderShopVO.setFullLogoUri(shop.getFullLogoUri());
                    venderShopVO.setBrief(shop.getBrief());
                    venderShopVO.setCsNo(shop.getCsNo());
                    venderShopVO.setShopType(shop.getShopType());
                    venderShopVO.setCompany(vender.getCompanyName());

                    return DomainResult.success(venderShopVO);
                } else {
                    System.out.println("该商家店铺信息不存在");
                    return null;
                }
            } else {
                System.out.println("服务端发生异常，具体错误日志为：" + shopResult.getErrorMsg());
                return null;
            }
        } catch (Exception e) {
            System.out.println("网络异常,做好降级准备");
            return null;
        }
    }

    private Integer queryRlation(String venderId,String pin) {

        PurchaseRelationQueryDto purchaseRelationDto = new PurchaseRelationQueryDto();
        purchaseRelationDto.setTenant("buId:406");
        purchaseRelationDto.setVenderId(Long.parseLong(venderId));
        purchaseRelationDto.setBPin(pin);
        PaginationResult<RelationDetailDto> relationPage = userRelationService.queryUserRelationPage(purchaseRelationDto, new UserCbiPageQuery());
        if (relationPage.isSuccess()){
            List<RelationDetailDto> dataList = relationPage.getDataList();
            if (!CollectionUtils.isEmpty(dataList)){
                PurchaseRelationDto relationCbiDto = dataList.get(0).getPurchaseRelationCbiDto();
                return relationCbiDto.getAuthStatus();
            }
            return null;
        }
        return null;
    }
}
