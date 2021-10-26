package com.jd.fastc.ext.shop.init;

import com.jd.fastc.ext.shop.App;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/***
 * @Auther: yejianjun
 * @Date: 2021/10/19
 * @Title:
 *
 */

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

        return application.sources(App.class);
    }
}
