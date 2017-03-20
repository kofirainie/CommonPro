package com.gpstrace.dlrc.provider;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

public class JsonProvider
{

	/**
	 * @根据数组对象通过反射来创建JSONArray
	 * @param array
	 * @数据对象
	 * @return
	 * @返回创建的JSONArray,JSONArray在使用时直接插入到JSONObject中
	 * @throws ArrayIndexOutOfBoundsException
	 * @throws IllegalArgumentException
	 * @throws JSONException
	 */
	public static JSONArray createArray(Object array)
			throws ArrayIndexOutOfBoundsException, IllegalArgumentException,
			JSONException
	{
		JSONArray jsonArray = null;
		if (array.getClass().isArray())
		{
			jsonArray = new JSONArray();
			for (int i = 0; i < Array.getLength(array); i++)
			{
				jsonArray.put(i, Array.get(array, i));
			}
		}

		return jsonArray;
	}

	/**
	 * @使用GSON生成JSON字符串
	 * @param model
	 * @要生成JSON格式字符串的实体对象
	 * @return
	 * @返回JSON字符串
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws JSONException
	 */
	public static String createJsonString(Object model)
	{
		Gson gson = new Gson();
		return gson.toJson(model);
	}

	/**
	 * @解析JSON字符串
	 * @param jsonStr
	 * @要解析的JSON字符串
	 * @param model
	 * @JSON格式所满足的实体
	 * @return
	 * @返回Object类型的实体
	 */
	public static Object parseJson(String jsonStr, Object model)
	{
		Gson gson = new Gson();
		return gson.fromJson(jsonStr, model.getClass());
	}

	/**
	 * @解析JSON字符串
	 * @param jsonStr
	 * @要解析的JSON字符串
	 * @param cls
	 * @JSON格式所满足的实体类型
	 * @return
	 * @返回实体类型的列表
	 */
	public static <T> T parseJson(String jsonStr, Class<T> cls)
	{
		T t = null;
		Gson gson = new Gson();
		t = gson.fromJson(jsonStr, cls);

		return t;
	}

	/**
	 * @解析JSON列表字符串
	 * @使用泛型解析的话会造成LinkedTreeMap无法强制类型转化的错误
	 * @param jsonStr
	 * @要解析的JSON字符串
	 ** @param model
	 * @JSON格式所满足的实体
	 * @return
	 * @返回model类型的列表
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws NoSuchFieldException
	 * @返回Object类型的实体列表
	 */
	public static List<Object> parseListJson(String jsonStr, Object model)
			throws IllegalArgumentException, IllegalAccessException,
			InstantiationException, NoSuchFieldException
	{
		List<Object> models = new ArrayList<Object>();
		Gson gson = new Gson();
		List<Object> objects = gson.fromJson(jsonStr,
				new TypeToken<List<Object>>()
				{
				}.getType());

		Map<String, Object> map = ReflectProvider.reflectModel(model);
		for (int i = 0; i < objects.size(); i++)
		{
			for (Entry<String, Object> entry : map.entrySet())
			{
				entry.setValue(((LinkedTreeMap<?, ?>) objects.get(i)).get(entry
						.getKey()));// 读取到的xml值，要赋值给实体
			}

			Object obj = model.getClass().newInstance();
			ReflectProvider.setModel(map, obj);// 检验map的引用类型传递是否会影响
			models.add(obj);
		}

		return models;
	}
}
