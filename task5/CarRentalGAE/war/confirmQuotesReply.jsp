<%@page import="ds.gae.view.JSPSite"%>
<%@page import="ds.gae.CarRentalModel"%>
<%
boolean success = false;
boolean proc = false;
boolean valid = true;
try{
	String batchId = request.getParameter("batchId");
	if(!CarRentalModel.get().batchProcessed(batchId)){
		//nothing
	}else{
		proc = true;
		if(CarRentalModel.get().batchSuccessful(batchId)){
			success = true;
		}else{
			
		}	
	}
}catch(Exception e){
	valid = false;
}
%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
	String renter = (String)session.getAttribute("renter");
	JSPSite currentSite = JSPSite.CONFIRM_QUOTES_RESPONSE;

%>   
 
<%@include file="_header.jsp" %>

<% 
if (currentSite != JSPSite.LOGIN && currentSite != JSPSite.PERSIST_TEST && renter == null) {
 %>
	<meta http-equiv="refresh" content="0;URL='/login.jsp'">
<% 
  request.getSession().setAttribute("lastSiteCall", currentSite);
} 
 %>
 <%if(!proc && valid){%>
 	<meta http-equiv="refresh" content="2">
 <%}%>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="style.css" />
	<title>Car Rental Application</title>
</head>
<body>
	<div id="mainWrapper">
		<div id="headerWrapper">
			<h1>Car Rental Application</h1>
		</div>
		<div id="navigationWrapper">
			<ul>
<% 
for (JSPSite site : JSPSite.publiclyLinkedValues()) {
	if (site == currentSite) {
 %> 
				<li><a class="selected" href="<%=site.url()%>"><%=site.label()%></a></li>
<% } else {
 %> 
				<li><a href="<%=site.url()%>"><%=site.label()%></a></li>
<% }}
 %> 

				</ul>
		</div>
		<div id="contentWrapper">
<% if (currentSite != JSPSite.LOGIN) { %>
			<div id="userProfile">
				<span>Logged-in as <%= renter %> (<a href="/login.jsp">change</a>)</span>
			</div>
<%
   }
 %>
			<div class="frameDiv" style="margin: 150px 150px;">
				<H2>Reply</H2>
				<div class="group">
					<p>
					<%
					if(!valid){
						%>Token not valid!<%
					}else{
						if(!proc){
								%>Your quotes have not yet been confirmed.<%
						}else{
							if(success){
								%>Your quotes have been confirmed!<%
							}else{
								%>Your quotes could not be confirmed.<%
							}	
						}
					}
					%>
					</p>
				</div>
			</div>



<%@include file="_footer.jsp" %>



