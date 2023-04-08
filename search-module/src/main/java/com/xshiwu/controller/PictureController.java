package com.xshiwu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xshiwu.common.BaseResponse;
import com.xshiwu.common.ErrorCode;
import com.xshiwu.common.ResultUtils;
import com.xshiwu.exception.ThrowUtils;
import com.xshiwu.model.dto.picture.PictureRequest;
import com.xshiwu.model.entity.Picture;
import com.xshiwu.service.PictureService;
import com.xshiwu.service.PostService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/picture")
public class PictureController {

    @Resource
    private PictureService pictureService;

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Picture>> getPicture(PictureRequest pictureRequest, HttpServletRequest request) {
        long current = pictureRequest.getCurrent();
        long pageSize = pictureRequest.getPageSize();
        String searchText = pictureRequest.getSearchText();
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        Page<Picture> picturePage = pictureService.searchPicture(current, pageSize, searchText);
        return ResultUtils.success(picturePage);

    }
}
