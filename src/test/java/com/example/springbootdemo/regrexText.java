package com.example.springbootdemo;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class regrexText {

    public static void main(String[] args) {
        String fileUrl="E:\\desktop\\rs232.log";
        List<String> outputs = new LinkedList<>();
        List<String> inputs = new LinkedList<>();

        List<String> strings = FileUtil.readLines(fileUrl, "utf-8");
//        strings.forEach(System.out::println);
        Pattern pattern = Pattern.compile("^将数据写入buf,buf.*?\"(.*)\"$");
        Pattern pattern2 = Pattern.compile("^serial receiver message.*?\"(.*)\"$");
        for (String string : strings) {
            Matcher matcher = pattern.matcher(string);
            Matcher matcher2 =pattern2.matcher(string);
            if (matcher.matches()) {
//                System.out.println("string = " + string);
//                boolean findFlag = matcher.find(0);
//                System.out.println("findFlag = " + findFlag);
//                int groupCount = matcher.groupCount();
//                System.out.println("groupCount = " + groupCount);
                String group = matcher.group(1);
                outputs.add(group);
//                System.out.println("group1 = " + group);
            }else if(matcher2.matches()){
//                System.out.println("string = " + string);
//                boolean findFlag = matcher2.find(0);
//                System.out.println("findFlag = " + findFlag);
//                int groupCount = matcher2.groupCount();
//                System.out.println("groupCount = " + groupCount);
                String group = matcher2.group(1);
                inputs.add(group);
//                System.out.println("group1 = " + group);
            }
        }

        System.out.println("===========输出语句=========");
        System.out.println("outputs.size() = " + outputs.size());


        System.out.println("===========输出语句=========");
        System.out.println("inputs.size() = " + inputs.size());

        outputs.forEach(System.out::println);
        String relativelyPath=System.getProperty("user.dir");
        System.out.println(relativelyPath);
        String s = relativelyPath + File.separator + "uartLogExtract.log";
        FileUtil.writeLines(inputs, s, "utf-8");

    }
}
