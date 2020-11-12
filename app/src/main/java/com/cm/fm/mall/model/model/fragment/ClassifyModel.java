package com.cm.fm.mall.model.model.fragment;

import android.content.Context;
import android.content.res.AssetManager;

import com.cm.fm.mall.contract.fragment.ClassifyContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClassifyModel implements ClassifyContract.Model {
    @Override
    public String getJsonData(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        //获得assets资源管理器
        AssetManager assetManager = context.getAssets();
        //使用IO流读取json文件内容
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open(fileName), "utf-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();

    }
}
