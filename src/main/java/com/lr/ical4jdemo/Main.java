package com.lr.ical4jdemo;

import com.sun.tools.internal.ws.wsdl.document.soap.SOAPUse;
import net.fortuna.ical4j.model.TimeZone;

import java.util.Calendar;
import java.util.Date;

/**
 * @author liurui
 * @date 2020/3/27 3:20 下午
 */
public class Main {
    public static void main(String[] args) {

        Calendar calendar = Calendar.getInstance();
        System.out.println("目前时间：" + calendar.getTime());
        System.out.println("Calendar时区：：" + calendar.getTimeZone().getID());
        System.out.println("user.timezone：" + System.getProperty("user.timezone"));
        System.out.println("user.country：" + System.getProperty("user.country"));
        System.out.println("默认时区：" + TimeZone.getDefault().getID());
    }
}
