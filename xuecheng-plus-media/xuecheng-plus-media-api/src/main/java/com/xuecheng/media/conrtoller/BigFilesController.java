package com.xuecheng.media.conrtoller;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.service.BigFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class BigFilesController {

    @Autowired
    private BigFilesService bigFilesService;

    /*检查文件是否存在是否存在*/
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkfile(@RequestParam("fileMd5") String fileMd5) throws Exception {
        return bigFilesService.checkFile(fileMd5);
    }
     /*分块文件上传前的检测*/
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5") String fileMd5,@RequestParam("chunk") int chunk) throws Exception {
        return bigFilesService.checkChunk(fileMd5,chunk);
    }
     /*分块上传*/
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadchunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws Exception {

        return bigFilesService.uploadChunk(fileMd5,chunk,file);
    }


      /*合并文件*/
    @PostMapping("/upload/mergechunks")
    public RestResponse mergechunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) throws Exception {
        return bigFilesService.mergechunks(fileMd5,chunkTotal,fileName);

    }


}
