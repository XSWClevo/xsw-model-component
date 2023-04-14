package com.xshiwu.crawler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.xshiwu.common.ErrorCode;
import com.xshiwu.exception.ThrowUtils;
import com.xuxueli.poi.excel.ExcelExportUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取Boss直聘职位信息
 * 只是demo，代码没有注意规范
 */
@Slf4j
public class GetWorkInfo {

    public static List<Map<String, String>> workInfoMapList = new ArrayList<>();

    public static void main(String[] args) {
        // 注意几个请求参数
        // 职业名称
        String workName = "java";
        // 当前页码从i开始
        for (int i = 1; i < 2; i++) {
            String url = String.format("https://www.zhipin.com/wapi/zpgeek/search/joblist.json?scene=1&query=Java&city=101200100&experience=&payType=&partTime=&degree=&industry=&scale=&stage=&position=&jobType=&salary=&multiBusinessDistrict=&multiSubway=&page=%d&pageSize=%d", i, 30);
            String json = getInterface(url);
            parseHtml(json);
        }

        System.out.println("一共获取到" + workInfoMapList.size() + "条与[" + workName + "相关]的招聘信息");

        //保存所有workinfo tag 到文本
        FileWriter writer = null;
        try {
            writer = new FileWriter("E:/tags.txt");
//            for (Map workInfo : workInfoMapList) {
//                for (String tag : workInfo.getTags()) {
//                    writer.write(tag + "  ");
//                }
//            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //保存数据到excel
        ExcelExportUtil.exportToFile(workInfoMapList, "E:\\re.xls");
    }

    /**
     * 模拟浏览器请求，获取网页内容
     */
    public static String getInterface(String url) {

        String result = "";

        // 通过址默认配置创建一个httpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 创建httpGet远程连接实例
        HttpGet httpGet = new HttpGet(url);
        // 设置请求头信息，鉴权
        setHeader(httpGet);

        // 设置配置请求参数
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 请求超时时间
                .setSocketTimeout(60000)// 数据读取超时时间
                .build();
        // 为httpGet实例设置配置
        httpGet.setConfig(requestConfig);
        // 执行get请求得到返回对象
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            // 通过返回对象获取返回数据
            result = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static void setHeader(HttpGet httpGet) {
        httpGet.setHeader("authority", "www.zhipin.com");
        httpGet.setHeader("accept", "application/json, text/plain, */*");
        httpGet.setHeader("accept-language", "zh-CN,zh;q=0.9");
        httpGet.setHeader("cookie", "warlockjssdkcross=%7B%22distinct_id%22%3A%2279064008%22%2C%22first_id%22%3A%221860b9cd43b1c1-0dde06f510aa3e-26021051-1327104-1860b9cd43c3f7%22%2C%22props%22%3A%7B%7D%2C%22device_id%22%3A%221860b9cd43b1c1-0dde06f510aa3e-26021051-1327104-1860b9cd43c3f7%22%7D; wd_guid=da6d95b7-7ba9-4dab-bf84-a421e37462fb; historyState=state; _bl_uid=3klFUdRUmd0v2UcO6j4g8q01ULm2; YD00951578218230%3AWM_TID=MkcoRnJ68khBVUFRFVKAbQ90qID%2FI5Av; YD00951578218230%3AWM_NI=Ey4LC3dP4MSPWaG4LgGL7o9GYbcZ2V9tL0l7GqhMxGfg70z8rGIZUd6U2u6GtGU6agTmGumG7oMSTEHRuifqMJDoMIIzWrU%2BRGjrHx1mQAFiGzGwPXdugoJXtpjsVjzUWWk%3D; YD00951578218230%3AWM_NIKE=9ca17ae2e6ffcda170e2e6eebae243aa8a9abad4648a8e8aa7c84b838b9a83d46e8c9c999bb34afbae8eaae62af0fea7c3b92a87a7fcadc239a890fcaed25c95bf89d5cf6d8594f990bc4d9bbeadbbb15efbac84d1ef3d85afa8aefb4da6b9f9d1f934b29987bbd870f68bfe96b54db8999bd7e46d98b38996f04df5b183b4d74f8c8ebb8ae57b8f968c90d0438ba69993d8219299a693cb74f3b39fa2c56385a68998d354a8aba990c954b8b0a694d562ba8b99b8ea37e2a3; wt2=Dl-StcVFopZJRmNGpxbE2qR26z3cDmqPQwqPB8nGb-sTn9kGOVICHd__efjKaI6UJzERtJUVqP2FnZl2I8RdNXw~~; wbg=0; lastCity=101200100; __g=-; collection_pop_window=1; Hm_lvt_194df3105ad7148dcf2b98a91b5e727a=1680770273,1681193263,1681369590,1681448660; Hm_lpvt_194df3105ad7148dcf2b98a91b5e727a=1681465304; __zp_stoken__=b295eEHMOUGMnCxtpZmR7XHpmbkozVl1ifUdteCswSDVpRWkGeGNQMycfDgARXWZfF3t0DGQDNWV%2Bd2o3fBBpdwp4d00ePwxdEDQAeElzEkxjHBZYPAg5CV1iamN9DiAyVUJGdz93NxhgRxY%3D; geek_zp_token=V1RtMnF-D92lZgXdNhzhkeLCO36jnexA~~; __c=1681448657; __l=l=%2Fwww.zhipin.com%2Fweb%2Fgeek%2Fjob%3Fquery%3DJava%26city%3D101200100&r=&g=&s=3&friend_source=0&s=3&friend_source=0; __a=92451503.1675232073.1681369592.1681448657.228.22.20.228");
        httpGet.setHeader("referer", "https://www.zhipin.com/web/geek/job?query=Java&city=101200100&page=1&pageSize=30");
        httpGet.setHeader("sec-ch-ua", "\"Chromium\";v=\"112\", \"Google Chrome\";v=\"112\", \"Not:A-Brand\";v=\"99\"");
        httpGet.setHeader("sec-ch-ua-mobile", "?0");
        httpGet.setHeader("sec-ch-ua-platform", "\"Windows\"");
        httpGet.setHeader("sec-fetch-dest", "empty");
        httpGet.setHeader("sec-fetch-mode", "cors");
        httpGet.setHeader("sec-fetch-site", "same-origin");
        httpGet.setHeader("token", "6PRC5jbpToBdozs");
        httpGet.setHeader("traceid", "B8A196FF-1BD5-4F5E-BC87-96DD71E3626B");
        httpGet.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
        httpGet.setHeader("x-requested-with", "XMLHttpRequest");
        httpGet.setHeader("zp_token", "V1RtMnF-D92lZgXdNhzhkeLCO36jnexA~~");
    }

    public static Object parseHtml(String json) {

        Gson gson = new Gson();
        // 如果你只需要大概信息，把这个JSON解析就够了
        Map map = gson.fromJson(json, Map.class);
        LinkedTreeMap zpData = (LinkedTreeMap) map.get("zpData");
        List<LinkedTreeMap> jobList = (List<LinkedTreeMap>) zpData.get("jobList");
        System.err.println("jobList size is " + jobList.size());
        // 这里组装URL原因是要获取每个职位的详细信息
        // 由于BOSS反爬虫比较厉害，这里每次请求时都需要获取最新的zp_token与traceid和cookie
        // 1. selenium使用浏览器访问网页

        for (LinkedTreeMap treeMap : jobList) {
            String securityId = (String) treeMap.get("securityId");
            String encryptJobId = (String) treeMap.get("encryptJobId");
            String urlDetail = String.format("https://www.zhipin.com/job_detail/%s.html?lid=166c7HbdPTj.search.1&securityId=%s", encryptJobId, securityId);
            String html = GetWorkInfo.getInterface(urlDetail);
            getJobDetails(html, urlDetail);
        }
        return null;

    }


    public static void getJobDetails(String html, String urlDetail) {
        Document document = Jsoup.parseBodyFragment(html);
        //获取body
        Element body = document.body();
        Element main = body.getElementById("main");
        ThrowUtils.throwIf(main == null, ErrorCode.SYSTEM_ERROR);
        // 公司基本情况
        String biasInfo = main.getElementsByClass("sider-company").get(0).text().replace("查看全部职位", "");
        System.err.println("公司基本情况 ====> " + biasInfo);
        // 详细职责
        Elements jobDetail = main.getElementsByClass("job-detail");
        for (Element element : jobDetail) {
            //招聘概要信息
            Map<String, String> workInfoMap = new HashMap<>();
            // 职责描述
            String jobDescription = element.getElementsByClass("job-sec-text").get(0).text().replaceAll("<br/>", "\r\n");
            Element bossAndActiveHouse = element.getElementsByClass("job-boss-info").get(0);
            String bossName = bossAndActiveHouse.getElementsByClass("name").get(0).text();
            String activeHouse = StrUtil.EMPTY;
            if (CollUtil.isNotEmpty(bossAndActiveHouse.getElementsByClass("boss-active-time"))) {
                activeHouse = bossAndActiveHouse.getElementsByClass("boss-active-time").get(0).text();
                System.err.println("activeHouse ======>" + activeHouse);
            }
            String bossAttr = bossAndActiveHouse.getElementsByClass("vdot").get(0).text();
            System.err.println("bossAttr ======>" + bossAttr);

            // 工作地址
            String workAddress = element.getElementsByClass("location-address").get(0).text();
            // 公司介绍
            String companyIntroductionString = companyIntroduction(element);
            System.err.println(companyIntroductionString);
            // 公司信息
            for (Element detailSectionItem : element.getElementsByClass("detail-section-item")) {
                // 公司介绍
                companyInfo(detailSectionItem, "company-info-box", "job-sec-text", "公司介绍 ====> ");
                // 工商信息
                getBusinessInfo(detailSectionItem);
                // 工作地址
                companyInfo(detailSectionItem, "company-address", "location-address", "工作地点 =====》");
            }
            String updateDate = element.getElementsByClass("gray").get(0).text();
            // 这里写对应的字段获取用MAP也可以
            workInfoMap.put("jobName", bossName);
            workInfoMap.put("workAddress", workAddress);
            workInfoMap.put("jobUpdateDate", updateDate);
            workInfoMap.put("urlDetail", urlDetail);
            workInfoMap.put("workDetail", jobDescription);

            workInfoMapList.add(workInfoMap);
        }
    }

    private static String companyIntroduction(Element element) {
        Elements companyIntroductions = element.getElementsByClass("job-detail-section");
        String companyIntroductionString = "";
        if (CollectionUtil.isNotEmpty(companyIntroductions)) {
            for (Element companyIntroduction : companyIntroductions) {
                Elements detailSectionItem = companyIntroduction.getElementsByClass("job-detail-section");
                if (CollectionUtil.isNotEmpty(detailSectionItem)) {
                    for (Element item : detailSectionItem) {
                        Elements jobSecTexts = item.getElementsByClass("job-sec-text");
                        if (CollectionUtil.isEmpty(jobSecTexts)) {
                            continue;
                        }
                        companyIntroductionString = jobSecTexts.get(0).text();
                    }
                }
            }
        }
        return companyIntroductionString;
    }

    private static void companyInfo(Element detailSectionItem, String s2, String s3, String s4) {
        Elements companyInfoBoxes = detailSectionItem.getElementsByClass(s2);
        String companyInfoString = StrUtil.EMPTY;
        if (CollectionUtil.isNotEmpty(companyInfoBoxes)) {
            for (Element companyInfoBox : companyInfoBoxes) {
                Elements jobSecTexts = companyInfoBox.getElementsByClass(s3);
                if (CollectionUtil.isEmpty(jobSecTexts)) {
                    continue;
                }
                companyInfoString = jobSecTexts.get(0).text();
            }
        }
        System.err.println(s4 + companyInfoString);
    }

    private static void getBusinessInfo(Element detailSectionItem) {
        // 工商信息
        Elements businessInfoBoxes = detailSectionItem.getElementsByClass("business-info-box");
        if (CollectionUtil.isNotEmpty(businessInfoBoxes)) {
            for (Element businessInfoBox : businessInfoBoxes) {
                try {
                    String companyName = businessInfoBox.getElementsByClass("company-name").text();
                    String companyUser = businessInfoBox.getElementsByClass("company-user").text();
                    String resTime = businessInfoBox.getElementsByClass("res-time").text();
                    String companyType = businessInfoBox.getElementsByClass("company-type").text();
                    String manageState = businessInfoBox.getElementsByClass("manage-state").text();
                    String companyFund = businessInfoBox.getElementsByClass("company-fund").text();

                    log.info("companyName" + "===>" + companyName);
                    log.info("companyUser" + "===>" + companyUser);
                    log.info("resTime" + "===>" + resTime);
                    log.info("companyType" + "===>" + companyType);
                    log.info("manageState" + "===>" + manageState);
                    log.info("companyFund" + "===>" + companyFund);
                } catch (Exception e) {
                    // 这里的cache是避免程序执行中断,获取不到后面的信息
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}