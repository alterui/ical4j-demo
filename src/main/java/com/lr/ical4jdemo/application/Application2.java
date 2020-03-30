package com.lr.ical4jdemo.application;


import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.UidGenerator;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


/**
 * @author liurui
 * @date 2020/3/27 11:37 上午
 */
public class Application2 {

    private static String from = "656812771@qq.com";
    /**
     * 收件人
     */
    private static String to = "liurui@moseeker.com";
     ///private static String to = "656812771@qq.com";

    //private static String to = "njut_lr@163.com";


    public static Multipart getContentText() throws Exception {


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


        // 创建一个时区（TimeZone）
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone timezone = registry.getTimeZone("America/Mexico_City");
        VTimeZone tz = timezone.getVTimeZone();

        // 起始时间是：2008 年 4 月 1 日 上午 9 点
        java.util.Calendar startDate = new GregorianCalendar();
        startDate.setTimeZone(timezone);
        startDate.set(java.util.Calendar.MONTH, java.util.Calendar.APRIL);
        startDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
        startDate.set(java.util.Calendar.YEAR, 2020);
        startDate.set(java.util.Calendar.HOUR_OF_DAY, 9);
        startDate.set(java.util.Calendar.MINUTE, 0);
        startDate.set(java.util.Calendar.SECOND, 0);

        // 结束时间是：2008 年 4 月 1 日 下午 1 点
        java.util.Calendar endDate = new GregorianCalendar();
        endDate.setTimeZone(timezone);
        endDate.set(java.util.Calendar.MONTH, java.util.Calendar.APRIL);
        endDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
        endDate.set(java.util.Calendar.YEAR, 2020);
        endDate.set(java.util.Calendar.HOUR_OF_DAY, 13);
        endDate.set(java.util.Calendar.MINUTE, 0);
        endDate.set(java.util.Calendar.SECOND, 0);

        // 创建事件
        String eventName = "Progress Meeting";
        DateTime start = new DateTime(startDate.getTime());
        DateTime end = new DateTime(endDate.getTime());
        VEvent meeting = new VEvent(start, end, eventName);

        // 添加时区信息
        meeting.getProperties().add(tz.getTimeZoneId());

        // 生成唯一标志符
        UidGenerator ug = new UidGenerator("uidGen");
        Uid uid = ug.generateUid();
        meeting.getProperties().add(uid);

        // 添加参加者 .
        Attendee dev1 = new Attendee(URI.create("mailto:dev1@mycompany.com"));
        dev1.getParameters().add(Role.REQ_PARTICIPANT);
        dev1.getParameters().add(new Cn("Developer 1"));
        meeting.getProperties().add(dev1);

        Attendee dev2 = new Attendee(URI.create("mailto:dev2@mycompany.com"));
        dev2.getParameters().add(Role.OPT_PARTICIPANT);
        dev2.getParameters().add(new Cn("Developer 2"));
        meeting.getProperties().add(dev2);

        // 创建日历
        net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
        icsCalendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
        icsCalendar.getProperties().add(CalScale.GREGORIAN);

        // 添加事件
        icsCalendar.getComponents().add(meeting);

//
//        // 提醒,提前10分钟
//        VAlarm valarm = new VAlarm(new Dur(0, 0, -10, 0));
//        valarm.getProperties().add(new Summary("事件提醒"));
//        valarm.getProperties().add(Action.DISPLAY);
//        valarm.getProperties().add(new Description("会议提醒描述，待定，不确定使用方式"));
//        // 将VAlarm加入VEvent
//        event.getAlarms().add(valarm);
//        // 添加事件
//        calendar.getComponents().add(event);
//        // 验证
//        try {
//            calendar.validate();
//        } catch (ValidationException e) {
//            e.printStackTrace();
//        }


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
            //标题
            String subject = "面试通知";
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
        } catch (Exception e) {// 邮件地址无效
            System.out.println(e);
            return false;
        }
    }


}
