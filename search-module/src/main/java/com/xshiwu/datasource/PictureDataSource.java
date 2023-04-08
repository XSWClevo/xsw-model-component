package com.xshiwu.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xshiwu.model.entity.Picture;
import com.xshiwu.service.PictureService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PictureDataSource implements AggDataSource<Picture> {

    @Resource
    private PictureService pictureService;

    @Override
    public Page<Picture> doSearch(String searchText, Integer pageNum, Integer pageSize) {
        return pictureService.searchPicture(pageNum, pageSize, searchText);
    }
}
