package com.xshiwu.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface AggDataSource<T> {

    /**
     * 聚合查询方法
     * @param searchText    查询参数
     * @param pageNum       当前页
     * @param pageSize      每页条数
     * @return
     * <p>目前实现的几种搜索</p>
     * <ul>
     *     <li>图片</li>
     *     <li>文章</li>
     *     <li>用户</li>
     *     <li>...</li>
     * </ul>
     */
    Page<T> doSearch(String searchText, Integer pageNum, Integer pageSize);
}
