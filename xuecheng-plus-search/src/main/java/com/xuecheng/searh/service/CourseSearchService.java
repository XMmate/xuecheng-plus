package com.xuecheng.searh.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.searh.model.dto.SearchCourseParamDto;
import com.xuecheng.searh.model.dto.SearchPageResultDto;
import com.xuecheng.searh.model.po.CourseIndex;

public interface CourseSearchService {
    SearchPageResultDto<CourseIndex> queryCoursePubIndex(PageParams pageParams, SearchCourseParamDto searchCourseParamDto);


}
