package com.paiagent.engine.model;

import lombok.Data;

/**
 * 工作流连线模型
 *
 * 支持两种边类型：
 * <ul>
 *   <li><b>default</b> — 默认边，无条件直接连接到目标节点（现有行为）</li>
 *   <li><b>conditional</b> — 条件边，基于源节点输出决定是否路由到此目标</li>
 * </ul>
 */
@Data
public class WorkflowEdge {

    /**
     * 连线 ID
     */
    private String id;

    /**
     * 源节点 ID
     */
    private String source;

    /**
     * 目标节点 ID
     */
    private String target;

    /**
     * 源节点输出端口
     */
    private String sourceHandle;

    /**
     * 目标节点输入端口
     */
    private String targetHandle;

    /**
     * 边类型：{@code "default"}（简单路由）或 {@code "conditional"}（条件分支）
     * 默认为 {@code "default"}，向后兼容
     */
    private String edgeType = "default";

    /**
     * 条件配置（仅当 edgeType 为 "conditional" 时有效）
     */
    private ConditionConfig condition;

    /**
     * 条件配置内部类
     *
     * 描述一个路由条件：当源节点输出的某个字段满足条件时，路由到对应的目标节点
     */
    @Data
    public static class ConditionConfig {

        /**
         * 要评估的状态/输出字段路径
         * 支持点号分隔的嵌套路径，如 {@code "output.classification"} 或 {@code "status"}
         */
        private String field;

        /**
         * 比较运算符：{@code "equals"}, {@code "contains"}, {@code "matches"}（正则）,
         * {@code "gt"}, {@code "lt"}, {@code "exists"}, {@code "notExists"}
         */
        private String operator;

        /**
         * 期望值（与 field 的值进行比较）
         */
        private String value;

        /**
         * 条件满足时路由到的目标节点 ID
         */
        private String targetNodeId;
    }

    /**
     * 判断是否为条件边
     */
    public boolean isConditional() {
        return "conditional".equals(edgeType);
    }
}
