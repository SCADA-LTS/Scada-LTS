package org.scada_lts.web.ws.config;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.ws.beans.ScadaPrincipal;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;

public class ScadaHandshakeHandler   extends DefaultHandshakeHandler {
    private final Log LOG = LogFactory.getLog(ScadaHandshakeHandler.class);
    
    
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Principal principal = request.getPrincipal();

        if(principal==null)
        {
            ServletServerHttpRequest req = (ServletServerHttpRequest)request;
            HttpSession session = req.getServletRequest().getSession();
            final User user = (User) session.getAttribute("sessionUser");
            //final User user = Common.getUser();
            
            if( user != null ) {
                LOG.debug("user.username: " + user.getUsername());
                LOG.debug("user.id: " + user.getId());
            }
            
//            principal=new Principal() {
//                @Override
//                public String getName() {
//                    return user!=null?user.getUsername():"";
//                }
//            };
            principal = new ScadaPrincipal(user); 
        }
        LOG.debug("principal: " +principal.getName());
        return principal;
    }
}


