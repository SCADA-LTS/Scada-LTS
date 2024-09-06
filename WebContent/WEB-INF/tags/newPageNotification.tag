<%@include file="/WEB-INF/tags/decl.tagf"%>
<%@tag body-content="empty"%>
<%@attribute name="href" rtexprvalue="true"%>
<%@attribute name="ref"%>

<div class="notification-alert" id="newPageNotification">
    <div class="image">
    <img src="images/warn.png"/>

    </div>
    <div class="content">
        <div class="message">
            <spring:message code="notification.newui.title"/>
        </div>
        <div class="actions">
            <a href="${href}"><spring:message code="notification.newui.move"/></a>
            <a onclick="dontShowAgain()"><spring:message code="notification.newui.dontshow"/></a>
        </div>
    </div>

    <div class="close">
        <img src="images/cross.png" onclick="closeNotification()"/>
    </div>

    <script>
        function closeNotification() {
            document.getElementById("newPageNotification").style.display = "none";
        }
        function dontShowAgain() {
            localStorage.setItem("${ref}", "true");
            closeNotification();
        }

        var showNotification = localStorage.getItem("${ref}");
        if(!!showNotification && showNotification === "true") {
            closeNotification();
        }
    </script>

    <style>
    .notification-alert {
	display: flex;
	position: fixed;
	background: #d6d5d5;
	bottom: 3vh;
	right: 15px;
	min-width: 250px;
	min-height: 40px;
	border: 1px solid #00000040;
	border-radius: 10px 10px 0 10px;
	padding: 15px;
	box-shadow: #0000005e 1px 2px 3px 0px;
    animation: slideIn 0.4s;
    animation-timing-function: cubic-bezier(0, 0, 0.36, 1.38);
    animation-delay: 1s;
    animation-fill-mode: both;
    z-index:99;
}
.notification-alert .image {
	display: flex;
	align-content: center;
	align-items: center;
}
.notification-alert > .image > img {
	height: 32px !important;
	width: 32px !important;
}
.notification-alert > .content {
    width: 100%;
    font-size: 1.3em;
    padding: 0px 5px;
}
.notification-alert > .content > .actions  {
    font-size: 0.8em;
    padding-top: 4px;
}
.notification-alert > .content > .actions > a:first-of-type{
    font-weight: bold;
    font-size: 1.1em;
}
@keyframes slideIn {
    0% {
        transform: translateX(200%);
    }
    100% {
        transform: translateX(0%);
    }
}
    </style>
</div>