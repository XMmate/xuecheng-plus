package com.xuecheng.media.conrtoller;


import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.dto.QueryFileParamsDto;
import com.xuecheng.media.dto.UploadFileResultDto;
import com.xuecheng.media.po.MediaFiles;
import com.xuecheng.media.service.MediaFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
public class MediaFilesController {
    @Autowired
    private MediaFilesService mediaFilesService;
        /**
     * 上传图片
     * @param upload
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile upload) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        return mediaFilesService.uploadFile(upload);
    }

    /**
     * 上传html
     * @param upload
     * @param objectName
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    @RequestMapping(value = "/upload/coursehtml", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadhtml(@RequestPart("filedata") MultipartFile upload,@RequestParam(value = "objectName",required=false) String objectName) throws IOException, NoSuchAlgorithmException {
        return mediaFilesService.uploadHtml(upload,objectName);
    }


    /**
     * 绑定视频和课程
     * @param pageParams
     * @param queryFileParams
     * @return
     */
    @PostMapping("/files")
    public PageResult<MediaFiles> querFileList(PageParams pageParams, @RequestBody(required=false) QueryFileParamsDto queryFileParams){
        return mediaFilesService.querFileList(pageParams,queryFileParams);
    }



}
