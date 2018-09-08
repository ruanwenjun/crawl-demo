package com.ruanwenjun.utils;

import com.huaban.analysis.jieba.JiebaSegmenter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 处理词汇的工具
 *
 * @Author RUANWENJUN
 * @Creat 2018-09-08 11:10
 */

public class WordUtil {
    private volatile static Set<String> stopSet = new HashSet<>();
    private static Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
    private static JiebaSegmenter segmenter = new JiebaSegmenter();
    static {
        try {
            // 加载过滤词
            URL path = ClassLoader.getSystemResource("stopword.txt");
            BufferedReader reader = new BufferedReader(new FileReader(path.getFile()));
            String line;
            while ((line = reader.readLine()) != null) {
                stopSet.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否为垃圾词
     * @return
     */
    public static boolean isBadWord(String word) {
        return word.equals("") || word.length() <= 1 || stopSet.contains(word) || pattern.matcher(word).matches();
    }

    /**
     * 将字符串转换从unicode转换
     * @param ori
     * @return
     */
    public static String convertUnicode(String ori) {
        char aChar;
        int len = ori.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = ori.charAt(x++);
            if (aChar == '\\') {
                aChar = ori.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = ori.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }

    /**
     * 对热词进行处理
     *
     * @param hotWord
     * @return
     */
    public static Set<String> processHot(String hotWord) {
        String hot = hotWord.replace("#", "").replace("'", "");
        Set<String> set = new HashSet<>();
        Set<String> result = new HashSet<>();
        if (hot.contains(" ")) {
            set.addAll(Arrays.asList(hot.split(" ")));
        } else {
            if (hot.length() > 6) {
                set.addAll(segmenter.sentenceProcess(hot));
            } else {
                set.add(hot);
            }
        }
        for (String s : set) {
            if (!WordUtil.isBadWord(s)) {
                result.add(s);
            }
        }
        return result;
    }

    public static List<String> sentenceProcess(String word){
        return segmenter.sentenceProcess(word);
    }


}
