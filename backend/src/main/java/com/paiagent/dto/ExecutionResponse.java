package com.paiagent.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 工作流执行响应 DTO
 */
@Data
public class ExecutionResponse {

    private Long executionId;
    private String status;
    private List<NodeResult> nodeResults;
    private String outputData;
    private Integer duration;

    /**
     * 分支路径信息（源节点 ID -> 可能的目标节点 ID 列表）
     */
    private Map<String, List<String>> branchingPaths;

    /**
     * 实际执行的分支追踪（分支源节点 ID -> 实际选择的目标节点 ID）
     */
    private Map<String, String> branchesTaken;
    
    @Data
    public static class NodeResult {
        private String nodeId;
        private String nodeName;
        private String status;
        private String input;
        private String output;
        private Integer duration;
        private String error;
    }
}
