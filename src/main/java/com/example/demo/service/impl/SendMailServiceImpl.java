package com.example.demo.service.impl;

import com.example.demo.entity.EmailAuthenticator;
import com.example.demo.entity.EmailSendInfo;
import com.example.demo.entity.Person;
import com.example.demo.mapper.MailMapper;
import com.example.demo.service.SendMailService;
import com.example.demo.util.RandomPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class SendMailServiceImpl implements SendMailService {

    private static String mailFrom = "plm@tcl.com";// 指明邮件的发件人
    private static String password_mailFrom = "Md@200612";// 指明邮件的发件人登陆密码
    private static String mailTo = null;    // 指明邮件的收件人
    private static String mailTittle = "PLM用户名和密码";// 邮件的标题
    private static String mailText = null;    // 邮件的文本内容
    private static String mail_host = "mail.tcl.com";    // 邮件的服务器域名

    @Autowired
    private MailMapper mailMapper;

    public boolean sendMail1() throws Exception {
        ArrayList<Person> allPersonList = mailMapper.getAllPerson();
        //文本固定内容 todo
        StringBuilder sb = new StringBuilder("新系统PLM的登录用户名是 本人域账号");
        for (int i = 0; i < allPersonList.size(); i++) {
            sb.append("，密码是：").append(RandomPassword.getSomeString());
            mailText = sb.toString();
            mailTo = allPersonList.get(i).getMail();
            sb = null;
        }

        Properties prop = new Properties();
        prop.setProperty("mail.host", mail_host);
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.auth", "true");
        prop.setProperty("mail.smtp.port", "587");

        // 使用JavaMail发送邮件的5个步骤

        // 1、创建session
        Session session = Session.getInstance(prop);
        // 开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
        session.setDebug(true);
        // 2、通过session得到transport对象
        Transport ts = session.getTransport();
        // 3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
        ts.connect(mail_host,mailFrom, password_mailFrom);
        // 4、创建邮件
        Message message = createSimpleMail(session,mailFrom,mailTo,mailTittle,mailText);
        // 5、发送邮件
        ts.sendMessage(message, message.getAllRecipients());
        ts.close();


        return true;
    }

    public static MimeMessage createSimpleMail(Session session, String mailfrom, String mailTo, String mailTittle,
                                               String mailText) throws Exception {
        // 创建邮件对象
        MimeMessage message = new MimeMessage(session);
        // 指明邮件的发件人
        message.setFrom(new InternetAddress(mailfrom));
        // 指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));
        // 邮件的标题
        message.setSubject(mailTittle);
        // 邮件的文本内容
        message.setContent(mailText, "text/html;charset=UTF-8");
        // 返回创建好的邮件对象
        return message;
    }


    @Override
    public boolean sendTextMail() {
        ArrayList<Person> allPersonList = mailMapper.getAllPerson();
        EmailSendInfo info = new EmailSendInfo();
        //文本固定内容 todo
        StringBuilder sb = new StringBuilder("新系统PLM的登录用户名是 本人域账号");
        //邮箱服务器ip todo
        info.setMailServerHost("mail.tcl.com");
        //邮箱服务器端口 todo
        info.setMailServerPort("587");
        //发送者邮箱用户名和密码 todo
        info.setUserName("plm@tcl.com");
        info.setPassword("Md@200612");
        info.setFromAddress("plm@tcl.com");
        //邮箱主题 todo
        info.setSubject("PLM用户名和密码");
        for (int i = 0; i < allPersonList.size(); i++) {
            info.setToAddress(allPersonList.get(i).getMail());
            sb.append("，密码是：").append(RandomPassword.getSomeString());
            info.setContent(sb.toString());
            sent(info);
            sb = null;
        }
        return true;
    }

    private boolean sent(EmailSendInfo mailInfo) {
        boolean sendStatus = false;//发送状态
        // 判断是否需要身份认证
        EmailAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        //如果需要身份认证，则创建一个密码验证器
        if (mailInfo.isValidate()) {
            authenticator = new EmailAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
        //【调试时使用】开启Session的debug模式
        sendMailSession.setDebug(true);
        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            Address to = new InternetAddress(mailInfo.getToAddress());
            // Message.RecipientType.TO属性表示接收者的类型为TO
            mailMessage.setRecipient(Message.RecipientType.TO, to);
            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // 设置邮件内容
            mailMessage.setContent(mailInfo.getContent(), "text/html;charset=utf-8");

            //生成邮件文件
            createEmailFile("/Users/apple/Desktop/file/EML_myself-HTML.eml", mailMessage);

            // 发送邮件
            Transport.send(mailMessage);
            sendStatus = true;
        } catch (MessagingException ex) {
            System.out.println("以HTML格式发送邮件出现异常" + ex);
            return sendStatus;
        }
        return sendStatus;
    }

    public static void createEmailFile(String fileName, Message mailMessage)
            throws MessagingException {

        File f = new File(fileName);
        try {
            mailMessage.writeTo(new FileOutputStream(f));
        } catch (IOException e) {
            System.out.println("IOException" + e);
        }
    }

    public void sentMail() {

    }
}
