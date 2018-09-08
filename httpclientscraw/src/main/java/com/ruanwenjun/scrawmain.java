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
        //JDBCUtil.insert(queryList);
    }


}
