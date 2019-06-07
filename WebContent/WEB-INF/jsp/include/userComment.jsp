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
--%>
<%--
  Usage:
    For event comments, your table of events should be id'd "eventsTable". Each events table row should include a
    table with a tbody id'd eventComments<eventId>.
    
    For data point comments, ...
--%>
<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<%@page import="com.serotonin.mango.vo.UserComment"%>
<script type="text/javascript">
  dojo.require("dojo.widget.Dialog");
  
  var commentTypeId;
  var commentReferenceId;
  function openCommentDialog(typeId, referenceId) {
      commentTypeId = typeId;
      commentReferenceId = referenceId;
      $set("commentText", "");
      dojo.widget.byId("CommentDialog").show();
      $("commentText").focus();
  }
  
  function saveComment() {
      var comment = $get("commentText");
      MiscDwr.addUserComment(commentTypeId, commentReferenceId, comment, saveCommentCB);
  }
  
  function saveCommentCB(comment) {
      if (!comment)
          alert("<fmt:message key="notes.enterComment"/>");
      else {
          closeCommentDialog();
          
          // Add a row for the comment by cloning the template.
          var content = $("comment_TEMPLATE_").cloneNode(true);
          updateTemplateNode(content, comment.ts);
          var commentsNode;
          if (commentTypeId == <%= UserComment.TYPE_EVENT %>)
              commentsNode = $("eventComments"+ commentReferenceId);
          else if (commentTypeId == <%= UserComment.TYPE_POINT %>)
              commentsNode = $("pointComments"+ commentReferenceId);
          commentsNode.appendChild(content);
          $("comment"+ comment.ts +"UserTime").innerHTML = comment.prettyTime +" <fmt:message key="notes.by"/> "+ comment.username;
          $("comment"+ comment.ts +"Text").innerHTML = comment.comment;
      }
  }
  
  function closeCommentDialog() {
      dojo.widget.byId("CommentDialog").hide();
  }
</script>
<style type="text/css">
  .dojoDialog {
      background : #eee;
      border : 1px solid #999;
      -moz-border-radius : 5px;
      padding : 4px;
  }
  #eventsTable .row td {
      vertical-align: top;
  }
  #eventsTable .rowAlt td {
      vertical-align: top;
  }
</style>

<div dojoType="dialog" id="CommentDialog" bgColor="white" bgOpacity="0.5" toggle="fade" toggleDuration="250">
  <span class="smallTitle"><fmt:message key="notes.addNote"/></span>
  <table>
    <tr>
      <td><textarea rows="8" cols="50" id="commentText"></textarea></td>
    </tr>
    <tr>
      <td align="center">
        <input type="button" value="<fmt:message key="notes.save"/>" onclick="saveComment();"/>
        <input type="button" value="<fmt:message key="notes.cancel"/>" onclick="closeCommentDialog();"/>
      </td>
    </tr>
  </table>
</div>

<table style="display:none;">
  <tr id="comment_TEMPLATE_">
    <td valign="top" width="16"><tag:img png="comment" title="notes.note"/></td>
    <td valign="top">
      <span id="comment_TEMPLATE_UserTime" class="copyTitle"><fmt:message key="notes.timeByUsername"/></span><br/>
      <span id="comment_TEMPLATE_Text"></span>
    </td>
  </tr>
</table>