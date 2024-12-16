package com.xuecheng.searh.controller;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.searh.model.po.CourseIndex;
import com.xuecheng.searh.service.CourseIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
public class CourseIndexController {
    @Autowired
    CourseIndexService courseIndexService;

    @Value("${elasticsearch.course.index}")
    private String courseIndexStore;
    /**
     * 添加索引
     * @param courseIndex
     * @return
     */
    @PostMapping("/course")
    public Boolean add(@RequestBody CourseIndex courseIndex) {
        Long id = courseIndex.getId();
        if(id==null){
            XueChengPlusException.cast("课程id为空");
        }

        Boolean result = courseIndexService.addCourseIndex(courseIndexStore, String.valueOf(id), courseIndex);
        if(!result){
            XueChengPlusException.cast("添加课程索引失败");
        }
        return result;
    }


}
