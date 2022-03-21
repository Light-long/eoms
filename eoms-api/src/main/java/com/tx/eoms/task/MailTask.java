package com.tx.eoms.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
@Scope("prototype")
public class MailTask {

    @Resource
    private JavaMailSender mailSender;

    @Value("${eoms.email.system}")
    private String systemEmail;

    @Async
    public void sendMailAsync(SimpleMailMessage message) {
        // 设置发件人
        message.setFrom(systemEmail);
        message.setCc(systemEmail);
        mailSender.send(message);
    }
}