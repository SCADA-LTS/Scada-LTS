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
--%><%@include file="/WEB-INF/tags/decl.tagf"%><%--
--%><%@tag body-content="empty"%><%--
--%><%@attribute name="username" required="true"%>
<script type="text/javascript">var djConfig = { isDebug: true };</script>
<!-- script type="text/javascript" src="http://o.aolcdn.com/dojo/0.4.2/dojo.js"></script -->
<script type="text/javascript" src="resources/dojo/dojo.js"></script>
<script type="text/javascript" src="dwr/engine.js"></script>
<script type="text/javascript" src="dwr/util.js"></script>
<script type="text/javascript" src="resources/common.js"></script>
<script type="text/javascript" src="dwr/interface/MiscDwr.js"></script>
<script type="text/javascript" src="dwr/interface/CustomViewDwr.js"></script>
<script type="text/javascript" src="resources/view.js"></script>
<mango:viewInit username="${username}"/>
<script type="text/javascript">
  dwr.util.setEscapeHtml(false);
  mango.view.initCustomView();
  dojo.addOnLoad(mango.longPoll.start);
  
  function setPoint(xid, value, callback) {
      mango.view.setPoint(xid, value, callback);
  }
</script>
