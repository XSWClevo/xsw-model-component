package com.xshiwu.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xshiwu.model.dto.user.UserQueryRequest;
import com.xshiwu.model.vo.UserVO;
import com.xshiwu.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserDataSource implements AggDataSource<UserVO> {

    @Resource
    private UserService userService;

    @Override
    public Page<UserVO> doSearch(String searchText, Integer pageNum, Integer pageSize) {
        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setUserName(searchText);
        userQueryRequest.setCurrent(pageNum);
        userQueryRequest.setPageSize(pageSize);
        return userService.listUserByPage(userQueryRequest);
    }
}
