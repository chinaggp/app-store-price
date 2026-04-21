package com.hypo.appstoreprice.logging;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 接口日志脱敏与裁剪
 *
 * @author codex
 * @date 2026-04-21
 */
@UtilityClass
public class ApiLogSanitizer {

    private static final String OMITTED_VALUE = "[omitted]";

    public Object sanitize(Object source) {
        if (source == null) {
            return null;
        }
        if (isIgnoredType(source)) {
            return OMITTED_VALUE;
        }
        if (source instanceof Map<?, ?> sourceMap) {
            return sanitizeMap(sourceMap);
        }
        if (source instanceof Collection<?> sourceCollection) {
            JSONArray result = new JSONArray(sourceCollection.size());
            sourceCollection.forEach(item -> result.add(sanitize(item)));
            return result;
        }
        if (source.getClass().isArray()) {
            return sanitizeArray(source);
        }
        if (isSimpleValue(source)) {
            return source;
        }
        return sanitizeJsonNode(JSON.toJSON(source));
    }

    private JSONArray sanitizeArray(Object source) {
        int length = Array.getLength(source);
        JSONArray result = new JSONArray(length);
        for (int i = 0; i < length; i++) {
            result.add(sanitize(Array.get(source, i)));
        }
        return result;
    }

    private Map<String, Object> sanitizeMap(Map<?, ?> sourceMap) {
        Map<String, Object> result = new LinkedHashMap<>(sourceMap.size());
        sourceMap.forEach((key, value) -> result.put(String.valueOf(key), sanitize(value)));
        return result;
    }

    private Object sanitizeJsonNode(Object jsonNode) {
        if (jsonNode instanceof JSONObject jsonObject) {
            JSONObject result = new JSONObject(jsonObject.size());
            jsonObject.forEach((key, value) -> result.put(key, sanitizeJsonNode(value)));
            return result;
        }
        if (jsonNode instanceof JSONArray jsonArray) {
            JSONArray result = new JSONArray(jsonArray.size());
            jsonArray.forEach(item -> result.add(sanitizeJsonNode(item)));
            return result;
        }
        if (isIgnoredType(jsonNode)) {
            return OMITTED_VALUE;
        }
        return jsonNode;
    }

    private boolean isIgnoredType(Object source) {
        if (source instanceof MultipartFile || source instanceof ServletRequest || source instanceof ServletResponse) {
            return true;
        }
        if (source == null || !source.getClass().isArray()) {
            return false;
        }
        return MultipartFile.class.isAssignableFrom(source.getClass().getComponentType())
            || ServletRequest.class.isAssignableFrom(source.getClass().getComponentType())
            || ServletResponse.class.isAssignableFrom(source.getClass().getComponentType());
    }

    private boolean isSimpleValue(Object source) {
        Class<?> type = source.getClass();
        return type.isPrimitive()
            || source instanceof String
            || source instanceof Number
            || source instanceof Boolean
            || source instanceof Character
            || source instanceof Enum<?>;
    }
}
