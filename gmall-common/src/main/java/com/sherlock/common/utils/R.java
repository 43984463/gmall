package com.sherlock.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author sherlockXue
 * @email xuesherlock@gmail.com
 * @date 2020-05-08 23:38:30
 *
 * R在初始设计时应该加上返回值的泛型规定
 *
 */
public class R<T> extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	public R() {
		put("code", 0);
		put("msg", "success");
	}

	public <T> T getData(String key, TypeReference<T> typeReference) {
		Object obj = get("key");

		String json = JSON.toJSONString(obj);

		T t = JSON.parseObject(json, typeReference);

		return t;
	}

	public <T> T getData(TypeReference<T> typeReference) {
		Object obj = get("data");

		String json = JSON.toJSONString(obj);

		T t = JSON.parseObject(json, typeReference);

		return t;
	}

	public R<T> setData(T data) {
		put("data", data);
		return this;
	}

	public static R error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
	}

	public static R error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
	}
	
	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}
	
	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}
	
	public static R ok() {
		return new R();
	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	public  Integer getCode() {
		return (Integer) this.get("code");
	}
}
