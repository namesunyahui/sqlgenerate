package org.example.sqlgenerate;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import jakarta.annotation.Resource;
import org.example.sqlgenerate.mongodb.service.FileInfoRepository;
import org.example.sqlgenerate.mongodb.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class TestMongoDb {

    @Value("${spring.cloud.api.key}")
    private String apiKey;
    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Resource
    private FileStorageService fileStorageService;

//    @Test
//    public void queryFile(){
//        // 查询文件信息
//        FileInfo fileInfo = fileInfoRepository.findById("6709e59311b9de10acdb0da1").orElse(null);
//        if (fileInfo != null) {
//            // 获取文件内容
//            String fileContent = fileInfo.getFileName();
//            // 处理文件内容...
//            System.out.println("文件"+fileContent);
//        }
//    }


//    @Test
    public void testDeleFile(){
        fileStorageService.deleteFile("670f233abf1d583395f214c8");
    }

//    @Test
    public void testAliAi(){
        try {
            GenerationResult result = this.callWithMessage();
            System.out.println(result.getOutput().getChoices().get(0).getMessage().getContent());
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            // 使用日志框架记录异常信息
            System.err.println("An error occurred while calling the generation service: " + e.getMessage());
        }
        System.exit(0);

    }

    public GenerationResult callWithMessage() throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("You are a helpful assistant.")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                //这里是问的问题
                .content("你可以帮我做什么")
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
