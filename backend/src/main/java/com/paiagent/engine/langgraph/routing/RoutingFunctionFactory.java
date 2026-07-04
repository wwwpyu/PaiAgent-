package com.paiagent.engine.langgraph.routing;

import com.paiagent.engine.model.WorkflowEdge;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.action.AsyncEdgeAction;
import org.bsc.langgraph4j.state.AgentState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 路由函数工厂
 *
 * 从一组共享相同源节点的条件边创建 LangGraph4j 兼容的 AsyncEdgeAction 路由函数。
 * 路由函数在节点执行后评估所有条件，返回第一个匹配条件的目标节点 ID。
 */
@Slf4j
@Component
public class RoutingFunctionFactory {

    @Autowired
    private ConditionEvaluator conditionEvaluator;

    /**
     * 为指定源节点创建条件路由函数
     *
     * @param sourceNodeId     源节点 ID
     * @param conditionalEdges 该源节点的所有条件边
     * @return LangGraph4j AsyncEdgeAction 路由函数
     */
    public AsyncEdgeAction<AgentState> createRoutingFunction(
            String sourceNodeId, List<WorkflowEdge> conditionalEdges) {

        return (AgentState state) -> {
            Map<String, Object> stateData = state.data();

            log.debug("路由决策: sourceNodeId={}, 条件边数={}", sourceNodeId, conditionalEdges.size());

            // 获取源节点的输出（用于条件评估）
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Object>> nodeOutputs =
                (Map<String, Map<String, Object>>) stateData.getOrDefault("nodeOutputs", Map.of());

            Map<String, Object> evalContext = new HashMap<>(stateData);
            // 同时将节点自身的输出扁平化到评估上下文中
            Map<String, Object> sourceOutput = nodeOutputs.get(sourceNodeId);
            if (sourceOutput != null) {
                evalContext.putAll(sourceOutput);
            }

            // 按顺序评估每个条件边
            for (WorkflowEdge edge : conditionalEdges) {
                WorkflowEdge.ConditionConfig condition = edge.getCondition();
                if (condition != null && conditionEvaluator.evaluate(condition, evalContext)) {
                    String targetId = condition.getTargetNodeId() != null
                            ? condition.getTargetNodeId()
                            : edge.getTarget();
                    log.info("条件路由匹配: {} ({} {} {}) -> {}",
                            sourceNodeId, condition.getField(),
                            condition.getOperator(), condition.getValue(), targetId);
                    return CompletableFuture.completedFuture(targetId);
                }
            }

            // 无匹配条件时走默认路径或结束
            log.info("条件路由无匹配: {}，执行结束", sourceNodeId);
            return CompletableFuture.completedFuture(StateGraph.END);
        };
    }

    /**
     * 构建条件边的路径映射
     * 用于 graph.addConditionalEdges() 的 pathMap 参数
     *
     * @param conditionalEdges 条件边列表
     * @return 路径标识到目标节点 ID 的映射
     */
    public Map<String, String> buildPathMap(List<WorkflowEdge> conditionalEdges) {
        Map<String, String> pathMap = new HashMap<>();

        for (WorkflowEdge edge : conditionalEdges) {
            WorkflowEdge.ConditionConfig condition = edge.getCondition();
            String targetId = condition != null && condition.getTargetNodeId() != null
                    ? condition.getTargetNodeId()
                    : edge.getTarget();
            pathMap.put(targetId, targetId);
        }

        // 添加 END 作为兜底路径
        pathMap.put(StateGraph.END, StateGraph.END);

        return pathMap;
    }
}
