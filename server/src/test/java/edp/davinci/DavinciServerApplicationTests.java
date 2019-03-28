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

package edp.davinci;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DavinciServerApplicationTests {

    @Test
    public void contextLoads() {
    	String username1 = "zhangsan@creditease.cn";
    	String username2 = "lisi@CREDITEASE.cn";
    	String domainName = "@creditease.cn";
    	System.out.println(username1.replaceAll("(?i)" + domainName, ""));
    	System.out.println(username2.replaceAll("(?i)" + domainName, ""));
    }

    public static void main(String[] args) {
    	String data = "digraph J48Tree {\n" +
    			"N0 [label=\"PVC烤箱（七点）\" ]\n" +
    			"N0->N1 [label=\"<= 212.1\"]\n" +
    			"N1 [label=\"合格 (1975.0/473.0)\" shape=box style=filled ]\n" +
    			"N0->N2 [label=\"> 212.1\"]\n" +
    			"N2 [label=\"PU烤箱（十点）\" ]\n" +
    			"N2->N3 [label=\"<= 101.6\"]\n" +
    			"N3 [label=\"PU料槽（十二点）\" ]\n" +
    			"N3->N4 [label=\"<= 55.2\"]\n" +
    			"N4 [label=\"合格 (100.0/27.0)\" shape=box style=filled ]\n" +
    			"N3->N5 [label=\"> 55.2\"]\n" +
    			"N5 [label=\"PVC烤箱（五点）\" ]\n" +
    			"N5->N6 [label=\"<= 213\"]\n" +
    			"N6 [label=\"合格 (17.0/5.0)\" shape=box style=filled ]\n" +
    			"N5->N7 [label=\"> 213\"]\n" +
    			"N7 [label=\"不合格 (445.0/178.0)\" shape=box style=filled ]\n" +
    			"N2->N8 [label=\"> 101.6\"]\n" +
    			"N8 [label=\"合格 (344.0/45.0)\" shape=box style=filled ]\n" +
    			"}" ;
    	List<String> namelist = new ArrayList<String>();
    	List<String> middlelist = new ArrayList<String>();
    	List<String> otherlist = new ArrayList<String>();
    	String[] strings = data.split("\n");
    	otherlist.add(strings[0]);
    	otherlist.add(strings[strings.length-1]);
    	for(String str:strings){
    		if(!otherlist.contains(str)){
    			if(str.contains("->")){
        			middlelist.add(str);
        		}else{
        			namelist.add(str);
        		}
    		}
    	}
    	Map<String, Object>  mapOption = getMapOption(namelist);
    	Map<String, Object>  middleOption = getMapOption(middlelist);
    	List<Map<String, Object>> linksOptionlist = new ArrayList<Map<String, Object>>();
    	Map<String, Object>  link = new HashMap<String, Object>();
    	for(Map.Entry<String, Object> entry : middleOption.entrySet()){
    		Map<String, Object>  linksOption = new HashMap<String, Object>();
    		String key = entry.getKey();
    		String value = (String) entry.getValue();
    		String sourceKey = key.split("->")[0].trim();
    		String targetKey = key.split("->")[1].trim();
    		String sourceValue = (String) mapOption.get(sourceKey);
    		String targetValue = (String) mapOption.get(targetKey);
    		linksOption.put("source", sourceValue);
    		linksOption.put("target", targetValue);
    		Map<String, Object>  normalMap = new HashMap<String, Object>();
    		Map<String, Object>  normalcontentMap = new HashMap<String, Object>();
    		normalcontentMap.put("show", true);
    		normalcontentMap.put("formatter", value);
    		normalcontentMap.put("fontSize", "11");
    		normalMap.put("normal", normalcontentMap);
    		linksOption.put("label", normalMap);
    		linksOptionlist.add(linksOption);
    	}
    	link.put("link", linksOptionlist);
    }
    
    public static Map<String, Object> getMapOption(List<String> namelist){
    	List<String> valList = new LinkedList<String>();
    	List<String> nameList = new LinkedList<String>();
    	Map<String, Object> map = new HashMap<String, Object>();
    	for(String name : namelist){
    		nameList.add(name.split("\\[")[0].trim());
    		String valstr = name.split("\\[")[1];
    		Pattern p=Pattern.compile("\"(.*?)\"");
            Matcher m=p.matcher(valstr);
            while(m.find()){
            	if(!valList.contains(m.group())){
            		valList.add(m.group());
            	}
            }
    	}
    	for(int i = 0 ; i < valList.size() ; i++){
    		for(int j = 0 ; j < nameList.size() ; j++){
    			if(i==j){
    				map.put(nameList.get(j),valList.get(i));
    			}
    		}
    	}
		return map;
    }
    
    
}
