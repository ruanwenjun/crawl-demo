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
 * 爬取微博的任务
 *
 * @Author RUANWENJUN
 * @Creat 2018-09-08 11:28
 */

public class ScrawWeiBoJob implements Callable<List<ScrawEntity>> {
    @Override
    public List<ScrawEntity> call() throws Exception {
        List<ScrawEntity> list = new ArrayList<>();
        HttpGet request = new HttpGet("http://s.weibo.com/top/summary?Refer=top_hot&topnav=1&wvr=6");
        //request.setConfig(config);
        request.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
        HttpResponse response = HttpClients.createDefault().execute(request);
        String content = EntityUtils.toString(response.getEntity());
        String doc = Jsoup.parse(content).toString();
        String[] split = doc.split("\n");
        for (String line : split) {
            if (line.contains("<script>STK && STK.pageletM && STK.pageletM.view({\"pid\":\"pl_top_realtimehot\"")) {
                int index = line.indexOf("html\":\"");
                if (index > 0) {
                    String page = WordUtil.convertUnicode(line.substring(index + 7, line.length())).replace("\\", "");
                    int index1 = page.indexOf("<p class=\"star_name\">");
                    Document parse = Jsoup.parse(page.substring(index1, page.length()));
                    List<String> hots = parse.select("a[href]").eachText();
                    List<String> counts = parse.select("p.star_num").eachText();
                    hots.remove(0);
                    for (int i = 0; i < hots.size() && i < counts.size(); i++) {
                        String word = hots.get(i);
                        Set<String> set = WordUtil.processHot(word);
                        set.remove("");
                        ScrawEntity weiboEntity = new ScrawEntity();
                        weiboEntity.setSite("weibo");
                        weiboEntity.setCount(counts.get(i));
                        weiboEntity.setWords(set);
                        weiboEntity.setHot(word);
                        list.add(weiboEntity);
                        System.out.println(Thread.currentThread().getName() + "---------" +weiboEntity);
                    }
                }
            }
        }
        list.sort((o1, o2) -> Integer.parseInt(o2.getCount()) - Integer.parseInt(o1.getCount()));
        return list;
    }

}
