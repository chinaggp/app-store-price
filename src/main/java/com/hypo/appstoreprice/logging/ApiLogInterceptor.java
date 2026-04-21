package com.hypo.appstoreprice.logging;

import com.alibaba.fastjson2.JSON;
import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 接口日志拦截器
 *
 * @author codex
 * @date 2026-04-21
 */
@Slf4j
@Component
public class ApiLogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        ApiLogContext context = ApiLogContextHolder.getContext();
        context.setStartTime(System.currentTimeMillis());
        context.setHttpMethod(request.getMethod());
        context.setRequestUri(request.getRequestURI());
        if (handler instanceof HandlerMethod handlerMethod) {
            context.setHandlerMethod(handlerMethod.getBeanType().getSimpleName() + "#" + handlerMethod.getMethod().getName());
        }
        if (context.getRequestBody() == null) {
            context.setRequestBody(buildRequestParams(request));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        ApiLogContext context = ApiLogContextHolder.getContext();
        long costMs = System.currentTimeMillis() - context.getStartTime();
        Map<String, Object> logMap = new LinkedHashMap<>();
        logMap.put("httpMethod", context.getHttpMethod());
        logMap.put("requestUri", context.getRequestUri());
        logMap.put("handlerMethod", context.getHandlerMethod());
        logMap.put("requestBody", context.getRequestBody());
        logMap.put("responseBody", context.getResponseBody());
        logMap.put("status", response.getStatus());
        logMap.put("costMs", costMs);
        if (ex != null) {
            logMap.put("exception", ex.getClass().getSimpleName());
        }
        log.info("apiLog={}", JSON.toJSONString(logMap));
        ApiLogContextHolder.clear();
    }

    private Object buildRequestParams(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.isEmpty()) {
            return null;
        }
        Map<String, Object> result = new LinkedHashMap<>(parameterMap.size());
        parameterMap.forEach((key, value) -> {
            if (ArrayUtil.isEmpty(value)) {
                result.put(key, null);
            } else if (value.length == 1) {
                result.put(key, value[0]);
            } else {
                result.put(key, value);
            }
        });
        return ApiLogSanitizer.sanitize(result);
    }
}
