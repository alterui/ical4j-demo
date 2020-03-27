package com.lr.ical4jdemo;


import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.UidGenerator;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Security;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author liurui
 * @date 2020/3/27 11:37 上午
 */
public class Application {

    private static String from = "656812771@qq.com";
    /**
     * 收件人
     */
    private static String to = "656812771@qq.com";
    //标题
    private static String subject = "test";
    //邮件内容
    private static String content = "青空报告总结会议";



    public static Multipart getContentText() throws Exception {
        // 时区
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();


        TimeZone timezone = registry.getTimeZone("Asia/Shanghai");
        VTimeZone tz = timezone.getVTimeZone();

        // 会议地点
        String location = "上海";
        // 会议时间
        java.util.Calendar cal = java.util.Calendar.getInstance();

        cal.setTimeZone(timezone);
        //会议启动时间n// 月份是要早一个月
        cal.set(2020, java.util.Calendar.MARCH, 28, 13, 45);
        DateTime start = new DateTime(cal.getTime());
        //会议结束时间
        cal.set(2020, java.util.Calendar.MARCH, 29, 18, 55);
        DateTime end = new DateTime(cal.getTime());

        // --------事件（VEvent start）----------
        VEvent vevent = new VEvent(start, end, subject);
        vevent.getProperties().add(timezone.getVTimeZone().getTimeZoneId());// 时区
        // 添加时区信息
        vevent.getProperties().add(tz.getTimeZoneId());

        // 会议地点
        vevent.getProperties().add(new Location(location));
        // 邮件主题
        vevent.getProperties().add(new Summary(subject));
        // 邮件内容
        vevent.getProperties().add(new Description(content));
        vevent.getProperties().add(new UidGenerator("meeting invite").generateUid());// 设置uid
        vevent.getProperties().add(new Organizer(URI.create("mailto:" + from)));
        // 与会人
        Set<String> emailSet = new HashSet<>();
        emailSet.add(from);
        emailSet.add(to);
        int i = 1;
        for (String email : emailSet) {
            Attendee attendee = new Attendee(URI.create("mailto:" + email));
            if (1 == i) {
                attendee.getParameters().add(Role.REQ_PARTICIPANT);
            } else {
                attendee.getParameters().add(Role.OPT_PARTICIPANT);
            }
            attendee.getParameters().add(new Cn("Developer" + i));
            vevent.getProperties().add(attendee);
            i++;
        }
        // --------VEvent Over----------

        // --------提醒（VAlarm Start）----------
        // 提前10分钟提醒
        VAlarm valarm = new VAlarm(new Dur(0, 0, -10, 0));
        // 重复一次
        valarm.getProperties().add(new Repeat(1));
        // 持续十分钟
        valarm.getProperties().add(new Duration(new Dur(0, 0, 10, 0)));

        // 提醒窗口显示的文字信息
        valarm.getProperties().add(new Summary("Event Alarm"));
        valarm.getProperties().add(Action.DISPLAY);
        valarm.getProperties().add(new Description("Progress Meeting at 9:30am"));
        vevent.getAlarms().add(valarm);// 将VAlarm加入VEvent
        // --------VAlarm Over-------------

        // --------日历对象 Start---------------
        Calendar icsCalendar = new Calendar();
        icsCalendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
        icsCalendar.getProperties().add(CalScale.GREGORIAN);
        icsCalendar.getProperties().add(Version.VERSION_2_0);
        icsCalendar.getProperties().add(Method.REQUEST);
        icsCalendar.getComponents().add(vevent);// 将VEvent加入Calendar


        // 将日历对象转换为二进制流
        CalendarOutputter co = new CalendarOutputter(false);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        co.output(icsCalendar, os);
        byte[] mailbytes = os.toByteArray();
        // --------日历对象 Over------------------

        BodyPart mbp = new MimeBodyPart();
        mbp.setContent(mailbytes, "text/calendar;method=REQUEST;charset=UTF-8");

        MimeMultipart mm = new MimeMultipart();
        mm.setSubType("related");
        mm.addBodyPart(mbp);
        return mm;
    }


