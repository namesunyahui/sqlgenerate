package org.example.sqlgenerate;

import jakarta.annotation.Resource;
import org.example.sqlgenerate.createSql.model.CreateFrom;
import org.example.sqlgenerate.createSql.service.GenerateSqlService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestCreateSql {

    @Resource
    private GenerateSqlService generateSqlService;

//    @Test
//    public void TestCreateOracle(){
//        CreateFrom createFrom = new CreateFrom();
//        createFrom.setTableName("test");
//        createFrom.setTableRemark("测试表");
//        createFrom.setFileId("670a1d946861a259b6049d23");
//
//        String string = generateSqlService.generateOracleSql(createFrom);
//        System.out.println(string);
//
//    }




}
