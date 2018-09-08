package com.ruanwenjun.entity;

/**
 * @Author RUANWENJUN
 * @Creat 2018-09-07 16:52
 */

public class WordCount {
    private String word;
    private Integer num;

    @Override
    public String toString() {
        return "com.ruanwenjun.entity.WordCount{" +
                "word='" + word + '\'' +
                ", num=" + num +
                '}';
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
