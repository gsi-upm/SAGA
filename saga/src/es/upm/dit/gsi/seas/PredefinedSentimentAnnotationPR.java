/*******************************************************************************
 * Copyright (c) 2014 - David Moreno Briz - Grupo de Sistemas Inteligentes - Universidad Politécnica de Madrid. (GSI-UPM)
 * http://www.gsi.dit.upm.es/
 *  
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 *  
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *  
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and  limitations under the License.
 ******************************************************************************/
/**
 * Look at execute() to see what this PR does.
 * 
 * @author David Moreno Briz
 *
 */

package es.upm.dit.gsi.seas;

import java.util.Iterator;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;


@CreoleResource(name = "Predefined Sentiment Annotation PR", 
comment = "Predefined Sentiment Annotation PR",
helpURL = "https://github.com/gsi-upm/SAGA/",
icon = "/es/upm/dit/gsi/seas/logo_gsi.png")
public class PredefinedSentimentAnnotationPR extends AbstractLanguageAnalyser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The Annotation Set that contains the annotation type to be analyzed
	 */
	private String inputASname;
	
	/**
	 * The annotation type to be analyzed
	 */
	private String annotationType;
	
	/**
	 * The name of the sentiment polarity feature
	 */
	private String SentimentPolarityName = "marl:hasPolarity";
	
	/**
	 * The value of the sentiment polarity feature
	 */
	private String SentimentPolarity = "marl:Positive";

	public String getInputASname() {
		return inputASname;
	}

	@Optional
	@RunTime
	@CreoleParameter(comment = "")
	public void setInputASname(String inputASname) {
		this.inputASname = inputASname;
	}

	public String getAnnotationType() {
		return annotationType;
	}

	@Optional
	@RunTime
	@CreoleParameter(comment = "")
	public void setAnnotationType(String annotationType) {
		this.annotationType = annotationType;
	}

	public String getSentimentPolarityName() {
		return SentimentPolarityName;
	}

	@Optional
	@RunTime
	@CreoleParameter(comment = "")
	public void setSentimentPolarityName(String sentimentPolarityName) {
		SentimentPolarityName = sentimentPolarityName;
	}

	public String getSentimentPolarity() {
		return SentimentPolarity;
	}

	@Optional
	@RunTime
	@CreoleParameter(comment = "")
	public void setSentimentPolarity(String sentimentPolarity) {
		SentimentPolarity = sentimentPolarity;
	}
	
	/**
	 * Initialize the PR. 
	 */
	@Override
	public Resource init() throws ResourceInstantiationException{
		System.out.println(getClass().getName() + " has been inited");
		return this;
	}
	
	/**
	 * This PR performs sentiment and/or emotion annotations defined by the user in the Runtime Parameters.
	 */
	@Override
	public void execute() throws ExecutionException{
		// We get the annotation set and the annotation type to we processed.
		AnnotationSet annotationSet = document.getAnnotations(this.getInputASname());
		AnnotationSet annotationSetByType = annotationSet.get(this.getAnnotationType());
		// Iterator whit all the annotation that matches our type.
		Iterator<Annotation> iteratorA = annotationSetByType.iterator();
		// It there is no Annotation Set or Type that matches we process the whole document.
		if(annotationSet.isEmpty() || annotationSetByType.size() == 0){
			this.setAnnotation(null);
		}else{ // If not.
			while(iteratorA.hasNext()){ // We process each annotation.
				Annotation annotation = iteratorA.next();
				try{
					this.setAnnotation(annotation);
				}catch(Exception e){
					System.out.println(e);
				}	
			}
		}
	}
	
	public void setAnnotation(Annotation annotation){
		FeatureMap sentimentFeatures = Factory.newFeatureMap();
		sentimentFeatures.put(this.getSentimentPolarityName(), this.getSentimentPolarity());
		// We put the features in the annotation or in the document.
		if(annotation != null){
			annotation.setFeatures(sentimentFeatures);
		}else{
			document.setFeatures(sentimentFeatures);
		}
	}
}
