package org.example.sqlgenerate.mongodb.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.sqlgenerate.mongodb.entity.FileInfo;
import org.example.sqlgenerate.mongodb.service.FileInfoRepository;
import org.example.sqlgenerate.mongodb.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/file")
public class FileManageController {

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/uploadMongoDB")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileName(file.getOriginalFilename());
            fileInfo.setContentType(file.getContentType());
            fileInfo.setFileSize(file.getSize());
            fileInfo.setFileContent(file.getBytes());
            fileInfo.setUploadDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            // 保存文件信息到MongoDB
            fileInfoRepository.save(fileInfo);
            return fileInfo.getId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFileGrid(@RequestParam("file") MultipartFile file) throws IOException {
        // 调用服务层方法上传文件
        String fileId = fileStorageService.uploadFile(file);
        System.out.println(fileId);

        // 返回文件的ID
        return new ResponseEntity<>(fileId, HttpStatus.OK);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileId, HttpServletResponse response) throws IOException {
        try {
            // 调用服务层方法获取文件的GridFSDownloadStream（它是InputStream）
            InputStream inputStream = fileStorageService.downloadFile(fileId);
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

            // 设置Excel文件的响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")); // .xlsx
            // 如果你的文件是.xls格式，使用下面的MediaType
            // headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel")); // .xls
            headers.setContentDispositionFormData("attachment", fileId + ".xlsx"); // 确保文件名以.xlsx结尾

            // 返回ResponseEntity，包含InputStreamResource和响应头
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(inputStreamResource);
        } catch (FileNotFoundException e) {
            // 如果文件未找到，则返回404错误
            return ResponseEntity.notFound().build();
        }
    }




}
