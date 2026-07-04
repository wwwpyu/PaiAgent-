package com.paiagent.engine.langgraph.routing;

import com.paiagent.engine.model.WorkflowEdge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * 条件评估器
 *
 * 评估 WorkflowEdge.ConditionConfig 描述的条件是否满足指定的状态数据。
 * 支持点号分隔的嵌套字段遍历、多种比较运算符和类型自动转换。
 */
@Slf4j
@Component
public class ConditionEvaluator {

    /**
     * 评估单个条件配置
     *
     * @param condition 条件配置
     * @param state     状态数据（顶层 Map，可包含嵌套 Map）
     * @return true 表示条件满足
     */
    public boolean evaluate(WorkflowEdge.ConditionConfig condition, Map<String, Object> state) {
        if (condition == null) {
            return false;
        }

        String field = condition.getField();
        String operator = condition.getOperator();
        String expectedValue = condition.getValue();

        // 从状态中提取字段值（支持点号路径）
        Object actualValue = resolveFieldValue(field, state);

        log.debug("条件评估: field={}, operator={}, expected={}, actual={}",
                field, operator, expectedValue, actualValue);

        return evaluateOperator(operator, actualValue, expectedValue);
    }

    /**
     * 从嵌套 Map 中解析点号分隔的字段值
     * 例如 "output.classification" -> state.get("output").get("classification")
     */
    @SuppressWarnings("unchecked")
    Object resolveFieldValue(String field, Map<String, Object> state) {
        if (field == null || state == null) {
            return null;
        }

        String[] parts = field.split("\\.");
        Object current = state.get(parts[0]);

        for (int i = 1; i < parts.length && current != null; i++) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(parts[i]);
            } else {
                return null; // 无法继续遍历
            }
        }

        return current;
    }

    /**
     * 根据运算符评估条件
     */
    private boolean evaluateOperator(String operator, Object actualValue, String expectedValue) {
        if (operator == null) {
            return false;
        }

        return switch (operator) {
            case "equals" -> evaluateEquals(actualValue, expectedValue);
            case "contains" -> evaluateContains(actualValue, expectedValue);
            case "matches" -> evaluateMatches(actualValue, expectedValue);
            case "gt" -> evaluateGreaterThan(actualValue, expectedValue);
            case "lt" -> evaluateLessThan(actualValue, expectedValue);
            case "exists" -> actualValue != null;
            case "notExists" -> actualValue == null;
            default -> {
                log.warn("不支持的运算符: {}", operator);
                yield false;
            }
        };
    }

    /**
     * equals 运算符：字符串相等比较
     */
    private boolean evaluateEquals(Object actualValue, String expectedValue) {
        if (actualValue == null) {
            return expectedValue == null;
        }
        return actualValue.toString().equals(expectedValue);
    }

    /**
     * contains 运算符：检查字符串是否包含子串
     */
    private boolean evaluateContains(Object actualValue, String expectedValue) {
        if (actualValue == null || expectedValue == null) {
            return false;
        }
        return actualValue.toString().contains(expectedValue);
    }

    /**
     * matches 运算符：正则匹配
     */
    private boolean evaluateMatches(Object actualValue, String expectedValue) {
        if (actualValue == null || expectedValue == null) {
            return false;
        }
        try {
            return Pattern.compile(expectedValue).matcher(actualValue.toString()).find();
        } catch (Exception e) {
            log.warn("正则匹配失败: pattern={}, input={}", expectedValue, actualValue);
            return false;
        }
    }

    /**
     * gt 运算符：大于比较（数值型）
     */
    private boolean evaluateGreaterThan(Object actualValue, String expectedValue) {
        try {
            double actual = Double.parseDouble(actualValue != null ? actualValue.toString() : "0");
            double expected = Double.parseDouble(expectedValue);
            return actual > expected;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * lt 运算符：小于比较（数值型）
     */
    private boolean evaluateLessThan(Object actualValue, String expectedValue) {
        try {
            double actual = Double.parseDouble(actualValue != null ? actualValue.toString() : "0");
            double expected = Double.parseDouble(expectedValue);
            return actual < expected;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
