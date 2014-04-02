#-------------------------------------------------------------------------------
# Copyright (c) 2014 - David Moreno Briz - Grupo de Sistemas Inteligentes - Universidad Politécnica de Madrid. (GSI-UPM)
# http://www.gsi.dit.upm.es/
#  
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the GNU Public License v2.0
# which accompanies this distribution, and is available at
#  
# http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
#  
# Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and  limitations under the License.
#-------------------------------------------------------------------------------
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
