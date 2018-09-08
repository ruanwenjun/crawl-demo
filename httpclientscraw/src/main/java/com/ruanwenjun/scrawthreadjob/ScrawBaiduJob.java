package com.ruanwenjun.scrawthreadjob;

import com.ruanwenjun.entity.ScrawEntity;
import com.ruanwenjun.utils.WordUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * 爬取百度相关信息
 *
 * @Author RUANWENJUN
 * @Creat 2018-09-08 14:49
 */

public class ScrawBaiduJob implements Callable<List<ScrawEntity>> {
    private String site;
    private String url;

    public ScrawBaiduJob(String site, String url) {
        this.site = site;
        this.url = url;
    }

    @Override
    public List<ScrawEntity> call() throws Exception {
        List<ScrawEntity> list = new ArrayList<>();
        HttpGet request = new HttpGet(url);
        //request.setConfig(config);
        request.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
        HttpResponse response = HttpClients.createDefault().execute(request);
        String content = EntityUtils.toString(response.getEntity(), "gbk");
        Document doc = Jsoup.parse(content);
        List<String> hots = doc.select("td.keyword").select("a.list-title").eachText();
        List<String> counts = doc.select("td.last").select("span").eachText();
        for (int i = 0; i < hots.size() && i < counts.size(); i++) {
            Set<String> set = WordUtil.processHot(hots.get(i));
            set.remove("");
            ScrawEntity baiduEntity = new ScrawEntity();
            baiduEntity.setSite(site);
            baiduEntity.setCount(counts.get(i));
            baiduEntity.setWords(set);
            baiduEntity.setHot(hots.get(i));
            list.add(baiduEntity);
            System.out.println(Thread.currentThread().getName() + "---------" + baiduEntity);
        }
        list.sort((o1, o2) -> Integer.parseInt(o2.getCount()) - Integer.parseInt(o1.getCount()));
        return list;
    }

}
