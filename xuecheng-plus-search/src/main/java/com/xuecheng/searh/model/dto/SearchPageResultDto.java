package com.xuecheng.searh.model.dto;

import com.xuecheng.base.model.PageResult;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 搜索结果类
 * @param <T>
 */

@Data
public class SearchPageResultDto<T> extends PageResult {
    //大分类列表
    List<String> mtList;
    //小分类列表
    List<String> stList;
    public SearchPageResultDto(List items, long counts, long page, long pageSize) {
        super(items, counts, page, pageSize);
    }
}
