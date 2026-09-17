package com.shouyang.controller.admin;

import com.shouyang.common.result.Result;
import com.shouyang.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 后台文件上传 Controller
 * 提供单张/多张图片上传功能，上传接口需要管理员登录
 *
 * @author shouyang
 */
@RestController
@RequestMapping("/api/admin/upload")
public class AdminFileController {

    /**
     * 上传文件存储路径（从配置文件读取）
     */
    @Value("${file.upload-path:./uploads/}")
    private String uploadPath;

    /**
     * 允许的图片类型（从配置文件读取）
     */
    @Value("${file.allowed-image-types:jpg,jpeg,png,gif,webp}")
    private String allowedImageTypes;

    /**
     * 单张图片最大大小（字节，从配置文件读取，默认 5MB）
     */
    @Value("${file.max-image-size:5242880}")
    private Long maxImageSize;

    /**
     * 单张图片上传
     * <p>
     * 请求方式：multipart/form-data
     * 参数名：file
     * 支持格式：jpg, jpeg, png, gif, webp
     * 大小限制：5MB
     *
     * @param file 上传的图片文件
     * @return 可访问的图片 URL 路径
     */
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = FileUploadUtils.upload(file, uploadPath, allowedImageTypes, maxImageSize);
        return Result.success("上传成功", url);
    }

    /**
     * 多张图片上传
     * <p>
     * 请求方式：multipart/form-data
     * 参数名：files（数组）
     * 每张图片独立校验类型和大小
     *
     * @param files 上传的图片文件数组
     * @return 可访问的图片 URL 路径列表
     */
    @PostMapping("/images")
    public Result<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        // 校验文件数组是否为空
        if (files == null || files.length == 0) {
            return Result.error("请选择要上传的文件");
        }

        List<String> urls = new ArrayList<>();
        // 逐张上传，任意一张失败会抛异常中断
        for (MultipartFile file : files) {
            String url = FileUploadUtils.upload(file, uploadPath, allowedImageTypes, maxImageSize);
            urls.add(url);
        }

        return Result.success("上传成功，共 " + urls.size() + " 张", urls);
    }
}
