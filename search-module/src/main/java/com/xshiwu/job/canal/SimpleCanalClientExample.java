package com.xshiwu.job.canal;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import com.xshiwu.esdao.PostEsDao;
import com.xshiwu.model.dto.post.PostEsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Order(value = 1)
//@Component
public class SimpleCanalClientExample implements CommandLineRunner {

    @Resource
    private PostEsDao postEsDao;

    // 创建链接
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 2, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));
    
    /**
     * 取instance.properties里面配置中的 - instance: example
     * 数据库账号密码
     */
    private static final CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("127.0.0.1",
            11111), "example", "root", "123456");

    @Order(value = 2)
    @Override
    public void run(String... args) {
        log.info("执行Canal监控方法");
        int batchSize = 1000;
        AtomicInteger emptyCount = new AtomicInteger(0);
        try {
            connector.connect();
            // connector.subscribe(".*\\..*");
            // 多表、多库
            // connector.subscribe("test\\..*,test2.user1,test3.user2");
            // 由于instance.properties 文件以设置正则，所以这里可以为空
            connector.subscribe();
            connector.rollback();
            int totalEmptyCount = 1000;
            while (emptyCount.get() < totalEmptyCount) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                threadPoolExecutor.execute(() -> {
                    Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                    long batchId = message.getId();
                    int size = message.getEntries().size();
                    if (batchId == -1 || size == 0) {
                        emptyCount.getAndAdd(1);
                        // System.out.println("empty count : " + emptyCount.get());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // 有数据库信息改变就重置计数
                        emptyCount.set(0);
                        printEntry(message.getEntries());
                    }

                    connector.ack(batchId); // 提交确认
                    // connector.rollback(batchId); // 处理失败, 回滚数据
                    countDownLatch.countDown();
                });
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            connector.disconnect();
        }
    }

    private void printEntry(List<Entry> list) {
        for (Entry entry : list) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN
                    || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }

            RowChange rowChange;
            try {
                rowChange = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }

            EventType eventType = rowChange.getEventType();
            log.info(String.format("================&gt; binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType));
            // 处理insert、update、delete操作
            List<PostEsDTO> postEsDTOList = new ArrayList<>();
            for (RowData rowData : rowChange.getRowDatasList()) {
                PostEsDTO postEsDTO = null;
                if (eventType == EventType.DELETE) {
                    printColumn(rowData.getBeforeColumnsList());
                } else if (eventType == EventType.INSERT) {
                    log.info("es 执行insert方法");
                    postEsDTO = printColumn(rowData.getAfterColumnsList());
                } else {
                    log.info("-------&gt; before");
                    printColumn(rowData.getBeforeColumnsList());
                    log.info("-------&gt; after");
                    printColumn(rowData.getAfterColumnsList());
                }
                if (ObjectUtil.isNotEmpty(postEsDTO)) {
                    postEsDTOList.add(postEsDTO);
                }
            }
            if (CollUtil.isNotEmpty(postEsDTOList)) {
                postEsDao.saveAll(postEsDTOList);
            }
        }
    }

    private PostEsDTO printColumn(List<Column> columns) {
        Map<String, Object> data = new HashMap<>();
        for (Column column : columns) {
            data.put(column.getName(), column.getValue());
            log.info(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
        // postEsDTO = MapToBeanUtils.mapToBean(data, PostEsDTO.class);
        return JSONObject.parseObject(JSONObject.toJSONString(data), PostEsDTO.class);
    }
}