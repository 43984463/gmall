package com.sherlock.gmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 品牌
 * 
 * @author sherlockXue
 * @email xuesherlock@gmail.com
 * @date 2020-05-09 01:08:38
 */
@Data
@Accessors(chain=true)
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "请输入品牌名称")
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(message = "请输入品牌地址")
	@URL(message = "请输入一个合法的url")
	private String logo;
	/**
	 * 介绍
	 */
	@NotBlank(message = "请输入简介")
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotBlank
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "请输入排序")
	@Min(value = 0, message = "请输入大于或者等于0的整数")
	private Integer sort;

}
