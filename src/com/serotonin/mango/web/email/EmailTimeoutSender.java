package com.serotonin.mango.web.email;

import com.serotonin.mango.util.SendEmailConfig;
import org.springframework.mail.MailException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Properties;

public class EmailTimeoutSender {

    private final JavaMailSenderImpl senderImpl;

    public EmailTimeoutSender(SendEmailConfig config) {
        this(config.getHost(), config.getPort(), config.isAuthorization(), config.getUsername(),
                config.getPassword(), config.isTls(), config.getTimeout());
    }

    private EmailTimeoutSender(String host, int port, boolean useAuth, String userName,
                               String password, boolean tls, int timeout) {
        this.senderImpl = new JavaMailSenderImpl();
        Properties props = senderImpl.getJavaMailProperties();
        if (useAuth) {
            props.put("mail.smtp.auth", "true");
            this.senderImpl.setUsername(userName);
            this.senderImpl.setPassword(password);
        }

        if (tls) {
            props.put("mail.smtp.starttls.enable", "true");
        }

        if (timeout != -1) {
            props.put("mail.smtp.connectiontimeout", timeout);
            props.put("mail.smtp.timeout", timeout);
            props.put("mail.smtp.writetimeout", timeout);
        }

        //this.senderImpl.setJavaMailProperties(props);
        this.senderImpl.setHost(host);
        if (port != -1) {
            this.senderImpl.setPort(port);
        }
    }

    public void send(String fromAddr, String toAddr, String subject, String contentPlain, String contentHtml) {
        this.send(fromAddr, toAddr, subject, IMsgContent.newInstance(contentPlain, contentHtml));
    }

    public void send(String fromAddr, String toAddr, String subject, IMsgContent content) {
        this.send(fromAddr, (String)null, toAddr, subject, content);
    }

    public void send(String fromAddr, String fromPersonal, String toAddr, String subject, String contentPlain, String contentHtml) {
        this.send(fromAddr, fromPersonal, toAddr, subject, IMsgContent.newInstance(contentPlain, contentHtml));
    }

    public void send(InternetAddress from, String toAddr, String subject, IMsgContent content) {
        try {
            this.send(from, new InternetAddress[]{new InternetAddress(toAddr)}, (InternetAddress[])null, (InternetAddress[])null,
                    subject, content);
        } catch (AddressException var6) {
            throw new MailPreparationException(var6);
        }
    }

    public void send(String fromAddr, String fromPersonal, String toAddr, String subject, IMsgContent content) {
        try {
            this.send(new InternetAddress(fromAddr, fromPersonal), new InternetAddress[]{new InternetAddress(toAddr)},
                    (InternetAddress[])null, (InternetAddress[])null, subject, content);
        } catch (AddressException var7) {
            throw new MailPreparationException(var7);
        } catch (UnsupportedEncodingException var8) {
            throw new MailPreparationException(var8);
        }
    }

    public void send(String fromAddr, String[] toAddr, String subject, String contentPlain, String contentHtml) {
        this.send(fromAddr, (String)null, toAddr, subject, IMsgContent.newInstance(contentPlain, contentHtml));
    }

    public void send(String fromAddr, String[] toAddr, String subject, IMsgContent content) {
        this.send(fromAddr, (String)null, toAddr, subject, content);
    }

    public void send(String fromAddr, String fromPersonal, String[] toAddr, String subject, String contentPlain, String contentHtml) {
        this.send(fromAddr, fromPersonal, toAddr, subject, IMsgContent.newInstance(contentPlain, contentHtml));
    }

    public void send(InternetAddress from, String[] toAddr, String subject, IMsgContent content) {
        try {
            InternetAddress[] toIAddr = new InternetAddress[toAddr.length];

            for(int i = 0; i < toAddr.length; ++i) {
                toIAddr[i] = new InternetAddress(toAddr[i]);
            }

            this.send(from, toIAddr, (InternetAddress[])null, (InternetAddress[])null, subject, content);
        } catch (AddressException var7) {
            throw new MailPreparationException(var7);
        }
    }

    public void send(String fromAddr, String fromPersonal, String[] toAddr, String subject, IMsgContent content) {
        try {
            InternetAddress[] toIAddr = new InternetAddress[toAddr.length];

            for(int i = 0; i < toAddr.length; ++i) {
                toIAddr[i] = new InternetAddress(toAddr[i]);
            }

            this.send(new InternetAddress(fromAddr, fromPersonal), toIAddr, (InternetAddress[])null, (InternetAddress[])null, subject, content);
        } catch (AddressException var8) {
            throw new MailPreparationException(var8);
        } catch (UnsupportedEncodingException var9) {
            throw new MailPreparationException(var9);
        }
    }

    public void send(InternetAddress from, InternetAddress[] to, String subject, IMsgContent content) {
        this.send(from, to, (InternetAddress[])null, (InternetAddress[])null, subject, content);
    }

    public MimeMessagePreparator createPreparator(final InternetAddress from, final InternetAddress replyTo, final InternetAddress[] to,
                                                  final InternetAddress[] cc, final InternetAddress[] bcc, final String subject,
                                                  final IMsgContent content) throws MailException {
        return mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, content.isMultipart(), content.getEncoding());
            helper.setFrom(from);
            if (replyTo != null) {
                helper.setReplyTo(replyTo);
            }

            helper.setTo(to);
            if (cc != null) {
                helper.setCc(cc);
            }

            if (bcc != null) {
                helper.setBcc(bcc);
            }

            String sub = subject.replaceAll("\\r", "");
            sub = sub.replaceAll("\\n", "");
            helper.setSubject(sub);
            if (content.getHtmlContent() == null) {
                helper.setText(content.getPlainContent(), false);
            } else if (content.getPlainContent() == null) {
                helper.setText(content.getHtmlContent(), true);
            } else {
                helper.setText(content.getPlainContent(), content.getHtmlContent());
            }

            Iterator i$ = content.getAttachments().iterator();

            while(i$.hasNext()) {
                AbstractEmailAttachment att = (AbstractEmailAttachment)i$.next();
                att.attach(helper);
            }

            i$ = content.getInlines().iterator();

            while(i$.hasNext()) {
                AbstractEmailInline inline = (AbstractEmailInline)i$.next();
                inline.attach(helper);
            }

        };
    }

    public void send(InternetAddress from, InternetAddress[] to, InternetAddress[] cc, InternetAddress[] bcc, String subject, IMsgContent content) throws MailException {
        this.send(this.createPreparator(from, (InternetAddress)null, to, cc, bcc, subject, content));
    }

    public void send(InternetAddress from, InternetAddress replyTo, InternetAddress[] to, InternetAddress[] cc, InternetAddress[] bcc, String subject, IMsgContent content) throws MailException {
        this.send(this.createPreparator(from, replyTo, to, cc, bcc, subject, content));
    }

    public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
        this.senderImpl.send(mimeMessagePreparator);
    }

    public void send(MimeMessagePreparator[] mimeMessagePreparators) throws MailException {
        this.senderImpl.send(mimeMessagePreparators);
    }
}
