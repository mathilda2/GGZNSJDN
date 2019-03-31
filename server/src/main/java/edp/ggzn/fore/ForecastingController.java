package edp.ggzn.fore;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.timeseries.WekaForecaster;
import weka.classifiers.trees.J48;
import weka.core.Drawable;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

@EnableAutoConfiguration
@Controller
public class ForecastingController {
	
	@Resource
	private  ForecastingService forecastingService;
	
	/**
	 * 跳转至预测页面
	 */
	@RequestMapping("/forecasting")
	public String forecasting() {
		return "forecasting";
	}
	
	
	
	/**
	 * 执行预测分析
	 * @param date
	 * @param attr
	 * @param zhouqi
	 * @return
	 * @throws Exception
	 */
	
	@RequestMapping("/start")
	@ResponseBody
	public  List<Object> forecastingStart(@Param("date") String date,@Param("attr") String attr, @Param("zhouqi") String zhouqi ) throws Exception{
		
		//新建一个list集合用来存放往前台传递的数值
		ArrayList<Object> listToWeb = new ArrayList<>();
		 try {
			 //读取arff文件
		    /*  Instances data = new Instances
		      (new BufferedReader
		    		  (new FileReader("D:\\666.arff")
		    				  ));*/
			 Instances data = forecastingService.queryAllData();
		      // 新建预测类实体
		      WekaForecaster forecaster = new WekaForecaster();
		      //设置要预测的目标
		      forecaster.setFieldsToForecast(attr);
		      // 使用底层默认分类器smoreg（svm）
		      //高斯过程回归
		      forecaster.setBaseForecaster(new MultilayerPerceptron());//GaussianProcesses
		      //设置时间戳（指定哪个数值为时间）
		      forecaster.getTSLagMaker().setTimeStampField("time"); 
		      forecaster.getTSLagMaker().setMinLag(1);
		      forecaster.getTSLagMaker().setMaxLag(12); // monthly data
		      //添加月份指示符字段
		      forecaster.getTSLagMaker().setAddMonthOfYear(true);
		      //添加季度指示符字段
//		      forecaster.getTSLagMaker().setAddQuarterOfYear(true);
		      //一个月中的一天
//		      forecaster.getTSLagMaker().setAddDayOfMonth(true);
		      //一个周中的一天
//		      forecaster.getTSLagMaker().setAddDayOfWeek(true);
		      //根据数据集创建模型
		      forecaster.buildForecaster(data, System.out);
		      //向预测类提供足够近期的历史数据
		      //迟滞期
		      forecaster.primeForecaster(data);
		     //对传入的字符串进行非空判断
		      int time = 0;
		      if(!"".equals(date)) {
		    	  time =Integer.parseInt(date);
		      }
		      //预测N个月
		      // training data
		      List<List<NumericPrediction>> forecast = forecaster.forecast(time, System.out);
              int num = data.numInstances();
		      listToWeb.add("总共为您分析了"+num+"条数据");
		      listToWeb.add("您要预测的属性值为："+attr);
		      listToWeb.add("根据您的选择为您预测了"+date+zhouqi+"的数据");
		      listToWeb.add("预测结果如下：");;
		      // 输出预测。
		      for (int i = 0; i < forecast.size(); i++) {
		        List<NumericPrediction> predsAtStep = forecast.get(i);
		        for (int j = 0; j < predsAtStep.size(); j++) {
		          NumericPrediction predForTarget = predsAtStep.get(j);
		          System.out.print("" + predForTarget.predicted() + " ");     
		          listToWeb.add(predForTarget.predicted());
		          System.out.println();
		        }
		 }
		 }
		     catch (Exception ex) {
		      ex.printStackTrace();
		    }
		 return listToWeb;
	}		
	
	

	
	
	/**
	 * 分类
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws Exception {
		Instances data = new Instances
			      (new BufferedReader
			    		  (new FileReader("C:\\Users\\Administrator\\Desktop\\克重B检验.arff")
			    				  ));
//		 Instances data = forecastingService.queryAllData();
		data.setClassIndex(data.numAttributes()-1);
		//-使用Filter进行预处理（可选）
		//如下删除指定列 
		       String[] options = new String[2]; 
		        options[0] = "-R";    // "range" 
		        options[1] = "1";    // first attribute 
		        Remove remove = new Remove();                // new instance of filter 
		        remove.setOptions(options);                  // set options 
		        // inform filter about dataset //**AFTER** setting options 
		        remove.setInputFormat(data);  
		        Instances newData = Filter.useFilter(data, remove);  // apply filter
		        //-选择某个分类器/聚类 （Classifiers/Clusterer）并训练之
		        //J48用以建立一个剪枝或不剪枝的c4.5决策树	
		        J48 m_classifier = new J48();
		        String optionss[]=new String[3];//训练参数数组
		        //使用reduced error pruning
		        optionss[0]="-R";
		        //叶子上的最小实例数	
		        optionss[1]="-M";
		        //set叶子上的最小实例数
		        optionss[2]="3";
		        //设置训练参数
		        m_classifier.setOptions(optionss);
		        m_classifier.buildClassifier(newData); //训练
		        //打印J48输出结果
		        System.out.println(m_classifier.toString());
		        //-Evaluating 评价
		        Evaluation eval = new Evaluation(newData); //构造评价器
		        eval.evaluateModel(m_classifier, newData);//用测试数据集来评价m_classifier
		        System.out.println(eval.toSummaryString("=== Summary ===\n",false));  //输出信息
		        System.out.println(eval.toClassDetailsString("===Detailed Accuracy By Class==="));
		        System.out.println(eval.toMatrixString("=== Confusion Matrix ===\n"));//Confusion Matrix
		        
		        String grph = ((Drawable) m_classifier).graph();		     
		        System.out.println("***********************");
		        System.out.println("---------------------");
		        System.out.println(grph);
		      
	}
}	