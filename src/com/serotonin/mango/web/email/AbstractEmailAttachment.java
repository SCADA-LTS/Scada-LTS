package com.serotonin.mango.web.email;

import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.io.File;

public abstract class AbstractEmailAttachment {
    protected String filename;

    public AbstractEmailAttachment(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return this.filename;
    }

    public abstract void attach(MimeMessageHelper var1) throws MessagingException;

    public static class ByteArrayAttachment extends AbstractEmailAttachment {
        final byte[] data;
        private final String contentType;

        public ByteArrayAttachment(String filename, byte[] data) {
            this(filename, data, (String)null);
        }

        public ByteArrayAttachment(String filename, byte[] data, String contentType) {
            super(filename);
            this.data = data;
            this.contentType = contentType;
        }

        public void attach(MimeMessageHelper mimeMessageHelper) throws MessagingException {
            InputStreamSource source = () -> new ByteArrayInputStream(this.data);
            if (this.contentType == null) {
                mimeMessageHelper.addAttachment(this.filename, source);
            } else {
                mimeMessageHelper.addAttachment(this.filename, source, this.contentType);
            }

        }
    }

    public static class DataSourceAttachment extends AbstractEmailAttachment {
        private final DataSource dataSource;

        public DataSourceAttachment(String filename, DataSource dataSource) {
            super(filename);
            this.dataSource = dataSource;
        }

        public void attach(MimeMessageHelper mimeMessageHelper) throws MessagingException {
            mimeMessageHelper.addAttachment(this.filename, this.dataSource);
        }
    }

    public static class FileAttachment extends AbstractEmailAttachment {
        private final File file;

        public FileAttachment(String filename, String systemFilename) {
            this(filename, new File(systemFilename));
        }

        public FileAttachment(File file) {
            super(file.getName());
            this.file = file;
        }

        public FileAttachment(String filename, File file) {
            super(filename);
            this.file = file;
        }

        public void attach(MimeMessageHelper mimeMessageHelper) throws MessagingException {
            mimeMessageHelper.addAttachment(this.filename, this.file);
        }
    }
}
