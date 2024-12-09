package org.example.sqlgenerate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.service.v4.embedding.Embedding;
import com.zhipu.oapi.service.v4.embedding.EmbeddingApiResponse;
import com.zhipu.oapi.service.v4.embedding.EmbeddingRequest;
import com.zhipu.oapi.service.v4.embedding.EmbeddingResult;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import com.zhipu.oapi.service.v4.model.ModelData;
import io.reactivex.Flowable;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import jakarta.annotation.Resource;
import org.example.sqlgenerate.ai.event.AiManager;
import org.example.sqlgenerate.ai.model.ZhiPuChatModel;
import org.example.sqlgenerate.ai.service.AiZhipuService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class TestZhipu {
    private static final Logger logger = LoggerFactory.getLogger(TestZhipu.class);
    @Resource
    private AiZhipuService aiZhipuService;
    @Qualifier("messageSource")
    @Autowired
    private MessageSource messageSource;

    @Resource
    private ClientV4 client;


    @Resource
    private AiManager aiManager;

    @Test
    public void testChatMessage(){

        ZhiPuChatModel zhiPuChatModel = new ZhiPuChatModel();
        zhiPuChatModel.setMessage("你可以做些什么");
       // zhiPuChatModel.setIsAsync(true);
        String string = aiZhipuService.testInvoke(zhiPuChatModel);
        System.out.println(string);
    }

    @Test
    public  void  testZhipuAycChat(){
        ZhiPuChatModel chatModel = new ZhiPuChatModel();
        chatModel.setMessage("我改如何给你定义一个角色");
        String string = aiZhipuService.testInvoke(chatModel);
        System.out.println(string);
    }

    @Test
    public void testZhipuSeeChat() throws JsonProcessingException {
        aiZhipuService.testSseInvoke();
//        aiZhipuService.testNonFunctionSSE();
    }

    /**
     * 调用 doStreamRequest 方法
     */
    @Test
    public void callDoStreamRequest() {
        // 构建消息列表
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage message1 = new ChatMessage();
        message1.setRole("user");
        message1.setContent("你好，世界！");
        messages.add(message1);

        ChatMessage message2 = new ChatMessage();
        message2.setRole("assistant");
        message2.setContent("你好！");
        messages.add(message2);

        // 设置温度参数
        float temperature = 0.7f;

        // 调用 doStreamRequest 方法
        Flowable<ModelData> response = aiManager.
                doStreamRequest(messages, temperature);

        // 订阅响应并处理数据
        response.subscribe(modelData -> {
            System.out.println("Received data: " + modelData.toString());
        }, error -> {
            logger.error("Error occurred: {}", error.getMessage(), error);
        });
    }


    @Test
    public void testDoStreamRequest() {
        // 构建测试数据
// 构建消息列表
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage message1 = new ChatMessage();
        message1.setRole("user");
        message1.setContent("你好，世界！");
        messages.add(message1);

        ChatMessage message2 = new ChatMessage();
        message2.setRole("assistant");
        message2.setContent("你好！");
        messages.add(message2);        Float temperature = 0.5f;

        // 构建模拟的响应
        ModelApiResponse mockApiResponse = new ModelApiResponse();
        Flowable<ModelData> mockFlowable = Flowable.just(new ModelData(), new ModelData());
        mockApiResponse.setFlowable(mockFlowable);

        // 模拟客户端行为
        //when(clientV4.invokeModelApi(any(ChatCompletionRequest.class))).thenReturn(mockApiResponse);

        // 调用被测试的方法
        Flowable<ModelData> result = aiManager.doStreamRequest(messages, temperature);

        // 创建一个测试订阅者
        TestSubscriber<ModelData> testSubscriber = new TestSubscriber<>();
        result.subscribe(testSubscriber);

        // 验证结果

        testSubscriber.assertComplete();
        testSubscriber.assertNoErrors();
    }


    @Test
    public void testEmbedding() {
        EmbeddingRequest world = EmbeddingRequest.builder().input("hello world").dimensions(512).model("embedding-3").build();
        EmbeddingApiResponse embeddingApiResponse = client.invokeEmbeddingsApi(world);
        EmbeddingResult data = embeddingApiResponse.getData();
        List<Embedding> data1 = data.getData();
        data1.forEach(embedding -> {
            logger.info("embedding:{}", embedding.getEmbedding());
            assert embedding.getEmbedding().size() == 512;
        });
        logger.info("apply:{}", embeddingApiResponse);
    }


}
