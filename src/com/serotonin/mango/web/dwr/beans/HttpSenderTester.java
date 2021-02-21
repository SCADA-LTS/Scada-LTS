/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.web.dwr.beans;

import java.util.Base64;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.serotonin.db.KeyValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.publish.httpSender.HttpSenderRT;
import com.serotonin.util.StringUtils;
import com.serotonin.web.http.HttpUtils;

/**
 * @author Matthew Lohbihler
 */
public class HttpSenderTester extends Thread implements TestingUtility {
    
    private final String url;
    private final String username;
    private final String password;
    private final boolean useJSON;
    private final boolean usePost;
    private final List<KeyValuePair> staticHeaders;
    private final List<KeyValuePair> staticParameters;

    private String result;

    public HttpSenderTester(String url, String username, String password, boolean useJSON, boolean usePost, List<KeyValuePair> staticHeaders,
            List<KeyValuePair> staticParameters) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.useJSON = useJSON;
        this.usePost = usePost;
        this.staticHeaders = staticHeaders;
        this.staticParameters = staticParameters;
        start();
    }

    @Override
    public void run() {
        HttpMethodBase method;
        if (usePost) {
            PostMethod post = new PostMethod(url);
            post.addParameters(convertToNVPs(staticParameters));
            if (useJSON) {
        	try {
        	    post.setRequestEntity(new StringRequestEntity("{}", "application/json", "utf-8"));
        	} catch (Exception e) {
        	    result = "ERROR: " + e.getMessage();
        	    return;
        	}
            }
            method = post;
        }
        else {
            GetMethod get = new GetMethod(url);
            get.setQueryString(convertToNVPs(staticParameters));
            method = get;
        }
        
        // Add authentication
        if (!(StringUtils.isEmpty(username) || StringUtils.isEmpty(password))) {
        	byte[] authBytes = (username + ':' + password).getBytes();
        	String authHeaderValue = "Basic " + Base64.getEncoder().encodeToString(authBytes);
        	
        	method.setDoAuthentication(true);
        	method.addRequestHeader("Authorization", authHeaderValue);
        }

        // Add a recognizable header
        method.addRequestHeader("User-Agent", HttpSenderRT.USER_AGENT);

        // Add the user-defined headers.
        for (KeyValuePair kvp : staticHeaders)
            method.addRequestHeader(kvp.getKey(), kvp.getValue());

        try {
            int code = Common.getHttpClient().executeMethod(method);
            if (code != HttpStatus.SC_OK)
                result = "ERROR: Invalid response code: " + code;
            else
                result = HttpUtils.readResponseBody(method, 1024);
        }
        catch (Exception e) {
            result = "ERROR: " + e.getMessage();
        }
        finally {
            method.releaseConnection();
        }
    }

    public String getResult() {
        return result;
    }

    private NameValuePair[] convertToNVPs(List<KeyValuePair> staticParameters) {
        NameValuePair[] nvps = new NameValuePair[staticParameters.size()];
        for (int i = 0; i < nvps.length; i++) {
            KeyValuePair kvp = staticParameters.get(i);
            nvps[i] = new NameValuePair(kvp.getKey(), kvp.getValue());
        }
        return nvps;
    }

    public void cancel() {
        // no op
    }
}
