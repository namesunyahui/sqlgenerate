package org.example.sqlgenerate.createSql.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.sqlgenerate.createSql.model.CreateFrom;
import org.example.sqlgenerate.createSql.service.GenerateSqlService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/sql")
@Tag(name = "sql生成", description = "这个主要用对sql的生成")
public class SqlGenerateController {

    @Resource
    private GenerateSqlService generateSqlService;




    @Operation(summary = "调用方法生成对应表的sql", description = "更详细的API描述无")
    @PostMapping("/generateOracleSql")
    public String uploadFile(@RequestBody CreateFrom createFrom) {
        return generateSqlService.generateSql(createFrom);
//        return generateSqlService.generateOracleSql(createFrom);
    }











}
