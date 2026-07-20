package com.example.mcp.config;


import com.example.mcp.tools.BookTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class McpConfig {

//配置类的作用，原先的BookTool知识让spring知道这是一个AI可以调用的函数，但是Mcp不会自己主动注册，这一步就是
//为了MCPServer可以主动识别，并且注册
    @Bean
    public ToolCallbackProvider bookTools(BookTool bookTool) {

        return MethodToolCallbackProvider
                .builder()
                .toolObjects(bookTool)
                .build();
    }

}