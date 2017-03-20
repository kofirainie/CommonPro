package com.gpstrace.dlrc.provider;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ReflectProvider
{
	/**
	 * @反射实体字段(全String的实体)
	 * @param model
	 * @实体实例
	 * @return
	 * @返回实体MAP
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static Map<String, String> reflectStringModel(Object model)
			throws IllegalArgumentException, IllegalAccessException
	{
		Map<String, String> map = new HashMap<String, String>();

		Field[] field = model.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组

		for (int j = 0; j < field.length; j++)
		{
			map.put(field[j].getName(), field[j].get(model).toString());
		}
		return map;
	}

	/**
	 * @反射实体字段
	 * @param model
	 * @实体实例
	 * @return
	 * @返回实体MAP
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static Map<String, Object> reflectModel(Object model)
			throws IllegalArgumentException, IllegalAccessException
	{
		Map<String, Object> map = new HashMap<String, Object>();

		Field[] field = model.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组

		for (int j = 0; j < field.length; j++)
		{
			map.put(field[j].getName(), field[j].get(model));
		}
		return map;
	}

	/**
	 * @通过反射给实体字段赋值(全String的实体)
	 * @param map
	 * @赋值实体字段遍历的map
	 * @param model
	 * @赋值的实体
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public static void setStringModel(Map<String, String> map, Object model)
			throws IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException
	{
		for (Entry<String, String> entry : map.entrySet())
		{
			// 通过map读取实体字段，并对字段进行赋值
			model.getClass().getField(entry.getKey())
					.set(model, entry.getValue());
		}
	}

	/**
	 * @通过反射给实体字段赋值
	 * @param map
	 * @赋值实体字段遍历的map
	 * @param model
	 * @赋值的实体
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public static void setModel(Map<String, Object> map, Object model)
			throws IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException
	{
		for (Entry<String, Object> entry : map.entrySet())
		{
			// 通过map读取实体字段，并对字段进行赋值
			model.getClass().getField(entry.getKey())
					.set(model, entry.getValue());
		}
	}

}
