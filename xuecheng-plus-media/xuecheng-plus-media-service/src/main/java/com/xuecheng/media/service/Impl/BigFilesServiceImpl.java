package com.xuecheng.media.service.Impl;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.dto.UploadFileParamsDto;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.po.MediaFiles;
import com.xuecheng.media.po.MediaProcess;
import com.xuecheng.media.service.BigFilesService;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
//TODO
//删除算法有问题
//校验md5有问题
@Slf4j
@Service
public class BigFilesServiceImpl implements BigFilesService {
    //视频文件桶
    @Value("${minio.bucket.videofiles}")
    private String bucket_videoFiles;
    @Autowired
    private MediaFilesMapper mediaFilesMapper;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MediaProcessMapper mediaProcessMapper;

    /*检查文件是否上传*/
    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            return RestResponse.success(false);
        }
        return RestResponse.success(true);
    }

    /*校验分块是否存在*/
    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        //得到分块文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;

        //文件流
        InputStream fileInputStream = null;
        try {
            fileInputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket_videoFiles)
                            .object(chunkFilePath)
                            .build());

            if (fileInputStream != null) {
                //分块已存在
                return RestResponse.success(true);
            }
        } catch (Exception e) {

        }
        //分块未存在
        return RestResponse.success(false);
    }





    /*上传分块*/
    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, MultipartFile upload) {
        try {
            InputStream inputStream = upload.getInputStream();
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(getChunkFileFolderPath(fileMd5)+chunk)
                    .bucket(bucket_videoFiles).stream(inputStream, inputStream.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);
            log.debug("上传分块到minio成功,bucket:{},objectName:{}", bucket_videoFiles, getChunkFileFolderPath(fileMd5));
            System.out.println("上传成功");
            return RestResponse.success(true);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件到minio出错,bucket:{},objectName:{},错误原因:{}", bucket_videoFiles, getChunkFileFolderPath(fileMd5), e.getMessage(), e);
            XueChengPlusException.cast("上传文件到文件系统失败");
        }
        return RestResponse.success(false);
    }





    /**
     * 合并分块
     *
     *
     * @param fileMd5
     * @param chunkTotal
     *
     * @return
     */
    @Override
    public RestResponse mergechunks( String fileMd5, int chunkTotal,String fileName) {
        Long companyId = 1232141425L;
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //文件扩展名
        String extName = fileName.substring(fileName.lastIndexOf("."));
        String mergeFilePath = getFilePathByMd5(fileMd5, extName);
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFileType("001002");
        uploadFileParamsDto.setTags("课程视频");
        uploadFileParamsDto.setRemark("");
        uploadFileParamsDto.setFilename(fileName);
        uploadFileParamsDto.setFileSize(getMinioFileSiez(bucket_videoFiles,mergeFilePath));

        //组成将分块文件路径组成 List<ComposeSource>
        List<ComposeSource> sourceObjectList = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal)
                .map(i -> ComposeSource.builder()
                        .bucket(bucket_videoFiles)
                        .object(chunkFileFolderPath.concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());

        try {
            ObjectWriteResponse response = minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(bucket_videoFiles)
                            .object(mergeFilePath)
                            .sources(sourceObjectList)
                            .build());
            log.debug("合并文件成功:{}", mergeFilePath);
        } catch (Exception e) {
            log.info("合并文件失败bucket{},object{}err{}", bucket_videoFiles, mergeFilePath, sourceObjectList);
            return RestResponse.validfail(false, "合并文件失败。");
        }


        if (true) {
            MediaFiles mediaFiles = addBigFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_videoFiles, mergeFilePath);
            //删除分块文件
            boolean b = deleteAllFile(bucket_videoFiles, getDeleteFileFolderPath(fileMd5));
            if (!b) {
                log.info("删除分块文件{}/{}出错",bucket_videoFiles,chunkFileFolderPath);
            }
            return RestResponse.success(mediaFiles);
        } else {
            log.info("{}文件上传过程中发生错误，请重新上传",fileName);
            return RestResponse.validfail(fileName+"文件上传过程中发生错误，请重新上传");
        }

    }


    /*获取合并后的文件路径*/
    private String getFilePathByMd5(String fileMd5, String extName) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5+ "/"+fileMd5 + extName;
    }


    /*获取minio里面文件的MD5*/
    private String getMinioMd5(String bucket, String objectName) {

        StatObjectResponse stat = null;
        try {
            stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            log.info("获取MD5失败");
            return null;
        }

        // 获取 ETag
        String eTag = stat.etag();
        // 如果 ETag 是 MD5，则返回 ETag
        return eTag;
    }


    /*批量删除文件*/
    public boolean deleteAllFile(String bucket, String objectName) {
        boolean tag = true;
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(objectName).build());
           log.info("{}里面的{}删除成功",bucket,objectName);
        } catch (Exception e) {
            tag = false;
            log.info("{}里面的{}删除失败，原因是{}",bucket,objectName,e);

        }
        return tag;
    }



/*保存文件信息到数据库*/
    @Transactional
    public MediaFiles addBigFilesToDb(Long companyId, String fileMd5,UploadFileParamsDto uploadFileParamsDto ,String bucket, String objectName){

        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles==null){
            mediaFiles = new MediaFiles();
            BeanUtils.copyProperties(uploadFileParamsDto,mediaFiles);

            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setUrl("/"+bucket+"/"+objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert<=0){
                log.error("保存文件信息到数据库失败,{}",mediaFiles.toString());
                XueChengPlusException.cast("保存文件信息失败");
            }
            addMediaProcessDb(mediaFiles);
            log.debug("保存文件信息到数据库成功,{}",mediaFiles.toString());
        }
            return mediaFiles;
    }

    /*获取合并后文件的大小*/
    public Long getMinioFileSiez(String bucket,String objectName){
        StatObjectResponse stat = null;
        try {
            stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            log.info("获取文件{}/{}大小失败原因是{}",bucket,objectName,e);
            return null;
        }
        // 获取文件大小
        return stat.size();
    }

    //得到分块文件的目录
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    //删除文件夹
    private String getDeleteFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" ;
    }

    private void addMediaProcessDb(MediaFiles mediaFiles){
        //文件名称
        String filename = mediaFiles.getFilename();
        //文件扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //文件mimeType
        String mimeType = getMimeType(extension);
        //如果是avi视频添加到视频待处理表
        if(mimeType.equals("video/x-msvideo")){
            MediaProcess mediaProcess = new MediaProcess();
            BeanUtils.copyProperties(mediaFiles,mediaProcess);
            mediaProcess.setStatus("1");//未处理
            mediaProcessMapper.insert(mediaProcess);
        }
    }

    /*取出文件类型*/
    private String getMimeType(String extension){
        if(extension==null)
            extension = "";
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        //通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if(extensionMatch!=null){
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }



}