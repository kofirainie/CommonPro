package com.gpstrace.dlrc.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;

/**
 * @Emoji表情解析类
 * @author YunZ
 * 
 */
public class EmojiParser
{
	// region fields

	private static final String TAG = EmojiParser.class.getSimpleName();
	private HashMap<List<Integer>, String> convertMap = new HashMap<List<Integer>, String>();
	private HashMap<String, ArrayList<String>> emoMap = new HashMap<String, ArrayList<String>>();
	private static EmojiParser mParser;

	// endregion

	// region methods

	/**
	 * @构造函数
	 * @param mContext
	 */
	private EmojiParser(Context mContext)
	{
		readMap(mContext);
	}

	/**
	 * @单件方法
	 * @param mContext
	 * @return
	 */
	public static EmojiParser getInstance(Context mContext)
	{
		if (mParser == null)
		{
			mParser = new EmojiParser(mContext);
		}
		return mParser;
	}

	/**
	 * @获取Emoji表情映射表
	 * @return
	 */
	public HashMap<String, ArrayList<String>> getEmoMap()
	{
		return emoMap;
	}

	/**
	 * @读取Emoji表情包
	 * @param mContext
	 */
	public void readMap(Context mContext)
	{
		if (convertMap == null || convertMap.size() == 0)
		{
			convertMap = new HashMap<List<Integer>, String>();
			XmlPullParser xmlpull = null;
			String fromAttr = null;
			String key = null;
			ArrayList<String> emos = null;
			try
			{
				XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
				xmlpull = xppf.newPullParser();
				InputStream stream = mContext.getAssets().open("emoji.xml");
				xmlpull.setInput(stream, "UTF-8");
				int eventCode = xmlpull.getEventType();
				while (eventCode != XmlPullParser.END_DOCUMENT)
				{
					switch (eventCode)
					{
					case XmlPullParser.START_DOCUMENT:
					{
						break;
					}
					case XmlPullParser.START_TAG:
					{
						if (xmlpull.getName().equals("key"))
						{
							emos = new ArrayList<String>();
							key = xmlpull.nextText();
						}
						if (xmlpull.getName().equals("e"))
						{
							fromAttr = xmlpull.nextText();
							emos.add(fromAttr);
							List<Integer> fromCodePoints = new ArrayList<Integer>();
							if (fromAttr.length() > 6)
							{
								String[] froms = fromAttr.split("\\_");
								for (String part : froms)
								{
									fromCodePoints.add(Integer.parseInt(part,
											16));
								}
							} else
							{
								fromCodePoints.add(Integer.parseInt(fromAttr,
										16));
							}
							convertMap.put(fromCodePoints, fromAttr);
						}
						break;
					}
					case XmlPullParser.END_TAG:
					{
						if (xmlpull.getName().equals("dict"))
						{
							emoMap.put(key, emos);
						}
						break;
					}
					case XmlPullParser.END_DOCUMENT:
					{
						Log.d("", "parse emoji complete");
						break;
					}
					}
					eventCode = xmlpull.next();
				}
			} catch (Exception e)
			{
				Log.e(TAG, e.toString(), e);
			}
		}
	}

	/**
	 * @解析带有表情的字符串
	 * @将字符串转化为unicode表情字符串，在表情部分加上标识,方便下一步转化成表情图片
	 * @param input
	 * @return
	 */
	public String parseEmojiStr(String input)
	{
		if (input == null || input.length() <= 0)
		{
			return "";
		}
		StringBuilder result = new StringBuilder();
		int[] codePoints = toCodePointArray(input);
		List<Integer> key = null;
		for (int i = 0; i < codePoints.length; i++)
		{
			key = new ArrayList<Integer>();
			if (i + 1 < codePoints.length)
			{
				key.add(codePoints[i]);
				key.add(codePoints[i + 1]);
				if (convertMap.containsKey(key))
				{
					String value = convertMap.get(key);
					if (value != null)
					{
						result.append("[e]" + value + "[/e]");
					}
					i++;
					continue;
				}
			}
			key.clear();
			key.add(codePoints[i]);
			if (convertMap.containsKey(key))
			{
				String value = convertMap.get(key);
				if (value != null)
				{
					result.append("[e]" + value + "[/e]");
				}
				continue;
			}
			result.append(Character.toChars(codePoints[i]));
		}
		return result.toString();
	}

	/**
	 * @字符串转化为字节数组
	 * @param str
	 * @return
	 */
	private int[] toCodePointArray(String str)
	{
		char[] ach = str.toCharArray();
		int len = ach.length;
		int[] acp = new int[Character.codePointCount(ach, 0, len)];
		int j = 0;
		for (int i = 0, cp; i < len; i += Character.charCount(cp))
		{
			cp = Character.codePointAt(ach, i);
			acp[j++] = cp;
		}
		return acp;
	}

	/**
	 * @字符串转化为Emoji表情字符串
	 * @param input
	 * @return
	 */
	public String convertToEmojiStr(String input)
	{
		if (input == null || input.length() <= 0)
		{
			return "";
		}
		StringBuilder result = new StringBuilder();
		int[] codePoints = toCodePointArray(input);
		List<Integer> key = null;
		for (int i = 0; i < codePoints.length; i++)
		{
			key = new ArrayList<Integer>();
			if (i + 1 < codePoints.length)
			{
				key.add(codePoints[i]);
				key.add(codePoints[i + 1]);
				if (convertMap.containsKey(key))
				{
					String value = convertMap.get(key);
					if (value != null)
					{
						result.append("[表情]");
					}
					i++;
					continue;
				}
			}
			key.clear();
			key.add(codePoints[i]);
			if (convertMap.containsKey(key))
			{
				String value = convertMap.get(key);
				if (value != null)
				{
					result.append("[表情]");
				}
				continue;
			}
			result.append(Character.toChars(codePoints[i]));
		}
		return result.toString();
	}

	/**
	 * @带有Emoji表情的字符串
	 * @param context
	 * @param imgId
	 * @param spannableString
	 * @return
	 */
	public SpannableString addFace(Context context, int imgId,
			String spannableString)
	{
		if (TextUtils.isEmpty(spannableString))
		{
			return null;
		}
		Drawable drawable = context.getResources().getDrawable(imgId);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		ImageSpan imageSpan = new ImageSpan(drawable, spannableString);
		SpannableString spannable = new SpannableString(spannableString);
		spannable.setSpan(imageSpan, 0, spannableString.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannable;
	}

	// endregion
}
