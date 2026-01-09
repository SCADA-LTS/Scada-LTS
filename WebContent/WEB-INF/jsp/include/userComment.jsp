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

  function ensureCommentDialog() {
    var dlg = dojo.widget.byId && dojo.widget.byId("CommentDialog");
    if (!dlg) {
      try { dojo.widget.parse(); dlg = dojo.widget.byId && dojo.widget.byId("CommentDialog"); } catch(e) {}
    }
    return dlg;
  }

  function unhideCommentSource() {
    var n = document.getElementById("CommentDialog");
    if (!n) return n;
    n.removeAttribute("hidden");
    n.setAttribute("aria-hidden", "false");
    n.style.visibility = "visible";
    n.style.display = "";
    n.style.float = "none";
    return n;
  }


  function normalizeWrapper(dlg, srcNode) {
    var w = (dlg && dlg.domNode) ? dlg.domNode : srcNode;
    if (!w) return;

    w.style.display = "block";
    w.style.visibility = "visible";
    w.style.opacity = "1";
    w.style.position = "fixed";
    w.style.top = "50%";
    w.style.left = "50%";
    w.style.transform = "translate(-50%, -50%)";
    w.style.margin = "0";
    w.style.zIndex = "20010";
    w.style.overflow = "visible";
  }

  function normalizeUnderlay() {
    var u = document.getElementById("dialogUnderlay");
    if (!u) return;
    u.style.position = "fixed";
    u.style.top = "0"; u.style.left = "0"; u.style.right = "0"; u.style.bottom = "0";
    u.style.width = "100%";
    u.style.height = "100%";
    u.style.zIndex = "20000";
    u.style.display = "block";
  }

  function resetUnderlay() {
    var u = document.getElementById("dialogUnderlay");
    if (!u) return;
    u.style.display = "none";
    u.style.zIndex = "";
    u.style.position = "";
    u.style.top = "";
    u.style.left = "";
    u.style.right = "";
    u.style.bottom = "";
    u.style.width = "";
    u.style.height = "";
  }

  function openCommentDialog(typeId, referenceId) {
    commentTypeId = typeId;
    commentReferenceId = referenceId;

    var ta = document.getElementById("commentText");
    if (ta) ta.value = "";

    var src = unhideCommentSource();
    var dlg = ensureCommentDialog();
    if (!dlg) return;

    normalizeWrapper(dlg, src);
    normalizeUnderlay();
    if (dlg.show) dlg.show();
    setTimeout(function(){
      normalizeWrapper(dlg, src);
      var ct = document.getElementById("commentText");
      if (ct && ct.focus) try { ct.focus(); } catch(e){}
    }, 0);
  }
  
  function saveComment() {
      var comment = (document.getElementById("commentText") || {}).value || "";
      MiscDwr.addUserComment(commentTypeId, commentReferenceId, comment, saveCommentCB);
  }

  function saveCommentCB(comment) {
      if (!comment)
          alert("<spring:message code="notes.enterComment"/>");
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
          $("comment"+ comment.ts +"UserTime").textContent = comment.prettyTime + ' <spring:message code="notes.by"/> ' + comment.username;
          $("comment"+ comment.ts +"Text").textContent = comment.comment;
      }
  }
  
  function closeCommentDialog() {
    var dlg = dojo.widget.byId && dojo.widget.byId("CommentDialog");
    if (dlg && dlg.hide) dlg.hide();
    resetUnderlay();
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

<div dojoType="dialog" id="CommentDialog" bgColor="white" bgOpacity="0.5" toggle="fade" toggleDuration="250"
     style="display:none; visibility:hidden;"
     aria-hidden="true"
     hidden>
  <span class="smallTitle"><spring:message code="notes.addNote"/></span>
  <table>
    <tr>
      <td><textarea rows="8" cols="50" id="commentText"></textarea></td>
    </tr>
    <tr>
      <td align="center">
        <input type="button" value="<spring:message code="notes.save"/>" onclick="saveComment();"/>
        <input type="button" value="<spring:message code="notes.cancel"/>" onclick="closeCommentDialog();"/>
      </td>
    </tr>
  </table>
</div>

<table style="display:none;">
  <tr id="comment_TEMPLATE_">
    <td valign="top" width="16"><tag:img png="comment" title="notes.note"/></td>
    <td valign="top">
      <span id="comment_TEMPLATE_UserTime" class="copyTitle"><spring:message code="notes.timeByUsername"/></span><br/>
      <span id="comment_TEMPLATE_Text"></span>
    </td>
  </tr>
</table>