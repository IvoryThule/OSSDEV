package com.whu.pet.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.whu.pet.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${file.upload-path}")
    private String uploadPath;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }

        // 获取文件后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);

        // 生成新文件名
        String newFileName = IdUtil.simpleUUID() + "." + suffix;

        // 按日期分目录
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = datePath + "/" + newFileName;
        String fullPath = uploadPath + relativePath;

        // 创建目录
        File destFile = new File(fullPath);
        FileUtil.mkParentDirs(destFile);

        try {
            // 保存文件
            file.transferTo(destFile);
            // 返回访问路径
            String accessUrl = "/uploads/" + relativePath;
            return Result.success("上传成功", accessUrl);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}
