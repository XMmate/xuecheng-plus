package com.xuecheng.searh.service.Impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.searh.service.CourseIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;
import java.io.IOException;
@Service
public class CourseIndexServiceImpl implements CourseIndexService {
    @Autowired
    RestHighLevelClient client;

    /**
     * 添加课程索引
     * @param indexName
     * @param id
     * @param object
     * @return
     */
    @Override
    public Boolean addCourseIndex(String indexName, String id, Object object) {
        //把object转换成json
        String jsonString = JSON.toJSONString(object);
           //创建IndexRequest对象，IndexRequest(indexName).id(id); 参数是索引库名字和id
        IndexRequest indexRequest = new IndexRequest(indexName).id(id);
        indexRequest.source(jsonString,XContentType.JSON);
        IndexResponse indexResponse = null;
        try {
            indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String name = indexResponse.getResult().name();
        return name.equalsIgnoreCase("created") || name.equalsIgnoreCase("updated");
    }
}
