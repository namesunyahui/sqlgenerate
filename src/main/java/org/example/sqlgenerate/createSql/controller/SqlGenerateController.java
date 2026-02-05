package org.example.sqlgenerate.createSql.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.sqlgenerate.createSql.model.CreateFrom;
import org.example.sqlgenerate.createSql.service.GenerateSqlService;
import org.example.sqlgenerate.createSql.util.ExcelTemplateGenerator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/sql")
@Tag(name = "SQL生成", description = "根据Excel生成SQL语句")
public class SqlGenerateController {

    @Resource
    private GenerateSqlService generateSqlService;

    @Operation(summary = "下载Excel模板", description = "下载表结构定义的Excel模板")
    @GetMapping("/download/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            byte[] template = ExcelTemplateGenerator.generateTemplate();

            String filename = URLEncoder.encode("表结构模板.xlsx", StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(template.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(template);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "生成SQL", description = "上传Excel文件，生成对应的SQL语句")
    @PostMapping("/generate")
    public ResponseEntity<String> generateSql(
            @RequestParam("file") MultipartFile file,
            @RequestParam("tableName") String tableName,
            @RequestParam(value = "tableRemark", required = false, defaultValue = "") String tableRemark,
            @RequestParam(value = "database", required = false, defaultValue = "mysql") String database) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("文件不能为空");
            }

            CreateFrom createFrom = new CreateFrom();
            createFrom.setTableName(tableName);
            createFrom.setTableRemark(tableRemark);
            createFrom.setDatabase(database);
            createFrom.setFile(file);

            String sql = generateSqlService.generateSqlFromFile(createFrom);
            return ResponseEntity.ok(sql);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("生成失败: " + e.getMessage());
        }
    }

    @Operation(summary = "生成MySQL SQL", description = "上传Excel文件，生成MySQL的CREATE TABLE语句")
    @PostMapping("/generate/mysql")
    public ResponseEntity<String> generateMysqlSql(
            @RequestParam("file") MultipartFile file,
            @RequestParam("tableName") String tableName,
            @RequestParam(value = "tableRemark", required = false, defaultValue = "") String tableRemark) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("文件不能为空");
            }

            CreateFrom createFrom = new CreateFrom();
            createFrom.setTableName(tableName);
            createFrom.setTableRemark(tableRemark);
            createFrom.setDatabase("mysql");
            createFrom.setFile(file);

            String sql = generateSqlService.generateSqlFromFile(createFrom);
            return ResponseEntity.ok(sql);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("生成失败: " + e.getMessage());
        }
    }

    @Operation(summary = "生成Oracle SQL", description = "上传Excel文件，生成Oracle的CREATE TABLE语句")
    @PostMapping("/generate/oracle")
    public ResponseEntity<String> generateOracleSql(
            @RequestParam("file") MultipartFile file,
            @RequestParam("tableName") String tableName,
            @RequestParam(value = "tableRemark", required = false, defaultValue = "") String tableRemark) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("文件不能为空");
            }

            CreateFrom createFrom = new CreateFrom();
            createFrom.setTableName(tableName);
            createFrom.setTableRemark(tableRemark);
            createFrom.setDatabase("oracle");
            createFrom.setFile(file);

            String sql = generateSqlService.generateSqlFromFile(createFrom);
            return ResponseEntity.ok(sql);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("生成失败: " + e.getMessage());
        }
    }
}
