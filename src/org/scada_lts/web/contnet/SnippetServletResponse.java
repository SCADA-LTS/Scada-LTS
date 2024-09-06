package org.scada_lts.web.contnet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class SnippetServletResponse implements HttpServletResponse {
    private final StringWriter writer = new StringWriter();
    private String characterEncoding;
    private Locale locale;
    private String contentType;

    public SnippetServletResponse() {}

    public PrintWriter getWriter() {
        return new PrintWriter(this.writer);
    }

    public String getContent() {
        return this.writer.toString();
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    @Override
    public void setCharacterEncoding(String charEncoding) {
        this.characterEncoding = charEncoding;
    }

    @Override
    public int getBufferSize() {
        return -1;
    }

    @Override
    public void setBufferSize(int size) {
    }

    public ServletOutputStream getOutputStream() {
        return new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {

            }

            @Override
            public void write(int b) throws IOException {

            }
        };
    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void resetBuffer() {
    }

    @Override
    public void reset() {
    }

    @Override
    public void setContentLength(int length) {
    }

    @Override
    public void flushBuffer() {
    }

    @Override
    public void sendError(int status) {
    }

    @Override
    public String encodeURL(String url) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return null;
    }

    @Override
    public void addCookie(Cookie cookie) {
    }

    @Override
    public String encodeUrl(String url) {
        return null;
    }

    @Override
    public void sendRedirect(String location) {
    }

    @Override
    public void addDateHeader(String name, long value) {
    }

    @Override
    public void setIntHeader(String name, int value) {
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return null;
    }

    @Override
    public void setStatus(int status) {
    }

    @Override
    public void addHeader(String name, String value) {
    }

    @Override
    public boolean containsHeader(String name) {
        return false;
    }

    @Override
    public void sendError(int status, String message) {
    }

    @Override
    public void addIntHeader(String name, int value) {
    }

    @Override
    public void setDateHeader(String name, long value) {
    }

    @Override
    public void setHeader(String name, String value) {
    }

    @Override
    public void setStatus(int status, String message) {
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public String getHeader(String s) {
        return null;
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

    @Override
    public void setContentLengthLong(long l) {

    }
}