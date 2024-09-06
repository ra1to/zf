package com.raito.zf_demo.infrastructure.util;


import com.raito.zf_demo.infrastructure.context.LoginContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author raito
 * @since 2024/08/28
 */
public class OrderNoUtils {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String getOrderNo() {
        return "ORDER_" + getNo() + "_USER_" + LoginContext.getUserId();
    }

    private static String getNo() {
        String newDate = sdf.format(new Date());
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            result.append(random.nextInt(10));
        }
        return newDate + result;
    }

    public static String getRefundNo() {
        return "REFUND_" + getNo() + "_USER_" + LoginContext.getUserId();
    }
}
