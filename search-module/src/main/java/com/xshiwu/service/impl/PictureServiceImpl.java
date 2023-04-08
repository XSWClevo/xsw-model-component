package com.xshiwu.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xshiwu.common.ErrorCode;
import com.xshiwu.exception.BusinessException;
import com.xshiwu.exception.ThrowUtils;
import com.xshiwu.model.entity.Picture;
import com.xshiwu.service.PictureService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PictureServiceImpl implements PictureService {
    @Override
    public Page<Picture> searchPicture(long pageIndex, long pageSize, String searchText) {
        ThrowUtils.throwIf(StrUtil.isBlank(searchText), ErrorCode.PARAMS_ERROR);
        // long current = (pageIndex - 1) * pageSize;
        String url = String.format("https://cn.bing.com/images/search?q=%s&first=%d", searchText, pageIndex);
        log.info("搜索URL为：{}", url);
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        Elements newsHeadlines = doc.select(".iuscp.isv");
        List<Picture> pictureList = new ArrayList<>();
        for (Element element : newsHeadlines) {
            String m = element.select(".iusc").get(0).attr("m");
            Elements select = element.select(".inflnk");
            if (ObjUtil.isEmpty(select)) {
                continue;
            }
            String title = select.get(0).attr("aria-label");
            if (StrUtil.isBlank(title)) {
                continue;
            }
            Map map = JSONUtil.toBean(m, Map.class);
            String murl = (String) map.get("murl");

            Picture picture = new Picture(title, murl);
            // 指定每页只返回20条
            if (pictureList.size() >= pageSize) {
                break;
            }
            pictureList.add(picture);
        }
        Page<Picture> page = new Page<>(pageIndex, pageSize);
        page.setRecords(pictureList);
        return page;
    }
}
