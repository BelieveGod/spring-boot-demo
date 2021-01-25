package com.example.springbootdemo.firmware;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LTJ
 * @version 1.0
 * @date 2021/1/25 14:45
 */
public class ExtractLog {
    public static void main(String[] args) {
        String relativelyPath=System.getProperty("user.dir");
        String fileUrl=relativelyPath+File.separator+"log"+File.separator+"canLog.log";
        List<String> outputs = new LinkedList<>();
        List<String> inputs = new LinkedList<>();

        List<String> strings = FileUtil.readLines(fileUrl, "utf-8");
        Pattern pattern = Pattern.compile("^将数据写入buf,buf.*?\"(.*)\"$");
        Pattern pattern2 = Pattern.compile("^serial receiver message.*?\"(.*)\"$");

        for (String string : strings) {
            Matcher matcher = pattern.matcher(string);
            Matcher matcher2 =pattern2.matcher(string);
            if (matcher.matches()) {
                String group = matcher.group(1);
                outputs.add(group);
            }else if(matcher2.matches()){
                String group = matcher2.group(1);
                outputs.add(group);
            }
        }
        System.out.println("outputs.size() = " + outputs.size());
        System.out.println(relativelyPath);
        String s = relativelyPath + File.separator + "canLogExtract.log";
        FileUtil.writeLines(outputs, s, "utf-8");

    }
}
