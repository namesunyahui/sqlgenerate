package org.example.sqlgenerate.createSql.controller;


import jakarta.annotation.Resource;
import org.example.sqlgenerate.createSql.model.CreateFrom;
import org.example.sqlgenerate.createSql.service.GenerateSqlService;
import org.example.sqlgenerate.mongodb.entity.FileInfo;
import org.example.sqlgenerate.mongodb.service.FileInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@RestController
@RequestMapping("/sql")
public class FileController {

    @Resource
    private GenerateSqlService generateSqlService;




    @PostMapping("/generateOracleSql")
    public String uploadFile(@ModelAttribute CreateFrom createFrom) {
        return generateSqlService.generateOracleSql(createFrom);
    }











}
