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

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
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
    private static String to = "liurui@moseeker.com";
    // private static String to = "656812771@qq.com";

   // private static String to = "njut_lr@163.com";


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

        System.setProperty("user.timezone","Asia/Shanghai");

        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone timezone = registry.getTimeZone("Asia/Shanghai");


        System.out.println("默认时区：" + timezone.getID());


        /*TimeZone timezone1 = (TimeZone) TimeZone.getDefault();*/

        VTimeZone tz = timezone.getVTimeZone();

        String location = "上海";

        // 创建日历
        Calendar calendar = new Calendar();

        calendar.getProperties().add(new ProdId("-//Microsoft Corporation//Outlook 9.0 MIMEDIR//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(Method.REQUEST);
       /* calendar.getProperties().add(CalScale.GREGORIAN);
*/
        // 下面这行很关键，缺少的话钉钉IOS邮箱会显示1970--01-01 08:00
        calendar.getProperties().add(Method.REQUEST);

        // Start Date is on: April 1, 2008, 9:00 am
        java.util.Calendar startDate = new GregorianCalendar();
        startDate.setTimeZone(timezone);
        startDate.set(java.util.Calendar.MONTH, java.util.Calendar.APRIL);
        startDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
        startDate.set(java.util.Calendar.YEAR, 2020);
        startDate.set(java.util.Calendar.HOUR_OF_DAY, 9);
        startDate.set(java.util.Calendar.MINUTE, 0);
        startDate.set(java.util.Calendar.SECOND, 0);

        // End Date is on: April 1, 2008, 13:00
        java.util.Calendar endDate = new GregorianCalendar();
        endDate.setTimeZone(timezone);
        endDate.set(java.util.Calendar.MONTH, java.util.Calendar.APRIL);
        endDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
        endDate.set(java.util.Calendar.YEAR, 2020);
        endDate.set(java.util.Calendar.HOUR_OF_DAY, 13);
        endDate.set(java.util.Calendar.MINUTE, 0);
        endDate.set(java.util.Calendar.SECOND, 0);


        DateTime start = new DateTime(startDate.getTime());
        DateTime end = new DateTime(endDate.getTime());

//        DateTime start = new DateTime(System.currentTimeMillis());
//        start.setTimeZone(timezone);
//        DateTime end = new DateTime(System.currentTimeMillis());
//        end.setTimeZone(timezone);
        //邮件内容
        String content = "青空报告总结会议";

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(df.format(start));
        System.out.println(df.format(end));


        VEvent event = new VEvent(start, end, content);
        event.getProperties().add(tz.getTimeZoneId());



        event.getProperties().add(new Location(location));
        try {
            // 生成唯一标示
            event.getProperties().add(new Uid(new UidGenerator("iCal4j").generateUid().getValue()));
            // 添加时区信息
            event.getProperties().add(tz.getTimeZoneId());
            // 组织者
            event.getProperties().add(new Organizer("mailto:moseeker.com"));
        } catch (SocketException | URISyntaxException e) {
            e.printStackTrace();
        }
        // 添加邀请者

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
            event.getProperties().add(attendee);
            i++;
        }

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
           // calendar.validate();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // 将日历对象转换为二进制流
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
