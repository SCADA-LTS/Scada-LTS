package com.serotonin.mango.web.email;


import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.io.File;

public abstract class AbstractEmailInline {
    protected String contentId;

    public AbstractEmailInline(String contentId) {
        this.contentId = contentId;
    }

    public String getContentId() {
        return this.contentId;
    }

    public abstract void attach(MimeMessageHelper var1) throws MessagingException;

    public static class ByteArrayInline extends AbstractEmailInline {
        final byte[] content;
        private final String contentType;

        public ByteArrayInline(String contentId, byte[] content) {
            super(contentId);
            this.content = content;
            ConfigurableMimeFileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();
            fileTypeMap.afterPropertiesSet();
            this.contentType = fileTypeMap.getContentType(contentId);
        }

        public ByteArrayInline(String contentId, byte[] content, String contentType) {
            super(contentId);
            this.content = content;
            this.contentType = contentType;
        }

        public void attach(MimeMessageHelper mimeMessageHelper) throws MessagingException {
            mimeMessageHelper.addInline(this.contentId, () -> new ByteArrayInputStream(this.content), this.contentType);
        }
    }

    public static class FileInline extends AbstractEmailInline {
        private final File file;

        public FileInline(String contentId, String filename) {
            this(contentId, new File(filename));
        }

        public FileInline(String contentId, File file) {
            super(contentId);
            this.file = file;
        }

        public void attach(MimeMessageHelper mimeMessageHelper) throws MessagingException {
            mimeMessageHelper.addInline(this.contentId, this.file);
        }
    }
}