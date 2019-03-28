package edp.davinci.controller;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Service;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.Sourcable;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.classifiers.rules.*;
import weka.classifiers.bayes.*;
import weka.classifiers.functions.*;
import weka.classifiers.lazy.*;
import weka.classifiers.meta.*;
import weka.classifiers.misc.*;
import weka.classifiers.trees.*;
import weka.core.BatchPredictor;
import weka.core.Environment;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.SerializedObject;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.gui.explorer.ClassifierErrorsPlotInstances;
import weka.gui.explorer.ExplorerDefaults;

/**Missing value Handling via weka 
 *  
 * @author wenbaoli 
 * 
 */  
public class ClassifierWeb {  
  
    /** 
     *  
     * @param arg 
     */
	public static Evaluation setupEval(Evaluation eval, Classifier classifier,
		    Instances inst, CostMatrix costMatrix,
		    ClassifierErrorsPlotInstances plotInstances,
		    AbstractOutput classificationOutput, boolean onlySetPriors)
		    throws Exception {

		    if (classifier instanceof weka.classifiers.misc.InputMappedClassifier) {
		      Instances mappedClassifierHeader =
		        ((weka.classifiers.misc.InputMappedClassifier) classifier)
		          .getModelHeader(new Instances(inst, 0));
		      if (classificationOutput != null) {
		        classificationOutput.setHeader(mappedClassifierHeader);
		      }

		      if (!onlySetPriors) {
		        if (costMatrix != null) {
		          eval =
		            new Evaluation(new Instances(mappedClassifierHeader, 0), costMatrix);
		        } else {
		          eval = new Evaluation(new Instances(mappedClassifierHeader, 0));
		        }
		      }

		      if (!eval.getHeader().equalHeaders(inst)) {
		        // When the InputMappedClassifier is loading a model,
		        // we need to make a new dataset that maps the training instances to
		        // the structure expected by the mapped classifier - this is only
		        // to ensure that the structure and priors computed by
		        // evaluation object is correct with respect to the mapped classifier
		        Instances mappedClassifierDataset =
		          ((weka.classifiers.misc.InputMappedClassifier) classifier)
		            .getModelHeader(new Instances(mappedClassifierHeader, 0));
		        for (int zz = 0; zz < inst.numInstances(); zz++) {
		          Instance mapped =
		            ((weka.classifiers.misc.InputMappedClassifier) classifier)
		              .constructMappedInstance(inst.instance(zz));
		          mappedClassifierDataset.add(mapped);
		        }
		        eval.setPriors(mappedClassifierDataset);
		        if (!onlySetPriors) {
		          if (plotInstances != null) {
		            plotInstances.setInstances(mappedClassifierDataset);
		            plotInstances.setClassifier(classifier);
		            /*
		             * int mappedClass =
		             * ((weka.classifiers.misc.InputMappedClassifier)classifier
		             * ).getMappedClassIndex(); System.err.println("Mapped class index "
		             * + mappedClass);
		             */
		            plotInstances.setClassIndex(mappedClassifierDataset.classIndex());
		            plotInstances.setEvaluation(eval);
		          }
		        }
		      } else {
		        eval.setPriors(inst);
		        if (!onlySetPriors) {
		          if (plotInstances != null) {
		            plotInstances.setInstances(inst);
		            plotInstances.setClassifier(classifier);
		            plotInstances.setClassIndex(inst.classIndex());
		            plotInstances.setEvaluation(eval);
		          }
		        }
		      }
		    } else {
		      eval.setPriors(inst);
		      if (!onlySetPriors) {
		        if (plotInstances != null) {
		          plotInstances.setInstances(inst);
		          plotInstances.setClassifier(classifier);
		          plotInstances.setClassIndex(inst.classIndex());
		          plotInstances.setEvaluation(eval);
		        }
		      }
		    }

		    return eval;
		  }
	public void printPredictionsHeader(StringBuffer outBuff,
		    AbstractOutput classificationOutput, String title) {
		    if (classificationOutput.generatesOutput()) {
		      outBuff.append("=== Predictions on " + title + " ===\n\n");
		    }
		    classificationOutput.printHeader();
	}
	//data  分析文件路径，classifierdata--分类算法, testoption--测试选项，testoptionvalue--选项值， evaloption点击more option可以进行多个选择 evaloptionvalue--选择的值， attribute-属性选择（0，1，2，3）,m_selectedEvalMetrics--Evaluation Metrics选择的值,xval%split--填的值,outputsourcecode--选择输出源码
	public synchronized String  startClassifier(String data,String classifierdata,String classifierdatachoose,String testoption,String testoptionvalue,String evaloption,String evaloptionvalue,int attribute,String m_selectedEvalMetricstemp,String xvalsplit,String outputsourcecode)  throws Exception{	
				  StringBuffer outBuff = new StringBuffer();  //输出的数据
		          // Copy the current state of things
		          CostMatrix costMatrix = null;
		          DataSource source = new DataSource(data);  //根据路径新建Instances
		          Instances inst = source.getDataSet(); 
		          Instances userTestStructure = null;
		          ClassifierErrorsPlotInstances plotInstances = null;
		          ArrayList<String> m_selectedEvalMetrics=new ArrayList<String>();
	              String[] tempmetric=m_selectedEvalMetricstemp.split(",");
	              for(int i=0;i<tempmetric.length;i++) {
	            	  m_selectedEvalMetrics.add(tempmetric[i]);
	              }
		          // for timing
		          long trainTimeStart = 0, trainTimeElapsed = 0;
		          long testTimeStart = 0, testTimeElapsed = 0;
		        
		          //选择--- 提供测试集   START  传一个字符串，按照“，”拆分成3个属性
		          try {
		            if (testoption.equals("B")) {
		              /* if (m_ClassifierEditor.getValue() instanceof BatchPredictor
		                && ((BatchPredictor) m_ClassifierEditor.getValue())
		                  .implementsMoreEfficientBatchPrediction()
		                && m_TestLoader instanceof ArffLoader) {
		                // we're not really streaming test instances in this case...
		                ((ArffLoader) m_TestLoader).setRetainStringVals(true);
		              } */
		              
		              ArrayList<String> al=new ArrayList<String>();
		              String[] testtemp=testoptionvalue.split(",");
		              for(int i=0;i<testtemp.length;i++) {
		            	  al.add(testtemp[i]);
		              }
		              /*String altemp=al.get(0);
		              System.out.println(altemp);
		              if(altemp.indexOf(".csv")!=-1 || altemp.indexOf(".CSV")!=-1) {
		            	  altemp="weka.core.converters.CSVLoader";
		              }
		              if(altemp.indexOf(".ARFF")!=-1 || altemp.indexOf(".arff")!=-1) {
		            	  altemp="weka.core.converters.ArffLoader";
		              }
		              if(altemp.indexOf(".json")!=-1 || altemp.indexOf(".JSON")!=-1) {
		            	  altemp="weka.core.converters.JSONLoader";
		              }
		              if(altemp.indexOf(".data")!=-1 || altemp.indexOf(".DATA")!=-1) {
		            	  altemp="weka.core.converters.C45Loader";
		              }
		              if(altemp.indexOf(".names")!=-1 || altemp.indexOf(".NAMES")!=-1) {
		            	  altemp="weka.core.converters.C45Loader";
		              }
		              Class clazzl = Class.forName(altemp);
		              Loader ld=(Loader)clazzl.newInstance();
		              //System.out.println(ld);
		              if (ld instanceof ArffLoader && evaloption.indexOf("F")!=-1) {
		                  ((ArffLoader) ld).setRetainStringVals(true);
		                }
		              ld.reset();*/
		              source = new DataSource(al.get(0));
		              //System.out.println(source);
		              userTestStructure = source.getStructure();
		              //System.out.println(userTestStructure);
		              //System.out.println("------------");
		              userTestStructure.setClassIndex(Integer.parseInt(al.get(2)));
		            }
		          } catch (Exception ex) {
		            ex.printStackTrace();
		          }//选择--- 提供测试集   END
		          
		          //判断cost-sensitive-evaluation 是否进行了选择
		          if (evaloption.indexOf("H")!=-1) {
		            //costMatrix =new CostMatrix((CostMatrix) m_CostMatrixEditor.getValue());不知道做啥用，可以取消此选项
		          }
		          //Classifier evaluation options
		          boolean outputModel = evaloption.indexOf("A")!=-1?true:false;  //output model
		          boolean outputModelsForTrainingSplits = evaloption.indexOf("B")!=-1?true:false;  //output models for training splits
		          boolean outputPerClass =evaloption.indexOf("C")!=-1?true:false;    //output per-class stats
		          boolean outputConfusion = evaloption.indexOf("E")!=-1?true:false;  //output confusion matrix
		          
		          boolean outputSummary = true;
		          boolean outputEntropy = evaloption.indexOf("D")!=-1?true:false;   //output entropy evaluation measures
		          boolean saveVis = evaloption.indexOf("F")!=-1?true:false;  //store predictions for visualization
		          boolean outputPredictionsText =evaloptionvalue.indexOf("Null")!=-1?false:true; // output predictions

		          String grph = null;

		          int testMode = 0;
		          int numFolds = 10;
		          double percent = 66;
		          int classIndex = attribute;
		          inst.setClassIndex(classIndex);
		          Class clazz1 = Class.forName(classifierdata);
		          Classifier classifier=null;
		          Classifier template = null;
		          //这里的classifier需要获取 classifierdatachoose 选择的分类值
		          String classname=classifierdata.substring(classifierdata.lastIndexOf(".")+1, classifierdata.length());
		          String[] options=weka.core.Utils.splitOptions(classifierdatachoose);
		          //System.out.println(classname);
		          //选择bayes分类
		          if(classname.equalsIgnoreCase("BayesNet")) {
		        	  BayesNet bayesnet = (BayesNet) clazz1.newInstance();
		        	  bayesnet.setOptions(options);
		        	  classifier=(Classifier) bayesnet;
		          }
		          if(classname.equalsIgnoreCase("NaiveBayes")) {
		        	  NaiveBayes naivebayes = (NaiveBayes) clazz1.newInstance();
		        	  naivebayes.setOptions(options);
		        	  classifier=(Classifier) naivebayes;
		          }
		          if(classname.equalsIgnoreCase("NaiveBayesMultinomial")) {
		        	  NaiveBayesMultinomial naivebayesmultinomial = (NaiveBayesMultinomial) clazz1.newInstance();
		        	  naivebayesmultinomial.setOptions(options);
		        	  classifier=(Classifier) naivebayesmultinomial;
		          }
		          if(classname.equalsIgnoreCase("NaiveBayesMultinomialText")) {
		        	  NaiveBayesMultinomialText naivebayesmultinomialtext = (NaiveBayesMultinomialText) clazz1.newInstance();
		        	  naivebayesmultinomialtext.setOptions(options);
		        	  classifier=(Classifier) naivebayesmultinomialtext;
		          }
		          if(classname.equalsIgnoreCase("NaiveBayesMultinomialUpdateable")) {
		        	  NaiveBayesMultinomialUpdateable naivebayesmultinomialupdateable = (NaiveBayesMultinomialUpdateable) clazz1.newInstance();
		        	  naivebayesmultinomialupdateable.setOptions(options);
		        	  classifier=(Classifier) naivebayesmultinomialupdateable;
		          }
		          if(classname.equalsIgnoreCase("NaiveBayesUpdateable")) {
		        	  NaiveBayesUpdateable naivebayesupdateable = (NaiveBayesUpdateable) clazz1.newInstance();
		        	  naivebayesupdateable.setOptions(options);
		        	  classifier=(Classifier) naivebayesupdateable;
		          }
		          //选择functions分类
		          if(classname.equalsIgnoreCase("GaussianProcesses")) {
		        	  GaussianProcesses gaussianprocesses = (GaussianProcesses) clazz1.newInstance();
		        	  gaussianprocesses.setOptions(options);
		        	  classifier=(Classifier) gaussianprocesses;
		          }
		          if(classname.equalsIgnoreCase("LinearRegression")) {
		        	  LinearRegression linearregression = (LinearRegression) clazz1.newInstance();
		        	  linearregression.setOptions(options);
		        	  classifier=(Classifier) linearregression;
		          }
		          if(classname.equalsIgnoreCase("Logistic")) {
		        	  Logistic logistic = (Logistic) clazz1.newInstance();
		        	  logistic.setOptions(options);
		        	  classifier=(Classifier) logistic;
		          }
		          if(classname.equalsIgnoreCase("MultilayerPerceptron")) {
		        	  MultilayerPerceptron multilayerperceptron = (MultilayerPerceptron) clazz1.newInstance();
		        	  multilayerperceptron.setOptions(options);
		        	  classifier=(Classifier) multilayerperceptron;
		          }
		          if(classname.equalsIgnoreCase("SGD")) {
		        	  SGD sgd = (SGD) clazz1.newInstance();
		        	  sgd.setOptions(options);
		        	  classifier=(Classifier) sgd;
		          }
		          if(classname.equalsIgnoreCase("SGDText")) {
		        	  SGDText sgdtext = (SGDText) clazz1.newInstance();
		        	  sgdtext.setOptions(options);
		        	  classifier=(Classifier) sgdtext;
		          }
		          if(classname.equalsIgnoreCase("SimpleLinearRegression")) {
		        	  SimpleLinearRegression simplelinearregression = (SimpleLinearRegression) clazz1.newInstance();
		        	  simplelinearregression.setOptions(options);
		        	  classifier=(Classifier) simplelinearregression;
		          }
		          if(classname.equalsIgnoreCase("SimpleLogistic")) {
		        	  SimpleLogistic simplelogistic = (SimpleLogistic) clazz1.newInstance();
		        	  simplelogistic.setOptions(options);
		        	  classifier=(Classifier) simplelogistic;
		          }
		          if(classname.equalsIgnoreCase("SMO")) {
		        	  SMO smo = (SMO) clazz1.newInstance();
		        	  smo.setOptions(options);
		        	  classifier=(Classifier) smo;
		          }
		          if(classname.equalsIgnoreCase("SMOreg")) {
		        	  SMOreg smoreg = (SMOreg) clazz1.newInstance();
		        	  smoreg.setOptions(options);
		        	  classifier=(Classifier) smoreg;
		          }
		          if(classname.equalsIgnoreCase("VotedPerceptron")) {
		        	  VotedPerceptron votedperceptron = (VotedPerceptron) clazz1.newInstance();
		        	  votedperceptron.setOptions(options);
		        	  classifier=(Classifier) votedperceptron;
		          }
		          //选择lazy分类
		          if(classname.equalsIgnoreCase("IBk")) {
		        	  IBk ibk = (IBk) clazz1.newInstance();
		        	  ibk.setOptions(options);
		        	  classifier=(Classifier) ibk;
		          }
		          if(classname.equalsIgnoreCase("KStar")) {
		        	  KStar kstar = (KStar) clazz1.newInstance();
		        	  kstar.setOptions(options);
		        	  classifier=(Classifier) kstar;
		          }
		          if(classname.equalsIgnoreCase("LWL")) {
		        	  LWL lwl = (LWL) clazz1.newInstance();
		        	  lwl.setOptions(options);
		        	  classifier=(Classifier) lwl;
		          }
		          //选择meta分类
		          if(classname.equalsIgnoreCase("AdaBoostM1")) {
		        	  AdaBoostM1 adaboostm1 = (AdaBoostM1) clazz1.newInstance();
		        	  adaboostm1.setOptions(options);
		        	  classifier=(Classifier) adaboostm1;
		          }
		          if(classname.equalsIgnoreCase("AdditiveRegression")) {
		        	  AdditiveRegression additieregression = (AdditiveRegression) clazz1.newInstance();
		        	  additieregression.setOptions(options);
		        	  classifier=(Classifier) additieregression;
		          }
		          if(classname.equalsIgnoreCase("AttributeSelectedClassifier")) {
		        	  AttributeSelectedClassifier attributeselectedclassifier = (AttributeSelectedClassifier) clazz1.newInstance();
		        	  attributeselectedclassifier.setOptions(options);
		        	  classifier=(Classifier) attributeselectedclassifier;
		          }
		          if(classname.equalsIgnoreCase("Bagging")) {
		        	  Bagging bagging = (Bagging) clazz1.newInstance();
		        	  bagging.setOptions(options);
		        	  classifier=(Classifier) bagging;
		          }
		          if(classname.equalsIgnoreCase("ClassificationViaRegression")) {
		        	  ClassificationViaRegression classificationviaregression = (ClassificationViaRegression) clazz1.newInstance();
		        	  classificationviaregression.setOptions(options);
		        	  classifier=(Classifier) classificationviaregression;
		          }
		          if(classname.equalsIgnoreCase("CostSensitiveClassifier")) {
		        	  CostSensitiveClassifier costsensitiveclassifier = (CostSensitiveClassifier) clazz1.newInstance();
		        	  costsensitiveclassifier.setOptions(options);
		        	  classifier=(Classifier) costsensitiveclassifier;
		          }
		          if(classname.equalsIgnoreCase("CVParameterSelection")) {
		        	  CVParameterSelection cvparameterselection = (CVParameterSelection) clazz1.newInstance();
		        	  cvparameterselection.setOptions(options);
		        	  classifier=(Classifier) cvparameterselection;
		          }
		          if(classname.equalsIgnoreCase("FilteredClassifier")) {
		        	  FilteredClassifier filteredclassifier = (FilteredClassifier) clazz1.newInstance();
		        	  filteredclassifier.setOptions(options);
		        	  classifier=(Classifier) filteredclassifier;
		          }
		          if(classname.equalsIgnoreCase("IterativeClassifierOptimizer")) {
		        	  IterativeClassifierOptimizer iterativeclassifieroptimizer = (IterativeClassifierOptimizer) clazz1.newInstance();
		        	  iterativeclassifieroptimizer.setOptions(options);
		        	  classifier=(Classifier) iterativeclassifieroptimizer;
		          }
		          if(classname.equalsIgnoreCase("LogitBoost")) {
		        	  LogitBoost logitboost = (LogitBoost) clazz1.newInstance();
		        	  logitboost.setOptions(options);
		        	  classifier=(Classifier) logitboost;
		          }
		          if(classname.equalsIgnoreCase("MultiClassClassifier")) {
		        	  MultiClassClassifier multiclassclassifier = (MultiClassClassifier) clazz1.newInstance();
		        	  multiclassclassifier.setOptions(options);
		        	  classifier=(Classifier) multiclassclassifier;
		          }
		          if(classname.equalsIgnoreCase("MultiClassClassifierUpdateable")) {
		        	  MultiClassClassifierUpdateable multiclassclassifierupdateable = (MultiClassClassifierUpdateable) clazz1.newInstance();
		        	  multiclassclassifierupdateable.setOptions(options);
		        	  classifier=(Classifier) multiclassclassifierupdateable;
		          }
		          if(classname.equalsIgnoreCase("MultiScheme")) {
		        	  MultiScheme multischeme = (MultiScheme) clazz1.newInstance();
		        	  multischeme.setOptions(options);
		        	  classifier=(Classifier) multischeme;
		          }
		          
		          if(classname.equalsIgnoreCase("RandomCommittee")) {
		        	  RandomCommittee randomcommittee = (RandomCommittee) clazz1.newInstance();
		        	  randomcommittee.setOptions(options);
		        	  classifier=(Classifier) randomcommittee;
		          }
		          if(classname.equalsIgnoreCase("RandomizableFilteredClassifier")) {
		        	  RandomizableFilteredClassifier randomizablefilteredclassifier = (RandomizableFilteredClassifier) clazz1.newInstance();
		        	  randomizablefilteredclassifier.setOptions(options);
		        	  classifier=(Classifier) randomizablefilteredclassifier;
		          }
		          if(classname.equalsIgnoreCase("RandomSubSpace")) {
		        	  RandomSubSpace randomsubspace = (RandomSubSpace) clazz1.newInstance();
		        	  randomsubspace.setOptions(options);
		        	  classifier=(Classifier) randomsubspace;
		          }
		          if(classname.equalsIgnoreCase("RegressionByDiscretization")) {
		        	  RegressionByDiscretization regressionbydiscretization = (RegressionByDiscretization) clazz1.newInstance();
		        	  regressionbydiscretization.setOptions(options);
		        	  classifier=(Classifier) regressionbydiscretization;
		          }
		          
		          if(classname.equalsIgnoreCase("Stacking")) {
		        	  Stacking stacking = (Stacking) clazz1.newInstance();
		        	  stacking.setOptions(options);
		        	  classifier=(Classifier) stacking;
		          }
		          if(classname.equalsIgnoreCase("Vote")) {
		        	  Vote vote = (Vote) clazz1.newInstance();
		        	  vote.setOptions(options);
		        	  classifier=(Classifier) vote;
		          }
		          if(classname.equalsIgnoreCase("WeightedInstancesHandlerWrapper")) {
		        	  WeightedInstancesHandlerWrapper weightedinstanceshandlerwrapper = (WeightedInstancesHandlerWrapper) clazz1.newInstance();
		        	  weightedinstanceshandlerwrapper.setOptions(options);
		        	  classifier=(Classifier) weightedinstanceshandlerwrapper;
		          }
		          //选择misc分类
		          if(classname.equalsIgnoreCase("InputMappedClassifier")) {
		        	  InputMappedClassifier inputmappedclassifier = (InputMappedClassifier) clazz1.newInstance();
		        	  inputmappedclassifier.setOptions(options);
		        	  classifier=(Classifier) inputmappedclassifier;
		          }
		          if(classname.equalsIgnoreCase("SerializedClassifier")) {
		        	  SerializedClassifier serializedclassifier = (SerializedClassifier) clazz1.newInstance();
		        	  serializedclassifier.setOptions(options);
		        	  classifier=(Classifier) serializedclassifier;
		          }
		          //选择rules分类
		          if(classname.equalsIgnoreCase("DecisionTable")) {
		        	  DecisionTable decisiontable = (DecisionTable) clazz1.newInstance();
		        	  decisiontable.setOptions(options);
		        	  classifier=(Classifier) decisiontable;
		          }
		          if(classname.equalsIgnoreCase("JRip")) {
		        	  JRip jrip = (JRip) clazz1.newInstance();
		        	  jrip.setOptions(options);
		        	  classifier=(Classifier) jrip;
		          }
		          if(classname.equalsIgnoreCase("M5Rules")) {
		        	  M5Rules m5rules = (M5Rules) clazz1.newInstance();
		        	  m5rules.setOptions(options);
		        	  classifier=(Classifier) m5rules;
		          }
		          if(classname.equalsIgnoreCase("OneR")) {
		        	  OneR oner = (OneR) clazz1.newInstance();
		        	  oner.setOptions(options);
		        	  classifier=(Classifier) oner;
		          }
		          if(classname.equalsIgnoreCase("PART")) {
		        	  PART part = (PART) clazz1.newInstance();
		        	  part.setOptions(options);
		        	  classifier=(Classifier) part;
		          }
		          if(classname.equalsIgnoreCase("ZeroR")) {
		        	  ZeroR zeror = (ZeroR) clazz1.newInstance();
		        	  zeror.setOptions(options);
		        	  classifier=(Classifier) zeror;
		          }
		          //选择trees分类
		          if(classname.equalsIgnoreCase("DecisionStump")) {
		        	  DecisionStump decisionstump = (DecisionStump) clazz1.newInstance();
		        	  decisionstump.setOptions(options);
		        	  classifier=(Classifier) decisionstump;
		          }
		          if(classname.equalsIgnoreCase("HoeffdingTree")) {
		        	  HoeffdingTree hoeffdingtree = (HoeffdingTree) clazz1.newInstance();
		        	  hoeffdingtree.setOptions(options);
		        	  classifier=(Classifier) hoeffdingtree;
		          }
		          if(classname.equalsIgnoreCase("J48")) {
		        	  J48 j48 = (J48) clazz1.newInstance();
		        	  j48.setOptions(options);
		        	  classifier=(Classifier) j48;
		          }
		          if(classname.equalsIgnoreCase("LMT")) {
		        	  LMT lmt = (LMT) clazz1.newInstance();
		        	  lmt.setOptions(options);
		        	  classifier=(Classifier) lmt;
		          }
		          if(classname.equalsIgnoreCase("M5P")) {
		        	  M5P m5p = (M5P) clazz1.newInstance();
		        	  m5p.setOptions(options);
		        	  classifier=(Classifier) m5p;
		          }
		          if(classname.equalsIgnoreCase("RandomForest")) {
		        	  RandomForest randomforest = (RandomForest) clazz1.newInstance();
		        	  randomforest.setOptions(options);
		        	  classifier=(Classifier) randomforest;
		          }
		          if(classname.equalsIgnoreCase("RandomTree")) {
		        	  RandomTree randomtree = (RandomTree) clazz1.newInstance();
		        	  randomtree.setOptions(options);
		        	  classifier=(Classifier) randomtree;
		          }
		          if(classname.equalsIgnoreCase("REPTree")) {
		        	  REPTree reptree = (REPTree) clazz1.newInstance();
		        	  reptree.setOptions(options);
		        	  classifier=(Classifier) reptree;
		          }
		          //String[] options=weka.core.Utils.splitOptions(classifierdatachoose);
		          //classifiers.setOptions(options);
		          
		          //System.out.println("22222222222");
		          
		          //System.out.println(classifier);
		          try {
		            template = AbstractClassifier.makeCopy(classifier);
		          } catch (Exception ex) {
		            System.out.println("Problem copying classifier: " + ex.getMessage());
		          }
		          Classifier fullClassifier = null;
		          //System.out.println("4444");
		          AbstractOutput classificationOutput = null;
		          if (outputPredictionsText) {
		        	Class clazz2 = Class.forName(evaloptionvalue);
		            classificationOutput =
		              (AbstractOutput)clazz2.newInstance();//weka.classifiers.evaluation.output.prediction.Null
		            Instances header = new Instances(inst, 0);
		            header.setClassIndex(classIndex);
		            classificationOutput.setHeader(header);
		            classificationOutput.setBuffer(outBuff);
		          }
		          //System.out.println("333333");
		          String name =
		            (new SimpleDateFormat("HH:mm:ss - ")).format(new Date());
		          String cname = "";
		          String cmd = "";
		          Evaluation eval = null;
		          try {
		            if (testoption.equals("C")) {//选择交叉验证
		              testMode = 1;
		              numFolds = Integer.parseInt(testoptionvalue);
		              if (numFolds <= 1) {
		                throw new Exception("Number of folds must be greater than 1");
		              }
		            } else if (testoption.equals("D")) {//选择拆分比例
		              testMode = 2;
		              percent = Double.parseDouble(testoptionvalue);
		              if ((percent <= 0) || (percent >= 100)) {
		                throw new Exception("Percentage must be between 0 and 100");
		              }
		            } else if (testoption.equals("A")) {//选择--- 使用训练集
		              testMode = 3;
		            } else if (testoption.equals("B")) {//选择--- 提供测试集
		              testMode = 4;
		              // Check the test instance compatibility
		              if (source == null) {
		                throw new Exception("No user test set has been specified");
		              }
		              if (!(classifier instanceof weka.classifiers.misc.InputMappedClassifier)) {
		            	 // System.out.println("333333");
		            	  //System.out.println("333333-------"+inst);
		            	  //System.out.println("333333-------"+userTestStructure);
		            	 // System.out.println("333333-------"+inst.equalHeaders(userTestStructure));
		                  if (!inst.equalHeaders(userTestStructure)) {
		                	  //System.out.println("55555");
		                    boolean wrapClassifier = false;
		                    //判定
		                    if (!wrapClassifier) {
		                      weka.classifiers.misc.InputMappedClassifier temp =
		                        new weka.classifiers.misc.InputMappedClassifier();

		                      // pass on the known test structure so that we get the
		                      // correct mapping report from the toString() method
		                      // of InputMappedClassifier
		                      temp.setClassifier(classifier);
		                      temp.setTestStructure(userTestStructure);
		                      classifier = temp;
		                    } else {
		                      throw new Exception(
		                        "Train and test set are not compatible\n"
		                          + inst.equalHeadersMsg(userTestStructure));
		                    }
		                  }
		                }
		            } else {
		              throw new Exception("Unknown test mode");
		            }

		            cname = classifier.getClass().getName();
		            if (cname.startsWith("weka.classifiers.")) {
		              name += cname.substring("weka.classifiers.".length());
		            } else {
		              name += cname;
		            }
		            cmd = classifier.getClass().getName();
		            /*if (classifier instanceof OptionHandler) {
		              cmd +=
		                " "
		                  + Utils
		                    .joinOptions(((OptionHandler) classifier).getOptions());
		            }*/

		            // set up the structure of the plottable instances for
		            // visualization
		            plotInstances = ExplorerDefaults.getClassifierErrorsPlotInstances();
		            plotInstances.setInstances(testMode == 4 ? userTestStructure : inst);
		            plotInstances.setClassifier(classifier);
		            plotInstances.setClassIndex(inst.classIndex());
		            plotInstances.setSaveForVisualization(saveVis);
		            plotInstances
		              .setPointSizeProportionalToMargin(evaloption.indexOf("G")!=-1?true:false);

		            // Output some header information

		            outBuff.append("=== Run information ===\n\n");
		            outBuff.append("Scheme:       " + cname);
		            //选择分类器选项的设定值
		            //System.out.println(classifier instanceof OptionHandler);
		            if (classifier instanceof OptionHandler) {
		              String[] o = classifierdatachoose.split(",");   
		              outBuff.append(" " + Utils.joinOptions(o));
		            }
		            outBuff.append("\n");
		            outBuff.append("Relation:     " + inst.relationName() + '\n');
		            outBuff.append("Instances:    " + inst.numInstances() + '\n');
		            outBuff.append("Attributes:   " + inst.numAttributes() + '\n');
		            if (inst.numAttributes() < 100) {
		              for (int i = 0; i < inst.numAttributes(); i++) {
		                outBuff.append("              " + inst.attribute(i).name()
		                  + '\n');
		              }
		            } else {
		              outBuff.append("              [list of attributes omitted]\n");
		            }

		            outBuff.append("Test mode:    ");
		            //System.out.println(testMode);
		            switch (testMode) {
		            case 3: // Test on training
		              outBuff.append("evaluate on training data\n");
		              break;
		            case 1: // CV mode
		              outBuff.append("" + numFolds + "-fold cross-validation\n");
		              break;
		            case 2: // Percent split
		              outBuff.append("split " + percent + "% train, remainder test\n");
		              break;
		            case 4: // Test on user split
		              if (source.isIncremental()) {
		                outBuff.append("user supplied test set: "
		                  + " size unknown (reading incrementally)\n");
		              } else {
		                outBuff.append("user supplied test set: "
		                  + source.getDataSet().numInstances() + " instances\n");
		              }
		              break;
		            }
		            if (costMatrix != null) {
		              outBuff.append("Evaluation cost matrix:\n")
		                .append(costMatrix.toString()).append("\n");
		            }
		            outBuff.append("\n");
		            // Build the model and output it.
		            if (outputModel || (testMode == 3) || (testMode == 4)) {
		              System.out.println("Building model on training data...");
		              trainTimeStart = System.currentTimeMillis();
		              classifier.buildClassifier(inst);
		              trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;
		            }
		            if (outputModel) {
		              outBuff
		                .append("=== Classifier model (full training set) ===\n\n");
		              //System.out.println("-----"+classifier.toString()+"------");
		              
		              outBuff.append(classifier.toString() + "\n");
		              outBuff.append("\nTime taken to build model: "
		                + Utils.doubleToString(trainTimeElapsed / 1000.0, 2)
		                + " seconds\n\n");
		              /*if (classifier instanceof Drawable) {
		                grph = null;
		                try {
		                  grph = ((Drawable) classifier).graph();
		                } catch (Exception ex) {
		                }
		              }*/
		              // copy full model for output
		              SerializedObject so = new SerializedObject(classifier);
		              fullClassifier = (Classifier) so.getObject();
		            }

		            switch (testMode) {
		            case 3: // Test on training
		              System.out.println("Evaluating on training data...");
		              eval = new Evaluation(inst, costMatrix);

		              // make adjustments if the classifier is an InputMappedClassifier
		              eval =
		                setupEval(eval, classifier, inst, costMatrix, plotInstances,
		                  classificationOutput, false);
		              eval.setMetricsToDisplay(m_selectedEvalMetrics);//[Correct, Incorrect, Kappa, Total cost, Average cost, KB relative, KB information, Correlation, Complexity 0, Complexity scheme, Complexity improvement, MAE, RMSE, RAE, RRSE, TP rate, FP rate, Precision, Recall, F-measure, MCC, ROC area, PRC area]

		              // plotInstances.setEvaluation(eval);
		              plotInstances.setUp();

		              if (outputPredictionsText) {
		                printPredictionsHeader(outBuff, classificationOutput,
		                  "training set");
		              }

		              testTimeStart = System.currentTimeMillis();
		              if (classifier instanceof BatchPredictor
		                && ((BatchPredictor) classifier)
		                  .implementsMoreEfficientBatchPrediction()) {
		                Instances toPred = new Instances(inst);
		                for (int i = 0; i < toPred.numInstances(); i++) {
		                  toPred.instance(i).setClassMissing();
		                }
		                double[][] predictions =
		                  ((BatchPredictor) classifier)
		                    .distributionsForInstances(toPred);
		                plotInstances.process(inst, predictions, eval);
		                if (outputPredictionsText) {
		                  for (int jj = 0; jj < inst.numInstances(); jj++) {
		                    classificationOutput.printClassification(predictions[jj],
		                      inst.instance(jj), jj);
		                  }
		                }
		              } else {
		                for (int jj = 0; jj < inst.numInstances(); jj++) {
		                  plotInstances.process(inst.instance(jj), classifier, eval);

		                  if (outputPredictionsText) {
		                    classificationOutput.printClassification(classifier,
		                      inst.instance(jj), jj);
		                  }
		                  if ((jj % 100) == 0) {
		                    System.out.println("Evaluating on training data. Processed "
			                        + jj + " instances...");
		                  }
		                }
		              }
		              testTimeElapsed = System.currentTimeMillis() - testTimeStart;
		              if (outputPredictionsText) {
		                classificationOutput.printFooter();
		              }
		              if (outputPredictionsText
		                && classificationOutput.generatesOutput()) {
		                outBuff.append("\n");
		              }
		              outBuff.append("=== Evaluation on training set ===\n");
		              break;

		            case 1: // CV mode
		              System.out.println("Randomizing instances...");
		              int rnd = 1;
		              try {
		                rnd = Integer.parseInt(xvalsplit);//random seed for cross validation or % split
		                // System.err.println("Using random seed "+rnd);
		              } catch (Exception ex) {
		                System.out.println("Trouble parsing random seed value");
		                rnd = 1;
		              }
		              Random random = new Random(rnd);
		              inst.randomize(random);
		              if (inst.attribute(classIndex).isNominal()) {
		                System.out.println("Stratifying instances...");
		                inst.stratify(numFolds);
		              }
		              eval = new Evaluation(inst, costMatrix);

		              // make adjustments if the classifier is an InputMappedClassifier
		              eval =
		                setupEval(eval, classifier, inst, costMatrix, plotInstances,
		                  classificationOutput, false);
		              eval.setMetricsToDisplay(m_selectedEvalMetrics);

		              // plotInstances.setEvaluation(eval);
		              plotInstances.setUp();

		              if (outputPredictionsText) {
		                printPredictionsHeader(outBuff, classificationOutput,
		                  "test data");
		              }

		              // Make some splits and do a CV
		              for (int fold = 0; fold < numFolds; fold++) {
		                System.out.println("Creating splits for fold " + (fold + 1)
				                  + "...");
		                Instances train = inst.trainCV(numFolds, fold, random);

		                // make adjustments if the classifier is an
		                // InputMappedClassifier
		                eval =
		                  setupEval(eval, classifier, train, costMatrix, plotInstances,
		                    classificationOutput, true);
		                eval.setMetricsToDisplay(m_selectedEvalMetrics);

		                // eval.setPriors(train);
		                System.out.println("Building model for fold " + (fold + 1)
				                  + "...");
		                Classifier current = null;
		                try {
		                  current = AbstractClassifier.makeCopy(template);
		                } catch (Exception ex) {
		                  System.out.println("Problem copying classifier: "
				                    + ex.getMessage());
		                }
		                current.buildClassifier(train);
		                if (outputModelsForTrainingSplits) {
		                  outBuff.append("\n=== Classifier model for fold " + (fold + 1) + " ===\n\n");
		                  outBuff.append(current.toString() + "\n");
		                }
		                Instances test = inst.testCV(numFolds, fold);
		                System.out.println("Evaluating model for fold " + (fold + 1)
				                  + "...");
		                if (classifier instanceof BatchPredictor
		                  && ((BatchPredictor) classifier)
		                    .implementsMoreEfficientBatchPrediction()) {
		                  Instances toPred = new Instances(test);
		                  for (int i = 0; i < toPred.numInstances(); i++) {
		                    toPred.instance(i).setClassMissing();
		                  }
		                  double[][] predictions =
		                    ((BatchPredictor) current)
		                      .distributionsForInstances(toPred);
		                  plotInstances.process(test, predictions, eval);
		                  if (outputPredictionsText) {
		                    for (int jj = 0; jj < test.numInstances(); jj++) {
		                      classificationOutput.printClassification(predictions[jj],
		                        test.instance(jj), jj);
		                    }
		                  }
		                } else {
		                  for (int jj = 0; jj < test.numInstances(); jj++) {
		                    plotInstances.process(test.instance(jj), current, eval);
		                    if (outputPredictionsText) {
		                      classificationOutput.printClassification(current,
		                        test.instance(jj), jj);
		                    }
		                  }
		                }
		              }
		              if (outputPredictionsText) {
		                classificationOutput.printFooter();
		              }
		              if (outputPredictionsText) {
		                outBuff.append("\n");
		              }
		              if (inst.attribute(classIndex).isNominal()) {
		                outBuff.append("=== Stratified cross-validation ===\n");
		              } else {
		                outBuff.append("=== Cross-validation ===\n");
		              }
		              break;

		            case 2: // Percent split
		              if (evaloption.indexOf("I")<=0) {
		                System.out.println("Randomizing instances...");
		                try {
		                  rnd = Integer.parseInt(xvalsplit);
		                } catch (Exception ex) {
		                  System.out.println("Trouble parsing random seed value");
		                  rnd = 1;
		                }
		                inst.randomize(new Random(rnd));
		              }
		              int trainSize =
		                (int) Math.round(inst.numInstances() * percent / 100);
		              int testSize = inst.numInstances() - trainSize;
		              Instances train = new Instances(inst, 0, trainSize);
		              Instances test = new Instances(inst, trainSize, testSize);
		              System.out.println("Building model on training split ("
				                + trainSize + " instances)...");
		              Classifier current = null;
		              try {
		                current = AbstractClassifier.makeCopy(template);
		              } catch (Exception ex) {
		                System.out.println("Problem copying classifier: "
				                  + ex.getMessage());
		              }
		              current.buildClassifier(train);
		              if (outputModelsForTrainingSplits) {
		                outBuff.append("\n=== Classifier model for training split (" + trainSize + " instances) ===\n\n");
		                outBuff.append(current.toString() + "\n");
		              }
		              eval = new Evaluation(train, costMatrix);

		              // make adjustments if the classifier is an InputMappedClassifier
		              eval =
		                setupEval(eval, classifier, train, costMatrix, plotInstances,
		                  classificationOutput, false);
		              eval.setMetricsToDisplay(m_selectedEvalMetrics);

		              // plotInstances.setEvaluation(eval);
		              plotInstances.setUp();
		              System.out.println("Evaluating on test split...");
		              if (outputPredictionsText) {
		                printPredictionsHeader(outBuff, classificationOutput,
		                  "test split");
		              }

		              testTimeStart = System.currentTimeMillis();
		              if (classifier instanceof BatchPredictor
		                && ((BatchPredictor) classifier)
		                  .implementsMoreEfficientBatchPrediction()) {
		                Instances toPred = new Instances(test);
		                for (int i = 0; i < toPred.numInstances(); i++) {
		                  toPred.instance(i).setClassMissing();
		                }

		                double[][] predictions =
		                  ((BatchPredictor) current).distributionsForInstances(toPred);
		                plotInstances.process(test, predictions, eval);
		                if (outputPredictionsText) {
		                  for (int jj = 0; jj < test.numInstances(); jj++) {
		                    classificationOutput.printClassification(predictions[jj],
		                      test.instance(jj), jj);
		                  }
		                }
		              } else {
		                for (int jj = 0; jj < test.numInstances(); jj++) {
		                  plotInstances.process(test.instance(jj), current, eval);
		                  if (outputPredictionsText) {
		                    classificationOutput.printClassification(current,
		                      test.instance(jj), jj);
		                  }
		                  if ((jj % 100) == 0) {
		                    System.out.println("Evaluating on test split. Processed "
				                      + jj + " instances...");
		                  }
		                }
		              }
		              testTimeElapsed = System.currentTimeMillis() - testTimeStart;
		              if (outputPredictionsText) {
		                classificationOutput.printFooter();
		              }
		              if (outputPredictionsText) {
		                outBuff.append("\n");
		              }
		              outBuff.append("=== Evaluation on test split ===\n");
		              break;

		            case 4: // Test on user split
		              System.out.println("Evaluating on test data...");
		              eval = new Evaluation(inst, costMatrix);
		              // make adjustments if the classifier is an InputMappedClassifier
		              eval =
		                setupEval(eval, classifier, inst, costMatrix, plotInstances,
		                  classificationOutput, false);
		              plotInstances.setInstances(userTestStructure);
		              eval.setMetricsToDisplay(m_selectedEvalMetrics);

		              // plotInstances.setEvaluation(eval);
		              plotInstances.setUp();

		              if (outputPredictionsText) {
		                printPredictionsHeader(outBuff, classificationOutput,
		                  "test set");
		              }

		              Instance instance;
		              int jj = 0;
		              Instances batchInst = null;
		              int batchSize = 100;
		              if (classifier instanceof BatchPredictor
		                && ((BatchPredictor) classifier)
		                  .implementsMoreEfficientBatchPrediction()) {
		                batchInst = new Instances(userTestStructure, 0);
		                String batchSizeS =
		                  ((BatchPredictor) classifier).getBatchSize();
		                if (batchSizeS != null && batchSizeS.length() > 0) {
		                  try {
		                    batchSizeS =
		                      Environment.getSystemWide().substitute(batchSizeS);
		                  } catch (Exception ex) {
		                  }

		                  try {
		                    batchSize = Integer.parseInt(batchSizeS);
		                  } catch (NumberFormatException ex) {
		                    // just go with the default
		                  }
		                }
		              }
		              testTimeStart = System.currentTimeMillis();
		              while (source.hasMoreElements(userTestStructure)) {
		                instance = source.nextElement(userTestStructure);

		                if (classifier instanceof BatchPredictor
		                  && ((BatchPredictor) classifier)
		                    .implementsMoreEfficientBatchPrediction()) {
		                  batchInst.add(instance);
		                  if (batchInst.numInstances() == batchSize) {
		                    Instances toPred = new Instances(batchInst);
		                    for (int i = 0; i < toPred.numInstances(); i++) {
		                      toPred.instance(i).setClassMissing();
		                    }
		                    double[][] predictions =
		                      ((BatchPredictor) classifier)
		                        .distributionsForInstances(toPred);
		                    plotInstances.process(batchInst, predictions, eval);

		                    if (outputPredictionsText) {
		                      for (int kk = 0; kk < batchInst.numInstances(); kk++) {
		                        classificationOutput.printClassification(
		                          predictions[kk], batchInst.instance(kk), kk);
		                      }
		                    }
		                    jj += batchInst.numInstances();
		                    System.out.println("Evaluating on test data. Processed "
				                      + jj + " instances...");
		                    batchInst.delete();
		                  }
		                } else {
		                  plotInstances.process(instance, classifier, eval);
		                  if (outputPredictionsText) {
		                    classificationOutput.printClassification(classifier,
		                      instance, jj);
		                  }
		                  if ((++jj % 100) == 0) {
		                    System.out.println("Evaluating on test data. Processed "
				                      + jj + " instances...");
		                  }
		                }
		              }

		             if (classifier instanceof BatchPredictor
		                && ((BatchPredictor) classifier)
		                  .implementsMoreEfficientBatchPrediction()
		                && batchInst.numInstances() > 0) {
		                // finish the last batch

		                Instances toPred = new Instances(batchInst);
		                for (int i = 0; i < toPred.numInstances(); i++) {
		                  toPred.instance(i).setClassMissing();
		                }

		                double[][] predictions =
		                  ((BatchPredictor) classifier)
		                    .distributionsForInstances(toPred);
		                plotInstances.process(batchInst, predictions, eval);

		                if (outputPredictionsText) {
		                  for (int kk = 0; kk < batchInst.numInstances(); kk++) {
		                    classificationOutput.printClassification(predictions[kk],
		                      batchInst.instance(kk), kk);
		                  }
		                }
		              }
		              testTimeElapsed = System.currentTimeMillis() - testTimeStart;

		              if (outputPredictionsText) {
		                classificationOutput.printFooter();
		              }
		              if (outputPredictionsText) {
		                outBuff.append("\n");
		              }
		              outBuff.append("=== Evaluation on test set ===\n");
		              break;

		            default:
		              throw new Exception("Test mode not implemented");
		            }

		            if (testMode != 1) {
		              String mode = "";
		              if (testMode == 2) {
		                mode = "test split";
		              } else if (testMode == 3) {
		                mode = "training data";
		              } else if (testMode == 4) {
		                mode = "supplied test set";
		              }
		              outBuff.append("\nTime taken to test model on " + mode + ": "
		                + Utils.doubleToString(testTimeElapsed / 1000.0, 2)
		                + " seconds\n\n");
		            }

		            if (outputSummary) {
		              outBuff.append(eval.toSummaryString(outputEntropy) + "\n");
		            }

		            if (inst.attribute(classIndex).isNominal()) {

		              if (outputPerClass) {
		                outBuff.append(eval.toClassDetailsString() + "\n");
		              }

		              if (outputConfusion) {
		                outBuff.append(eval.toMatrixString() + "\n");
		              }
		            }

		            if ((fullClassifier instanceof Sourcable)
		              && evaloption.indexOf("J")!=-1) {
		              outBuff.append("=== Source code ===\n\n");
		              outBuff.append(Evaluation.wekaStaticWrapper(
		                ((Sourcable) fullClassifier), outputsourcecode));
		            }
		            System.out.println("Finished " + cname);
		            System.out.println("OK");
		          } catch (Exception ex) {
		            ex.printStackTrace();
		            System.out.println(ex.getMessage());
		            System.out.println("Problem evaluating classifier");
		          } 
            return outBuff.toString();
	}
    public static void main(String[] arg) {  
        try {  
            String fn = "E:/gogetter/airline.arff";  
            ClassifierWeb wd= new ClassifierWeb();
            String metric="Correct,Incorrect,Kappa,Total cost,Average cost,KB relative,KB information,Correlation,Complexity 0,Complexity scheme,Complexity improvement,MAE,RMSE,RAE,RRSE,TP rate,FP rate,Precision,Recall,F-measure,MCC,ROC area,PRC area";
            ArrayList<String> al=new ArrayList<String>();
            String[] tempmetric=metric.split(",");
            for(int i=0;i<tempmetric.length;i++) {
            	al.add(tempmetric[i]);
            }
            String temp=wd.startClassifier(fn,"weka.classifiers.rules.M5Rules","-N -U -R -M 4.0 -output-debug-info -do-not-check-capabilities","A",fn+",null,1","A,C,E,F","weka.classifiers.evaluation.output.prediction.Null",1,"Correct,Incorrect,Kappa,Total cost,Average cost,KB relative,KB information,Correlation,Complexity 0,Complexity scheme,Complexity improvement,MAE,RMSE,RAE,RRSE,TP rate,FP rate,Precision,Recall,F-measure,MCC,ROC area,PRC area","1","WekaClassifier");
            System.out.println(temp);
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
          
          
    }  
}  