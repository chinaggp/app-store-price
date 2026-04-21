package com.hypo.appstoreprice.logging;

import lombok.experimental.UtilityClass;

/**
 * 接口日志上下文持有者
 *
 * @author codex
 * @date 2026-04-21
 */
@UtilityClass
public class ApiLogContextHolder {

    private static final ThreadLocal<ApiLogContext> CONTEXT_HOLDER = new ThreadLocal<>();

    public ApiLogContext getContext() {
        ApiLogContext context = CONTEXT_HOLDER.get();
        if (context == null) {
            context = new ApiLogContext();
            CONTEXT_HOLDER.set(context);
        }
        return context;
    }

    public void clear() {
        CONTEXT_HOLDER.remove();
    }
}
