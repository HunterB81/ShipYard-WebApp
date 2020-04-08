<!DOCTYPE html>
<%-- 

/* Name: Hunter Busa

Project Four Assignment title: A Three-Tier Distributed Web-Based Application
Date: December 1, 2019
*/

--%>

<html lang="en">
<head>
	<meta charset="utf-8" />

	<style type="text/css">
		<!--
			body{background-color: blue;}
			-->
		</style>
	</head>
<body>
	<h1 style="text-align: center; color: white;">
		Welcome to the ShipYard Web Application <br>
		A Remote Database Management System
	</h1>
	<hr style="color: white;">

	<div id="center" style="text-align: center; color: white;">

		<h2 style="text-align: center; color: white;">
			You are connected to the ShipYard Enterprise System database. <br>
			Please enter any valid SQL query or updata statement. <br>
			If no query/update command is initially provided the Execute button will display all supplier information in the database. <br>
			All execution results will appear below. <br>
		</h2>

		<form method="post" action='/Project4/logic' name='index'>
			<textarea id="commandBox" name="commandBox" rows="15" cols="100" style="color: green; background-color: black;">
			</textarea><br>
			<input type="submit" value="Execute Command" name="execute" style="color: yellow; background-color: black;">
			<input type="reset" value="Reset Form" name="reset" style="color: yellow; background-color: black;">
		</form>

	</div>

	<hr style="color: white;">

	<div id="bottom" style="text-align: center; color: white;">
		<h2 style="text-align: center; color: white;">
			Database Results: 
		</h2>

		<%-- start scriplet --%>
		<%
			String sqlStatement = (String)session.getAttribute("commandBox");
			if (sqlStatement == null) sqlStatement = "select * from suppliers";
			String output = (String) session.getAttribute("output");
			if (output == null) output = " ";
		%>
		
		<table>
			<%-- JSP expression to access servlet variable: message --%>
			<%=output%>
		</table>
		
	</div>
</body>
</html>
