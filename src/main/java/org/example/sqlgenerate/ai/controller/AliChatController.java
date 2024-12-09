package org.example.sqlgenerate.ai.controller;

import com.alibaba.fastjson.JSON;
import com.zhipu.oapi.service.v4.model.ModelData;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.example.sqlgenerate.ai.event.AiGenerateQuestionRequest;
import org.example.sqlgenerate.ai.event.AiManager;
import org.example.sqlgenerate.ai.service.AliTongYiService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/ai")
@Tag(name = "ai问答", description = "处理各种ai问答的场景")
public class AliChatController {

    @Resource
    private AliTongYiService   aliTongYiService;

    @Resource
    private AiManager aiManager;

    @RequestMapping("/chat")
    public String chatMessage(String message) {
        return aliTongYiService.chatMessage(message);
    }

    /**
     * AI生成题目（非流式）
     *
     * @param aiGenerateQuestionRequest
     * @return
     */
    @PostMapping("/ai_generate")
    public String aiGenerateQuestion(@RequestBody AiGenerateQuestionRequest aiGenerateQuestionRequest) {
        Integer questionsNum = aiGenerateQuestionRequest.getQuestionsNum();
        Integer optionsNum = aiGenerateQuestionRequest.getOptionsNum();
        String userPrompt = getUserPrompt( questionsNum, optionsNum);
        String result = aiManager.doSyncStableRequest(SystemPromptQuestion,userPrompt);
        int startIndex = result.indexOf("[");
        int endIndex = result.lastIndexOf("]");
        String jsonStr = result.substring(startIndex, endIndex + 1);
        System.out.println(jsonStr);
        return  jsonStr;
    }


    /**
     * AI生成题目（流式）
     *+
     * @param aiGenerateQuestionRequest
     * @return
     */
    @GetMapping("/ai_generate/sse")
    public SseEmitter aiGenerateQuestionSSE(AiGenerateQuestionRequest aiGenerateQuestionRequest) {
        Integer questionsNum = aiGenerateQuestionRequest.getQuestionsNum();
        Integer optionsNum = aiGenerateQuestionRequest.getOptionsNum();
        String userPrompt = getUserPrompt( questionsNum, optionsNum);
        Flowable<ModelData> modelDataFlowable = aiManager.doStreamRequest(SystemPromptQuestion, userPrompt, null);
        AtomicInteger count = new AtomicInteger();//用AtomicInteger可以保证线程安全（因为在使用流的时候，其实是个多线程的环境，普通的int无法保证线程安全）
        SseEmitter sseEmitter = new SseEmitter();
        StringBuilder stringBuilder = new StringBuilder();
        //todo gpt提供了另一种方法
        modelDataFlowable.observeOn(Schedulers.io())
                .map(modelData -> modelData.getChoices().get(0).getDelta().getContent())
                //.map(modelData -> modelData.replaceAll("\\s", ""))//\\s 是一个正则表达式，表示匹配任意空白字符。 replaceAll("\\s", "") 这行代码用于去除字符串中的所有空白字符，包括空格、制表符(Tab)、换行符和其他空白字符
                .filter(StringUtils::isNotBlank)
                .flatMap(message -> {
                    List<Character> characterList = new ArrayList<>();
                    for (char c : message.toCharArray()) {
                        characterList.add(c);
                    }
                    return Flowable.fromIterable(characterList);
                })//latMap 是 ReactiveX（RxJava）库中的一个非常重要且常用的操作符。它的作用是将源 Observable 或 Flowable 发送的每个数据项映射为一个新的 Observable 或 Flowable
                .doOnNext(character -> {
                    if (character.equals('{')) {
                        count.addAndGet(1);
                    }
                    if(count.get() > 0){
                        stringBuilder.append(character);
                    }
                    if (character.equals('}')) {
                        count.addAndGet(-1);
                        if (count.get() == 0){
                            sseEmitter.send(JSON.toJSONString(stringBuilder.toString()));
                            stringBuilder.setLength(0);
                        }
                    }
                })
                .doOnError(sseEmitter::completeWithError)
                .doOnComplete(sseEmitter::complete)
                .subscribe();
        return sseEmitter;

    }

    /**
     * 流式回答问题
     *
     * @param requestMessage
     * @return
     */
    @GetMapping("/ai_generate_reply/sse")
    public Flowable<String> aiGenerateReplySse(String requestMessage) {

        System.out.println("收到请求：" + requestMessage);
        Flowable<ModelData> modelDataFlowable = aiManager.doStreamRequest("你作为一个Java架构师，对我提出的问题，进行分析解答", requestMessage, null);
        return modelDataFlowable.observeOn(Schedulers.io()).map(modelData -> modelData.getChoices().get(0).getDelta().getContent());


    }


    private static final String SystemPromptQuestion = "你是一位严谨的出题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "应用类别，\n" +
            "要生成的题目数，\n" +
            "每个题目的选项数\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来出题：\n" +
            "1. 要求：题目和选项尽可能地短，题目不要包含序号，每题的选项数以我提供的为主，题目不能重复\n" +
            "2. 严格按照下面的 json 格式输出题目和选项\n" +
            "```\n" +
            "[{\"options\":[{\"value\":\"选项内容\",\"key\":\"A\"},{\"value\":\"\",\"key\":\"B\"}],\"title\":\"题目标题\"}]\n" +
            "```\n" +
            "title 是题目，options 是选项，每个选项的 key 按照英文字母序（比如 A、B、C、D）以此类推，value 是选项内容\n" +
            "3. 检查题目是否包含序号，若包含序号则去除序号\n" +
            "4. 返回的题目列表格式必须为 JSON 数组\n";

    /**
     * 封装用户的prompt
     *
     * @param questionsNum
     * @param optionsNum
     * @return
     */
    public String getUserPrompt( Integer questionsNum, Integer optionsNum) {
        StringBuilder userPrompt = new StringBuilder();
        userPrompt.append(questionsNum).append("\n");
        userPrompt.append(optionsNum);
        return userPrompt.toString();
    }
}
