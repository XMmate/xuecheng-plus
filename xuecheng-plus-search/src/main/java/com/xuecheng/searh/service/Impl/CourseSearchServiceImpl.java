package com.xuecheng.searh.service.Impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.searh.model.dto.SearchCourseParamDto;
import com.xuecheng.searh.model.dto.SearchPageResultDto;
import com.xuecheng.searh.model.po.CourseIndex;
import com.xuecheng.searh.service.CourseSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CourseSearchServiceImpl implements CourseSearchService {

    @Value("${elasticsearch.course.index}")
    private String courseIndexStore;
    @Value("${elasticsearch.course.source_fields}")
    private String sourceFields;

    @Autowired
    RestHighLevelClient client;

    /**
     * 搜索课程列表
     * @param pageParams
     * @param searchCourseParamDto
     * @return
     */
    //TODO 算法待完善
    @Override
    public SearchPageResultDto<CourseIndex> queryCoursePubIndex(PageParams pageParams, SearchCourseParamDto searchCourseParamDto) {
        //设置索引库，构建搜索对象searchRequest
        SearchRequest searchRequest = new SearchRequest(courseIndexStore);
        //构建查询请求对象searchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建布尔查询对象boolQueryBuilder
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //sourceFields 在配置文件里是用逗号分割的字段，表示返回文档的那些字段
        String[] sourceFieldsArray = sourceFields.split(",");
        //设置返回的字段
        searchSourceBuilder.fetchSource(sourceFieldsArray, new String[]{});

        if(StringUtils.isNotEmpty(searchCourseParamDto.getKeywords())){
            //匹配关键字
            //这段代码使用了Elasticsearch中的MultiMatchQueryBuilder来构建一个多字段匹配查询。具体地，它将用户提供的关键字（searchCourseParamDto.getKeywords()）在两个字段（name和description）中进行匹配搜索
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(searchCourseParamDto.getKeywords(), "name", "description");
            //设置匹配占比
            multiMatchQueryBuilder.minimumShouldMatch("70%");
            //提升另个字段的Boost值
            multiMatchQueryBuilder.field("name",10);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        //过虑
        if(StringUtils.isNotEmpty(searchCourseParamDto.getMt())){
            //精确匹配mtName
            boolQueryBuilder.filter(QueryBuilders.termQuery("mtName",searchCourseParamDto.getMt()));
        }
        if(StringUtils.isNotEmpty(searchCourseParamDto.getSt())){
            //精确匹配stName
            boolQueryBuilder.filter(QueryBuilders.termQuery("stName",searchCourseParamDto.getSt()));
        }
        if(StringUtils.isNotEmpty(searchCourseParamDto.getGrade())){
            //精确匹配grade
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",searchCourseParamDto.getGrade()));
        }




        //分页
        Long pageNo = pageParams.getPageNo();
        Long pageSize = pageParams.getPageSize();
        //开始的条数
        int start = (int) ((pageNo-1)*pageSize);
        searchSourceBuilder.from(start);
        searchSourceBuilder.size(Math.toIntExact(pageSize));
        //设置布尔查询的条件
        searchSourceBuilder.query(boolQueryBuilder);
        //搜索对象
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("课程搜索异常：{}",e.getMessage());
            return new SearchPageResultDto<CourseIndex>(new ArrayList(),0,0,0);
        }

        //结果集处理
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        /* 记录总数 */
        TotalHits totalHits = hits.getTotalHits();
        //数据列表
        List<CourseIndex> list = new ArrayList<>();

        for (SearchHit hit : searchHits) {
            String sourceAsString = hit.getSourceAsString();
            CourseIndex courseIndex = JSON.parseObject(sourceAsString, CourseIndex.class);
            list.add(courseIndex);
        }

        SearchPageResultDto<CourseIndex> pageResult = new SearchPageResultDto<>(list, totalHits.value,pageNo,pageSize);
        //获取聚合结果
        List<String> mtList= getAggregation(searchResponse.getAggregations(), "mtAgg");
        List<String> stList = getAggregation(searchResponse.getAggregations(), "stAgg");

        pageResult.setMtList(mtList);
        pageResult.setStList(stList);
        return pageResult;
    }



    private void buildAggregation(SearchRequest request) {
        request.source().aggregation(AggregationBuilders
                .terms("mtAgg")
                .field("mtName")
                .size(100)
        );
        request.source().aggregation(AggregationBuilders
                .terms("stAgg")
                .field("stName")
                .size(100)
        );

    }

    //获取

    private List<String> getAggregation(Aggregations aggregations, String aggName) {
        // 4.1.根据聚合名称获取聚合结果
        Terms brandTerms = aggregations.get(aggName);
        // 4.2.获取buckets
        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
        // 4.3.遍历
        List<String> brandList = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            // 4.4.获取key
            String key = bucket.getKeyAsString();
            brandList.add(key);
        }
        return brandList;
    }

}
