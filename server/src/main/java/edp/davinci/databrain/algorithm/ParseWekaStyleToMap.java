package edp.davinci.databrain.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseWekaStyleToMap {

	public static Map<String,Object> getFinalMap(String classifierStr) {
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
    	String data1 = "digraph J48Tree {\n" +
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
    	String[] strings = classifierStr.split("\n");
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
    	List<Map<String, Object>> sourceOrtarget = new ArrayList<Map<String, Object>>();
    	Map<String, Object>  link = new HashMap<String, Object>();
    	for(Map.Entry<String, Object> entry : middleOption.entrySet()){
    		Map<String, Object>  linksOption = new HashMap<String, Object>();
    		Map<String, Object>  sOrTMap = new HashMap<String, Object>();
    		String key = entry.getKey();
    		String value = (String) entry.getValue();
    		String sourceKey = key.split("->")[0].trim();
    		String targetKey = key.split("->")[1].trim();
    		String sourceValue = (String) mapOption.get(sourceKey);
    		String targetValue = (String) mapOption.get(targetKey);
    		sOrTMap.put("source", sourceKey);
    		sOrTMap.put("target", targetKey);
    		linksOption.put("source", sourceValue.replace("\"", ""));
    		linksOption.put("target", targetValue.replace("\"", ""));
    		Map<String, Object>  normalMap = new HashMap<String, Object>();
    		Map<String, Object>  normalcontentMap = new HashMap<String, Object>();
    		normalcontentMap.put("show", true);
    		normalcontentMap.put("formatter", value.replace("\"", ""));
    		normalcontentMap.put("fontSize", "11");
    		normalMap.put("normal", normalcontentMap);
    		linksOption.put("label", normalMap);
    		linksOptionlist.add(linksOption);
    		sourceOrtarget.add(sOrTMap);
    	}
    	link.put("link", linksOptionlist);
    	//获取层级d
    	int level = getLevel(sourceOrtarget);
    	//拼接x轴了么
    	int xWidth = 100;
    	int yHeight = 100;
    	List<List<Map<String,Object>>> list = new ArrayList<List<Map<String,Object>>>();
    	List<List<Map<String,Object>>> listlistZmap = new ArrayList<List<Map<String,Object>>>();
    	for(int j = level ; j >= 0 ; j--) {
	    	List<Map<String,Object>> listC = new ArrayList<Map<String,Object>>();
	    	List<Map<String,Object>> listZmap = new ArrayList<Map<String,Object>>();
	    	for(int i = 0 ; i < Math.pow(2, j) ; i++) {
	    		Map<String,Object> zMap = new HashMap<String,Object>();
	    		Map<String,Object> map = new LinkedHashMap<String,Object>();
	    		map.put("name", "");
    			map.put("x", i*xWidth);
    			map.put("y", (j+1)*yHeight);
	    		listC.add(map);
	    		if((i+1) % 2 == 0) {
	    			zMap.put("z",((i-1)*xWidth+i*xWidth)/2);
	    			listZmap.add(zMap);
	    		}
	    		System.out.println(j);
	    	}
	    	listlistZmap.add(listZmap);
	    	list.add(listC);
    	}
    	List<Map<String,Object>> maplist = listlistZmap.get(0);
    	list1.add(maplist);
    	List<List<Map<String,Object>>> sss = calculate(maplist);
    	for(int i = 1 ; i < list.size() ; i++) {
    		for(int j = 0 ; j < list.get(i).size(); j++) {
    			for(int k = 0 ; k < sss.size(); k++) {
    				for(int l = 0 ; l < sss.get(k).size(); l++) {
        				if(i-1==k && j==l) {
        					Map<String, Object> map = list.get(i).get(j);
        					Map<String, Object> map2 = sss.get(k).get(l);
        					map.put("x", map2.get("z"));
        				}
                	}	
            	}	
        	}
    	}
    	List<Map<String,Object>> nlist = new ArrayList<Map<String,Object>>();
    	Map<String,Object> mapC = new HashMap<String,Object>();
    	mapC.put("name", "N0");
    	mapC.put("idx", 0);
    	nlist.add(mapC);
    	Map<String,Object> treeMap = new HashMap<String,Object>();
		treeMap.put("name", "N0");
		treeMap.put("idx", 0);
		treeMap.put("level", 0);
		treelistMapTotal.add(treeMap);
    	divideMap(nlist,sourceOrtarget,0,0);
    	//开始替换
    	for(int i = 0 ; i < treelistMapTotal.size() ; i++) {
    		Map<String,Object> m = treelistMapTotal.get(i);
    		int idx = (int) m.get("idx");
    		int childlevel = (int) m.get("level");
    		String name = (String) m.get("name");
    		Map<String,Object> med = list.get(list.size()-childlevel-1).get(idx);
    		med.put("name", m.get("name"));
    	}
    	//开始删除多余没有name的
    	Iterator<List<Map<String, Object>>> it = list.iterator();
    	while(it.hasNext()){
    	    List<Map<String, Object>> x = it.next();
    	    Iterator<Map<String, Object>> itx = x.iterator();
    	    while(itx.hasNext()) {
    	    	Map<String, Object> next = itx.next();
    	    	String name = (String) next.get("name");
    	    	if("".equals(name)||name==null) {
    	    		itx.remove();
    	    	}
    	    }
    	}
    	//将二维数组放到一维中去
    	List<Map<String,Object>> li = new ArrayList<Map<String,Object>>();
		for(int j = 0 ; j < list.size() ; j++) {
		    for(int k = 0 ; k < list.get(j).size() ; k++) {
		    	Map<String,Object> map = list.get(j).get(k);
		    	String name = (String) map.get("name");
		    	String realName =(String) mapOption.get(name); 
		    	map.put("name",realName.replace("\"", ""));
		    	li.add(list.get(j).get(k));
		    }
		}
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("data", li);
		result.put("links", link);
		return result;
    }
	public static int getLevel(List<Map<String, Object>> sourceOrtarget) {
    	Set<String> set = new HashSet<String>();
    	for(Map<String, Object> map : sourceOrtarget) {
    		String source = (String) map.get("source");
    		set.add(source);
    	}
    	int length = set.size();
		return length;
	}
    static List<Map<String,Object>> treelistMapTotal = new ArrayList<Map<String,Object>>();
    public static void divideMap(List<Map<String, Object>> nlist, List<Map<String, Object>> sourceOrtarget, int idx,int level) {
    	List<Map<String,Object>> treelistMap = new ArrayList<Map<String,Object>>();
    	++level;
    	for(int i = 0 ; i < sourceOrtarget.size() ; i++) {
    		Map<String,Object> m = sourceOrtarget.get(i);
    		String source = (String) m.get("source");
    		String target = (String) m.get("target");
    		for(int k = 0 ; k < nlist.size() ; k++) {
    			if(source.equals(nlist.get(k).get("name"))) {
    				Map<String,Object> treeMap = new HashMap<String,Object>();
					treeMap.put("name", target);
					treeMap.put("idx", (Integer)nlist.get(k).get("idx")*2+idx);
					treeMap.put("level", level);
					treelistMap.add(treeMap);
					treelistMapTotal.add(treeMap);
					idx++;
    			}
			}
    	}
		if(treelistMap.size() > 0) {
			divideMap(treelistMap, sourceOrtarget,idx=0,level);
		}
	}
	static List<List<Map<String,Object>>> list1 = new ArrayList<List<Map<String,Object>>>();
    public static List<List<Map<String,Object>>> calculate(List<Map<String, Object>> maplist) {
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	 for(int i = 0 ; i < maplist.size() ; i++) {
    		if(i % 2 == 0 && maplist.size()>1) {
    			Map<String,Object> tMap = new HashMap<String,Object>();
    			Map<String, Object> firstM = maplist.get(i);
        		int a = (int) firstM.get("z");
        		Map<String, Object> firstN = maplist.get(i+1);
        		int b = (int) firstN.get("z");
        		tMap.put("z", (a+b)/2);
        		list.add(tMap);
    		}
    	}
    	if(list.size() >= 1) {
    		list1.add(list);
    		calculate(list);
    	} 
		return list1;
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
            	/*if(!valList.contains(m.group())){
            	valList.add(m.group());
            	}*/
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
