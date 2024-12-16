package com.xuecheng.media.service;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.dto.UploadFileParamsDto;
import org.springframework.web.multipart.MultipartFile;

public interface BigFilesService {
    /*查询文件*/
    public RestResponse<Boolean> checkFile(String fileMd5);

    /*查询分块*/
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

      /*上传分块*/
    public RestResponse uploadChunk(String fileMd5, int chunk, MultipartFile file);


    /*合并分块*/
    public RestResponse mergechunks( String fileMd5, int chunkTotal,String fileName);
}
