package org.example.sqlgenerate.ai.service.Impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import io.reactivex.Flowable;
import jakarta.annotation.Resource;
import org.example.sqlgenerate.ai.model.ZhiPuChatModel;
import org.example.sqlgenerate.ai.service.AiZhipuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class AiZhipuServiceIpml implements AiZhipuService {

    private final static Logger logger = LoggerFactory.getLogger(AliTongYiServiceImpl.class);

    @Value("${spring.cloud.zhipu.api-key}")
    private String apiKey ;

    @Resource
    private ClientV4 client;


    private static final ObjectMapper mapper = defaultObjectMapper();


    public static ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
//        mapper.addMixIn(ChatFunction.class, ChatFunctionMixIn.class);
//        mapper.addMixIn(ChatCompletionRequest.class, ChatCompletionRequestMixIn.class);
//        mapper.addMixIn(ChatFunctionCall.class, ChatFunctionCallMixIn.class);
        return mapper;
    }



//    private MapStreamToAccumulator mapStreamToAccumulator;

    @Override
    public String chatMessage(String msg) {
        return "";
    }


    /**
     * 同步调用
     *
     * @return
     */
    @Override
    public String testInvoke(ZhiPuChatModel chatModel) {

        String invokeMethod ;
        if(chatModel.getIsAsync()){
            invokeMethod=Constants.invokeMethodAsync;
        }else {
            invokeMethod = Constants.invokeMethod;
        }

        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), chatModel.getMessage());
        messages.add(chatMessage);
        String requestId = String.format("request-id:", System.currentTimeMillis());

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(chatModel.getIsStream()) //同步调用时应设置为false或省略。表示模型在生成所有内容后一次性返回所有内容。
                .invokeMethod(invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .doSample(false) //采样策略
                .build();
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        if(chatModel.getIsAsync()){
            return invokeModelApiResp.getData().getId();
        }else {
            return invokeModelApiResp.getData().getChoices().get(0).getMessage().getContent().toString();
        }


    }




    /* sse调用*/
    @Override
    public   void testSseInvoke() {
        ClientV4 client = new ClientV4.Builder(apiKey).build();

        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "你可以做些什么");
        messages.add(chatMessage);
        String requestId = String.format("request-id:", System.currentTimeMillis());

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.TRUE)
                .messages(messages)
                .requestId(requestId)
                .build();
        ModelApiResponse sseModelApiResp = client.invokeModelApi(chatCompletionRequest);
        if (sseModelApiResp.isSuccess()) {
            AtomicBoolean isFirst = new AtomicBoolean(true);
            ChatMessageAccumulator chatMessageAccumulator = mapStreamToAccumulator(sseModelApiResp.getFlowable())
                    .doOnNext(accumulator -> {
                        {
                            if (isFirst.getAndSet(false)) {
                                System.out.print("Response: ");
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getTool_calls() != null) {
                                String jsonString = mapper.writeValueAsString(accumulator.getDelta().getTool_calls());
                                System.out.println("tool_calls: " + jsonString);
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getContent() != null) {
                                System.out.print(accumulator.getDelta().getContent());
                            }
                        }
                    })
                    .doOnComplete(System.out::println)
                    .lastElement()
                    .blockingGet();

            Choice choice = new Choice();
            List<Choice> choices = new ArrayList<>();
            choices.add(choice);
            ModelData data = new ModelData();
            data.setChoices(choices);
            data.setUsage(chatMessageAccumulator.getUsage());
            data.setId(chatMessageAccumulator.getId());
            data.setCreated(chatMessageAccumulator.getCreated());
            data.setRequestId(chatCompletionRequest.getRequestId());
            sseModelApiResp.setFlowable(null);
            sseModelApiResp.setData(data);
        }
        System.out.println("model output:" + JSON.toJSONString(sseModelApiResp));
    }


    public static Flowable<ChatMessageAccumulator> mapStreamToAccumulator(Flowable<ModelData> flowable) {
        return flowable.map(chunk -> {
            return new ChatMessageAccumulator(chunk.getChoices().get(0).getDelta(), null, chunk.getChoices().get(0), chunk.getUsage(), chunk.getCreated(), chunk.getId());
        });
    }

    @Override
    public void testNonFunctionSSE() throws JsonProcessingException {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "你可以做些什么");
        messages.add(chatMessage);
        HashMap<String, Object> extraJson = new HashMap<>();
        extraJson.put("temperature", 0.5);
        extraJson.put("max_tokens", 3);

        String requestId = String.format("request-id:", System.currentTimeMillis());
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.TRUE)
                .messages(messages)
                .requestId(requestId)
                .extraJson(extraJson)
                .build();
        ModelApiResponse sseModelApiResp = client.invokeModelApi(chatCompletionRequest);
        // stream 处理方法
        if (sseModelApiResp.isSuccess()) {
            AtomicBoolean isFirst = new AtomicBoolean(true);
            List<Choice> choices = new ArrayList<>();
            ChatMessageAccumulator chatMessageAccumulator = mapStreamToAccumulator(sseModelApiResp.getFlowable())
                    .doOnNext(accumulator -> {
                        {
                            if (isFirst.getAndSet(false)) {
                                logger.info("Response: ");
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getTool_calls() != null) {
                                String jsonString = mapper.writeValueAsString(accumulator.getDelta().getTool_calls());
                                logger.info("tool_calls: {}", jsonString);
                            }
                            if (accumulator.getDelta() != null && accumulator.getDelta().getContent() != null) {
                                logger.info("accumulator.getDelta().getContent(): {}", accumulator.getDelta().getContent());
                            }
                            choices.add(accumulator.getChoice());
                        }
                    })
                    .doOnComplete(System.out::println)
                    .lastElement()
                    .blockingGet();


            ModelData data = new ModelData();
            data.setChoices(choices);
            data.setUsage(chatMessageAccumulator.getUsage());
            data.setId(chatMessageAccumulator.getId());
            data.setCreated(chatMessageAccumulator.getCreated());
            data.setRequestId(chatCompletionRequest.getRequestId());
            sseModelApiResp.setFlowable(null);// 打印前置空
            sseModelApiResp.setData(data);
        }
        logger.info("model output: {}", JSON.toJSONString(sseModelApiResp));
    }


}
