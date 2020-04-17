package com.lr.ical4jdemo.email;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author liurui
 * @date 2020/4/15 3:40 下午
 */
public class TimeUtil {
    public static void main(String[] args) throws ParseException {

        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);



        String time = "2020-04-16 15:00:00";

        Date parse = sdf.parse(time);

        Date afterTime = new Date(parse.getTime() + 15 * 60*1000);




        String format = sdf.format(parse);
        System.out.println(format);


        System.out.println(sdf.format(afterTime));


    }
}
