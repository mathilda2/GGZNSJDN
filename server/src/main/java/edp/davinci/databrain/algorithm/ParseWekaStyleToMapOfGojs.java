package edp.davinci.databrain.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseWekaStyleToMapOfGojs {
	public static Map<String, Object> getFinalMap(String gostr) {
		String data = "digraph J48Tree {\n" +
    			"N0 [label=\"PVC烤箱（三点）\" ]\n" +
    			"N0->N1 [label=\"<= 206.6\"]\n" +
    			"N1 [label=\"合格 (81.0)\" shape=box style=filled ]\n" +
    			"N0->N2 [label=\"> 206.6\"]\n" +
    			"N2 [label=\"PVC烤箱（八点）\" ]\n" +
    			"N2->N3 [label=\"<= 183.7\"]\n" +
    			"N3 [label=\"PVC烤箱（三点）\" ]\n" +
    			"N3->N4 [label=\"<= 208.5\"]\n" +
    			"N4 [label=\"PVC烤箱（七点）\" ]\n" +
    			"N4->N5 [label=\"<= 212.1\"]\n" +
    			"N5 [label=\"PU烤箱（九点）\" ]\n" +
    			"N5->N6 [label=\"<= 102.2\"]\n" +
    			"N6 [label=\"PU烤箱（十点）\" ]\n" +
    			"N6->N7 [label=\"<= 99.8\"]\n" +
    			"N7 [label=\"PVC烤箱（四点）\" ]\n" +
    			"N7->N8 [label=\"<= 204\"]\n" +
    			"N8 [label=\"合格 (180.0/36.0)\" shape=box style=filled ]\n" +
    			"N7->N9 [label=\"> 204\"]\n" +
    			"N9 [label=\"PU烤箱（九点）\" ]\n" +
    			"N9->N10 [label=\"<= 101.3\"]\n" +
    			"N10 [label=\"合格 (620.0/241.0)\" shape=box style=filled ]\n" +
    			"N9->N11 [label=\"> 101.3\"]\n" +
    			"N11 [label=\"PVC烤箱（四点）\" ]\n" +
    			"N11->N12 [label=\"<= 204.2\"]\n" +
    			"N12 [label=\"合格 (15.0)\" shape=box style=filled ]\n" +
    			"N11->N13 [label=\"> 204.2\"]\n" +
    			"N13 [label=\"PU烤箱（九点）\" ]\n" +
    			"N13->N14 [label=\"<= 101.4\"]\n" +
    			"N14 [label=\"合格 (38.0/7.0)\" shape=box style=filled ]\n" +
    			"N13->N15 [label=\"> 101.4\"]\n" +
    			"N15 [label=\"PU烤箱（九点）\" ]\n" +
    			"N15->N16 [label=\"<= 101.6\"]\n" +
    			"N16 [label=\"PVC烤箱（二点）\" ]\n" +
    			"N16->N17 [label=\"<= 199.8\"]\n" +
    			"N17 [label=\"PVC烤箱（五点）\" ]\n" +
    			"N17->N18 [label=\"<= 213\"]\n" +
    			"N18 [label=\"PVC烤箱（一点）\" ]\n" +
    			"N18->N19 [label=\"<= 177.524\"]\n" +
    			"N19 [label=\"合格 (3.0/1.0)\" shape=box style=filled ]\n" +
    			"N18->N20 [label=\"> 177.524\"]\n" +
    			"N20 [label=\"不合格 (2.0)\" shape=box style=filled ]\n" +
    			"N17->N21 [label=\"> 213\"]\n" +
    			"N21 [label=\"合格 (3.0/1.0)\" shape=box style=filled ]\n" +
    			"N16->N22 [label=\"> 199.8\"]\n" +
    			"N22 [label=\"PU料槽（十二点）\" ]\n" +
    			"N22->N23 [label=\"<= 52.9\"]\n" +
    			"N23 [label=\"PU烤箱（十点）\" ]\n" +
    			"N23->N24 [label=\"<= 99.3\"]\n" +
    			"N24 [label=\"合格 (5.0/2.0)\" shape=box style=filled ]\n" +
    			"N23->N25 [label=\"> 99.3\"]\n" +
    			"N25 [label=\"不合格 (3.0/1.0)\" shape=box style=filled ]\n" +
    			"N22->N26 [label=\"> 52.9\"]\n" +
    			"N26 [label=\"PVC烤箱（一点）\" ]\n" +
    			"N26->N27 [label=\"<= 176.4\"]\n" +
    			"N27 [label=\"合格 (11.0/4.0)\" shape=box style=filled ]\n" +
    			"N26->N28 [label=\"> 176.4\"]\n" +
    			"N28 [label=\"PVC烤箱（八点）\" ]\n" +
    			"N28->N29 [label=\"<= 182.4\"]\n" +
    			"N29 [label=\"合格 (2.0)\" shape=box style=filled ]\n" +
    			"N28->N30 [label=\"> 182.4\"]\n" +
    			"N30 [label=\"不合格 (2.0)\" shape=box style=filled ]\n" +
    			"N15->N31 [label=\"> 101.6\"]\n" +
    			"N31 [label=\"PVC烤箱（八点）\" ]\n" +
    			"N31->N32 [label=\"<= 182.6\"]\n" +
    			"N32 [label=\"合格 (3.0)\" shape=box style=filled ]\n" +
    			"N31->N33 [label=\"> 182.6\"]\n" +
    			"N33 [label=\"PU烤箱（十点）\" ]\n" +
    			"N33->N34 [label=\"<= 99.7\"]\n" +
    			"N34 [label=\"合格 (5.0/2.0)\" shape=box style=filled ]\n" +
    			"N33->N35 [label=\"> 99.7\"]\n" +
    			"N35 [label=\"不合格 (3.0/1.0)\" shape=box style=filled ]\n" +
    			"N6->N36 [label=\"> 99.8\"]\n" +
    			"N36 [label=\"合格 (886.0/171.0)\" shape=box style=filled ]\n" +
    			"N5->N37 [label=\"> 102.2\"]\n" +
    			"N37 [label=\"合格 (135.0)\" shape=box style=filled ]\n" +
    			"N4->N38 [label=\"> 212.1\"]\n" +
    			"N38 [label=\"PU烤箱（十点）\" ]\n" +
    			"N38->N39 [label=\"<= 101.6\"]\n" +
    			"N39 [label=\"PU料槽（十二点）\" ]\n" +
    			"N39->N40 [label=\"<= 55.2\"]\n" +
    			"N40 [label=\"合格 (79.0/27.0)\" shape=box style=filled ]\n" +
    			"N39->N41 [label=\"> 55.2\"]\n" +
    			"N41 [label=\"PVC烤箱（五点）\" ]\n" +
    			"N41->N42 [label=\"<= 213\"]\n" +
    			"N42 [label=\"合格 (17.0/5.0)\" shape=box style=filled ]\n" +
    			"N41->N43 [label=\"> 213\"]\n" +
    			"N43 [label=\"不合格 (265.0/106.0)\" shape=box style=filled ]\n" +
    			"N38->N44 [label=\"> 101.6\"]\n" +
    			"N44 [label=\"合格 (200.0/30.0)\" shape=box style=filled ]\n" +
    			"N3->N45 [label=\"> 208.5\"]\n" +
    			"N45 [label=\"PVC烤箱（六点）\" ]\n" +
    			"N45->N46 [label=\"<= 218.8\"]\n" +
    			"N46 [label=\"不合格 (180.0/72.0)\" shape=box style=filled ]\n" +
    			"N45->N47 [label=\"> 218.8\"]\n" +
    			"N47 [label=\"合格 (60.0/12.0)\" shape=box style=filled ]\n" +
    			"N2->N48 [label=\"> 183.7\"]\n" +
    			"N48 [label=\"合格 (83.0/3.0)\" shape=box style=filled ]\n" +
    			"}";
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
    	List<Map<String, Object>> linksOptionlist = new LinkedList<Map<String, Object>>();
    	Map<String, Object>  link = new HashMap<String, Object>();
    	Map<String, Object>  goMapT = new LinkedHashMap<String, Object>();
    	String text = (String) mapOption.get("N0");
    	goMapT.put("key","N0" ); 
    	goMapT.put("parent", "");
    	goMapT.put("linkText", "");
    	goMapT.put("text", text.replace("\"", ""));
    	linksOptionlist.add(goMapT);
    	for(Map.Entry<String, Object> entry : middleOption.entrySet()){
    		Map<String, Object>  goOption = new LinkedHashMap<String, Object>();
    		String key = entry.getKey();
    		String value = (String) entry.getValue();
    		String sourceKey = key.split("->")[0].trim();
    		String targetKey = key.split("->")[1].trim();
    		String sourceValue = (String) mapOption.get(sourceKey);
    		String targetValue = (String) mapOption.get(targetKey);
    		String replaceSourceValue = sourceValue.replace("\"", "");
    		String replaceTargetValue = targetValue.replace("\"", "");
    		String replaceValue = value.replace("\"", "");
    		goOption.put("key", targetKey);
    		goOption.put("parent", sourceKey);
    		goOption.put("linkText", replaceValue);
    		goOption.put("text", replaceTargetValue);
    		linksOptionlist.add(goOption);
    	}
    	Map<String, Object>  nodeMap = new LinkedHashMap<String, Object>();
    	nodeMap.put("nodeDataArray", linksOptionlist);
    	link.put("gojs", nodeMap);
    	System.out.println(link);
    	return link;
	}
	public static Map<String, Object> getMapOption(List<String> namelist){
    	List<String> valList = new LinkedList<String>();
    	List<String> nameList = new LinkedList<String>();
    	Map<String, Object> map = new LinkedHashMap<String, Object>();
    	for(String name : namelist){
    		nameList.add(name.split("\\[")[0].trim());
    		String valstr = name.split("\\[")[1];
    		Pattern p=Pattern.compile("\"(.*?)\"");
            Matcher m=p.matcher(valstr);
            while(m.find()){
            	valList.add(m.group());
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
