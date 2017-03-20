package com.gpstrace.dlrc.view;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gpstrace.dlrc.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;

public class EmojiUtil
{
	// region fields

	private static final String TAG = EmojiUtil.class.getSimpleName();

	private static final String REGEX_STR = "\\[e\\](.*?)\\[/e\\]";

	// endregion

	// region methods

	private static String decodeUnicode(String theString)
	{
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;)
		{
			aChar = theString.charAt(x++);
			if (aChar == '\\')
			{
				aChar = theString.charAt(x++);
				if (aChar == 'u')
				{
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++)
					{
						aChar = theString.charAt(x++);
						switch (aChar)
						{
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}

					}
					outBuffer.append((char) value);
				} else
				{
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}

	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 */
	public static void dealExpression(Context context,
			SpannableString spannableString, Pattern patten, int start)
			throws Exception
	{
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find())
		{
			String key = matcher.group();
			Log.e("Key", key);
			if (matcher.start() < start)
			{
				continue;
			}

			Field field = null;

			try
			{
				field = R.drawable.class.getDeclaredField("emoji_"
						+ key.substring(key.indexOf("]") + 1,
								key.lastIndexOf("[")));
			} catch (Exception e)
			{
				field = R.drawable.class.getDeclaredField("emoji_common");
			}

			int resId = Integer.parseInt(field.get(null).toString());
			if (resId != 0)
			{
				// Bitmap bitmap = BitmapFactory.decodeResource(
				// context.getResources(), resId);
				// ImageSpan imageSpan = new ImageSpan(bitmap);
				Drawable drawable = context.getResources().getDrawable(resId);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 2,
						drawable.getIntrinsicHeight() / 2);

				VerticalImageSpan imageSpan = new VerticalImageSpan(drawable);
				int end = matcher.start() + key.length();
				spannableString.setSpan(imageSpan, matcher.start(), end,
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				if (end < spannableString.length())
				{
					dealExpression(context, spannableString, patten, end);
				}
				break;
			}
		}
	}

	/**
	 * @desc <pre>
	 * 解析Unicode字符串,并将其中的表情字符串替换成表情图片
	 * </pre>
	 * @author Weiliang Hu
	 * @date 2013-12-17
	 * @param context
	 * @param str
	 * @return
	 */
	public static SpannableString getExpressionString(Context context,
			String str)
	{
		SpannableString spannableString = new SpannableString(str);
		Pattern sinaPatten = Pattern.compile(REGEX_STR,
				Pattern.CASE_INSENSITIVE);
		// Pattern sinaPatten = Pattern
		// .compile(
		// "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
		// Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
		try
		{
			dealExpression(context, spannableString, sinaPatten, 0);
		} catch (Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
		return spannableString;
	}

	/**
	 * @desc <pre>
	 * 若字符串信息中含有表情图片，则需要完全转成unicode字符字符串
	 * </pre>
	 * @author Weiliang Hu
	 * @date 2013-12-17
	 * @param cs
	 * @param mContext
	 * @return
	 */
	public static String convertToMsg(CharSequence cs, Context mContext)
	{
		SpannableStringBuilder ssb = new SpannableStringBuilder(cs);
		ImageSpan[] spans = ssb.getSpans(0, cs.length(), ImageSpan.class);
		for (int i = 0; i < spans.length; i++)
		{
			ImageSpan span = spans[i];
			String c = span.getSource();
			int a = ssb.getSpanStart(span);
			int b = ssb.getSpanEnd(span);
			if (c.contains("["))
			{
				ssb.replace(a, b, convertUnicode(c));
			}
		}
		ssb.clearSpans();
		return ssb.toString();
	}

	/**
	 * 转成unicode字符字符串
	 * 
	 * @param emo
	 * @return
	 */
	private static String convertUnicode(String emo)
	{
		emo = emo.substring(1, emo.length() - 1);
		if (emo.length() < 6)
		{
			return new String(Character.toChars(Integer.parseInt(emo, 16)));
		}
		String[] emos = emo.split("_");
		char[] char0 = Character.toChars(Integer.parseInt(emos[0], 16));
		char[] char1 = Character.toChars(Integer.parseInt(emos[1], 16));
		char[] emoji = new char[char0.length + char1.length];
		for (int i = 0; i < char0.length; i++)
		{
			emoji[i] = char0[i];
		}
		for (int i = char0.length; i < emoji.length; i++)
		{
			emoji[i] = char1[i - char0.length];
		}
		return new String(emoji);
	}

	// endregion
}
