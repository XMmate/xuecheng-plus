package com.xuecheng.searh.service;

public interface CourseIndexService {
    /**
     * 添加课程索引
     * @param indexName
     * @param id
     * @param object
     * @return
     */
    public Boolean addCourseIndex(String indexName,String id,Object object);
}
