/*
 * <<
 * Davinci
 * ==
 * Copyright (C) 2016 - 2018 EDP
 * ==
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * >>
 */

package edp.davinci.controller;

import static org.hamcrest.CoreMatchers.nullValue;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.swing.SpinnerNumberModel;

import org.apache.xmlbeans.impl.xb.xsdschema.impl.PublicImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sun.tools.internal.xjc.generator.bean.ImplStructureStrategy.Result;

import edp.core.annotation.CurrentUser;
import edp.core.enums.HttpCodeEnum;
import edp.davinci.common.controller.BaseController;
import edp.davinci.core.common.Constants;
import edp.davinci.core.common.ResultMap;
import edp.davinci.model.User;
import edp.davinci.service.SourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;
import weka.classifiers.Classifier;
import weka.classifiers.timeseries.WekaForecaster;
import weka.classifiers.timeseries.core.TSLagUser;
import weka.classifiers.timeseries.eval.TSEvaluation;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.core.WekaPackageClassLoaderManager;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.supervised.attribute.TSLagMaker;
import weka.gui.Logger;
import weka.gui.TaskLogger;
import net.sf.json.JSONObject;


@Api(value = "/classifier", tags = "classifier", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@ApiResponses(@ApiResponse(code = 404, message = "classifier not found"))
@Slf4j
@RestController
@RequestMapping(value = Constants.BASE_API_PATH + "/classifier", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ClassifierController extends BaseController {

    @Autowired
    private SourceService sourceService;
    protected Logger m_log;

    @ApiOperation(value = "get classifier")
    @GetMapping
    public ResponseEntity getforecasting(@RequestParam Long projectId,
                                         @ApiIgnore @CurrentUser User user,
                                         HttpServletRequest request) {
        if (invalidId(projectId)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid project id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }
        try {
            ResultMap resultMap = sourceService.getSources(projectId, user, request);
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return ResponseEntity.status(HttpCodeEnum.SERVER_ERROR.getCode()).body(HttpCodeEnum.SERVER_ERROR.getMessage());
        }
    }
    protected Thread m_runThread;
    List<String> xList = new ArrayList<>();
    List<String> yList = new ArrayList<>();
    int inputNumberVal = 1 ;
    String periodicityVal = "";
    String result = "";
    @ResponseBody
    @PostMapping("/startClassifier")
	public ResponseEntity startClassifier(@RequestBody String params,
								            @ApiIgnore @CurrentUser User user,
								            HttpServletRequest request) throws Exception{
    	//startClassifier();
    	/*ResultMap map = new ResultMap();
        List<String> xlist = new ArrayList<String>();
        xlist.add(Math.random()*30+"");
        xlist.add(Math.random()*60+"");
        xlist.add(Math.random()*20+"");
        xlist.add(Math.random()*50+"");
        xlist.add(Math.random()*40+"");
        xlist.add(Math.random()*70+"");
        map.put("xList", xlist);
        List<String> ylist = new ArrayList<String>();
        ylist.add("衬衫");
        ylist.add("羊毛衫");
        ylist.add("雪纺衫");
        ylist.add("裤子");
        ylist.add("高跟鞋");
        ylist.add("袜子");
        map.put("ylist", ylist);*/
    	int horizon = 0;
    	JSONObject jsonObject = JSONObject.fromObject(params);
		String inputNumberVal1 = jsonObject.getString("inputNumberVal");
		periodicityVal = jsonObject.getString("periodicityVal");
    	WekaForecaster m_threadForecaster = new WekaForecaster();
    	 inputNumberVal = Integer.parseInt(inputNumberVal1) ;
    	m_runThread = new ForecastingThread(m_threadForecaster, null,"MultilayerPerceptron",Integer.parseInt(inputNumberVal1)); 

        m_runThread.setPriority(Thread.MIN_PRIORITY);
        m_runThread.run();
    	//startClassifier();
    	ResultMap map = new ResultMap();
    	map.put("xList", yList);
    	map.put("ylist", xList);
    	System.out.println("-------------------------------------");
		return ResponseEntity.status(200).body(map);
	}
    @Test
    public void test() throws Exception{
    	startClassifier();
    }
    
    Instances inst = null;
    protected class ForecastingThread extends Thread {
    	protected boolean m_configAndBuild = true;
	    protected WekaForecaster m_threadForecaster = null;
	    protected String m_name;
	    protected String algorithmClass = "";
	    protected int horizon = 0;
	    public ForecastingThread(WekaForecaster forecaster, String name,String algorithmClass,int horizon) {
	      m_threadForecaster = forecaster;
	      m_name = name;
	      algorithmClass = algorithmClass;
	      horizon = horizon;
	    }
	    public void setConfigureAndBuild(boolean configAndBuild) {
	      m_configAndBuild = configAndBuild;
	    }
	    @Override
	    public void run() {
	    	String data = "E:/gogetter/airline.arff";
			DataSource source;
			try {
				source = new DataSource(data);
				LogPrintStream logger = new LogPrintStream();
			    logger.println("Setting up...");
		        
		        boolean m_configAndBuild = true;
		        
		        String name = m_name;
		        StringBuffer outBuff = null;

		        if (name == null) {
		          name = (new SimpleDateFormat("HH:mm:ss - ")).format(new Date());
		          outBuff = new StringBuffer();
		        }
		        
		        String fname = "";
		        inst = source.getDataSet(); 
		        
		        TSEvaluation eval =new TSEvaluation(inst, 0.0);
		        
		        //将算法通过反射的形式弄进来 MultilayerPerceptron
		       String className = "weka.classifiers.functions."+"MultilayerPerceptron";
		        Classifier clazz = (Classifier) WekaPackageClassLoaderManager.forName(className).newInstance();
		        String options = "-L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a";
		        String[] arr = Utils.splitOptions(options);
		        ((OptionHandler) clazz).setOptions(arr);
		        
		        m_threadForecaster.setBaseForecaster(clazz);
		        m_simpleConfigPanel_applyToForecaster(m_threadForecaster);
		        m_simpleConfigPanel_applyToEvaluation(eval,m_threadForecaster);
		        //output选项卡最下面的两个checkbox
		        //m_advancedConfigPanel .getOutputFuturePredictions() || m_advancedConfigPanel.getGraphFuturePredictions()
		        eval.setForecastFuture(true);
		        eval.setHorizon(inputNumberVal);//yzh
		        fname = m_threadForecaster.getAlgorithmName();
		        if (m_name == null) {
		            String algoName = fname.substring(0, fname.indexOf(' '));
		            if (algoName.startsWith("weka.classifiers.")) {
		              name += algoName.substring("weka.classifiers.".length());
		            } else {
		              name += algoName;
		            }
		          }
		        
		      // name="13:47:55 - LinearRegression";
		       String lagOptions = "";
		       if (m_threadForecaster instanceof TSLagUser) {
		         TSLagMaker lagMaker =
		           ((TSLagUser) m_threadForecaster).getTSLagMaker();
		         lagOptions = Utils.joinOptions(lagMaker.getOptions());
		       }
		       if (lagOptions.length() > 0 && m_name == null) {
		           name += " [" + lagOptions + "]";
		        }
		       if (m_log != null) {
		           m_log.logMessage("Started " + fname);
		           if (m_configAndBuild) {
		             logger.println("Training forecaster...");
		           }
		           if (m_log instanceof TaskLogger) {
		             ((TaskLogger) m_log).taskStarted();
		           }
		         }
		       Instances trainInst = eval.getTrainingData();
		       m_threadForecaster.buildForecaster(trainInst, logger);
		       eval.evaluateForecaster(m_threadForecaster, false, logger);
		       System.out.println(eval.printFutureTrainingForecast(m_threadForecaster));
		        xList = TSEvaluation.xList;
		        yList = TSEvaluation.yList;
		        result = eval.printFutureTrainingForecast(m_threadForecaster);
		       System.out.println(xList+"---"+yList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  //根据路径新建Instances
			
	    }
    }
   
	public  void startClassifier()  throws Exception {
		String data = "E:/gogetter/airline.arff";
		DataSource source = new DataSource(data);  //根据路径新建Instances
		 LogPrintStream logger = new LogPrintStream();
	     logger.println("Setting up...");
         inst = source.getDataSet(); 
        boolean m_configAndBuild = true;
        WekaForecaster m_threadForecaster = new WekaForecaster();
        String m_name="";
        String name = m_name;
        TSEvaluation eval =new TSEvaluation(inst, 0.0);
      //将算法通过反射的形式弄进来
        /*
         *MultilayerPerceptron  353.7495  
         *GaussianProcesses     376.0651 
         *LinearRegression      365.653
         *SMOreg                353.4434 
         */
       
        Classifier clazz = (Classifier) WekaPackageClassLoaderManager.forName("weka.classifiers.functions.MultilayerPerceptron").newInstance();
        m_threadForecaster.setBaseForecaster(clazz);
        m_simpleConfigPanel_applyToForecaster(m_threadForecaster);
        String options = "-L 0.36 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a";
        String[] arr = Utils.splitOptions(options);
       /* String[] arr = {"-L","0.3","-M","0.2","-N","500","-V"," 0","-S","0","-E","20","-H","a"};*/
        ((OptionHandler) clazz).setOptions(arr);
        if (clazz instanceof OptionHandler) {
            options = Utils.joinOptions(((OptionHandler) clazz).getOptions());
          }
        
        m_simpleConfigPanel_applyToEvaluation(eval,m_threadForecaster);
        eval.setForecastFuture(true);
        String fname = "";
        fname = m_threadForecaster.getAlgorithmName();
       /* if (m_name == null) {
            String algoName = fname.substring(0, fname.indexOf(' '));
            if (algoName.startsWith("weka.classifiers.")) {
              name += algoName.substring("weka.classifiers.".length());
            } else {
              name += algoName;
            }
          }*/
        
       name="13:47:55 - LinearRegression";
       String lagOptions = "";
       if (m_threadForecaster instanceof TSLagUser) {
         TSLagMaker lagMaker =
           ((TSLagUser) m_threadForecaster).getTSLagMaker();
         lagOptions = Utils.joinOptions(lagMaker.getOptions());
       }

       Instances trainInst = eval.getTrainingData();
       m_threadForecaster.buildForecaster(trainInst, logger);
       eval.evaluateForecaster(m_threadForecaster, false, logger);
       System.out.println(eval.printFutureTrainingForecast(m_threadForecaster));
        xList = TSEvaluation.xList;
        yList = TSEvaluation.yList;
        result = eval.printFutureTrainingForecast(m_threadForecaster);
       System.out.println(xList+"---"+yList);
	}
	private void m_simpleConfigPanel_applyToEvaluation(TSEvaluation eval, WekaForecaster forecaster) {
		int horizon = 1;//((SpinnerNumberModel) m_horizonSpinner.getModel()).getNumber().intValue();
	    if (horizon < 1) {
	      try {
			throw new Exception("Must specify a non-zero number of steps to"
			    + "forecast into the future");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    }

	   // eval.setHorizon(horizon);
	    eval.setPrimeWindowSize(forecaster.getTSLagMaker().getMaxLag());
	}

	private void m_simpleConfigPanel_applyToForecaster(WekaForecaster forecaster) {
		if (forecaster != null) {
		      TSLagMaker lagMaker = forecaster.getTSLagMaker();
		      /*时间戳的下拉*/
		      String selected = "时间";
		      if (!selected.equals("<Use an artificial time index>")
		        && !selected.equals("<None>")) {
		        lagMaker.setTimeStampField(selected);
		      } else {
		        lagMaker.setTimeStampField("");
		      }

		      if (selected.equals("<None>")) {
		        lagMaker.setAdjustForTrends(false);
		      } else {
		        lagMaker.setAdjustForTrends(true);
		      }
		      /*周期性的下拉*/
		     /* if ("<Detect automatically>"
		        .equals("<Detect automatically>")
		        && (selected.equals("<Use an artificial time index>") && !m_advancedConfig
		          .isUsingCustomLags())) {
		        throw new Exception(
		          "Cannot automatically detect periodicity when using "
		            + "an artificial time index.");
		      }*/
/*
		      if ("<Detect automatically>"
		        .equals("<Detect automatically>")
		        && !selected.equals("<Use an artificial time index>")
		        && !selected.equals("<None>")
		        && (!m_instances.attribute(selected).isDate() && !m_advancedConfig
		          .isUsingCustomLags())) {
		        throw new Exception(
		          "Cannot automatically detect periodicity when using "
		            + "a non-date time stamp (select manually or use custom lags.");
		      }*/

		      // reset any date-derived periodics to false
		      lagMaker.setAddAMIndicator(false);
		      lagMaker.setAddDayOfWeek(false);
		      lagMaker.setAddMonthOfYear(false);
		      lagMaker.setAddQuarterOfYear(false);
		      lagMaker.setAddWeekendIndicator(false);
		      if (!selected.equals("<None>")) {
		        checkPeriodicity(forecaster);
		      }

		     /* if (m_computeConfidence.isSelected()) {
		        forecaster.setCalculateConfIntervalsForForecasts(getHorizon());
		        double confLevel = ((SpinnerNumberModel) m_confidenceLevelSpinner
		          .getModel()).getNumber().doubleValue();
		        forecaster.setConfidenceLevel(confLevel / 100.0);
		      } else {*/
		        forecaster.setCalculateConfIntervalsForForecasts(0);
		      /*}*/
		        // m_targetPanel.getSelectedAttributes();
		        //基础配置目标选择
		        Instances m_targetHeader=inst;
		      int[] selectedTargets ={0};
		      StringBuffer targetBuf = new StringBuffer();
		      for (int selectedTarget : selectedTargets) {
		        targetBuf.append(m_targetHeader.attribute(selectedTarget).name())
		          .append(",");
		      }
		      String temp = targetBuf.substring(0, targetBuf.lastIndexOf(","));
		      if (temp.length() == 0) {
		        try {
					throw new Exception("You must select some fields to forecast!");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      }
		      try {
				forecaster.setFieldsToForecast(temp);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    }
	}
	public void checkPeriodicity(WekaForecaster forecaster) {
		Instances m_instances=inst;
	    //if (m_advancedConfig != null) {// 如果高级配置有没有东西
	      String timeStampSelected = "时间";
	      String selectedP =periodicityVal;// "Monthly";//"<Detect automatically>";
	      if (selectedP.equals("<Detect automatically>")
	        && !timeStampSelected.equals("<Use an artificial time index>")
	        && !timeStampSelected.equals("<None>") && forecaster != null) {

	        forecaster.getTSLagMaker().setPeriodicity(
	          TSLagMaker.Periodicity.UNKNOWN);
	        TSLagMaker.PeriodicityHandler detected = TSLagMaker
	          .determinePeriodicity(m_instances, forecaster.getTSLagMaker()
	            .getTimeStampField(), forecaster.getTSLagMaker()
	            .getPeriodicity());
	        switch (detected.getPeriodicity()) {
	        case HOURLY:
	          selectedP = "Hourly";
	          break;
	        case DAILY:
	          selectedP = "Daily";
	          break;
	        case WEEKLY:
	          selectedP = "Weekly";
	          break;
	        case MONTHLY:
	          selectedP = "Monthly";
	          break;
	        case QUARTERLY:
	          selectedP = "Quarterly";
	          break;
	        case YEARLY:
	          selectedP = "Yearly";
	          break;
	        }
	      }

	      if (forecaster != null) {
	        if (selectedP.equals("Hourly")) {
	          forecaster.getTSLagMaker().setPeriodicity(
	            TSLagMaker.Periodicity.HOURLY);
	        } else if (selectedP.equals("Daily")) {
	          forecaster.getTSLagMaker().setPeriodicity(
	            TSLagMaker.Periodicity.DAILY);
	        } else if (selectedP.equals("Weekly")) {
	          forecaster.getTSLagMaker().setPeriodicity(
	            TSLagMaker.Periodicity.WEEKLY);
	        } else if (selectedP.equals("Monthly")) {
	          forecaster.getTSLagMaker().setPeriodicity(
	            TSLagMaker.Periodicity.MONTHLY);
	        } else if (selectedP.equals("Quarterly")) {
	          forecaster.getTSLagMaker().setPeriodicity(
	            TSLagMaker.Periodicity.QUARTERLY);
	        } else if (selectedP.equals("Yearly")) {
	          forecaster.getTSLagMaker().setPeriodicity(
	            TSLagMaker.Periodicity.YEARLY);
	        } else {
	          forecaster.getTSLagMaker().setPeriodicity(
	            TSLagMaker.Periodicity.UNKNOWN);
	        }
	        //暂时都是些空的
	       /* if (m_skipText.isEnabled() && m_skipText.getText() != null
	          && m_skipText.getText().length() > 0) {
	          forecaster.getTSLagMaker()
	            .setSkipEntries(m_skipText.getText().trim());
	        } else {*/
	          forecaster.getTSLagMaker().setSkipEntries(""); // clear any previously
	                                                         // set ones
	      //  }
	      }

	      // only set these defaults if the user is not using custom lag lengths!
	      //m_advancedConfig.isUsingCustomLags()  高级配置重点 lag creation中的 checkbox
	      if (!false) {
	        if (forecaster != null) {
	          forecaster.getTSLagMaker().setMinLag(1);
	        }
	       // m_advancedConfig.m_minLagSpinner.setValue(1);

	        if (selectedP.equals("Hourly")) {
	          if (forecaster != null) {
	            forecaster.getTSLagMaker().setMaxLag(
	              Math.min(m_instances.numInstances() / 2, 24));
	          }
	         /* m_advancedConfig.m_maxLagSpinner.setValue(Math.min(
	            m_instances.numInstances() / 2, 24));*/
	        } else if (selectedP.equals("Daily")) {
	          if (forecaster != null) {
	            forecaster.getTSLagMaker().setMaxLag(
	              Math.min(m_instances.numInstances() / 2, 7));
	            // forecaster.getTSLagMaker().setSkipEntries("sat,sun");
	          }
	          /*m_advancedConfig.m_maxLagSpinner.setValue(Math.min(
	            m_instances.numInstances() / 2, 7));*/
	        } else if (selectedP.equals("Weekly")) {
	          if (forecaster != null) {
	            forecaster.getTSLagMaker().setMaxLag(
	              Math.min(m_instances.numInstances() / 2, 52));
	          }
	        /*  m_advancedConfig.m_maxLagSpinner.setValue(Math.min(
	            m_instances.numInstances() / 2, 52));*/
	        } else if (selectedP.equals("Monthly")) {
	          if (forecaster != null) {
	            forecaster.getTSLagMaker().setMaxLag(
	              Math.min(m_instances.numInstances() / 2, 12));
	          }
	          /*m_advancedConfig.m_maxLagSpinner.setValue(Math.min(
	            m_instances.numInstances() / 2, 12));*/
	        } else if (selectedP.equals("Quarterly")) {
	          if (forecaster != null) {
	            forecaster.getTSLagMaker().setMaxLag(
	              Math.min(m_instances.numInstances() / 2, 4));
	          }
	          /*m_advancedConfig.m_maxLagSpinner.setValue(Math.min(
	            m_instances.numInstances() / 2, 4));*/
	        } else if (selectedP.equals("Yearly")) {
	          if (forecaster != null) {
	            forecaster.getTSLagMaker().setMaxLag(
	              Math.min(m_instances.numInstances() / 2, 5));
	          }
	         /* m_advancedConfig.m_maxLagSpinner.setValue(Math.min(
	            m_instances.numInstances() / 2, 5));*/
	        } else {
	          // default (<Unknown>)
	          if (forecaster != null) {
	            forecaster.getTSLagMaker().setMaxLag(
	              Math.min(m_instances.numInstances() / 2, 12));
	          }
	          /*m_advancedConfig.m_maxLagSpinner.setValue(Math.min(
	            m_instances.numInstances() / 2, 12));*/
	        }
	      }
	      //m_advancedConfig.getCustomizeDateDerivedPeriodics()
	      //高级配置 第三个选项卡
	      if (!false
	        && forecaster != null) {
	        // configure defaults based on the above periodicity
	        if (selectedP.equals("Hourly")) {
	          forecaster.getTSLagMaker().setAddAMIndicator(true);
	        } else if (selectedP.equals("Daily")) {
	          forecaster.getTSLagMaker().setAddDayOfWeek(true);
	          forecaster.getTSLagMaker().setAddWeekendIndicator(true);
	        } else if (selectedP.equals("Weekly")) {
	          forecaster.getTSLagMaker().setAddMonthOfYear(true);
	          forecaster.getTSLagMaker().setAddQuarterOfYear(true);
	        } else if (selectedP.equals("Monthly")) {
	          forecaster.getTSLagMaker().setAddMonthOfYear(true);
	          forecaster.getTSLagMaker().setAddQuarterOfYear(true);
	        }
	      }
	   // }
	  }
	class LogPrintStream extends PrintStream {
	    public LogPrintStream() {
	      super(System.out);
	    }
	    private void logStatusMessage(String string) {
	      if (m_log != null) {
	        m_log.statusMessage(string);
	        if (string.contains("WARNING") || string.contains("ERROR")) {
	          m_log.logMessage(string);
	        }
	      }
	    }

	    @Override
	    public void println(String string) {
	      System.out.println(string);
	      logStatusMessage(string);
	    }

	    @Override
	    public void println(Object obj) {
	      println(obj.toString());
	    }

	    @Override
	    public void print(String string) {
	      System.out.print(string);
	      logStatusMessage(string);
	    }

	    @Override
	    public void print(Object obj) {
	      print(obj.toString());
	    }
	  }
}
