<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@page import="de.trispeedys.resourceplanning.interaction.HelperInteraction"%>
<%@page import="de.trispeedys.resourceplanning.configuration.AppConfiguration"%>
<head>
  <link rel="stylesheet" href="resources/css/main.css" type="text/css"/>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title><%out.println(AppConfiguration.getInstance().getText("helperCallback"));%></title>
</head>
<body>
	<%
      Long eventId = Long.parseLong(request.getParameter("eventId"));
	  Long helperId = Long.parseLong(request.getParameter("helperId"));
	  Long chosenPositionId = Long.parseLong(request.getParameter("chosenPosition"));	  
	  // render action result
	  out.println(HelperInteraction.processPositionRecoveryOnCancellation(eventId, helperId, chosenPositionId, null));
	%>
</body>
</html>