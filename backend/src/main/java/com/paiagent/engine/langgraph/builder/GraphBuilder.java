package com.paiagent.engine.langgraph.builder;

import com.paiagent.dto.ExecutionEvent;
import com.paiagent.engine.langgraph.adapter.NodeAdapter;
import com.paiagent.engine.langgraph.routing.ConditionEvaluator;
import com.paiagent.engine.langgraph.routing.RoutingFunctionFactory;
import com.paiagent.engine.model.WorkflowConfig;
import com.paiagent.engine.model.WorkflowEdge;
import com.paiagent.engine.model.WorkflowNode;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.state.AgentState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 图构建器
 *
 * 将工作流配置转换为 LangGraph StateGraph。
 * 负责节点注册、边添加、入口出口设置。
 *
 * <p>支持两种边类型：
 * <ul>
 *   <li><b>默认边</b> — 使用 {@link StateGraph#addEdge(String, String)} 无条件路由</li>
 *   <li><b>条件边</b> — 使用 {@link StateGraph#addConditionalEdges(String, org.bsc.langgraph4j.action.AsyncEdgeAction, Map)} 基于节点输出动态路由</li>
 * </ul>
 */
@Slf4j
@Component
public class GraphBuilder {

    @Autowired
    private NodeAdapter nodeAdapter;

    @Autowired
    private ConditionEvaluator conditionEvaluator;

    @Autowired
    private RoutingFunctionFactory routingFunctionFactory;

    /**
     * 构建 LangGraph StateGraph
     *
     * @param config        工作流配置
     * @param eventCallback 事件回调（可选）
     * @return 编译后的 StateGraph
     * @throws Exception 图构建异常
     */
    public org.bsc.langgraph4j.CompiledGraph<AgentState> buildGraph(
            WorkflowConfig config,
            Consumer<ExecutionEvent> eventCallback) throws Exception {

        log.info("开始构建 LangGraph: 节点数={}, 边数={}",
            config.getNodes().size(), config.getEdges().size());

        // 创建 StateGraph
        StateGraph<AgentState> graph = new StateGraph<>(AgentState::new);

        // 添加所有节点
        addNodes(graph, config.getNodes(), eventCallback);

        // 分离默认边和条件边
        Map<Boolean, List<WorkflowEdge>> edgeGroups = config.getEdges().stream()
                .collect(Collectors.partitioningBy(WorkflowEdge::isConditional));

        List<WorkflowEdge> defaultEdges = edgeGroups.get(false);
        List<WorkflowEdge> conditionalEdges = edgeGroups.get(true);

        log.info("边分类: 默认边={}, 条件边={}", defaultEdges.size(), conditionalEdges.size());

        // 添加默认边
        addDefaultEdges(graph, defaultEdges);

        // 添加条件边
        addConditionalEdges(graph, conditionalEdges);

        // 设置入口和出口
        setEntryAndExit(graph, config.getNodes(), defaultEdges, conditionalEdges);

        // 编译图
        var compiled = graph.compile();

        log.info("LangGraph 构建完成");
        return compiled;
    }

    /**
     * 添加节点到图中
     */
    private void addNodes(
            StateGraph<AgentState> graph,
            List<WorkflowNode> nodes,
            Consumer<ExecutionEvent> eventCallback) throws Exception {

        for (WorkflowNode node : nodes) {
            log.debug("添加节点: id={}, type={}", node.getId(), node.getType());

            // 使用 NodeAdapter 将节点适配为 LangGraph NodeAction
            var nodeAction = nodeAdapter.adaptNode(node, eventCallback);

            graph.addNode(node.getId(), nodeAction);
        }
    }

    /**
     * 添加默认边（无条件路由）
     */
    private void addDefaultEdges(StateGraph<AgentState> graph, List<WorkflowEdge> edges) throws Exception {
        for (WorkflowEdge edge : edges) {
            log.debug("添加默认边: {} -> {}", edge.getSource(), edge.getTarget());
            graph.addEdge(edge.getSource(), edge.getTarget());
        }
    }

    /**
     * 添加条件边
     *
     * 将共享相同源节点的条件边分组，为每组创建一个 AsyncEdgeAction 路由函数，
     * 然后通过 graph.addConditionalEdges() 注册。
     */
    private void addConditionalEdges(
            StateGraph<AgentState> graph,
            List<WorkflowEdge> conditionalEdges) throws Exception {

        if (conditionalEdges.isEmpty()) {
            return;
        }

        // 按源节点分组
        Map<String, List<WorkflowEdge>> groupedBySource = conditionalEdges.stream()
                .collect(Collectors.groupingBy(WorkflowEdge::getSource));

        for (Map.Entry<String, List<WorkflowEdge>> entry : groupedBySource.entrySet()) {
            String sourceNodeId = entry.getKey();
            List<WorkflowEdge> sourceConditionalEdges = entry.getValue();

            log.info("添加条件边: source={}, 分支数={}", sourceNodeId, sourceConditionalEdges.size());

            // 创建路由函数
            var routingFunction = routingFunctionFactory.createRoutingFunction(
                    sourceNodeId, sourceConditionalEdges);

            // 构建路径映射
            Map<String, String> pathMap = routingFunctionFactory.buildPathMap(sourceConditionalEdges);

            // 注册条件边
            graph.addConditionalEdges(sourceNodeId, routingFunction, pathMap);
        }
    }

    /**
     * 设置图的入口和出口节点
     */
    private void setEntryAndExit(
            StateGraph<AgentState> graph,
            List<WorkflowNode> nodes,
            List<WorkflowEdge> defaultEdges,
            List<WorkflowEdge> conditionalEdges) throws Exception {

        // 合并所有边用于入口/出口检测
        List<WorkflowEdge> allEdges = new ArrayList<>();
        allEdges.addAll(defaultEdges);
        allEdges.addAll(conditionalEdges);

        // 找到入口节点（没有前置节点的节点）
        WorkflowNode entryNode = findEntryNode(nodes, allEdges);
        if (entryNode != null) {
            log.info("设置入口节点: {}", entryNode.getId());
            graph.addEdge(StateGraph.START, entryNode.getId());
        } else {
            log.warn("未找到入口节点，使用第一个节点作为入口");
            if (!nodes.isEmpty()) {
                graph.addEdge(StateGraph.START, nodes.get(0).getId());
            }
        }

        // 对于有条件边的源节点，LangGraph 会通过路由函数的 fallback 自动处理出口
        // 对于只有默认边的节点，按原有逻辑设置出口
        WorkflowNode exitNode = findExitNode(nodes, defaultEdges);
        if (exitNode != null && !isSourceOfConditionalEdge(exitNode.getId(), conditionalEdges)) {
            log.info("设置出口节点: {}", exitNode.getId());
            graph.addEdge(exitNode.getId(), StateGraph.END);
        } else if (!nodes.isEmpty()) {
            // 如果所有叶子节点都有条件边，由路由函数的 fallback (END) 处理
            log.info("出口由条件路由 fallback 处理");
        }
    }

    /**
     * 查找入口节点（没有前置节点）
     */
    private WorkflowNode findEntryNode(List<WorkflowNode> nodes, List<WorkflowEdge> edges) {
        for (WorkflowNode node : nodes) {
            boolean hasIncomingEdge = edges.stream()
                .anyMatch(edge -> edge.getTarget().equals(node.getId()));

            if (!hasIncomingEdge) {
                return node;
            }
        }
        return null;
    }

    /**
     * 查找出口节点（没有后继节点，仅基于默认边）
     */
    private WorkflowNode findExitNode(List<WorkflowNode> nodes, List<WorkflowEdge> defaultEdges) {
        for (WorkflowNode node : nodes) {
            boolean hasOutgoingEdge = defaultEdges.stream()
                .anyMatch(edge -> edge.getSource().equals(node.getId()));

            if (!hasOutgoingEdge) {
                return node;
            }
        }
        return null;
    }

    /**
     * 检查节点是否是条件边的源节点
     */
    private boolean isSourceOfConditionalEdge(String nodeId, List<WorkflowEdge> conditionalEdges) {
        return conditionalEdges.stream()
                .anyMatch(edge -> edge.getSource().equals(nodeId));
    }
}
