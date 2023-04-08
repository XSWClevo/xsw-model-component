package com.xshiwu.job.once;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xshiwu.common.ErrorCode;
import com.xshiwu.exception.BusinessException;
import com.xshiwu.model.entity.Post;
import com.xshiwu.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 全量同步帖子到 es
 *
 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class FetchInitPostList implements CommandLineRunner {

    @Resource
    private PostService postService;

    @Override
    public void run(String... args) {
        try {
            String paramJson = "{\"current\":1,\"pageSize\":8,\"sortField\":\"createTime\",\"sortOrder\":\"descend\",\"category\":\"文章\",\"reviewStatus\":1}";
            String url = "https://www.code-nav.cn/api/post/search/page/vo";
            String result = HttpRequest.post(url).body(paramJson).execute().body();
            Map map = JSONUtil.toBean(result, Map.class);
            Integer code = (Integer) map.get("code");
            if (code != 0) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
            }
            JSONObject data = (JSONObject) map.get("data");
            if (ObjUtil.isEmpty(data)) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
            }
            JSONArray records = (JSONArray) data.get("records");
            if (CollUtil.isEmpty(records)) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
            }
            List<Post> postList = new ArrayList<>();
            for (Object json : records) {
                JSONObject tempRecord = (JSONObject) json;
                JSONArray tags = (JSONArray) tempRecord.get("tags");
                List<String> tagList = tags.toList(String.class);
                Post post = new Post();
                if (CollUtil.isNotEmpty(tagList)) {
                    post.setTags(JSONUtil.toJsonStr(tagList));
                }
                String content = tempRecord.getStr("content");
                if (StrUtil.isNotEmpty(content)) {
                    post.setContent(content);
                }
                String title = tempRecord.getStr("title");
                if (StrUtil.isNotEmpty(title)) {
                    post.setTitle(title);
                }
                post.setUserId(999L);
                postList.add(post);
            }
            if (CollUtil.isNotEmpty(postList)) {
                boolean b = postService.saveBatch(postList);
                if (b) {
                    log.info("项目启动文章内容初始化成功,初始化条目共有：{}条", postList.size());
                } else {
                    log.error("项目启动文章内容初始化失败");
                }
            }
        } catch (Exception e) {
            log.error("项目启动时数据初始化失败");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }
}
