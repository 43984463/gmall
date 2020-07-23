package com.sherlock.gmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sherlock.common.valid.AddGroup;
import com.sherlock.common.valid.ListValue;
import com.sherlock.common.valid.UpdateGroup;
import com.sherlock.common.valid.UpdateStatusGroup;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
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
	@NotNull(message = "修改时必须输入品牌ID", groups = {UpdateGroup.class, UpdateStatusGroup.class})
	@Null(message = "新增时不能输入品牌ID", groups = AddGroup.class)
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "请输入品牌名称", groups = AddGroup.class)
	private String name;
	/**
	 * 品牌logo
	 */
	@NotBlank(message = "请输入品牌地址", groups = AddGroup.class)
	@URL(message = "请输入一个合法的url", groups = {AddGroup.class, UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	@NotBlank(message = "请输入简介", groups = AddGroup.class)
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@ListValue(values = {0,1}, groups = {AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotBlank(message = "请输入检索首字母", groups = AddGroup.class)
	@Pattern(regexp = "^[a-zA-Z]$", message = "请输入检索首字母", groups = {AddGroup.class, UpdateGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "请输入排序", groups = AddGroup.class)
	@Min(value = 0, groups = {AddGroup.class, UpdateGroup.class})
	private Integer sort;

}
