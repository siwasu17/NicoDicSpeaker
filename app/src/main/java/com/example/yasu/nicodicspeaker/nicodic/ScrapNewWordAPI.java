package com.example.yasu.nicodicspeaker.nicodic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Scraping Niconico dictionary words which recently updated.
 * Created by yasu on 15/10/23.
 */
public class ScrapNewWordAPI {
    private static final String LOG_TAG = ScrapNewWordAPI.class.getSimpleName();

    //最近更新された単語を取得する
    public static ArrayList<String> getNewWordList() throws IOException {
        final String dicListUrl = "http://dic.nicovideo.jp/m/u/a/1-";
        Document document = Jsoup.connect(dicListUrl).get();

        Elements elements = document.select("div.overflow-title a");

        ArrayList<String> list = new ArrayList<>();
        for (Element element : elements) {
            list.add(element.text());
        }
        return list;
    }
}
