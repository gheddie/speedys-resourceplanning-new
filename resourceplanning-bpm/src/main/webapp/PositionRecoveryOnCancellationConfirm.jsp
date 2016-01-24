<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@page import="de.trispeedys.resourceplanning.interaction.HelperConfirmation"%>
<%@page import="de.trispeedys.resourceplanning.configuration.AppConfiguration"%>
<%@page import="de.trispeedys.resourceplanning.util.parser.ParserUtil"%>
<head>
  <link rel="stylesheet" href="resources/css/main.css" type="text/css"/>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title><%out.println(AppConfiguration.getInstance().getText("helperCallback"));%></title>
</head>
<body>
	<%
      Long eventId = ParserUtil.parseLong(request.getParameter("eventId"));
	  Long helperId = ParserUtil.parseLong(request.getParameter("helperId"));
	  Long positionId = ParserUtil.parseLong(request.getParameter("positionId"));
	  // render action result
	  out.println(HelperConfirmation.processPositionRecoveryConfirmation(eventId, helperId, positionId, null));
	%>
</body>
</html>