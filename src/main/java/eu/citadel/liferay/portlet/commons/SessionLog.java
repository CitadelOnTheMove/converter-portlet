package eu.citadel.liferay.portlet.commons;
import javax.portlet.PortletSession;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class SessionLog implements Log {
	private String sessionId;
	private long userId;
	private Log liferayLog;
	
	public SessionLog(PortletSession session, String logName) {
		this.sessionId = session.getId();
		this.liferayLog = LogFactoryUtil.getLog(logName);
		this.userId = (long) session.getAttribute(ConverterConstants.SESSION_ATTR_USER_ID);
	}
	
	public SessionLog(String sessionId, String logName, long userId) {
		this.sessionId = sessionId;
		this.liferayLog = LogFactoryUtil.getLog(logName);
		this.userId = userId;
	}

	@Override
	public void debug(Object msg) {
		liferayLog.debug("UserID:" + userId + "session: " + sessionId + ": " + msg.toString());
	}

	@Override
	public void debug(Object msg, Throwable t) {
		liferayLog.debug("UserID:" + userId + "session: " + sessionId + ": " + msg.toString(), t);
	}

	@Override
	public void debug(Throwable t) {
		liferayLog.debug(sessionId + ": error", t);
	}
	
	@Override
	public void error(Object msg) {
		liferayLog.error("UserID:" + userId + "session: " + sessionId + ": " + msg.toString());
	}

	@Override
	public void error(Object msg, Throwable t) {
		liferayLog.error("UserID:" + userId + "session: " + sessionId + ": " + msg.toString(), t);
	}

	@Override
	public void error(Throwable t) {
		liferayLog.error(sessionId + ": error", t);
	}
	
	@Override
	public void fatal(Object msg) {
		liferayLog.fatal("UserID:" + userId + "session: " + sessionId + ": " + msg.toString());
	}

	@Override
	public void fatal(Object msg, Throwable t) {
		liferayLog.fatal("UserID:" + userId + "session: " + sessionId + ": " + msg.toString(), t);
	}

	@Override
	public void fatal(Throwable t) {
		liferayLog.fatal(sessionId + ": fatal", t);
	}
	
	@Override
	public void info(Object msg) {
		liferayLog.info("UserID:" + userId + "session: " + sessionId + ": " + msg.toString());
	}

	@Override
	public void info(Object msg, Throwable t) {
		liferayLog.info("UserID:" + userId + "session: " + sessionId + ": " + msg.toString(), t);
	}

	@Override
	public void info(Throwable t) {
		liferayLog.info(sessionId + ": info", t);
	}

	@Override
	public boolean isDebugEnabled() {
		return liferayLog.isDebugEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return liferayLog.isErrorEnabled();
	}

	@Override
	public boolean isFatalEnabled() {
		return liferayLog.isFatalEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return liferayLog.isInfoEnabled();
	}

	@Override
	public boolean isTraceEnabled() {
		return liferayLog.isTraceEnabled();
	}

	@Override
	public boolean isWarnEnabled() {
		return liferayLog.isWarnEnabled();
	}

	@Override
	public void setLogWrapperClassName(String className) {
		liferayLog.setLogWrapperClassName(className);
	}
	
	@Override
	public void trace(Object msg) {
		liferayLog.trace("UserID:" + userId + "session: " + sessionId + ": " + msg.toString());
	}

	@Override
	public void trace(Object msg, Throwable t) {
		liferayLog.trace("UserID:" + userId + "session: " + sessionId + ": " + msg.toString(), t);
	}

	@Override
	public void trace(Throwable t) {
		liferayLog.trace(sessionId + ": trace", t);
	}
	
	@Override
	public void warn(Object msg) {
		liferayLog.warn("UserID:" + userId + "session: " + sessionId + ": " + msg.toString());
	}

	@Override
	public void warn(Object msg, Throwable t) {
		liferayLog.warn("UserID:" + userId + "session: " + sessionId + ": " + msg.toString(), t);
	}

	@Override
	public void warn(Throwable t) {
		liferayLog.warn(sessionId + ": warn", t);
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}
