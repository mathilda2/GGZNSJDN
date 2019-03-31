package edp.ggzn.fore;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

/**
 * 数据实体类
 * @author Administrator
 *
 */
@Data
public class Product {
	private String id;
	private String name;
	private String price;
	private Date time;

}
