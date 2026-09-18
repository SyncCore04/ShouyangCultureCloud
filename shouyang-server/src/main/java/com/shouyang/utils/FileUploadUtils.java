package com.shouyang.utils;

import com.shouyang.common.exception.BusinessException;
import com.shouyang.common.result.ResultCode;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传工具类
 * 提供文件类型校验、大小校验、唯一文件名生成、按日期分目录存储等功能
 *
 * @author shouyang
 */
public class FileUploadUtils {

    /**
     * 日期目录格式化（yyyy/MM）
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM");

    /**
     * 上传文件
     * <p>
     * 完整流程：校验空文件 → 校验类型 → 校验大小 → 生成日期目录 →
     * 生成 UUID 文件名 → 创建目录 → 保存文件 → 返回可访问 URL
     *
     * @param file         MultipartFile 文件对象
     * @param basePath     基础存储路径（如 ./uploads/）
     * @param allowedTypes 允许的文件类型，逗号分隔（如 jpg,jpeg,png,gif,webp）
     * @param maxSize      最大文件大小（字节）
     * @return 可访问的 URL 路径（如 /uploads/2024/01/abc123.jpg）
     */
    public static String upload(MultipartFile file, String basePath, String allowedTypes, long maxSize) {
        // 1. 校验文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAIL);
        }

        // 2. 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException("文件名格式不正确，缺少扩展名");
        }
        // 提取扩展名并转小写
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        // 3. 校验文件类型
        List<String> allowedTypeList = Arrays.asList(allowedTypes.toLowerCase().split(","));
        if (!allowedTypeList.contains(extension)) {
            throw new BusinessException(ResultCode.FILE_TYPE_NOT_ALLOWED);
        }

        // 4. 校验文件大小
        if (file.getSize() > maxSize) {
            throw new BusinessException(ResultCode.FILE_SIZE_EXCEEDED);
        }

        // 5. 生成日期目录路径（yyyy/MM/）
        String datePath = LocalDate.now().format(DATE_FORMATTER);

        // 6. 生成唯一文件名（UUID 去掉横杠 + 扩展名）
        // 防止中文文件名乱码和重名覆盖
        String newFilename = UUID.randomUUID().toString().replace("-", "") + "." + extension;

        // 7. 构建相对路径和完整存储路径
        String relativePath = datePath + "/" + newFilename;

        // 将基础路径转换为绝对路径，确保 transferTo 能正确定位
        // 修复相对路径导致的上传失败问题
        File baseDir = new File(basePath);
        if (!baseDir.isAbsolute()) {
            baseDir = baseDir.getAbsoluteFile();
        }
        String fullPath = baseDir.getAbsolutePath() + File.separator + relativePath;

        // 8. 创建目标目录（如果不存在）
        File destFile = new File(fullPath);
        File parentDir = destFile.getParentFile();
        if (!parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                throw new BusinessException(ResultCode.FILE_UPLOAD_FAIL + "：无法创建目录 " + parentDir.getAbsolutePath());
            }
        }

        // 9. 保存文件到磁盘
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAIL + "：" + e.getMessage());
        }

        // 10. 返回可访问的 URL 路径
        // WebMvcConfig 中已将 /uploads/** 映射到本地 uploads 目录
        return "/uploads/" + relativePath;
    }
}
