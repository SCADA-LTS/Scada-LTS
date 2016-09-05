package org.scada_lts.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;
import org.scada_lts.cache.UnsilencedAlarmCache;
import org.scada_lts.dao.UnsilencedAlarmDAO;
import org.scada_lts.dao.model.UnsilencedAlarmLevelCache;
import org.scada_lts.quartz.UpdateUnsilencedAlarmLevel;
import org.scada_lts.web.ws.beans.ScadaPrincipal;
import org.scada_lts.web.ws.beans.UserAlarmLevel;
import org.scada_lts.web.ws.config.WebsocketApplicationListener;
import org.scada_lts.web.ws.config.WebsocketSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.maint.work.WorkItem;
import com.serotonin.mango.util.timeout.TimeoutClient;
import com.serotonin.timer.OneTimeTrigger;
import com.serotonin.timer.TimerTask;
import com.serotonin.timer.TimerTrigger;


@Service
public class UserHighestAlarmLevelService implements WebsocketSessionListener, UserHighestAlarmLevelListener {
    private static final Log LOG = LogFactory.getLog(PointHierarchyService.class);

    private static final String USER_ALARM_LEVEL_TOPIC_PREFIX = "/topic/alarmLevel/";
    
    private static UserHighestAlarmLevelService instance = null;
    
    // maps UserId to state for active users 
    private Map<Integer, UserAlarmLevel>  mapUserAlarmLevels = new ConcurrentHashMap<Integer, UserAlarmLevel>();
    private long lastScheduled = 0;
    private long lastExecuted  = 0;
    
    private SimpMessagingTemplate simpTemplate;
    private WebsocketApplicationListener  websocketApplicationListener;

    
    @Autowired
    public UserHighestAlarmLevelService(WebsocketApplicationListener websocketApplicationListener) {
    	this.websocketApplicationListener = websocketApplicationListener;
    	this.websocketApplicationListener.addListener(this);
    	instance = this;
    	// EventManager is created before all spring components (at servlet init time) so shall be ready here  
    	Common.ctx.getEventManager().addUserHighestAlarmLevelListener(this);
    }
    
    @PostConstruct 
    public void startup() {
    	LOG.debug("startup...");
    	// here load initial state into mapUserAlarmLevels if needed
    }
    
    @PreDestroy
    public void shutdown() {
    	LOG.debug("shutdown...");
    	Common.ctx.getEventManager().removeUserHighestAlarmLevelListener(this);
    }
    
    
    @Autowired
    public void setSimpTemplate(SimpMessagingTemplate template) {
        this.simpTemplate = template;
    }
    
	public static UserHighestAlarmLevelService getInstance() {
		LOG.trace("Get UserHighestAlarmLevelService instance ");
		return instance;
	}


    public void registerSession(ScadaPrincipal user, String sessionId) {
    	if( !mapUserAlarmLevels.containsKey(user.getId())) {
    		mapUserAlarmLevels.put(user.getId(), new UserAlarmLevel(user.getName()));
    		LOG.info("user registered: " + user.getNameId());
    		Common.ctx.getBackgroundProcessing().addWorkItem(new UserHighestAlarmLevelWorkItem(this));
    	}
    	mapUserAlarmLevels.get(user.getId()).addSession(sessionId);
    	LOG.debug(user.getNameId() + ": session registered: " + sessionId);
    }

    public void unregisterSession(ScadaPrincipal user, String sessionId) {
    	UserAlarmLevel ual = mapUserAlarmLevels.get(user.getId());
    	if( ual != null ) {
    		if( ual.removeSession(sessionId) == 0 ) {
    			mapUserAlarmLevels.remove(user.getId());
    			LOG.info("user removed: " + user.getNameId());
    		}
    	} else {
    		LOG.warn("trying to unregister unknown user: " + user.getNameId());
    	}
    }

    public void updateUserAlarmLevels(TreeMap<Integer, Integer> mapCurrentAlarmLevel) {
        //  go through active users  
    	LOG.trace("");
        for(Integer userId : mapUserAlarmLevels.keySet()) {
        	UserAlarmLevel userAlarmLevel = mapUserAlarmLevels.get(userId);
            Integer current = mapCurrentAlarmLevel.get(userId) == null? 0 : mapCurrentAlarmLevel.get(userId);
            Integer last = userAlarmLevel.getLevel();
            
            if( current != last ) {
                simpTemplate.convertAndSend("/topic/alarmLevel/" + userAlarmLevel.getUsername(), new AlarmLevelMessage(current));
                LOG.debug("sending ["+last+"->"+current+"] to user: " + userAlarmLevel.getUsername());
            }
            userAlarmLevel.setLevel(current);
        }
    }
    
