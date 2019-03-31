package edp.ggzn.fore;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import weka.core.Instances;



public interface ForecastingService {
		

	
    
    /**
     * 查找所有数据
     */
    Instances queryAllData();


}
