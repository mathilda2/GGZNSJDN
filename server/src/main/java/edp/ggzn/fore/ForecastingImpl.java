package edp.ggzn.fore;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import weka.core.Instances;
import weka.experiment.InstanceQuery;
@Service
public class ForecastingImpl implements ForecastingService{
	

	/**
	 * 查询所有weka数据
	 */
	@Override
	public Instances queryAllData() {
//		List<Product> list = forecastingDao.queryAllData();
		Instances instances = null;
		try {
			InstanceQuery query = new InstanceQuery();
			query.setDatabaseURL("jdbc:mysql://localhost:3306/ggzn");
			query.setUsername("root");
			query.setPassword("root");
			query.setQuery("select * from product");
			instances = query.retrieveInstances();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	/*	//将list集合转换为instance数据集
		ArrayList<Attribute> attr = new ArrayList<>();
		
		 *数据格式应该为numeric和date 类型   否则数据无效
		 	
			attr.add(new Attribute("id",(ArrayList<String>)null));
			attr.add(new Attribute("name",(ArrayList<String>)null));
			attr.add(new Attribute("price",(ArrayList<String>)null));
			attr.add(new Attribute("time"));
			Instances instances = new Instances("product", attr, 0);
	        instances.setClassIndex(instances.numAttributes() - 1);
	        //添加实例
	        for (Product product : list) {
        	Instance instance = new DenseInstance(attr.size());
			instance.setDataset(instances);
			instance.setValue(0,product.getId());
			instance.setValue(1,product.getName());
			instance.setValue(2, ""+product.getPrice());
			//此处抛异常(attribute niether nominal nor string)
			//原因：上面定义的attribute属性是date类型的 然而下面填充的是String  所以报错
			instance.setValue(3, product.getDate());
			instances.add(instance);
        }
       //将实例集转换为Arff文件
        ArffSaver saver = new ArffSaver();
        saver.setInstances(instances);
        try {
            saver.setFile(new File("D:\\ceshi.arff"));
            saver.writeBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
		return instances;
		
	}
	
	
	
	}
