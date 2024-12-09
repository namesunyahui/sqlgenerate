package org.example.sqlgenerate.ai.event;

import lombok.Data;

import java.io.Serializable;

/***
 * ai生成题目请求
 */
@Data
public class AiGenerateQuestionRequest implements Serializable {


    /**
     * 应用id
     */
    private Long appId;

    /**
     * 要生成的题目数
     */
    private Integer questionsNum = 10;
    /**
     * 每个题目的选项数
     */
    private Integer optionsNum = 4;

    private static final long serialVersionUID = 1L;
}
