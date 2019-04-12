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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import edp.core.annotation.CurrentUser;
import edp.core.enums.HttpCodeEnum;
import edp.davinci.common.controller.BaseController;
import edp.davinci.core.common.Constants;
import edp.davinci.core.common.ResultMap;
import edp.davinci.databrain.algorithm.ParseWekaStyleToMap;
import edp.davinci.databrain.algorithm.ParseWekaStyleToMapOfGojs;
import edp.davinci.model.User;
import edp.davinci.service.SourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Drawable;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

@Api(value = "/equipment", tags = "forecasting", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@ApiResponses(@ApiResponse(code = 404, message = "forecasting not found"))
@Slf4j
@RestController
@RequestMapping(value = Constants.BASE_API_PATH + "/equipment", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class EquipmentController extends BaseController {

    @Autowired
    private SourceService sourceService;

    @ApiOperation(value = "get equipment")
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

    @ResponseBody
    @GetMapping("/startClassifier")
	public ResponseEntity startClassifier(@RequestParam(required=false,value="projectId") Long projectId,
						            @ApiIgnore @CurrentUser User user,
						            HttpServletRequest request) throws Exception{
    	//String temp = getFirstStyle();
    	ResponseEntity<Map<String, Object>> temp = getSuperStyle();
        
		return temp;
	}

    public ResponseEntity<Map<String, Object>> getSuperStyle() throws Exception {
    	Instances data = new Instances
                (new BufferedReader
                    (new FileReader("E:\\gogetter\\五分厂数据输入\\烤箱多对一\\蓝帆 - 副本echart.arff")
                        ));
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
                String optionss[]=new String[4];//训练参数数组
                //使用reduced error pruning
                optionss[0]="-C";
                //叶子上的最小实例数  
                optionss[1]="0.25";
                //set叶子上的最小实例数
                optionss[2]="-M";
                optionss[3]="2";
                //设置训练参数
                m_classifier.setOptions(optionss);
                m_classifier.buildClassifier(data); //训练  
                //-Evaluating 评价
                Evaluation eval = new Evaluation(data); //构造评价器
                eval.evaluateModel(m_classifier, newData);//用测试数据集来评价m_classifier
                String resultstr = eval.toSummaryString("=== Summary ===\n",false)+"\n"+
                				   eval.toClassDetailsString("===Detailed Accuracy By Class===")+"\n"+
                				   eval.toMatrixString("=== Confusion Matrix ===\n");
                System.out.println(eval.toCumulativeMarginDistributionString());
                System.out.println(eval.toSummaryString("=== Summary ===\n",false));  //输出信息
                System.out.println(eval.toClassDetailsString("===Detailed Accuracy By Class==="));
                System.out.println(eval.toMatrixString("=== Confusion Matrix ===\n"));//Confusion Matrix
                String grph = ((Drawable) m_classifier).graph();
                Map<String, Object> finalMap = ParseWekaStyleToMapOfGojs.getFinalMap(grph);
                finalMap.put("resultClassifier", resultstr);
		return ResponseEntity.status(200).body(finalMap);
	}

}
