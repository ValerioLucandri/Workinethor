<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>  
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>

<meta charset="ISO-8859-1">
<style type="text/css">
    <%@include file="../bootstrap/css/bootstrap.css" %>
    <%@include file="../bootstrap/css/bootstrap-theme.css" %>
</style>
<script src="bootstrap/js/jquery.js"></script>
<script src="bootstrap/js/bootstrap.js"></script>
<title>
HomePage
</title>

</head>
<body background="images/background.png">

	<!-- navigation -->
	<nav class="navbar navbar-default">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand color-me-black" href="">WorkineThor</a>
			</div>
			<ul class="nav navbar-nav">
				<li><a href="#home" class="color-me-black">Home</a>
				<li><a href="jsp/create-project.jsp" class="color-me-black">Create Project</a>
				<li><a href="#browse" class="color-me-black">Browse Projects</a>
			</ul>
			<ul class="nav navbar-nav navbar-right">
				<li><a href="#" class="color-me-black"><img src="images/signup.png" class="media-object" style="width:20px">Sign Up</a>
			</ul>
		</div>
	</nav>
	
	<br>
	<br>
	<br>
	
	<!-- users data (projects) by clicking on a project you ask the ProjectServlet to display project.jsp-->
	<div class="tumbotron text-center">
		<h2 style="color:white">User: ${user}</h2>
		<ul class="list-group justify-content-center" >
   		<c:forEach items="${project_list}" varStatus="i">
   			<li class="list-group-item" style="width:170px">
   				<a href="project?id=${project_list[i.index]}">
   					<img src="images/project-icon.png" class="media-object" style="width:20px">    				
 					${project_list[i.index]}
   				</a>
   				<br>
    		</li>
    	</c:forEach>
		</ul>
	</div>	

</body>
</html>