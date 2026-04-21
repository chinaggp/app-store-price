package com.hypo.appstoreprice.logging;

import lombok.Data;

/**
 * 接口日志上下文
 *
 * @author codex
 * @date 2026-04-21
 */
@Data
public class ApiLogContext {

    /**
     * 请求开始时间
     */
    private long startTime;

    /**
     * 请求方法
     */
    private String httpMethod;

    /**
     * 请求路径
     */
    private String requestUri;

    /**
     * 控制器方法
     */
    private String handlerMethod;

    /**
     * 请求参数
     */
    private Object requestBody;

    /**
     * 响应结果
     */
    private Object responseBody;
}
