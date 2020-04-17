package com.lr.ical4jdemo.email;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.UidGenerator;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

/**
 * @author liurui
 * @date 2020/4/15 2:50 下午
 */
public class CreateFile {

    public static void main(String[] args) throws MessagingException, IOException, ValidationException {
        get1();

    }



    public static void get1() throws ValidationException, IOException, MessagingException {
        // 创建一个时区（TimeZone）
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone timezone = registry.getTimeZone("Asia/Shanghai");
        VTimeZone tz = timezone.getVTimeZone();

        // 创建日历
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        // 时间主题
        String summary = "面试邀请";
        // 开始时间
        DateTime start = new DateTime(new java.util.Date());
        // 开始时间转换为UTC时间（UTC ＋ 时区差 ＝ 本地时间 ）
        start.setUtc(true);
        // 结束时间
        DateTime end = new DateTime(new java.util.Date());
        // 结束时间设置成UTC时间（UTC ＋ 时区差 ＝ 本地时间 ）
        end.setUtc(true);
        // 新建普通事件
        VEvent event = new VEvent(start, end, summary);
        // 定义全天事件（注意默认是UTC时间）
        //VEvent event = new VEvent(new Date(), new Date(), summary);
        //event.getProperties().add(new Location("南京堵路"));
        // 生成唯一标示
        event.getProperties().add(new Uid(new UidGenerator("iCal4j").generateUid().getValue()));
        // 添加时区信息
        event.getProperties().add(tz.getTimeZoneId());
        // 添加邀请者
        Attendee dev1 = new
                Attendee(URI.create("liurui@moseeker.com"));
        dev1.getParameters().add(Role.REQ_PARTICIPANT);
        dev1.getParameters().add(new Cn("张三"));
        event.getProperties().add(dev1);
//			// 重复事件
//			Recur recur = new Recur(Recur.WEEKLY, Integer.MAX_VALUE);
//			recur.getDayList().add(WeekDay.MO);
//			recur.getDayList().add(WeekDay.TU);
//			recur.getDayList().add(WeekDay.WE);
//			recur.getDayList().add(WeekDay.TH);
//			recur.getDayList().add(WeekDay.FR);
//			RRule rule = new RRule(recur);
//			event.getProperties().add(rule);
        // 提醒,提前10分钟
        VAlarm valarm = new VAlarm(new Dur(0, 0, -10, 0));
        valarm.getProperties().add(new Summary("Event Alarm"));
        valarm.getProperties().add(Action.DISPLAY);
        valarm.getProperties().add(new Description("Progress Meeting at 9:30am"));
        // 将VAlarm加入VEvent
        event.getAlarms().add(valarm);
        // 添加事件
        calendar.getComponents().add(event);
        // 验证
        calendar.validate();
       /* // 将日历对象转换为二进制流
        CalendarOutputter co = new CalendarOutputter(false);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        co.output(calendar, os);
        byte[] mailbytes = os.toByteArray();
        // --------日历对象 Over------------------

        BodyPart mbp = new MimeBodyPart();
        mbp.setContent(mailbytes, "text/calendar;method=REQUEST;charset=UTF-8");

        MimeMultipart mm = new MimeMultipart();
        mm.setSubType("related");
        mm.addBodyPart(mbp);
*/

        FileOutputStream fout = new FileOutputStream("/Users/liurui/Desktop/6.ics");
        CalendarOutputter outputter = new CalendarOutputter();
        outputter.output(calendar, fout);
    }

}
