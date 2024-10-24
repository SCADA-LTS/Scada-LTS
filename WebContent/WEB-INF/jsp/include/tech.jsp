<%--
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
    along with this program.  If not, see http://www.gnu.org/licenses/.
--%><%@ page contentType="text/html;charset=UTF-8" language="java" %><%--
--%><%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %><%--
--%><%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %><%--
--%><fmt:setBundle basename="messages" /><%--
--%><fmt:setLocale scope='session' value="${sessionScope.get('org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE')}"/><%--
--%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%--
--%><%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %><%--
--%><%@ taglib prefix="mango" uri="/WEB-INF/tld/mango.tld" %><%--
--%><%@ taglib prefix="log" uri="http://jakarta.apache.org/taglibs/log-1.0" %><%--
--%><%@ taglib prefix="sst" uri="/WEB-INF/tld/escape.tld" %><%--
--%><%@ taglib prefix="tag" tagdir="/WEB-INF/tags" %><%--
--%><%@ taglib prefix="mobileTag" tagdir="/WEB-INF/tags/mobile" %>