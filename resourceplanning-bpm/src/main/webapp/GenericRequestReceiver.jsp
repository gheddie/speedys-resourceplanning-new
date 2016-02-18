<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ page import="de.trispeedys.resourceplanning.entity.misc.HelperCallback"%>
<%@ page import="de.trispeedys.resourceplanning.interaction.GenericHelperInteraction"%>
<%@ page import="de.trispeedys.resourceplanning.interaction.RequestType"%>
<%@page import="de.trispeedys.resourceplanning.configuration.AppConfiguration"%>
<%@page import="de.trispeedys.resourceplanning.util.parser.ParserUtil"%>
<head>
  <link rel="stylesheet" href="resources/css/main.css" type="text/css"/>
  <meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
  <title><%out.println(AppConfiguration.getInstance().getText("helperCallback"));%></title>
</head>
<body>
	<%
	  // render action result
      out.println(GenericHelperInteraction.processRequest(request, null));
	%>
</body>
</html>