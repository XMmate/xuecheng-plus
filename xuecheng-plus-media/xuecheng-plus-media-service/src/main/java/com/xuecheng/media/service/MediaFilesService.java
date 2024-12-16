package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.dto.QueryFileParamsDto;
import com.xuecheng.media.dto.UploadFileParamsDto;
import com.xuecheng.media.dto.UploadFileResultDto;
import com.xuecheng.media.po.MediaFiles;
import com.xuecheng.media.po.MediaProcess;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface MediaFilesService {
    public UploadFileResultDto uploadFile(MultipartFile upload) throws NoSuchAlgorithmException, IOException, InvalidKeyException;

    PageResult<MediaFiles> querFileList(PageParams pageParams, QueryFileParamsDto queryFileParams);


    MediaFiles getFileById(String mediaId);

    public UploadFileResultDto uploadFile(MultipartFile uploadFile, String objectName) throws NoSuchAlgorithmException, IOException;

    String uploadHtml(MultipartFile upload, String objectName) throws NoSuchAlgorithmException, IOException;
}
