package com.jd.fastbe.ext.shop.bootstrap;

import com.jd.fastbe.tpl.common.components.isv.export.addr.ext.AddressQueryExt;
import com.jd.fastbe.tpl.common.components.isv.export.addr.request.GetAddressRequest;
import com.jd.fastbe.tpl.common.components.isv.export.addr.vo.AreaVO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * Created by SoulW on 2021/7/15.
 *
 * @author SoulW
 * @since 2021/7/15 16:33
 */
@SpringBootApplication
@ImportResource("jsf-consumer.xml")
public class App implements CommandLineRunner {

    @Resource
    private AddressQueryExt addressQueryExt;

    /**
     * 程序启动
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(App.class)
                .profiles("isv-test")
                .build().run(args);
    }

    /**
     * 扩展点测试
     *
     * @param args 参数
     */
    @Override
    public void run(String... args) {
        System.out.println(addressQueryExt.getAddress(new GetAddressRequest(), dto -> new ArrayList<AreaVO>()));
    }
}
