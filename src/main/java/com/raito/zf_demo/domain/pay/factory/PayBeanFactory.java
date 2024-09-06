package com.raito.zf_demo.domain.pay.factory;

import com.raito.zf_demo.domain.pay.enums.PayType;
import com.raito.zf_demo.domain.pay.service.PayService;
import com.raito.zf_demo.infrastructure.exception.NotFoundException;
import com.raito.zf_demo.infrastructure.util.SpringContextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author raito
 * @since 2024/09/05
 */
public abstract class PayBeanFactory {

    private static final Map<String, Object> beans;

    static {
        beans = new HashMap<>();
        PayBeanFactory.init();
    }

    private static void init() {
        beans.put(PayType.WX_PAY.name(), SpringContextUtils.getBean("wxPayService", PayService.class));
//        beans.put(PayType.ALIPAY.name(), SpringContextUtils.getBean("alipayService", PayService.class));
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name, Class<T> clazz) {
        Object bean = beans.get(name);
        if (bean == null || !clazz.isAssignableFrom(bean.getClass())) {
            throw new NotFoundException("bean " + name + " not found");
        }
        return (T) bean;
    }


}