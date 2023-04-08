package com.xshiwu.service;

import com.xshiwu.model.dto.search.SearchRequest;
import com.xshiwu.model.vo.SearchVO;

import javax.servlet.http.HttpServletRequest;

public interface SearchService {

    SearchVO search(SearchRequest searchRequest, HttpServletRequest request);
}
