<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <link rel="stylesheet" href="resources/css/main.css" type="text/css"/>
  <meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
  <title>R&uumlckmeldung erhalten</title>
</head>
<%@page import="de.trispeedys.resourceplanning.interaction.HelperInteraction"%>
<body>
	<%
      Long eventId = Long.parseLong(request.getParameter("eventId"));
	  Long helperId = Long.parseLong(request.getParameter("helperId"));
	  Long chosenPositionId = Long.parseLong(request.getParameter("chosenPosition"));        	        
	  // render action result
	  out.println(HelperInteraction.processPositionChosenCallback(eventId, helperId, chosenPositionId));
	%>
</body>
</html>