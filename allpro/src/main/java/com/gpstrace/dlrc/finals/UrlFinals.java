package com.gpstrace.dlrc.finals;

/**
 * @author kofirainie
 * @function 
 * @date 日期
 * @param 参数
 */
public class UrlFinals {
	//以下为生产接口URL
//	public final static String URL_HEADER = "http://schoopia.com";//生产服务器
//	public final static String TONGXUEPAI_URL_HEADER = "http://schoopia.com:90";//同学派服务器
//	public final static String OSS_URL_HEADER = "http://b-chiron.oss-cn-beijing.aliyuncs.com"; // oss测试服务器
	
	//以下为测试接口URL
	public final static String URL_HEADER = "http://test.schoopia.com";// 内网服务器
	public final static String TONGXUEPAI_URL_HEADER = "http://test.schoopia.com:91"; // 同学派服务器	
	public final static String OSS_URL_HEADER = "http://chirondlrc.oss-cn-shenzhen.aliyuncs.com"; // oss测试服务器

	
	public static final String SPLASH_URL_STR = URL_HEADER
			+ "/system/application-config/list";// 闪屏页获取URL
	
	public static final String SPLASH_IMAGE_URL_STR = URL_HEADER
			+ "/img/save/%1$d/%2$d.%3$s";// 闪屏照片获取URL字符串
	
	public static final String SPLASH_ARTICLE_URL_STR = OSS_URL_HEADER
			+ "/art/v1/%1$d/%2$d.json";// 闪屏文章获取URL
	
	public static final String SPLASH_TEXT_URL_STR = OSS_URL_HEADER
			+ "/conf/guang/magazine_text.json";// 获取动态文本
}
