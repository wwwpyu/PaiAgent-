package com.paiagent.engine.llm.tool;

import lombok.Data;

import java.util.Map;
import java.util.function.Function;

/**
 * 工具/函数定义 DTO
 *
 * 描述一个可供 LLM 调用的函数，包含名称、描述、参数 JSON Schema
 * 以及对应的 Java 处理回调函数
 */
@Data
public class ToolDefinition {

    /**
     * 工具/函数名称（LLM 通过此名称来识别和调用）
     */
    private String name;

    /**
     * 工具描述（帮助 LLM 理解何时调用此工具）
     */
    private String description;

    /**
     * 参数 JSON Schema 定义
     * 遵循 JSON Schema 规范，描述函数参数的结构
     */
    private Map<String, Object> parameters;

    /**
     * Java 端的处理函数
     * 接收 LLM 传入的 JSON 参数 Map，返回处理结果的字符串
     */
    private Function<Map<String, Object>, String> handler;
}
