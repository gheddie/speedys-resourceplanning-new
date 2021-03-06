<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@page import="de.trispeedys.resourceplanning.interaction.HelperInteraction"%>
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
	  // render action result
	  out.println(HelperInteraction.processDeactivationRecovery(eventId, helperId, null));
	%>
</body>
</html>