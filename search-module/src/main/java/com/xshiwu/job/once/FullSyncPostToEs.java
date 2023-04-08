package com.xshiwu.job.once;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpUtil;
import com.xshiwu.esdao.PostEsDao;
import com.xshiwu.model.dto.post.PostEsDTO;
import com.xshiwu.model.entity.Post;
import com.xshiwu.service.PostService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全量同步帖子到 es
 */
//@Component
@Slf4j
public class FullSyncPostToEs implements CommandLineRunner {

    @Resource
    private PostService postService;

    @Resource
    private PostEsDao postEsDao;

    @Override
    public void run(String... args) {
        List<Post> postList = postService.list();
        if (CollectionUtil.isEmpty(postList)) {
            return;
        }
        List<PostEsDTO> postEsDTOList = postList.stream().map(PostEsDTO::objToDto).collect(Collectors.toList());
        final int pageSize = 500;
        int total = postEsDTOList.size();
        log.info("FullSyncPostToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            postEsDao.saveAll(postEsDTOList.subList(i, end));
        }
        log.info("FullSyncPostToEs end, total {}", total);
    }

    public static void main(String[] args) {
        String url = "http://101.35.142.24:8080/proxy";
        String key = "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";
        String content = "自己怎么开发IDEA插件，请说出项目创建步骤";
        String paramStr = String.format("{\n" +
                "  \"method\": \"POST\",\n" +
                "  \"url\": \"https://api.openai.com/v1/chat/completions\",\n" +
                "  \"data\": {\n" +
                "    \"model\": \"gpt-3.5-turbo\",\n" +
                "    \"messages\": [\n" +
                "      {\n" +
                "        \"role\": \"user\",\n" +
                "        \"content\": \"%s\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"timeout\": 1000,\n" +
                "  \"headers\": {\n" +
                "    \"Authorization\": \"Bearer %s\"\n" +
                "  }\n" +
                "}", content, key);
        for (int i = 0; i < 100; i++) {
            String post = HttpUtil.post(url, paramStr);
            System.out.println(post);
            System.out.println(i + "=====================");
        }


    }
}
