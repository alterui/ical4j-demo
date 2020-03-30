package com.lr.ical4jdemo.email;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

/**
 * @author liurui
 * @date 2020/3/30 10:01 上午
 */
public class Solution {
    public static void main(String[] args) {
        sendMeetingInvitationEmail();
    }

    private static Properties props;
    private static Session session;

    public static void sendMeetingInvitationEmail() {
        try {
            props = new Properties();
            //发件人
            String fromEmail = props.getProperty("fromEmail", "656812771@qq.com");
            //收件人(面试官)
            String toEmail = props.getProperty("toEmail", "liurui@moseeker.com");
            //String toEmail = props.getProperty("toEmail", "2389889598@qq.com");
            //String toEmail = props.getProperty("toEmail", "656812771@qq.com");
            //String toEmail = props.getProperty("toEmail", "alter0129@gmail.com");
            //String toEmail = props.getProperty("toEmail", "njut_lr@163.com");
            //props.put("mail.smtp.port", "587");
            props.put("mail.smtp.host", "smtp.qq.com");
            //当前smtp host设为可信任 否则抛出javax.mail.MessagingException: Could not  convert socket to TLS
            props.put("mail.smtp.ssl.trust", "smtp.qq.com");
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl", "true");
            //开启debug调试，控制台会打印相关信息
            props.put("mail.debug", "true");
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    //发件人邮箱账号
                    String userId = props.getProperty("userId", "656812771@qq.com");
                    //发件人邮箱密码(qq、163等邮箱用的是授权码,outlook是密码)
                    String password = props.getProperty("password", "gduwdjvkwbfzbaje");
                    return new PasswordAuthentication(userId, password);
                }
            };
            session = Session.getInstance(props, authenticator);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            //标题
            message.setSubject("XXX公司诚邀应聘");
            //面试开始时间
            String startTime = getUtc("2020-03-30 14:00");
            //面试结束时间
            String endTime = getUtc("2020-03-30 15:00");
            BodyPart messageBodyPart = new MimeBodyPart();
            String buffer = "BEGIN:VCALENDAR\n"
                    + "PRODID:-//Microsoft Corporation//Outlook 9.0 MIMEDIR//EN\n"
                    + "VERSION:2.0\n"
                    + "METHOD:REQUEST\n"
                    + "BEGIN:VEVENT\n"
                    //参会者
                    + "ATTENDEE;ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:你和应聘者\n"
                    //组织者
                    //+ "ORGANIZER:MAILTO:张三\n"
                    + "DTSTART:" + startTime + "\n"
                    + "DTEND:" + endTime + "\n"
                    //面试地点
                    + "LOCATION:会议室01\n"
                    //如果id相同的话，outlook会认为是同一个会议请求，所以使用uuid。
                    + "UID:" + UUID.randomUUID().toString() + "\n"
                    + "CATEGORIES:\n"
                    //会议描述
                    //+ "DESCRIPTION:Stay Hungry.<br>Stay Foolish.\n\n"
                    + "SUMMARY:面试邀请\n" + "PRIORITY:5\n"
                    + "CLASS:PUBLIC\n" + "BEGIN:VALARM\n"
                    //提前10分钟提醒
                    + "TRIGGER:-PT10M\n" + "ACTION:DISPLAY\n"
                    + "DESCRIPTION:Reminder\n" + "END:VALARM\n"
                    + "END:VEVENT\n" + "END:VCALENDAR";
            //参会者
            //组织者
            //+ "ORGANIZER:MAILTO:张三\n"
            //面试地点
            //如果id相同的话，outlook会认为是同一个会议请求，所以使用uuid。
            //会议描述
            //+ "DESCRIPTION:Stay Hungry.<br>Stay Foolish.\n\n"
            //提前10分钟提醒
            messageBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(buffer,
                    "text/calendar;method=REQUEST;charset=\"UTF-8\"")));
            MimeMultipart multipart = new MimeMultipart();
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            //String emailText = getHtmlContent(sendEmailApi.getTemplateContent(tempValue),tempMap);
            //文本类型正文
            mimeBodyPart.setText("尊敬的XX:\r您好！\r特邀您来面试");
            //html类型正文
            //mimeBodyPart.setContent(emailText,"text/html;charset=UTF-8");
            //添加正文
            multipart.addBodyPart(mimeBodyPart);
            //添加日历
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            message.setSentDate(new Date());
            message.saveChanges();
            Transport.send(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 转utc时间
     *
     * @param str
     * @return
     */
    private static String getUtc(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long millionSeconds = 0;
        try {
            millionSeconds = sdf.parse(str).getTime();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        //utc时间差8小时
        long currentTime = millionSeconds - 8 * 60 * 60 * 1000;
        Date date = new Date(currentTime);
        //格式化日期
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = "";
        nowTime = df.format(date);
        //转换utc时间
        return nowTime.replace("-", "").replace(" ", "T").replace(":", "");
    }
}