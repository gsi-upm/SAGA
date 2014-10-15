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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import gate.Node;
import gate.Document;
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


@CreoleResource(name = "TSV Emotion Parser",
comment = "TSV Emotion Parser",
helpURL = "https://github.com/gsi-upm/SAGA/",
icon = "/es/upm/dit/gsi/seas/logo_gsi.png")
public class TSVEmotionParser extends AbstractLanguageAnalyser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    
    private static boolean parse = true;
    
    private String TSVPath = "";

	/**
	 * The name of the sentiment polarity feature
	 */
	private String EmotionCategoryName = "onyx:hasEmotionCategory";

	public String getEmotionCategoryName() {
		return EmotionCategoryName;
	}

	@Optional
	@RunTime
	@CreoleParameter(comment = "")
	public void setEmotionCategoryName(String emotionCategoryName) {
		EmotionCategoryName = emotionCategoryName;
	}
    
    public String getTSVPath() {
		return TSVPath;
	}
    
	@Optional
	@RunTime
	@CreoleParameter(comment = "")
	public void setTSVPath(String tSVPath) {
		TSVPath = tSVPath;
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
        if(parse == true){
            BufferedReader br = null;
            
            try {
                
                String sCurrentLine;
                
                br = new BufferedReader(new FileReader(this.getTSVPath()));
                
                while ((sCurrentLine = br.readLine()) != null) {
                    String[] text = sCurrentLine.split("\\t");
                    try{
                        Document doc = Factory.newDocument(text[0]);
                        AnnotationSet set = doc.getAnnotations("Original");
                        FeatureMap sentimentFeatures = Factory.newFeatureMap();
                        if(text[1].equalsIgnoreCase("n/c")){
                            sentimentFeatures.put(this.getEmotionCategoryName(), "wna:neutral-emotion");
                        }else{
                            String[] emotions = text[1].split(";");
                            sentimentFeatures.put(this.getEmotionCategoryName(), emotions[0]);
                        }
                        
                        AnnotationSet set2 = doc.getAnnotations("Analyzed");
                        FeatureMap sentimentFeatures2 = Factory.newFeatureMap();
                        sentimentFeatures2.put(this.getEmotionCategoryName(), "");
                        
                        Long i = new Long(0);
                        try{
                            set.add(i, doc.getContent().size() - 1, "emotion", sentimentFeatures);
                            set2.add(i, doc.getContent().size() - 1, "emotion", sentimentFeatures2);
                        }catch(Exception in){
                            in.printStackTrace();
                        }
                        corpus.add(doc);
                        
                    }catch (ResourceInstantiationException r){
                        r.printStackTrace();
                    }
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null)br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        parse = false;
	}
}
