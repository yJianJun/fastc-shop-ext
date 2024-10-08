package com.jd.fastc.ext.shop.ext;

import com.google.common.collect.Sets;
import com.jd.b2b.user.sdk.domain.B2bUserResult;
import com.jd.b2b.user.sdk.domain.PaginationResult;
import com.jd.b2b.user.sdk.enums.PurTypeEnum;
import com.jd.b2b.user.sdk.enums.VenderSourceEnum;
import com.jd.fastbe.framework.model.base.DomainParam;
import com.jd.fastbe.framework.model.base.DomainResult;
import com.jd.fastc.biz.shop.manage.common.RestultException;
import com.jd.fastc.biz.shop.manage.enums.ConstantCode;
import com.jd.fastc.biz.shop.manage.enums.ResultCode;
import com.jd.fastc.ext.shop.enums.ShopStatus;
import com.jd.fastc.ext.shop.rpc.ShopManageRpc;
import com.jd.fastc.shop.ext.sdk.manage.ShopManagetExt;
import com.jd.fastc.shop.ext.sdk.manage.vo.VenderShopVO;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.pop.vender.center.service.shop.dto.BasicShop;
import com.jd.pop.vender.center.service.shop.dto.BasicShopResult;
import com.jd.pop.vender.center.service.vbinfo.dto.VenderBasicResult;
import com.jd.pop.vender.center.service.vbinfo.dto.VenderBasicVO;
import com.jd.tp.common.masterdata.BU;
import com.yibin.b2b.user.core.query.sdk.dto.purchaserelation.GetPurchaseRelReq;
import com.yibin.b2b.user.core.query.sdk.dto.purchaserelation.PurchaseRelationDto;
import com.yibin.b2b.user.core.query.sdk.dto.purchaserelation.PurchaseRelationQueryDto;
import com.yibin.b2b.user.core.query.sdk.dto.purchaserelation.RelationDetailDto;
import com.yibin.b2b.user.core.query.sdk.enums.QueryStrategyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/***
 * @Auther: yejianjun
 * @Date: 2021/10/12
 * @Title:
 *
 */
@Component
@Slf4j
public class ShopManageExtImpl implements ShopManagetExt {

    @Resource
    private ShopManageRpc shopManageRpc;

    @Override
    public DomainResult<VenderShopVO> detail(String venderId, String pin) {

        VenderShopVO venderShopVO = new VenderShopVO();

        // 查询合作关系状态
        PurchaseRelationDto relationDto = queryRlation(venderId, pin);
        Integer status = relationDto.getAuthStatus();
        Long relId = relationDto.getRelId();
        // 查询店铺基本信息
        BasicShop shop = queryShop(venderId);
        //查询商家基本信息
        VenderBasicVO vender = queryVender(venderId);

        if (Objects.nonNull(shop) && Objects.nonNull(vender) && Objects.nonNull(status) && Objects.nonNull(relId)) {
            venderShopVO.setShopId(shop.getId() + "");
            venderShopVO.setVenderId(shop.getVenderId() + "");
            venderShopVO.setShopName(shop.getTitle());
            Integer shopStatus = shop.getStatus();
            venderShopVO.setShopStatus(shopStatus == 0 ? ShopStatus.Disable.getValue() : (shopStatus == 1 ? ShopStatus.Enable.getValue() : ShopStatus.Await.getValue()));
            venderShopVO.setLogo(shop.getFullLogoUri());
            venderShopVO.setContact(shop.getCsNo());
            venderShopVO.setCompanyName(vender.getCompanyName());
            venderShopVO.setCooperationStatus(status);
            venderShopVO.setRelId(relId);
            return DomainResult.success(venderShopVO);
        }
        throw new RestultException(ResultCode.DATA_ERROR);
    }

    private VenderBasicVO queryVender(String venderId) {
        log.info("-------商家基本信息传参:{}-----------", JsonUtils.toJSONString(venderId));
        VenderBasicResult venderResult = shopManageRpc.getBasicVenderInfoByVenderId(Long.parseLong(venderId));
        log.info("-------商家基本信息结果:{}-----------", JsonUtils.toJSONString(venderResult));
        if (venderResult.isSuccess()) {
            VenderBasicVO basicVO = venderResult.getVenderBasicVO();
            if (Objects.nonNull(basicVO)) {
                return basicVO;
            }
            throw new RestultException(ResultCode.DATA_ERROR);
        }
        throw new RestultException(ResultCode.RPC_ERROR);
    }

    private BasicShop queryShop(String venderId) {
        log.info("-------店铺基本信息传参:{}-----------", JsonUtils.toJSONString(venderId));
        BasicShopResult shopResult = shopManageRpc.getBasicShopByVenderId(Long.parseLong(venderId));
        log.info("-------店铺基本信息结果:{}-----------", JsonUtils.toJSONString(shopResult));
        if (shopResult.isSuccess()) {
            BasicShop basicShop = shopResult.getBasicShop();
            if (Objects.nonNull(basicShop)) {
                return basicShop;
            }
            throw new RestultException(ResultCode.DATA_ERROR);
        }
        throw new RestultException(ResultCode.RPC_ERROR);
    }

    private PurchaseRelationDto queryRlation(String venderId, String pin) {

        GetPurchaseRelReq relReq = new GetPurchaseRelReq();
        relReq.setPurType(PurTypeEnum.B_PIN.getCode());
        relReq.setPurCode(pin);
        relReq.setVenderSource(VenderSourceEnum.B2B.getCode());
        relReq.setVenderIds(Sets.newHashSet(Long.parseLong(venderId)));
        relReq.setTenant(BU.YB_B2B.getId() + "");
        relReq.setQueryType(QueryStrategyEnum.REAL_TIME.getCode());

        log.info("-------用户商家合作关系传参:{}-----------", JsonUtils.toJSONString(relReq));
        B2bUserResult<List<RelationDetailDto>> relationResult = shopManageRpc.getPurchaseRelByPurCode(relReq);
        log.info("-------用户商家合作关系结果:{}-----------", JsonUtils.toJSONString(relationResult));
        PurchaseRelationDto relationCbiDto;
        if (relationResult.isSuccess()) {
            List<RelationDetailDto> dataList = relationResult.getData();
            if (!CollectionUtils.isEmpty(dataList)) {
                RelationDetailDto detailDto = dataList.get(0);
                if (Objects.nonNull(detailDto)) {
                    relationCbiDto = detailDto.getPurchaseRelationCbiDto();
                    if (Objects.nonNull(relationCbiDto)) {
                        return relationCbiDto;
                    }
                }
            }
            relationCbiDto = new PurchaseRelationDto();
            relationCbiDto.setAuthStatus(ConstantCode.Not_Cooperating.getCode());
            return relationCbiDto;
        }
        throw new RestultException(ResultCode.RPC_ERROR);
    }
}