    public void update(int userId) {
    	UserAlarmLevel userAlarmLevel = mapUserAlarmLevels.get(userId);
    	if( userAlarmLevel != null ) {
	    	Integer level = userAlarmLevel.getLevel();
	        simpTemplate.convertAndSend("/topic/alarmLevel/" + userAlarmLevel.getUsername(), new AlarmLevelMessage(level));
	        LOG.debug("updating ["+ level +"] to user: " + userAlarmLevel.getUsername());
    	}
    	else
    		LOG.debug("No userAlarmLevel found for userId: "+userId);
    }

	@Override
    public void onSubscribe(StompHeaderAccessor stomp, ScadaPrincipal user) {
    	String sessionId = stomp.getSessionId();
    	String destination = stomp.getDestination();
    	
    	if( destination.equals(USER_ALARM_LEVEL_TOPIC_PREFIX + user.getName()) ) {
    		registerSession(user, sessionId);
    	}
    }

	@Override
    public void onUnsubscribe(StompHeaderAccessor stomp, ScadaPrincipal user) {
    	String sessionId = stomp.getSessionId();
    	String destination = stomp.getDestination();

    	// destination will be null when called from onDisconnected event
    	if( destination == null || destination.equals(USER_ALARM_LEVEL_TOPIC_PREFIX + user.getName()) ) {
    		unregisterSession(user, sessionId);
    	}
    }

	@Override
	public void onConnected(StompHeaderAccessor stomp, ScadaPrincipal user) {
	}

	@Override
	public void onDisconnected(StompHeaderAccessor stomp, ScadaPrincipal user) {
		this.onUnsubscribe(stomp, user);
	}

	//////// UserHighestAlarmLevelListener methods
	@Override
	public void onAlarmTimestampChange(long alarmTimestamp) {
		// last scheduled workitem ...?
		// not more often then 1 sec?;
		if( lastScheduled + 1000  > alarmTimestamp ) { 
			// schedule for next second after done?
		}
		else
			;// execute immediately
			
			
	}

	@Override
	public void onEventRaise(int eventId, int userId, int alarmLevel) {
	}

	@Override
	public void onEventAck(int eventId, int userId) {
	}

	@Override
	public void onEventToggle(int eventId, int userId, boolean isSilenced) {
	}
}



class AlarmLevelMessage {
    private int alarmLevel;

    public AlarmLevelMessage(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public int getAlarmlevel() {
        return alarmLevel;
    }
}


class UserHighestAlarmLevelWorkItem extends UnsilencedAlarmDAO implements WorkItem {
	private static final Log LOG = LogFactory.getLog(UserHighestAlarmLevelWorkItem.class);
	private final UserHighestAlarmLevelService userHighestAlarmLevelService;
	
	UserHighestAlarmLevelWorkItem(UserHighestAlarmLevelService service) {
		userHighestAlarmLevelService = service;
	}
	
	@Override
	public void execute() {
		LOG.debug("UserHighestAlarmLevelWorkItem::execute");
		try {
			List<UnsilencedAlarmLevelCache> listUnsilencedAlarmLevel = getAll();
			TreeMap<Integer, Integer> mapUnsilencedAlarmLevel = getMapUnsilencedAlarmLevelForUser(listUnsilencedAlarmLevel);
			userHighestAlarmLevelService.updateUserAlarmLevels(mapUnsilencedAlarmLevel);
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	@Override
	public int getPriority() {
		return PRIORITY_MEDIUM;
	}
}


class UserHighestAlarmLevelTask extends TimerTask {
    private final UserHighestAlarmLevelWorkItem userHighestAlarmLevelWorkItem;
	
    public UserHighestAlarmLevelTask(long delay, UserHighestAlarmLevelWorkItem workitem) {
        this(new OneTimeTrigger(delay), workitem);
    }

    public UserHighestAlarmLevelTask(TimerTrigger trigger, UserHighestAlarmLevelWorkItem workitem) {
        super(trigger);
        this.userHighestAlarmLevelWorkItem = workitem;
        Common.timer.schedule(this);
    }

    @Override
    public void run(long runtime) {
    	userHighestAlarmLevelWorkItem.execute();
    }
}




