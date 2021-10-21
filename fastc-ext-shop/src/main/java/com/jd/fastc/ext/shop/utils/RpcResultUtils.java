package com.jd.fastc.ext.shop.utils;

import com.jd.b2b.aurora.sdk.domain.resp.RPCResult;
import com.jd.fastbe.framework.client.exception.RpcException;
import com.jd.fastbe.framework.client.utils.NetworkUtils;

/**
 * RpcResultUtils
 *
 */
public class RpcResultUtils {


    public static <T> T deal(RPCResult<T> result) {

        if (result == null) {
            throw new RpcException("RPC 返回空");
        }

        if (!result.isSuccess()) {
            throw new RpcException(String.format("接口返回异常code:%s, message:%s", result.getCode(), result.getErrorMsg()));
        }

        return result.getResult();
    }

    public static com.yibin.b2b.user.core.base.sdk.domain.ClientInfoReq buildYiBinClient() {
        com.yibin.b2b.user.core.base.sdk.domain.ClientInfoReq clientInfoReq = new com.yibin.b2b.user.core.base.sdk.domain.ClientInfoReq();
        clientInfoReq.setIp(NetworkUtils.loadIP());
        return clientInfoReq;
    }

}
