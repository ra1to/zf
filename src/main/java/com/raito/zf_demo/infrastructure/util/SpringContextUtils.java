package com.raito.zf_demo.infrastructure.util;

import com.raito.zf_demo.infrastructure.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author raito
 * @since 2024/09/05
 */
@Component
public class SpringContextUtils {

    private static ApplicationContext ac;

    public static <T> T getBean(Class<T> clazz) {
        return ac.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        Object bean = ac.getBean(name);
        if (clazz.isAssignableFrom(bean.getClass())) {
            return clazz.cast(bean);
        } else {
            throw new NotFoundException("bean " + name + " is not " + clazz.getName());
        }
    }

    @Autowired
    public void setAc(ApplicationContext ac) {
        SpringContextUtils.ac = ac;
    }

}
