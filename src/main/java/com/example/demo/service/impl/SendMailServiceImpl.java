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
    private static String mailTittle = "PLM系统用户名和密码";// 邮件的标题
    private static String mailText = null;    // 邮件的文本内容
    private static String mail_host = "mail.tcl.com";    // 邮件的服务器域名

    @Autowired
    private MailMapper mailMapper;

    public boolean sendMail() throws Exception {
        ArrayList<Person> allPersonList = mailMapper.getAllPerson();

        Properties prop = new Properties();
        prop.setProperty("mail.host", mail_host);
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.auth", "true");
        prop.setProperty("mail.smtp.port", "587");

        for (int i = 0; i < allPersonList.size(); i++) {
//            StringBuilder sb = new StringBuilder("  新系统PLM登录链接：https://plm.tcl.com/3dspace" + "\n" + "登录用户名：" + allPersonList.get(i).getId() + "\n");
//            sb.append("，密码是：").append(allPersonList.get(i).getPassword());
            String content = "<html><head><head><body>" +
                    "<p>Hi, " + allPersonList.get(i).getId() + ":" +
                    "<p>PLM系统登录相关信息如下：</p>" +
                    "<p>&nbsp&nbsp&nbsp PLM系统登录链接：https://plm.tcl.com/3dspace</p>" +
                    "<p>&nbsp&nbsp&nbsp 登录用户名：" + allPersonList.get(i).getId() + "</p>" +
                    "<p>&nbsp&nbsp&nbsp 初始密码：Aa123456</p>" +
                    "<p>&nbsp&nbsp&nbsp 修改密码及操作手册文档链接：https://plm.tcl.com/3dspace/engineeringcentral/tclCommonDownloadDocFS.jsp</p>" +
                    "<p>如有问题，请联系PLM项目组。感谢支持与配合！</p>" +
                    "</body></html>";
            mailText = content;
            mailTo = allPersonList.get(i).getMail();

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
        }

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
}
