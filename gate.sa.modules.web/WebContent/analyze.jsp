<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 5 Transitional//EN" "http://www.w3.org/TR/html5/loose.dtd">
<html>
<jsp:include page="header.jsp" />
<body>
<div class="row">
  <div class="col-md-1"></div>
  <div class="col-md-3">
  	<form method="GET" action='Controller' name="analyze">
    	<input type="text" class="form-control" name="input" value="Insert your spanish financial and emoticon text here." style="height: 122px; width: 246px">
    	<input type="hidden" name="informat" value="text">
    	<input type="hidden" name="intype" value="direct">
    	<p>
    	<p>
    	<input class="btn btn-success" type="submit" value="Analyze">
	</form>
  </div>
  <div class="col-md-1"></div>
  <div class="col-md-6">
  <pre class="alert alert-info" ${marlVisible}>
  ${eurosentiment}
  </pre>
</div>
</div>
</body>
<jsp:include page="footer.jsp" />
</html> 
