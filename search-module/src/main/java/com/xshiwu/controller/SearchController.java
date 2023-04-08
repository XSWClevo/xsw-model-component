package com.xshiwu.controller;

import com.xshiwu.annotation.RateLimiter;
import com.xshiwu.common.BaseResponse;
import com.xshiwu.common.ResultUtils;
import com.xshiwu.model.dto.search.SearchRequest;
import com.xshiwu.model.enums.LimitType;
import com.xshiwu.model.vo.SearchVO;
import com.xshiwu.service.SearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Resource
    private SearchService searchService;

    @RateLimiter(time = 10, count = 3, limitType = LimitType.IP) //10秒内允许访问三次
    @PostMapping("/all")
    public BaseResponse<SearchVO> getPicture(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        return ResultUtils.success(searchService.search(searchRequest, request));
    }
}
