package com.xuecheng.searh.controller;

import com.xuecheng.searh.model.dto.SearchPageResultDto;
import com.xuecheng.searh.service.CourseSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.xuecheng.searh.model.dto.SearchCourseParamDto;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.searh.model.po.CourseIndex;
@RestController
public class CourseSearhController {
    @Autowired
    CourseSearchService courseSearchService;
    /**
     * 添加文档
     */
    @PostMapping("/course-publish/_doc/{id}")
    private void aadCourseSearh(){

    }

    /**
     * 查询文档
     * @param indexName
     * @param id
     */

    @GetMapping("/{indexName}/_doc/{id}")
    private void  searhDoc(@PathVariable String indexName,Long id){
    }

    /**
     * 全量更新文档
     * @param indexName
     * @param id
     */
    @PutMapping("/{indexName}/_doc/{id}")
    private void upadteDocPlus(@PathVariable String indexName ,Long id){

    }

    @PostMapping("/{indexName}/_update/{id}")
    private void updateDoc(@PathVariable String indexName ,Long id){

    }

    @DeleteMapping("/{indexName}/_doc/{id}")
    private void deleteDoc(@PathVariable String indexName ,Long id){

    }

    /**
     * 课程搜索列表
     * @param pageParams
     * @param searchCourseParamDto
     * @return
     */
    @GetMapping("/list")
    public PageResult<CourseIndex> list(PageParams pageParams, SearchCourseParamDto searchCourseParamDto){
        SearchPageResultDto<CourseIndex> courseIndexSearchPageResultDto = courseSearchService.queryCoursePubIndex(pageParams, searchCourseParamDto);
        return courseIndexSearchPageResultDto;
    }
}
