package com.ruanwenjun.entity;

import java.util.Set;

/**
 * @Author RUANWENJUN
 * @Creat 2018-09-06 11:30
 */

public class ScrawEntity {
    /**
     * 爬取的站点
     */
    private String site;
    /**
     * 热词
     */
    private String hot;
    /**
     * 该hot的热度
     */
    private String count;
    /**
     * 分词
     */
    private Set<String> words;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }


    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public Set<String> getWords() {
        return words;
    }

    public void setWords(Set<String> words) {
        this.words = words;
    }

    public String getHot() {
        return hot;
    }

    public void setHot(String hot) {
        this.hot = hot;
    }
    @Override
    public String toString() {
        return "ScrawEntity{" +
                "site='" + site + '\'' +
                ", count='" + count + '\'' +
                ", words=" + words +
                ", hot='" + hot + '\'' +
                '}';
    }
}