    public static void main(String[] args) {
        // 链接邮件服务器
        Properties props = new Properties();
        // 邮件协议
        props.put("mail.transport.protocol", "smtp");
        // 服务器域名
        props.put("mail.smtp.host", "smtp.qq.com");
        props.put("mail.smtp.auth", "true");
        //账号密码认证
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                String username = "656812771@qq.com"; // 大多数是你邮件@前面的部分
                String pwd = "gduwdjvkwbfzbaje";
                return new PasswordAuthentication(username, pwd);
            }
        };
        Session mailSession = Session.getInstance(props, auth);
        // 获取Message对象
        Message msg = new MimeMessage(mailSession);
        try {
            // 设置邮件基本信息
            //发件人
            msg.setFrom(new InternetAddress(from));
            //收件人
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            //发送时间
            msg.setSentDate(new java.util.Date());
            //发送标题
            msg.setSubject(subject);
            // 获取不同类型的邮件的邮件内容
            Multipart mp = getContentText();
            msg.setContent(mp);
            msg.saveChanges();
        } catch (Exception ex) {
        }
        System.out.println(sendEmail(msg));
    }

    public static Boolean sendEmail(Message msg) {
        // 发送邮件
        try {
            Transport.send(msg);
            return true;
        } catch (SendFailedException e) {// 邮件地址无效
            System.out.println(e);
            return false;
        } catch (Exception e) {

            System.out.println(e);
            return false;
        }
    }




    /**
     * 构建会议邀约日历对象
     *
     * @param summary            摘要，会议邮件显示在日历插件上的标题
     * @param startTimestamp     会议开始时间，GMT+8
     * @param endTimestamp       会议结束时间，GMT+8
     * @param LocationContent    会议位置
     * @param toMailAddressArray 邀约人
     * @return
     */
    public Calendar buildCalendar(String summary, Long startTimestamp, Long endTimestamp, String LocationContent, String[] toMailAddressArray) {
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone timezone = registry.getTimeZone("Asia/Shanghai");
        VTimeZone tz = timezone.getVTimeZone();

        // 创建日历
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        // ⭐️下面这行很关键，缺少的话钉钉IOS邮箱会显示1970--01-01 08:00
        calendar.getProperties().add(Method.REQUEST);

        DateTime start = new DateTime(startTimestamp);
        start.setTimeZone(timezone);
        DateTime end = new DateTime(endTimestamp);
        end.setTimeZone(timezone);
        VEvent event = new VEvent(start, end, summary);
        event.getProperties().add(new Location(LocationContent));
        try {
            // 生成唯一标示
            event.getProperties().add(new Uid(new UidGenerator("iCal4j").generateUid().getValue()));
            // 添加时区信息
            event.getProperties().add(tz.getTimeZoneId());
            // 组织者
            event.getProperties().add(new Organizer("mailto:preview@alibaba-inc.com"));
        } catch (SocketException | URISyntaxException e) {
            e.printStackTrace();
        }
        // 添加邀请者
        for (int i = 0; i < toMailAddressArray.length; i++) {
            Attendee dev = new Attendee(URI.create("mailto:" + toMailAddressArray[i]));
            dev.getParameters().add(Role.REQ_PARTICIPANT);
            dev.getParameters().add(new Cn("Developer " + (i + 1)));
            event.getProperties().add(dev);
        }
    /*
    // 重复事件
    Recur recur = new Recur(Recur.WEEKLY, Integer.MAX_VALUE);
    recur.getDayList().add(WeekDay.MO);
    recur.getDayList().add(WeekDay.TU);
    recur.getDayList().add(WeekDay.WE);
    recur.getDayList().add(WeekDay.TH);
    recur.getDayList().add(WeekDay.FR);
    RRule rule = new RRule(recur);
    event.getProperties().add(rule);
    */
        // 提醒,提前10分钟
        VAlarm valarm = new VAlarm(new Dur(0, 0, -10, 0));
        valarm.getProperties().add(new Summary("事件提醒"));
        valarm.getProperties().add(Action.DISPLAY);
        valarm.getProperties().add(new Description("会议提醒描述，待定，不确定使用方式"));
        // 将VAlarm加入VEvent
        event.getAlarms().add(valarm);
        // 添加事件
        calendar.getComponents().add(event);
        // 验证
        try {
            calendar.validate();
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        return calendar;
    }




}
