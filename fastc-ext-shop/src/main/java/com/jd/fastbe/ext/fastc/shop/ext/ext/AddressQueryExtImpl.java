package com.jd.fastbe.ext.fastc.shop.ext.ext;

import com.jd.fastbe.framework.model.base.DomainResult;
import com.jd.fastbe.tpl.common.components.isv.export.addr.ext.AddressQueryExt;
import com.jd.fastbe.tpl.common.components.isv.export.addr.request.GetAddressById;
import com.jd.fastbe.tpl.common.components.isv.export.addr.request.GetAddressRequest;
import com.jd.fastbe.tpl.common.components.isv.export.addr.vo.AreaVO;
import com.jd.fastbe.tpl.common.components.isv.export.addr.vo.GroupAreaVO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

/**
 * Created by SoulW on 2021/7/15.
 *
 * @author SoulW
 * @since 2021/7/15 16:31
 */
@Component
public class AddressQueryExtImpl implements AddressQueryExt {
    @Override
    public DomainResult<List<AreaVO>> getAddress(GetAddressRequest getAddressRequest, Function<GetAddressRequest, List<AreaVO>> function) {
        return DomainResult.success(function.apply(getAddressRequest));
    }

    @Override
    public DomainResult<List<GroupAreaVO>> getGroupAddress(GetAddressRequest getAddressRequest, Function<GetAddressRequest, List<GroupAreaVO>> function) {
        return DomainResult.success(function.apply(getAddressRequest));
    }

    @Override
    public DomainResult<AreaVO> getAddressById(GetAddressById getAddressById, Function<GetAddressById, AreaVO> function) {
        return DomainResult.success(function.apply(getAddressById));
    }

    @Override
    public DomainResult<List<AreaVO>> getParentCascade(GetAddressById getAddressById, Function<GetAddressById, List<AreaVO>> function) {
        return DomainResult.success(function.apply(getAddressById));
    }
}
