package com.xuecheng.media.service.Impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.dto.QueryFileParamsDto;
import com.xuecheng.media.dto.UploadFileParamsDto;
import com.xuecheng.media.dto.UploadFileResultDto;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.po.MediaFiles;
import com.xuecheng.media.service.MediaFilesService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Slf4j
public class MediaFilesServiceImpl implements MediaFilesService {

    //普通文件桶
    @Value("${minio.bucket.files}")
    private String bucket_files;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MediaFilesMapper mediaFilesMapper;

    //获取文件默认存储目录路径 年/月/日
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folder = sdf.format(new Date()).replace("-", "/")+"/";
        return folder;
    }


    //获取文件的md5
    private String getFileMd5(MultipartFile  file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = file.getBytes();
        byte[] md5Bytes = md.digest(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : md5Bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
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

    public boolean addMediaFilesToMinIO(String mimeType,String bucket, String objectName,MultipartFile upload ){
        try {
            InputStream inputStream = upload.getInputStream();
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(objectName)
                    .contentType(mimeType)
                    .bucket(bucket).stream(inputStream,inputStream.available(),-1)
                    .build();
            minioClient.putObject(putObjectArgs);
            log.debug("上传文件到minio成功,bucket:{},objectName:{}",bucket,objectName);
            System.out.println("上传成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件到minio出错,bucket:{},objectName:{},错误原因:{}",bucket,objectName,e.getMessage(),e);
            XueChengPlusException.cast("上传文件到文件系统失败");
        }
        return false;
    }

    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName){
        //从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");
            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert < 0) {
                log.error("保存文件信息到数据库失败,{}",mediaFiles.toString());
                XueChengPlusException.cast("保存文件信息失败");
            }
            log.debug("保存文件信息到数据库成功,{}",mediaFiles.toString());

        }
        return mediaFiles;

    }

    @Transactional
    @Override
    public UploadFileResultDto uploadFile(MultipartFile upload) throws IOException, NoSuchAlgorithmException {
        Long companyId = 1232141425L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        //文件大小
        uploadFileParamsDto.setFileSize(upload.getSize());
        //图片
        uploadFileParamsDto.setFileType("001001");
        uploadFileParamsDto.setFilename(upload.getOriginalFilename());//文件名称
        //文件大小
        long fileSize = upload.getSize();
        uploadFileParamsDto.setFileSize(fileSize);
        String filename = upload.getOriginalFilename();
        //文件扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //文件mimeType
        String mimeType = getMimeType(extension);
        //文件的md5值
        String fileMd5 = getFileMd5(upload);
        //文件的默认目录
        String defaultFolderPath = getDefaultFolderPath();
        //存储到minio中的对象名(带目录)
        String  objectName = defaultFolderPath + fileMd5 + extension;
        //将文件上传到minio
        boolean b = addMediaFilesToMinIO(mimeType, bucket_files,objectName,upload);
        //文件大小
        uploadFileParamsDto.setFileSize(upload.getSize());
        //将文件信息存储到数据库
        System.out.println("文件的MD5:"+fileMd5);
        MediaFiles mediaFiles = addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_files, objectName);
        //准备返回数据
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;

    }

    @Override
    public PageResult<MediaFiles> querFileList(PageParams pageParams, QueryFileParamsDto queryFileParams) {
        LambdaQueryWrapper<MediaFiles> queryWrapper=new LambdaQueryWrapper<>();

        if (StringUtils.isNotEmpty(queryFileParams.getType())){

            queryWrapper.eq(MediaFiles::getFileType,queryFileParams.getType());
        }
        if (StringUtils.isNotEmpty(queryFileParams.getAuditStatus())){
            queryWrapper.eq(MediaFiles::getAuditStatus,queryFileParams.getAuditStatus());
        }
            queryWrapper.like(StringUtils.isNotEmpty(queryFileParams.getFilename()), MediaFiles::getFilename,queryFileParams.getFilename() );

         Page<MediaFiles> page=new Page<>(pageParams.getPageNo(),pageParams.getPageSize());
        Page<MediaFiles> filesList = mediaFilesMapper.selectPage(page, queryWrapper);
        PageResult<MediaFiles> filePageResult = new PageResult<>(filesList.getRecords(), filesList.getTotal(), pageParams.getPageNo(), pageParams.getPageSize());
        return filePageResult;
    }

    @Override
    public MediaFiles getFileById(String mediaId) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(mediaId);

        return mediaFiles;
    }

    @Override
    public UploadFileResultDto uploadFile(MultipartFile uploadFile, String objectName) throws NoSuchAlgorithmException, IOException {
        String filename = uploadFile.getOriginalFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        if (objectName==null){
            if(StringUtils.isEmpty(objectName)){
                String fileMd5 = getFileMd5(uploadFile);
                objectName = getDefaultFolderPath() + fileMd5 + extension;
            }
        }
        String mimeType = getMimeType(extension);
        boolean b = addMediaFilesToMinIO(mimeType, bucket_files,objectName,uploadFile);
        return null;
    }

    @Override
    public String uploadHtml(MultipartFile uploadFile, String objectName) throws NoSuchAlgorithmException, IOException {
        String filename = uploadFile.getOriginalFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        if (objectName==null){
            if(StringUtils.isEmpty(objectName)){
                String fileMd5 = getFileMd5(uploadFile);
                objectName = getDefaultFolderPath() + fileMd5 + extension;
            }
        }
        String mimeType = getMimeType(extension);
        boolean b = addMediaFilesToMinIO(mimeType, bucket_files,objectName,uploadFile);
        return "T";
    }


}
