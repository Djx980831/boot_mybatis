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
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@Service
public class SendMailServiceImpl implements SendMailService {

    @Autowired
    private MailMapper mailMapper;

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
        //邮箱主题 todo
        info.setSubject("PLM用户名和密码");
        for (int i = 0; i < allPersonList.size(); i++) {
            info.setFromAddress(allPersonList.get(i).getMail());
            sb.append("，密码是：").append(RandomPassword.getSomeString());
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
        Session sendMailSession = Session.getDefaultInstance(pro,authenticator);
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
            mailMessage.setRecipient(Message.RecipientType.TO,to);
            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // 设置邮件内容
            mailMessage.setContent(mailInfo.getContent(), "text/html;charset=utf-8");

            //生成邮件文件
            createEmailFile("file/EML_myself-HTML.eml", mailMessage);

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

}
