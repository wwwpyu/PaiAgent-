package com.paiagent.engine.executor;

import com.paiagent.dto.ExecutionEvent;
import com.paiagent.engine.model.WorkflowNode;

import java.util.Map;
import java.util.function.Consumer;

public interface NodeExecutor {

    Map<String, Object> execute(WorkflowNode node, Map<String, Object> input) throws Exception;

    default Map<String, Object> execute(WorkflowNode node, Map<String, Object> input, Consumer<ExecutionEvent> progressCallback) throws Exception {
        return execute(node, input);
    }

    /**
     * 获取该执行器支持的节点类型
     *
     * @return 节点类型标识
     */
    String getSupportedNodeType();

    /**
     * 检查该执行器是否支持函数调用 (Function Calling)
     * 默认返回 false，LLM 执行器应覆盖此方法返回 true
     *
     * @return true 表示支持函数调用
     */
    default boolean supportsFunctionCalling() {
        return false;
    }
}