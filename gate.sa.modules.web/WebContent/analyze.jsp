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
<div class="row">
  <div class="col-md-1"></div>
  <div class="col-md-3">
  	<form method="GET" action='Controller' name="analyze">
    	<input type="text" class="form-control" name="input" value="Insert tour text here." style="height: 122px; width: 246px">
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
"@context": "http://www.gsi.dit.upm.es/ontologies/eurosentiment/context.jsonld",
        "analysis": [
        {
            "@id": "http://www.gsi.dit.upm.es/ontologies/analysis#GATESentimentAnalysis",
            "@type": [
                "marl:SentimentAnalysis",
            ],
            "marl:maxPolarityValue": 1.0,
            "marl:minPolarityValue": -1.0,
        }
    ],
    "entries": [
        {
            "@id": "analyzedText",
            "opinions": [
            {
                "marl:hasPolarity": "marl:${polarity}",
                "marl:polarityValue": ${value},
                <% if(false) out.print("\"marl:describesObject\": \"dbpedia:BBVA\""); %>
            }
            ],
            "dc:language": "es",
            "nif:isString": "${textToAnalize}",
            "strings": [ <% if(request.getAttribute("words") != null){
            String words[][] = (String[][]) request.getAttribute("words");
            for(int i = 0; i < words.length; i++){ 
               if(words[i][4].equals("Neutral") == false){
               out.println("                {");
               out.println("                \"nif:anchorOf\": \"" + words[i][0] + "\",");
               out.println("                \"nif:beginIndex\": " + words[i][1] + ",");
               out.println("                \"nif:endIndex\": " + words[i][2] + ",");
               out.println("                \"opinion\": [");
               out.println("                	{");
               out.println("                     \"marl:hasPolarity\": \"marl:" + words[i][4] + "\",");
               out.println("                     \"marl:polarityValue\": " + words[i][3] + ",");
               out.println("                	}");
               out.println("                ]");
               out.println("                }, ");
               }
           }};%>{
            ]
        }
    ]
}
</pre>
</div>
</div>
</body>
<jsp:include page="footer.jsp" />
</html> 
