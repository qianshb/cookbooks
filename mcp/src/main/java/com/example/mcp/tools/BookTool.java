package com.example.mcp.tools;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;


@Service
public class BookTool {


    @Tool(
            name = "queryBook",
            description = "根据书名查询图书信息，返回图书作者、类型和简介"
    )
    public String queryBook(String bookName) {


        if ("三体".equals(bookName)) {

            return """
                    书名：三体
                    作者：刘慈欣
                    类型：科幻小说
                    """;
        }


        return "没有找到图书：" + bookName;
    }
}