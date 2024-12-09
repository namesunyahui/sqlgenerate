package org.example.sqlgenerate.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.sqlgenerate.ai.model.ZhiPuChatModel;

public interface AiZhipuService {

    String chatMessage(String msg);

    String testInvoke(ZhiPuChatModel chatModel);

    void testSseInvoke();

    void testNonFunctionSSE() throws JsonProcessingException;
}
