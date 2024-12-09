package org.example.sqlgenerate.ai.service.Impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
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
import lombok.extern.slf4j.Slf4j;
import org.example.sqlgenerate.ai.service.AliTongYiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class AliTongYiServiceImpl implements AliTongYiService {
    private static final String requestIdTemplate = "mycompany-%d";


    @Value("${spring.cloud.api.key}")
    private String apiKey;


    /**
     * 预设角色
     */
    private static final String ROLE = "You are a helpful assistant";


    private static final ObjectMapper mapper = new ObjectMapper();


    public static ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return mapper;
    }

    @Override
    public String chatMessage(String message) {
        try {
            GenerationResult generationResult = callWithMessage(ROLE,message);
            return generationResult.getOutput().getChoices().get(0).getMessage().getContent();
        } catch (NoApiKeyException | InputRequiredException e) {
//            new MyExceptionHandler("请检查配置文件是否正确");
            throw new RuntimeException(e);

        }
    }


    /**
     * 调用AI接口，并返回结果
     * @param role 角色
     * @param message 问题
     * @return
     * @throws ApiException
     * @throws NoApiKeyException
     * @throws InputRequiredException
     */
    public GenerationResult callWithMessage(String role,String message) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(role)
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                //这里是问的问题
                .content(message)
                .build();
        GenerationParam param = GenerationParam.builder()
                .model("qwen-max")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .temperature(0.8f)
                .apiKey(apiKey) // 添加 API key 到参数中
                .build();
        return gen.call(param);
    }



}
