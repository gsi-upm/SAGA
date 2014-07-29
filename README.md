[SAGA](https://github.com/gsi-upm/SAGA)
=====
![GSI Logo](http://gsi.dit.upm.es/templates/jgsi/images/logo.png)

## Introduction
SAGA (Sentiment and Emotion Analysis integrated in GATE)  is a set of processing and linguistic resources, written in Java, developed to run sentiment and emotion analysis over text using [GATE](http://gate.ac.uk) platform.
SAGA is distributed as a GATE plugin.

## Getting started
Whether you intend to use this repository as an user or as a developer, you should download the last release of [GATE Developer](https://gate.ac.uk/family/developer.html) in order to run the graphic mode of this tool. You can find it in its [download page](https://gate.ac.uk/download/#latest). This download will include GATE Developer and Embedded and requires at least Java 7 in order to be run.
If you are not familiar with GATE, check out these [training modules](https://gate.ac.uk/conferences/training-modules.html) to understand what GATE can do.

If you are a developer, you should know that in order to develop your own processing resources using GATE Embedded (GATE APIs), a suitable Java Development Environment is required. We strongly recommend using [Eclipse IDE for Java EE developers](http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/keplersr1), because it's going to make easier the next configuration tasks.

## Installation
The installation process couldn't be easier:

1. Download this [zip](http://demos.gsi.dit.upm.es/SAGA/saga.zip).
2. Unzip the folder called _saga_ into the folder called _plugins_ that is inside your GATE installation.
3. Open GATE. The new plugin should be available.

![Plugin installed](imgs/plugin_installed.png)

Another way to install it is to open GATE -> File -> Manage CREOLE Plugins -> Configuration tab -> Click on the + symbol -> add the repository name: GSI UPM  url,  http://demos.gsi.dit.upm.es/SAGA/gate-update-site.xml -> Apply all -> Available to install tab -> Mark the SAGA plugin to install it -> Apply all -> Go to the Installed Plugins tab. There it is.

It is recommended to deploy [SEAS's project](https://github.com/gsi-upm/SEAS) as a local service in your computer to use this plugin.

## How to use the plugin
This plugin contains only one PR that offers a variety of sentiment and emotion analysis services. To load it, right click on Processing Resources -> New -> Sentiment and emotion analysis calling SEAS and Eurosentiment -> Name it -> OK.

![New PR](imgs/new_pr.png)

Then, add this new PR to your current application or create a new one so you can configure its runtime parameters:

![Runtime parameter example](imgs/runtime_example.png)

These parameters can be explained as follows:

    inputASName:
        The Annotation Set that contains the annotation type to be analyzed
    annotationType:
        The annotation type to be analyzed
    sentimentAnalysis:
        Runtime parameter that sets if the PR is going to perform sentiment analysis with the chosen algorithm.
    emotionAnalysis:
        Runtime parameter that sets if the PR is going to perform emotion analysis with the chosen algorithm.
    SentimentServiceURL:
        The endpoint of the sentiment analysis service
    EmotionServiceURL:
        The endpoint of the emotion analysis service, you can use:
            If you deploy SEAS as a local service in your computer (Recommended):
                http://localhost:8080/SAGAtoNIF/Service
                http://localhost:8080/RestrictedToNIF/RestrictedService
            ONLY FOR LITTLE TESTS:
                http://demos.gsi.dit.upm.es/tomcat/SAGAtoNIF/Service
                http://demos.gsi.dit.upm.es/tomcat/RestrictedToNIF/RestrictedService
        For more endpoints visit the Eurosentiment portal - https://portal.eurosentiment.eu
    APIKey:
        Eurosentiment token to use their services or other similar services that require an API KEY
    ApiKeyName:
        Eurosentiment (or other similar services) token name to use their services
    sentimentAlgorithm:
        Runtime parameter that sets the sentiment algorithm that the service is going to use. At the moment, you can use dictionary based algorithms.
    sentimentDictionary:
        Runtime parameter that sets the sentiment dictionary that the service is going to use (in case that sentimentAlgorithm has been chosen). You can use:
        AUTO (Detects language)
        Spanish_finances_Paradigma (SentimentServiceURL = http://demos.gsi.dit.upm.es/tomcat/SAGAtoNIF/Service)
        English_finances_Loughran_McDonald (SentimentServiceURL = http://demos.gsi.dit.upm.es/tomcat/RestrictedToNIF/RestrictedService)
        Emoticon (SentimentServiceURL = http://demos.gsi.dit.upm.es/tomcat/SAGAtoNIF/Service)
        Spanish_finances_and_Emoticon (SentimentServiceURL = http://demos.gsi.dit.upm.es/tomcat/SAGAtoNIF/Service)
        English_finances_and_Emoticon (SentimentServiceURL = http://demos.gsi.dit.upm.es/tomcat/RestrictedToNIF/RestrictedService)
    emotionAlgorithm:
        Runtime parameter that sets the emotion algorithm that the service is going to use. You can use:
        AUTO (Detects language)
        onyx (no endpoint needed)
        ANEW2010All (EmotionServiceURL = http://demos.gsi.dit.upm.es/tomcat/RestrictedToNIF/RestrictedService)
        ANEW2010Men (EmotionServiceURL = http://demos.gsi.dit.upm.es/tomcat/RestrictedToNIF/RestrictedService)
        ANEW2010Woman (EmotionServiceURL = http://demos.gsi.dit.upm.es/tomcat/RestrictedToNIF/RestrictedService)
    SentimentPolarityName:
        The name of the sentiment polarity feature
    SentimentValueName:
        The name of the sentiment value feature
    EmotionCategoryName:
        The name of the emotion category feature
    EmotionValueName:
        The name of the emotion value feature
        
## Example of use - Sentiment analysis over a finances domain
This plugin contains little corpus to test the PR:

1. Create a new corpus and populate it. Right click on the corpus -> Populate -> Go to the _saga_ plugin folder -> _resources_ -> _example_ -> _input_ -> Choose _en_ -> OK
2. Set _emotionAnalysis_ parameter to _false_
3. Configure the runtime parameters as follows (Be careful, the features inside the annotationType you choose to analyze will be substituted with the results of the analysis.):

![Runtime parameter financial example](imgs/financial_runtime_example.png)
![Runtime parameter financial example2](imgs/financial_runtime_example2.png)

3. Run this application.
4. Check the results:

![Result eng finances](imgs/result_en_finances.png)

## Example of use - Emotion analysis using Onyxemote

1. Create a new corpus and populate it. Right click on the corpus -> Populate -> Go to the _saga_ plugin folder -> _resources_ -> _example_ -> _input_ -> Choose _en_ -> OK
2. Set _sentimentAnalysis_ parameter to _false_
3. Configure the runtime parameters as follows (Be careful, the features inside the annotationType you choose to analyze will be substituted with the results of the analysis.):

![Runtime parameter emotion onyx example](imgs/emotion_runtime_example.png)

3. Run this application.
4. Check the results:

![Result eng finances](imgs/results_emotion.png)

## Example of use - Eurosentiment services

1. Sign up in the [EUROSENTIMENT portal](https://portal.eurosentiment.eu).
2. Get your token an put it in the runtime parameter called _APIKey_.
3. Set the runtime parameter called _ApiKeyName_ as _x-eurosentiment-token_.
4. Set the runtime parameters called _SentimentServiceURL_ or _EmotionServiceURL_ whith the ones offered in the [EUROSENTIMENT portal](https://portal.eurosentiment.eu).
5. Load your corpus and run the application.

## Accuracy of the algorithms over an already annotated corpus
You can test our services over an already classified corpus to see how good this analysis tool works. In this project we include a set of positive and negative text from an article called "Extracting Investor Sentiment from Weblog Texts: A Knowledge-based Approach" by Klein, A; Altuntas, O; Hausser, T. and Kessler, W.
To do so, and in order to only make a little test because of time processing, we have reduced this corpus to one positive and one negative text:

1. Create a new corpus and populate it. Right click on the corpus -> Populate -> Go to the _saga_ plugin folder -> _resources_ -> _example_ -> _input_ -> _siple_ -> Choose _pos_ -> OK
2. Create a new corpus and populate it. Right click on the corpus -> Populate -> Go to the _saga_ plugin folder -> _resources_ -> _example_ -> _input_ -> _siple_ -> Choose _neg_ -> OK
3. Load ANNIE with defaults.
4. Add the PR at the end of the ANNIE application.
5. Configure the runtime parameters as follows and ignore the rest of them:

![Test 1](imgs/test1.png)
![Test 2](imgs/test2.png)
![Test 3](imgs/test3.png)

Run the application over each corpus and check out the resoults.

## How to add new NIF's services, algorithms or dictionaries?
Check out [SEAS's project](https://github.com/gsi-upm/SAGA)

## Licenses

The source code on this repository is under the following licenses:

```
Copyright (c) 2014 GSI, UPM. All rights reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
```

```
Copyright (c) 2013 GSI, UPM. All rights reserved.

Licensed under Lesser General Public License For Linguistic Resources; 
You may not use this repository and code except in compliance with the License. 
You may obtain a copy of the License at http://www.ida.liu.se/~sarst/bitse/lgpllr.html Unless required by 
applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific 
language governing permissions and limitations under the License.
```


This work has been partialy funded by the Ministry of Industry, Energy and Tourism through the R&D project Financial Twitter Tracker (TSI-090100-2011-114)
![Financial Twitter Tracker](http://demos.gsi.dit.upm.es/ftt/img/ftt_header.png)

EUROSENTIMENT PROJECT
Grant Agreement no: 296277
Starting date: 01/09/2012
Project duration: 24 months

![Eurosentiment Logo](imgs/logo_grande.png)
![FP7 logo](imgs/logo_fp7.gif)

