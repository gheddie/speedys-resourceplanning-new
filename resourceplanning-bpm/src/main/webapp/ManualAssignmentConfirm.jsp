<%@page import="de.trispeedys.resourceplanning.interaction.HelperConfirmation"%>
<%@page import="de.trispeedys.resourceplanning.exception.ResourcePlanningException"%>
<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ page import="de.trispeedys.resourceplanning.entity.misc.HelperCallback"%>
<%@ page import="de.trispeedys.resourceplanning.interaction.HelperConfirmation"%>
<%@page import="de.trispeedys.resourceplanning.configuration.AppConfiguration"%>
<%@page import="de.trispeedys.resourceplanning.util.parser.ParserUtil"%>	
<%@page import="de.trispeedys.resourceplanning.interaction.JspRenderer"%>	
<head>
<link rel="stylesheet" href="resources/css/main.css" type="text/css" />
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>
	<%
	    out.println(AppConfiguration.getInstance().getText("helperCallback"));
	%>
</title>
</head>
<body>
	<%
	    Long eventId = ParserUtil.parseLong(request.getParameter("eventId"));
	    Long helperId = ParserUtil.parseLong(request.getParameter("helperId"));
	    // render action result
	    try
	    {
		    out.println(HelperConfirmation.processManualAssignmentConfirmation(eventId, helperId,
		            request.getParameter("helperMessage"), null));	        
	    }
		catch (ResourcePlanningException e)
		{
		    out.println(JspRenderer.renderPlanningException(helperId, e.getMessage()));
		}
	%>
</body>
</html>