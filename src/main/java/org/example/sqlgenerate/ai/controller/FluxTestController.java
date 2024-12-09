package org.example.sqlgenerate.ai.controller;

import com.zhipu.oapi.service.v4.model.ModelData;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import jakarta.annotation.Resource;
import org.example.sqlgenerate.ai.event.AiManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 测试Flux的架构的使用
 */
@RestController("flux")
public class FluxTestController {

    @Resource
    private AiManager aiManager;

    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Hello, Reactive World!");
    }

    @GetMapping("/numbers")
    public Flowable<String> numbers() {


        Flowable<ModelData> modelDataFlowable = aiManager.doStreamRequest("你作为一个问题大全，对我提出的问题做出解答", "你可以做些什么", null);

        return modelDataFlowable.observeOn(Schedulers.io()).map(modelData -> modelData.getChoices().get(0).getDelta().getContent());

    }

}
