package com.xuecheng.learing.service;

import com.xuecheng.base.model.PageResult;
import com.xuecheng.learing.model.dto.MyCourseTableParams;
import com.xuecheng.learing.model.dto.XcChooseCourseDto;
import com.xuecheng.learing.model.dto.XcCourseTablesDto;
import com.xuecheng.learing.model.po.XcCourseTables;

/**
 * 我的课程表
 */
public interface MyCourseTablesService {
    /**
     * 添加课程
     * @param userId
     * @param courseId
     * @return
     */
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId);

    /**
     * 判断学习资格
     * @param userId
     * @param courseId
     * @return
     */
     XcCourseTablesDto getLearningStatus(String userId, Long courseId);

    /**
     * 保存选课成功的接口
     * @param choosecourseId
     * @return
     */
    boolean saveChooseCourseSuccess(String choosecourseId);

    /**
     * 我的课程表
     * @param params
     * @return
     */
    public PageResult<XcCourseTables> mycoursetables(MyCourseTableParams params);

}
