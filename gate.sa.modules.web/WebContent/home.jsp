<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 5 Transitional//EN" "http://www.w3.org/TR/html5/loose.dtd">
<html>
<jsp:include page="header.jsp" />
<body>
<div id="container">
 <div id="content">
<center><h1><font face="helvetica" color="#F8F8F8">Welcome to </font><font face="helvetica" color="#F8F8F8">sa</font><font face="helvetica" color="#00CCFF">ga</font></h1><p>
<font face="helvetica" color="#F8F8F8">SAGA is a set of processing and linguistic resources, written in java, developed to run sentiment analysis over text using GATE plataform. Because of the nature of GATE, the text format should be plain or XML.</font>

<form method="GET" action='Controller' name="home">
  <p> 
  <input class="btn btn-success" type="submit" name="analyze" value="Start analyzing your text!" />&nbsp; 
  </p>
</form>
</center>
</div></div>
<div id="footer"><jsp:include page="footer.jsp" /></div>
</body>
</html>