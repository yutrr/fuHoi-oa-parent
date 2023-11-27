package com.fuHoi.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuHoi.common.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @title: ResponseUtil
 * @Author Xie
 * @Date: 2023/6/8 20:06
 * @Version 1.0
 */
public class ResponseUtil {

    public static void out(HttpServletResponse resp, Result r) {
        ObjectMapper mapper = new ObjectMapper();
        resp.setStatus(HttpStatus.OK.value());
        resp.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try {
            mapper.writeValue(resp.getWriter(),r);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
