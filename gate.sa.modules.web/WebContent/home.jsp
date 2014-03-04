<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 5 Transitional//EN" "http://www.w3.org/TR/html5/loose.dtd">
<html>
<jsp:include page="header.jsp" />
<body>

<center><h1>Welcome to GATE-SA-modules-WebService</h1><p>
"GATE sentiment analysis modules" is a set of Java modules prepared to run sentiment analysis algorithms over text in plain/XML format.

<form method="GET" action='Controller' name="home">
  <p> 
  <input class="btn btn-success" type="submit" name="analyze" value="Start analyzing your text!" />&nbsp; 
  </p>
</form>
</center>
</body>
</html>