package com.xuecheng.learing.controller;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.learing.model.dto.MyCourseTableParams;
import com.xuecheng.learing.model.dto.XcCourseTablesDto;
import com.xuecheng.learing.model.dto.XcChooseCourseDto;
import com.xuecheng.learing.service.MyCourseTablesService;
import com.xuecheng.learing.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.xuecheng.learing.model.po.XcCourseTables;

/**
 * 我的课程表
 */
@Slf4j
@RestController
public class MyCourseTablesController {
    @Autowired
    MyCourseTablesService courseTablesService;

    /**
     * 添加选课
     * @param courseId
     * @return
     */
    @RequestMapping("/choosecourse/{courseId}")
    public XcChooseCourseDto addChooseCourse(@PathVariable("courseId") Long courseId)  {
        //登录用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user == null){
            XueChengPlusException.cast("请登录后继续选课");
        }
        String userId = user.getId();

        return  courseTablesService.addChooseCourse(userId, courseId);
    }

    /**
     * 查询学习资格
     * @param courseId
     * @return
     */
    @PostMapping("/choosecourse/learnstatus/{courseId}")
    public XcCourseTablesDto getLearnstatus(@PathVariable("courseId") Long courseId){
        //登录用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user == null){
            XueChengPlusException.cast("请登录后继续选课");
        }
        String userId = user.getId();

        return  courseTablesService.getLearningStatus(userId,courseId);
    }

    @GetMapping("/mycoursetable")
    public PageResult<XcCourseTables> mycoursetable(MyCourseTableParams params) {
        //登录用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user == null){
            XueChengPlusException.cast("请登录后继续选课");
        }
        String userId = user.getId();
//设置当前的登录用户
        params.setUserId(userId);
        return   courseTablesService.mycoursetables(params);

    }
}
