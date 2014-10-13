<%@page import="eu.citadel.liferay.portlet.commons.ConverterConstants"%>
<%@page import="javax.portlet.*"%>
<%@page import="com.liferay.portlet.*"%>
<%@page import="com.liferay.portal.kernel.util.*"%>
<%@page import="com.liferay.portal.kernel.language.*"%>
<%@page import="com.liferay.portal.model.*"%>
<%@page import="com.liferay.portal.service.*"%>
<%@page import="java.util.*"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0"	prefix="portlet"%>
<%@ taglib uri="http://alloy.liferay.com/tld/aui"	prefix="aui"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"	prefix="c"%>
<%@ taglib uri="http://liferay.com/tld/theme"		prefix="liferay-theme"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"	prefix="fmt"%>
<%@ taglib uri="http://liferay.com/tld/portlet"		prefix="liferay-portlet"%>
<%@ taglib uri="http://liferay.com/tld/ui"			prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/util"		prefix="liferay-util"%>

<%@ taglib uri="http://www.citadel.eu/taglib"		prefix="citadel"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<%
	String message = (String) request.getAttribute(ConverterConstants.PAGE_ATTR_ERROR_MESSAGE);
	if(Validator.isNotNull(message)) {
		pageContext.getOut().write("<div class=\"alert alert-error\"> " + LanguageUtil.get(pageContext, message) + " </div>");
	}
%>