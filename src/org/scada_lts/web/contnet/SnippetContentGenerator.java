package org.scada_lts.web.contnet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class SnippetContentGenerator {
    private final HttpServletRequest request;
    private final String contentJsp;
    private final Map<String, Object> model;

    public SnippetContentGenerator(HttpServletRequest request, String contentJsp, Map<String, Object> model) {
        this.request = request;
        this.contentJsp = contentJsp;
        this.model = model;
    }

    public String generateContent() throws ServletException, IOException {
        return generateContent(this.request, this.contentJsp, this.model);
    }

    public static String generateContent(HttpServletRequest request, String contentJsp, Map<String, Object> model) throws ServletException, IOException {
        SnippetServletResponse response = new SnippetServletResponse();
        response.setCharacterEncoding(request.getCharacterEncoding());
        response.setLocale(request.getLocale());
        response.setContentType(request.getContentType());
        Map<String, Object> oldValues = new HashMap();
        Iterator i$;
        String key;
        if (model != null) {
            for(i$ = model.keySet().iterator(); i$.hasNext(); request.setAttribute(key, model.get(key))) {
                key = (String)i$.next();
                Object oldValue = request.getAttribute(key);
                if (oldValue != null) {
                    oldValues.put(key, oldValue);
                }
            }
        }

        boolean var14 = false;

        label199: {
            try {
                var14 = true;
                request.getRequestDispatcher(contentJsp).forward(request, response);
                var14 = false;
                break label199;
            } catch (MissingResourceException var15) {
                key = "Resource " + contentJsp + " not found";
                var14 = false;
            } finally {
                if (var14) {
                    if (model != null) {
                        Iterator i = model.keySet().iterator();

                        while(i.hasNext()) {
                            key = (String)i.next();
                            request.removeAttribute(key);
                        }

                        i = oldValues.keySet().iterator();

                        while(i.hasNext()) {
                            key = (String)i.next();
                            request.setAttribute(key, oldValues.get(key));
                        }
                    }

                }
            }

            if (model != null) {
                Iterator i = model.keySet().iterator();

                while(i.hasNext()) {
                    key = (String)i.next();
                    request.removeAttribute(key);
                }

                i = oldValues.keySet().iterator();

                while(i.hasNext()) {
                    key = (String)i.next();
                    request.setAttribute(key, oldValues.get(key));
                }
            }

            return key;
        }

        if (model != null) {
            Iterator i = model.keySet().iterator();

            while(i.hasNext()) {
                key = (String)i.next();
                request.removeAttribute(key);
            }

            i = oldValues.keySet().iterator();

            while(i.hasNext()) {
                key = (String)i.next();
                request.setAttribute(key, oldValues.get(key));
            }
        }

        return response.getContent();
    }
}
