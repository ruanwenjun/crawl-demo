package com.ruanwenjun;

import com.ruanwenjun.entity.WordCount;
import com.ruanwenjun.scrawthreadjob.ScrawBaiduJob;
import com.ruanwenjun.scrawthreadjob.ScrawWeiBoJob;
import com.ruanwenjun.entity.ScrawEntity;
import com.ruanwenjun.utils.WordUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

/**
 * @Author RUANWENJUN
 * @Creat 2018-09-06 11:03
 */

public class scrawmain {

    public static void main(String[] args) throws ExecutionException, InterruptedException, SQLException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<List<ScrawEntity>> weiboFeature = executorService.submit(new ScrawWeiBoJob());
        Future<List<ScrawEntity>> baiduNowFeature = executorService.submit(new ScrawBaiduJob("baidu实时热点", "http://top.baidu.com/buzz?b=1&c=513&fr=topbuzz_b42_c513"));
        Future<List<ScrawEntity>> baiduTodayFeature = executorService.submit(new ScrawBaiduJob("baidu今日热点", "http://top.baidu.com/buzz?b=341&c=513&fr=topbuzz_b1_c513"));
        Future<List<ScrawEntity>> baiduPeopleFeature = executorService.submit(new ScrawBaiduJob("baidu民生热点", "http://top.baidu.com/buzz?b=342&c=513&fr=topbuzz_b42_c513"));
        Future<List<ScrawEntity>> baiduStarFeature = executorService.submit(new ScrawBaiduJob("baidu娱乐热点", "http://top.baidu.com/buzz?b=344&c=513&fr=topbuzz_b342_c513"));
        Future<List<ScrawEntity>> baiduSportFeature = executorService.submit(new ScrawBaiduJob("baidu体育热点", "http://top.baidu.com/buzz?b=11&c=513&fr=topbuzz_b344_c513"));
        List<ScrawEntity> queryList = new ArrayList<>(weiboFeature.get());
        queryList.addAll(baiduNowFeature.get());
        queryList.addAll(baiduTodayFeature.get());
        queryList.addAll(baiduPeopleFeature.get());
        queryList.addAll(baiduStarFeature.get());
        queryList.addAll(baiduSportFeature.get());
        //extend(queryList);
        //JDBCUtil.insert(queryList);
    }


    public static void extend(List<ScrawEntity> list) {
        // site,hot,set,count
        for (ScrawEntity entity : list) {
            try {
                List<WordCount> search = search(entity.getHot());
                Set<String> oriset = entity.getWords();
                System.out.println(search);
                System.out.println(oriset);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 在微博搜索
     *
     * @param searchkey
     * @throws IOException
     */
    public static List<WordCount> search(String searchkey) throws IOException {
        List<WordCount> reverseList = new ArrayList<>();
        String fullPath = "http://s.weibo.com/list/relpage?search=" + searchkey.replace(" ", "");
        System.out.println(fullPath);
        HttpGet request = new HttpGet(fullPath);
        //request.setConfig(config);
        request.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
        HttpResponse response = HttpClients.createDefault().execute(request);
        String content = EntityUtils.toString(response.getEntity());
        String[] split = content.split("\n");
        for (String line : split) {
            if (line.contains("<script>STK && STK.pageletM && STK.pageletM.view({\"pid\":\"pl_list_relpage\"")) {
                int index = line.indexOf("html\":\"");
                if (index > 0) {
                    String substring = line.substring(index + 7, line.length());
                    Document parse = Jsoup.parse(WordUtil.convertUnicode(substring));
                    List<String> titles = parse.select("p.link_title2").select("a").eachAttr("title");
                    HashMap<String, Integer> map = new HashMap<>();
                    for (String title : titles) {
                        List<String> hots = WordUtil.sentenceProcess(title);
                        for (String hot : hots) {
                            map.put(hot, map.getOrDefault(hot, 0) + 1);
                        }
                    }
                    List<WordCount> wordCounts = new ArrayList<>();
                    for (Map.Entry<String, Integer> entry : map.entrySet()) {
                        WordCount wordCount = new WordCount();
                        wordCount.setWord(entry.getKey());
                        wordCount.setNum(entry.getValue());
                        wordCounts.add(wordCount);
                    }
                    wordCounts.sort(Comparator.comparingInt(WordCount::getNum));
                    if (wordCounts.size() > 20) {
                        reverseList = wordCounts.subList(0, 20);
                    } else {
                        reverseList = wordCounts;
                    }
                }
            }
        }
        return reverseList;
    }


}
