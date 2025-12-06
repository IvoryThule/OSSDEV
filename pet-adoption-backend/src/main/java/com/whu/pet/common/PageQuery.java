package com.whu.pet.common;

import lombok.Data;

/**
 * 分页查询参数
 */
@Data
public class PageQuery {

    /**
     * 当前页码，默认1
     */
    private Integer pageNum = 1;

    /**
     * 每页条数，默认10
     */
    private Integer pageSize = 10;

    /**
     * 搜索关键词
     */
    private String keyword;
}
