package com.gpstrace.dlrc.provider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;


/**
 * @author YunZ
 * @文件上传类
 * @可以使用封装的第三方类，也可以自己去实现
 * @本类为自己实现，方便与服务器端对接
 * @本类中可以实现单文件的上传，多文件上传的部分需要参考进行修改
 * @多文件上传需要注意的地方是防止OOM
 */
public class FileUploader {
	// region fields

	private static final String TAG = "FileUploader";

	private static final String BOUNDARY = UUID.randomUUID().toString();// 边界标识随机生成
	private static final String PREFIX = "--";
	private static final String LINE_END = "\r\n";
	private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型
	private static final String CHARSET = "utf-8"; // 设置编码
	private int readTimeOut = 10 * 1000; // 读取超时
	private int connectTimeout = 10 * 1000; // 超时时间
	private int requestTime = 0;// 请求使用多长时间

	public static final int UPLOAD_SUCCESS_CODE = 1;// 上传成功
	public static final int UPLOAD_ARG_ERROR_CODE = 2;// 上传参数错误
	public static final int UPLOAD_FILE_NOT_EXISTS_CODE = 3;// 文件不存在
	public static final int UPLOAD_SERVER_ERROR_CODE = 4;// 服务器出错
	public static final int UPLOAD_FAILED_CODE = 5;// 上传失败
	public static final int UPLOAD_ERROR_CODE = 6;// 上传出错

	protected static final int WHAT_TO_UPLOAD = 1;
	protected static final int WHAT_UPLOAD_DONE = 2;

	private static FileUploader uploader;

	private OnUploadProcessListener onUploadProcessListener;// 上传回调事件

	private int reqWidth;// 图片压缩时图片的最大宽度
	private int reqHeight;// 图片压缩时图片的最大高度

	private int tempWidth;// 临时存储上传图片时压缩后的宽度
	private int tempHeight;// 临时存储上传图片时压缩后的高度

	private boolean isLimit = false;// 设置宽高限制是否有效,默认为false

	// endregion

	// region interface

	/**
	 * @author YunZ
	 * @回调函数，用到回调上传文件是否完成
	 */
	public interface OnUploadProcessListener {
		/**
		 * @param responseCode
		 * @param message
		 * @param obj
		 * @上传响应
		 */
		void onUploadDone(int responseCode, String message, Object obj);

		/**
		 * @param uploadSize
		 * @上传中
		 */
		void onUploadProcess(int uploadSize);

		/**
		 * @param fileSize
		 * @准备上传
		 */
		void initUpload(int fileSize);
	}

	// endregion

	// region propertys

	/**
	 * @return
	 * @获取读取超时
	 */
	public int getReadTimeOut() {
		return readTimeOut;
	}

	/**
	 * @param readTimeOut
	 * @设置读取超时
	 */
	public void setReadTimeOut(int readTimeOut) {
		this.readTimeOut = readTimeOut;
	}

	/**
	 * @return
	 * @获取连接超时时间
	 */
	public int getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * @param connectTimeout
	 * @设置连接超时时间
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * @return
	 * @获取上传使用的时间
	 */
	public int getRequestTime() {
		return requestTime;
	}

	/**
	 * @param width
	 * @设置压缩的最大宽度
	 */
	public void setReqWidth(int width) {
		this.reqWidth = width;
	}

	/**
	 * @param height
	 * @设置压缩的最大高度
	 */
	public void setReqHeight(int height) {
		this.reqHeight = height;
	}

	/**
	 * @param limit
	 * @设置宽高限制是否有效
	 */
	public void setLimit(boolean limit) {
		this.isLimit = limit;
	}

	/**
	 * @param onUploadProcessListener
	 * @设置回调事件
	 */
	public void setOnUploadProcessListener(
			OnUploadProcessListener onUploadProcessListener) {
		this.onUploadProcessListener = onUploadProcessListener;
	}

	// endregion

	// region methods

	/**
	 * @return
	 * @单例模式获取上传工具类
	 */
	public static FileUploader getInstance() {
		if (null == uploader) {
			uploader = new FileUploader();
		}
		return uploader;
	}

	// region 对外接口方法

	/**
	 * @param filePath
	 * @param fileKey
	 * @param RequestURL
	 * @param param
	 * @param cookieValue
	 * @android上传文件到服务器
	 * @需要上传的文件的路径
	 * @在网页上<input type=file name=xxx/> xxx就是这里的fileKey
	 * @请求的URL
	 * @请求的传递参数
	 * @登录态值
	 */
	public void uploadFile(String filePath, String fileKey, String RequestURL,
	                       Map<String, String> param, String cookieValue) {
		if (!checkArgs(filePath) || !checkArgs(fileKey)
				|| !checkArgs(RequestURL)) {
			sendMessage(UPLOAD_ARG_ERROR_CODE, "参数错误");
			return;
		}

		try {
			File file = new File(filePath);
			uploadFile(file, fileKey, RequestURL, param, cookieValue);
		} catch (Exception e) {
			sendMessage(UPLOAD_ERROR_CODE, "上传出错");
			return;
		}
	}

	/**
	 * @param imageFile
	 * @param fileKey
	 * @param RequestURL
	 * @param param
	 * @param cookieValue
	 * @android上传图片对应的位图到服务器
	 * @需要上传的文件的路径
	 * @在网页上<input type=file name=xxx/> xxx就是这里的fileKey
	 * @请求的URL
	 * @请求的传递参数
	 * @登录态值
	 */
	public void uploadFile(Bitmap imageFile, String fileKey, String RequestURL,
	                       Map<String, String> param, String cookieValue) {
		if (!checkArgs(fileKey) || !checkArgs(RequestURL)) {
			sendMessage(UPLOAD_ARG_ERROR_CODE, "参数错误");
			return;
		}

		try {
			uploadBitmap(imageFile, fileKey, RequestURL, param, cookieValue);
		} catch (Exception e) {
			sendMessage(UPLOAD_ERROR_CODE, "上传出错");
			return;
		}
	}

	// endregion

	// region 基本方法

	/**
	 * @param file
	 * @param fileKey
	 * @param RequestURL
	 * @param param
	 * @param cookieValue
	 * @android上传文件到服务器
	 * @需要上传的文件
	 * @在网页上<input type=file name=xxx/> xxx就是这里的fileKey
	 * @请求的URL
	 * @请求的传递参数
	 * @登录态值
	 */
	private void uploadFile(final File file, final String fileKey,
	                        final String RequestURL, final Map<String, String> param,
	                        final String cookieValue) {
		if (file == null || !file.exists()) {
			sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE, "文件不存在");
			return;
		}

		Log.i(TAG, "请求的URL=" + RequestURL);
		Log.i(TAG, "请求的fileName=" + file.getName());
		Log.i(TAG, "请求的fileKey=" + fileKey);
		new Thread(new Runnable() { // 开启线程上传文件
			@Override
			public void run() {
				checkLoginStatus(cookieValue);
				startUploadFile(file, fileKey, RequestURL, param, cookieValue);
			}
		}).start();

	}

	/**
	 * @param file
	 * @param fileKey
	 * @param RequestURL
	 * @param param
	 * @param cookieValue
	 * @开始上传文件
	 * @请求的传递参数
	 * @登录态值
	 */
	private void startUploadFile(File file, String fileKey, String RequestURL,
	                             Map<String, String> param, String cookieValue) {
		String result = null;
		requestTime = 0;

		long requestTime = System.currentTimeMillis();
		long responseTime = 0;

		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(readTimeOut);
			conn.setConnectTimeout(connectTimeout);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			if (cookieValue != null && !cookieValue.equals("")) {
				conn.setRequestProperty("Cookie", cookieValue);
			}

			/*
			 * conn.setRequestProperty("user-agent",
			 * "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			 */
			conn.setRequestProperty("Content-Type",
					CONTENT_TYPE + ";boundary=" + PREFIX + PREFIX + BOUNDARY);
			// conn.setRequestProperty("Content-Type",
			// "application/x-www-form-urlencoded");

			/**
			 * 当文件不为空，把文件包装并且上传
			 */
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			StringBuffer sb = null;
			String params = "";

			/***
			 * 以下是用于上传参数
			 */
			if (param != null && param.size() > 0) {
				Iterator<String> it = param.keySet().iterator();
				while (it.hasNext()) {
					sb = null;
					sb = new StringBuffer();
					String key = it.next();
					String value = param.get(key);
					sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
					sb.append("Content-Disposition: form-data; name=\"")
							.append(key).append("\"").append(LINE_END)
							.append(LINE_END);
					sb.append(value).append(LINE_END);
					params = sb.toString();
					Log.i(TAG, key + "=" + params + "##");
					dos.write(params.getBytes());
					// dos.flush();
				}
			}

			sb = null;
			params = null;
			sb = new StringBuffer();
			/**
			 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
			 * filename是文件的名字，包含后缀名的 比如:abc.png
			 */
			sb.append(PREFIX + PREFIX + PREFIX).append(BOUNDARY)
					.append(LINE_END);
			sb.append("Content-Disposition:form-data; name=\"" + fileKey
					+ "\"; filename=\"" + file.getName() + "\"" + LINE_END);
			sb.append("Content-Type:image/jpeg" + LINE_END); // 这里配置的Content-type很重要的
			// ，用于服务器端辨别文件的类型的
			sb.append(LINE_END);
			params = sb.toString();
			sb = null;

			Log.i(TAG, file.getName() + "=" + params + "##");
			dos.write(params.getBytes());
			/** 上传文件 */
			// InputStream is = new FileInputStream(file);
			// onUploadProcessListener.initUpload((int) file.length());

			// InputStream is =
			// compressInputStream(getScaleImage(file.getPath()),
			// 70);

			// InputStream is = compressInputStream(
			// rotateBitmapByDegree(
			// getScaleImageWithLocader(file.getPath()),
			// getBitmapDegree(file.getPath())), 70);

			InputStream is = compressInputStream(getScaleImage(file.getPath()),
					100);

			byte[] bytes = new byte[1024];
			int len = 0;
			int curLen = 0;
			while ((len = is.read(bytes)) != -1) {
				curLen += len;
				dos.write(bytes, 0, len);
				onUploadProcessListener.onUploadProcess(curLen);
			}
			is.close();

			dos.write(LINE_END.getBytes());
			byte[] end_data = (PREFIX + PREFIX + PREFIX + BOUNDARY + PREFIX
					+ LINE_END).getBytes();
			dos.write(end_data);
			dos.flush();
			//
			// dos.write(tempOutputStream.toByteArray());
			/**
			 * 获取响应码 200=成功 当响应成功，获取响应的流
			 */
			int res = conn.getResponseCode();
			responseTime = System.currentTimeMillis();
			this.requestTime = (int) ((responseTime - requestTime) / 1000);
			Log.e(TAG, "response code:" + res);
			if (res == 200) {
				Log.e(TAG, "request success");
				InputStream input = conn.getInputStream();
				StringBuffer sb1 = new StringBuffer();
				int ss;
				while ((ss = input.read()) != -1) {
					sb1.append((char) ss);
				}
				result = sb1.toString();
				Log.e(TAG, "result : " + result);
				// sendMessage(UPLOAD_SUCCESS_CODE, "上传结果：" + result);
				sendMessage(UPLOAD_SUCCESS_CODE, result);// 回传图片ID的JSON字符串，需要自己组织url
				return;
			} else {
				Log.e(TAG, "request error");
				sendMessage(UPLOAD_SERVER_ERROR_CODE, "服务器错误：code=" + res);
				return;
			}
		} catch (MalformedURLException e) {
			sendMessage(UPLOAD_FAILED_CODE, "上传失败：error=" + e.getMessage());
			e.printStackTrace();
			return;
		} catch (IOException e) {
			sendMessage(UPLOAD_FAILED_CODE, "上传失败：error=" + e.getMessage());
			e.printStackTrace();
			return;
		} catch (Exception e) {
			sendMessage(UPLOAD_FAILED_CODE, "上传失败：error=" + e.getMessage());
			e.printStackTrace();
			return;
		}

	}

	/**
	 * @param imageFile
	 * @param fileKey
	 * @param RequestURL
	 * @param param
	 * @param cookieValue
	 * @上传图片
	 */
	private void uploadBitmap(final Bitmap imageFile, final String fileKey,
	                          final String RequestURL, final Map<String, String> param,
	                          final String cookieValue) {
		if (imageFile == null) {
			sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE, "位图不存在");
			return;
		}

		new Thread(new Runnable() { // 开启线程上传文件
			@Override
			public void run() {
				checkLoginStatus(cookieValue);
				startUploadBitmap(imageFile, fileKey, RequestURL, param,
						cookieValue);
			}
		}).start();

	}

	/**
	 * @param imageFile
	 * @param fileKey
	 * @param RequestURL
	 * @param param
	 * @param cookieValue
	 * @开始上传图片
	 * @请求的传递参数
	 * @登录态值
	 */
	private void startUploadBitmap(Bitmap imageFile, String fileKey,
	                               String RequestURL, Map<String, String> param, String cookieValue) {
		String result = null;
		requestTime = 0;

		long requestTime = System.currentTimeMillis();
		long responseTime = 0;

		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(readTimeOut);
			conn.setConnectTimeout(connectTimeout);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			if (cookieValue != null && !cookieValue.equals("")) {
				conn.setRequestProperty("Cookie", cookieValue);
			}

			/*
			 * conn.setRequestProperty("user-agent",
			 * "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			 */
			conn.setRequestProperty("Content-Type",
					CONTENT_TYPE + ";boundary=" + PREFIX + PREFIX + BOUNDARY);
			// conn.setRequestProperty("Content-Type",
			// "application/x-www-form-urlencoded");

			/**
			 * 当文件不为空，把文件包装并且上传
			 */
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			StringBuffer sb = null;
			String params = "";

			/***
			 * 以下是用于上传参数
			 */
			if (param != null && param.size() > 0) {
				Iterator<String> it = param.keySet().iterator();
				while (it.hasNext()) {
					sb = null;
					sb = new StringBuffer();
					String key = it.next();
					String value = param.get(key);
					sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
					sb.append("Content-Disposition: form-data; name=\"")
							.append(key).append("\"").append(LINE_END)
							.append(LINE_END);
					sb.append(value).append(LINE_END);
					params = sb.toString();
					Log.i(TAG, key + "=" + params + "##");
					dos.write(params.getBytes());
					// dos.flush();
				}
			}

			sb = null;
			params = null;
			sb = new StringBuffer();
			/**
			 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
			 * filename是文件的名字，包含后缀名的 比如:abc.png
			 */
			sb.append(PREFIX + PREFIX + PREFIX).append(BOUNDARY)
					.append(LINE_END);
			sb.append("Content-Disposition:form-data; name=\"" + fileKey
					+ "\"; filename=\"" + "header.jpeg" + "\"" + LINE_END);
			sb.append("Content-Type:image/jpeg" + LINE_END); // 这里配置的Content-type很重要的
			// ，用于服务器端辨别文件的类型的
			sb.append(LINE_END);
			params = sb.toString();
			sb = null;

			dos.write(params.getBytes());
			/** 上传文件 */
			// InputStream is = compressInputStream(imageFile, 100);
			InputStream is = compressInputStream(getScaleImage(imageFile), 100);
			byte[] bytes = new byte[1024];
			int len = 0;
			int curLen = 0;
			while ((len = is.read(bytes)) != -1) {
				curLen += len;
				dos.write(bytes, 0, len);
				onUploadProcessListener.onUploadProcess(curLen);
			}
			is.close();

			dos.write(LINE_END.getBytes());
			byte[] end_data = (PREFIX + PREFIX + PREFIX + BOUNDARY + PREFIX
					+ LINE_END).getBytes();
			dos.write(end_data);
			dos.flush();
			//
			// dos.write(tempOutputStream.toByteArray());
			/**
			 * 获取响应码 200=成功 当响应成功，获取响应的流
			 */
			int res = conn.getResponseCode();
			responseTime = System.currentTimeMillis();
			this.requestTime = (int) ((responseTime - requestTime) / 1000);
			Log.e(TAG, "response code:" + res);
			if (res == 200) {
				Log.e(TAG, "request success");
				InputStream input = conn.getInputStream();
				StringBuffer sb1 = new StringBuffer();
				int ss;
				while ((ss = input.read()) != -1) {
					sb1.append((char) ss);
				}
				result = sb1.toString();
				Log.e(TAG, "result : " + result);
				// sendMessage(UPLOAD_SUCCESS_CODE, "上传结果：" + result);
				sendMessage(UPLOAD_SUCCESS_CODE, result);// 回传图片ID的JSON字符串，需要自己组织url
				return;
			} else {
				Log.e(TAG, "request error");
				sendMessage(UPLOAD_SERVER_ERROR_CODE, "服务器错误：code=" + res);
				return;
			}
		} catch (MalformedURLException e) {
			sendMessage(UPLOAD_FAILED_CODE, "上传失败：error=" + e.getMessage());
			e.printStackTrace();
			return;
		} catch (IOException e) {
			sendMessage(UPLOAD_FAILED_CODE, "上传失败：error=" + e.getMessage());
			e.printStackTrace();
			return;
		} catch (Exception e) {
			sendMessage(UPLOAD_FAILED_CODE, "上传失败：error=" + e.getMessage());
			e.printStackTrace();
			return;
		}
	}

	/**
	 * @return
	 * @检查参数
	 */
	private boolean checkArgs(String arg) {
		if (arg == null || arg.equals("")) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @param cookieValue
	 * @当session存在时，需要检查登录态
	 */
	private void checkLoginStatus(String cookieValue) {
//		try {
//			if (cookieValue != null && !cookieValue.equals("")) {
//				AppHandler.getInstance().checkLoginStaus();
//			}
//		} catch (AppException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * @param srcPath
	 * @return
	 * @获得压缩后的比例图片（精确压缩） @使用ImageLoader来处理，因为getScaleImage(String
	 * @srcPath)中的Bitmap.createBitmap会造成内存溢出
	 * @当然也可以先进行压缩在使用Bitmap.createBitmap
	 */
	private Bitmap getScaleImage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
		newOpts.inJustDecodeBounds = false;

		// region 增加旋转判断

		int degree = getBitmapDegree(srcPath);
		int w;
		int h;
		if (degree == 90 || degree == 270)// 是否进行了旋转
		{
			w = newOpts.outHeight;
			h = newOpts.outWidth;
		} else {
			w = newOpts.outWidth;
			h = newOpts.outHeight;
		}

		// endregion

		double ratio = 1.0f;

		ImageSize targetSize = null;

		if (this.isLimit)// 是否需要限制宽高比例
		{
			if (h > reqHeight || w > reqWidth) {
				double heightRatio = this.reqHeight / (double) h;
				double widthRatio = this.reqWidth / (double) w;
				ratio = heightRatio < widthRatio ? heightRatio : widthRatio;
				targetSize = new ImageSize((int) Math.floor(ratio * w),
						(int) Math.floor(ratio * h));
			} else {
				targetSize = new ImageSize(w, h);
			}
		} else {
			targetSize = new ImageSize(w, h);
		}

		Bitmap resizeBitmap = ImageProvider.Loader.loadImageSync(
				"file://" + srcPath, targetSize, ImageProvider.UploadOptions);

		tempHeight = resizeBitmap.getHeight();
		tempWidth = resizeBitmap.getWidth();

		return resizeBitmap;
	}

	/**
	 * @param image
	 * @return
	 * @获得压缩后的图片
	 * @先非精确压缩，在进行精确压缩
	 * @也可以尝试使用ThumbnailUtils.extractThumbnail(source, width, height)方法
	 * @据说该方法是兼顾精确压缩和非精确压缩的方法，但没测试过
	 */
	private Bitmap getScaleImage(Bitmap image) {
		// region 将图片读入到输出流，并做防溢出处理，暂时不能释放image，因为引用类型传递，释放的话会造成其他地方位图丢失

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024)// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
		{
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}

		// endregion

		// region 把输出流的图片读入到输入流，并读取记录位图属性

		ByteArrayInputStream isBm = new ByteArrayInputStream(
				baos.toByteArray());

		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);// 只读取属性，不加载进内存，此处bitmap为null
		newOpts.inJustDecodeBounds = false;

		int w = newOpts.outWidth;
		int h = newOpts.outHeight;

		// endregion

		// region 非精确压缩

		int be = 1;// be=1表示不缩放

		// region 计算缩放比

		if (this.isLimit)// 当限制的宽高有效时才计算
		{
			if (h > reqHeight || w > reqWidth)// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
			{
				int heightRatio = Math.round((float) h / this.reqHeight);
				int widthRatio = Math.round((float) w / this.reqWidth);
				be = heightRatio > widthRatio ? heightRatio : widthRatio;
			}
		}

		// endregion

		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

		// endregion

		// region 精确压缩

		w = newOpts.outWidth;
		h = newOpts.outHeight;
		float ratio = 1.0f;

		if (this.isLimit) {
			if (h > reqHeight || w > reqWidth) {
				float heightRatio = this.reqHeight / (float) h;
				float widthRatio = this.reqWidth / (float) w;
				ratio = heightRatio < widthRatio ? heightRatio : widthRatio;
			} else {
				tempWidth = w;
				tempHeight = h;
				return bitmap;
			}
		} else {
			tempWidth = w;
			tempHeight = h;
			return bitmap;
		}

		Matrix matrix = new Matrix();
		matrix.postScale(ratio, ratio);// 产生缩放后的Bitmap对象

		Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix,
				true);

		tempHeight = resizeBitmap.getHeight();
		tempWidth = resizeBitmap.getWidth();

		// endregion

		bitmap.recycle();// 释放原Bitmap

		return resizeBitmap;
	}

	/**
	 * @param path
	 * @return
	 * @获取照片的旋转角度
	 */
	private int getBitmapDegree(String path) {
		int degree = 0;
		try {
			// 从指定路径下读取图片，并获取其EXIF信息
			ExifInterface exifInterface = new ExifInterface(path);
			// 获取图片的旋转信息
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * @param bm
	 * @param quality
	 * @return
	 * @把图片压缩到流
	 */
	private InputStream compressInputStream(Bitmap bm, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);

		// region 增加图片质量检测，若大于服务器规定的2M，则持续压缩

		// region 按新压缩质量计算算法来压缩

		int options = getCompressQuality(quality);
		while (baos.toByteArray().length / 1024 > 2048)// 循环判断如果压缩后图片是否大于2M,大于则继续压缩
		{
			baos.reset();// 重置baos即清空baos
			bm.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options = getCompressQuality(options);
		}

		// endregion

		// region 原实现

		// int imageSize = baos.toByteArray().length;
		//
		// if ((imageSize / 1024) > 2048)
		// {
		// int options = quality > 10 ? quality - 10 : quality - 1;
		// while (baos.toByteArray().length / 1024 > 2048)
		// { // 循环判断如果压缩后图片是否大于2M,大于继续压缩
		// baos.reset();// 重置baos即清空baos
		// bm.compress(Bitmap.CompressFormat.JPEG, options, baos);//
		// 这里压缩options%，把压缩后的数据存放到baos中
		// options = options > 10 ? options - 10 : options - 1;
		// }
		// }

		// endregion

		// endregion

		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		bm.recycle();// 回收Bitmap
		return is;
	}

	/**
	 * 获取图片的压缩质量
	 *
	 * @param quality
	 * @return
	 * @quality的正常取值范围为0到100
	 */
	private int getCompressQuality(int quality) {
		if (quality < 0) {
			return 0;
		} else if (quality > 100) {
			return 100;
		} else {
			if (quality < 1) {
				return quality;
			} else {
				if (quality >= 90) {
					return quality - 10;
				} else if (quality >= 60) {
					return quality - 5;
				} else if (quality >= 30) {
					return quality - 2;
				} else {
					return quality - 1;
				}
			}
		}
	}

	/**
	 * @param responseCode
	 * @param responseMessage
	 * @发送上传结果
	 */
	private void sendMessage(int responseCode, String responseMessage) {
		if (responseCode == UPLOAD_SUCCESS_CODE) {
			Point imgPoint = new Point(tempWidth, tempHeight);// 传递图片的比例大小
			onUploadProcessListener.onUploadDone(responseCode, responseMessage,
					imgPoint);
		} else {
			onUploadProcessListener.onUploadDone(responseCode, responseMessage,
					null);
		}

	}

	// endregion

	// region 暂时不使用

	/**
	 * @param picPaths
	 * @param requestURL
	 * @同时上传多文件，里面的内容还未进行修改
	 */
	public void uploadFiles(String[] picPaths, String requestURL) {
		String boundary = UUID.randomUUID().toString(); // 边界标识 随机生成
		String prefix = "--", end = "\r\n";
		String content_type = "multipart/form-data"; // 内容类型
		String CHARSET = "utf-8"; // 设置编码
		int TIME_OUT = 10 * 10000000; // 超时时间
		try {
			URL url = new URL(requestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", "utf-8"); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type",
					content_type + ";boundary=" + boundary);
			/**
			 * 当文件不为空，把文件包装并且上传
			 */
			OutputStream outputSteam = conn.getOutputStream();
			DataOutputStream dos = new DataOutputStream(outputSteam);

			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(prefix);
			stringBuffer.append(boundary);
			stringBuffer.append(end);
			dos.write(stringBuffer.toString().getBytes());

			String name = "userName";
			dos.writeBytes("Content-Disposition: form-data; name=\"" + name
					+ "\"" + end);
			dos.writeBytes(end);
			dos.writeBytes("zhangSan");
			dos.writeBytes(end);

			for (int i = 0; i < picPaths.length; i++) {
				File file = new File(picPaths[i]);

				StringBuffer sb = new StringBuffer();
				sb.append(prefix);
				sb.append(boundary);
				sb.append(end);

				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */
				sb.append("Content-Disposition: form-data; name=\"" + i
						+ "\"; filename=\"" + file.getName() + "\"" + end);
				sb.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + end);
				sb.append(end);
				dos.write(sb.toString().getBytes());

				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[8192];// 8k
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(end.getBytes());// 一个文件结束标志
			}
			byte[] end_data = (prefix + boundary + prefix + end).getBytes();// 结束
			// http
			// 流
			dos.write(end_data);
			dos.flush();
			/**
			 * 获取响应码 200=成功 当响应成功，获取响应的流
			 */
			int res = conn.getResponseCode();
			Log.e("TAG", "response code:" + res);
			if (res == 200) {
				return;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param fileKey
	 * @param RequestURL
	 * @param param
	 * @param cookieValue
	 * @android上传文件到服务器（暂时不使用） @param filePath
	 * @需要上传的文件的路径
	 * @在网页上<input type=file name=xxx/> xxx就是这里的fileKey
	 * @请求的URL
	 * @请求的传递参数
	 * @登录态值
	 */
	public void uploadImageFile(String filePath, String fileKey,
	                            String RequestURL, Map<String, String> param, String cookieValue) {
		if (filePath == null) {
			sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE, "文件不存在");
			return;
		}
		try {
			File file = new File(filePath);
			if (file == null || (!file.exists())) {
				sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE, "文件不存在");
				return;
			}
//			try {
//				AppHandler.getInstance().checkLoginStaus();
//			} catch (AppException e) {
//				e.printStackTrace();
//			}
			startUploadFile(file, fileKey, RequestURL, param, cookieValue);
		} catch (Exception e) {
			sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE, "文件不存在");
			e.printStackTrace();
			return;
		}
	}

	/**
	 * @param srcPath
	 * @return
	 * @获得压缩后的图片（非精确比例压缩） @非精确比例压缩后，图片大小可能不会发生变化（暂时不使用） @因为是不会产生新的Bitmap
	 */
	private Bitmap getImage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = this.reqHeight;// 这里设置高度为800f
		float ww = this.reqWidth;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放

		// region 原缩放算法

		// if (w > h && w > ww)
		// {// 如果宽度大的话根据宽度固定大小缩放
		// be = (int) (newOpts.outWidth / ww);
		// } else if (w < h && h > hh)
		// {// 如果高度高的话根据宽度固定大小缩放
		// be = (int) (newOpts.outHeight / hh);
		// }

		// endregion

		// region 新缩放算法

		if (h > reqHeight || w > reqWidth) {
			final int heightRatio = Math.round((float) h / hh);
			final int widthRatio = Math.round((float) w / ww);
			be = heightRatio > widthRatio ? heightRatio : widthRatio;
		}

		// endregion

		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		// 设置压缩后的图片宽高
		tempHeight = newOpts.outHeight;
		tempWidth = newOpts.outWidth;
		return bitmap;
		// return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * @param image
	 * @return
	 * @获得压缩后的图片,暂时不使用
	 */
	private Bitmap getImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(
				baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = this.reqHeight;// 这里设置高度为800f
		float ww = this.reqWidth;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放

		// region 原缩放算法

		// if (w > h && w > ww)
		// {// 如果宽度大的话根据宽度固定大小缩放
		// be = (int) (newOpts.outWidth / ww);
		// } else if (w < h && h > hh)
		// {// 如果高度高的话根据高度固定大小缩放
		// be = (int) (newOpts.outHeight / hh);
		// }

		// endregion

		// region 新缩放算法

		if (h > reqHeight || w > reqWidth) {
			final int heightRatio = Math.round((float) h / (float) reqHeight);
			final int widthRatio = Math.round((float) w / (float) reqWidth);
			be = heightRatio > widthRatio ? heightRatio : widthRatio;
		}

		// endregion

		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

		tempHeight = newOpts.outHeight;
		tempWidth = newOpts.outWidth;

		// ThumbnailUtils.extractThumbnail(source, width, height);
		return bitmap;
		// return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * @param srcPath
	 * @return
	 * @获得压缩后的比例图片（精确压缩） @采用精确压缩是重新创建（暂时不使用） @压缩后可能会缩减图片的大小
	 * @需要分两步，第一步是采用非精确压缩，防止图片读入内存时出现OOM
	 * @第二步当宽高仍然不符合时，采用精确压缩
	 */
	private Bitmap getScaleImageWithLocader(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = this.reqHeight;// 这里设置高度为800f
		float ww = this.reqWidth;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放

		// region 原缩放算法

		// if (w > h && w > ww)
		// {// 如果宽度大的话根据宽度固定大小缩放
		// be = (int) (newOpts.outWidth / ww);
		// } else if (w < h && h > hh)
		// {// 如果高度高的话根据宽度固定大小缩放
		// be = (int) (newOpts.outHeight / hh);
		// }

		// endregion

		// region 新缩放算法

		if (h > reqHeight || w > reqWidth) {
			int heightRatio = Math.round((float) h / hh);
			int widthRatio = Math.round((float) w / ww);
			be = heightRatio > widthRatio ? heightRatio : widthRatio;
		}

		// endregion

		// region 第一步非精确压缩

		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 进行精确压缩

		// endregion

		// region 精确压缩

		w = newOpts.outWidth;
		h = newOpts.outHeight;
		float ratio = 1.0f;

		if (h > reqHeight || w > reqWidth) {
			float heightRatio = hh / (float) h;
			float widthRatio = ww / (float) w;
			ratio = heightRatio < widthRatio ? heightRatio : widthRatio;
		} else {
			tempWidth = w;
			tempHeight = h;
			return bitmap;
		}

		Matrix matrix = new Matrix();
		matrix.postScale(ratio, ratio);// 产生缩放后的Bitmap对象

		Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix,
				true);

		tempHeight = resizeBitmap.getHeight();
		tempWidth = resizeBitmap.getWidth();

		// endregion

		bitmap.recycle();// 释放原Bitmap
		// saveBitmap(resizeBitmap,100);
		return resizeBitmap;
	}

	/**
	 * @param image
	 * @return
	 * @质量压缩
	 */
	private Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(
				baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * @param mBitmap
	 * @测试压缩质量存储
	 */
	private void saveBitmap(Bitmap mBitmap, int quality) {
		String path = "/sdcard/myHead/";
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
			return;
		}
		FileOutputStream b = null;
		File file = new File(path);
		file.mkdirs();// 创建文件夹
		String fileName = path + "headtest.jpg";// 图片名字
		try {
			b = new FileOutputStream(fileName);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, quality, b);// 把数据写入文件

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭流
				b.flush();
				b.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * @param bm
	 * @param degree
	 * @return
	 * @按旋转角度得到照片
	 */
	private Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
		Bitmap returnBm = null;

		// 根据旋转角度，生成旋转矩阵
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		try {
			// 将原始图片按照旋转矩阵进行旋转，并得到新的图片
			returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
		}
		if (returnBm == null) {
			returnBm = bm;
		}
		if (bm != returnBm) {
			bm.recycle();
		}
		return returnBm;
	}

	// endregion

	// region 原始实现

	// private void toUploadFile(File file, String fileKey, String RequestURL,
	// Map<String, String> param)
	// {
	// String result = null;
	// requestTime = 0;
	//
	// long requestTime = System.currentTimeMillis();
	// long responseTime = 0;
	//
	// try
	// {
	// URL url = new URL(RequestURL);
	// HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	// conn.setReadTimeout(readTimeOut);
	// conn.setConnectTimeout(connectTimeout);
	// conn.setDoInput(true); // 允许输入流
	// conn.setDoOutput(true); // 允许输出流
	// conn.setUseCaches(false); // 不允许使用缓存
	// conn.setRequestMethod("POST"); // 请求方式
	// conn.setRequestProperty("Charset", CHARSET); // 设置编码
	// conn.setRequestProperty("connection", "keep-alive");
	// conn.setRequestProperty("user-agent",
	// "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
	// conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
	// + BOUNDARY);
	// // conn.setRequestProperty("Content-Type",
	// // "application/x-www-form-urlencoded");
	//
	// /**
	// * 当文件不为空，把文件包装并且上传
	// */
	// DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
	// StringBuffer sb = null;
	// String params = "";
	//
	// /***
	// * 以下是用于上传参数
	// */
	// if (param != null && param.size() > 0)
	// {
	// Iterator<String> it = param.keySet().iterator();
	// while (it.hasNext())
	// {
	// sb = null;
	// sb = new StringBuffer();
	// String key = it.next();
	// String value = param.get(key);
	// sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
	// sb.append("Content-Disposition: form-data; name=\"")
	// .append(key).append("\"").append(LINE_END)
	// .append(LINE_END);
	// sb.append(value).append(LINE_END);
	// params = sb.toString();
	// Log.i(TAG, key + "=" + params + "##");
	// dos.write(params.getBytes());
	// // dos.flush();
	// }
	// }
	//
	// sb = null;
	// params = null;
	// sb = new StringBuffer();
	// /**
	// * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
	// * filename是文件的名字，包含后缀名的 比如:abc.png
	// */
	// sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
	// sb.append("Content-Disposition:form-data; name=\"" + fileKey
	// + "\"; filename=\"" + file.getName() + "\"" + LINE_END);
	// sb.append("Content-Type:image/pjpeg" + LINE_END); //
	// 这里配置的Content-type很重要的
	// // ，用于服务器端辨别文件的类型的
	// sb.append(LINE_END);
	// params = sb.toString();
	// sb = null;
	//
	// Log.i(TAG, file.getName() + "=" + params + "##");
	// dos.write(params.getBytes());
	// /** 上传文件 */
	// InputStream is = new FileInputStream(file);
	// onUploadProcessListener.initUpload((int) file.length());
	// byte[] bytes = new byte[1024];
	// int len = 0;
	// int curLen = 0;
	// while ((len = is.read(bytes)) != -1)
	// {
	// curLen += len;
	// dos.write(bytes, 0, len);
	// onUploadProcessListener.onUploadProcess(curLen);
	// }
	// is.close();
	//
	// dos.write(LINE_END.getBytes());
	// byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
	// .getBytes();
	// dos.write(end_data);
	// dos.flush();
	// //
	// // dos.write(tempOutputStream.toByteArray());
	// /**
	// * 获取响应码 200=成功 当响应成功，获取响应的流
	// */
	// int res = conn.getResponseCode();
	// responseTime = System.currentTimeMillis();
	// this.requestTime = (int) ((responseTime - requestTime) / 1000);
	// Log.e(TAG, "response code:" + res);
	// if (res == 200)
	// {
	// Log.e(TAG, "request success");
	// InputStream input = conn.getInputStream();
	// StringBuffer sb1 = new StringBuffer();
	// int ss;
	// while ((ss = input.read()) != -1)
	// {
	// sb1.append((char) ss);
	// }
	// result = sb1.toString();
	// Log.e(TAG, "result : " + result);
	// // sendMessage(UPLOAD_SUCCESS_CODE, "上传结果：" + result);
	// sendMessage(UPLOAD_SUCCESS_CODE, result);// 回传图片ID的JSON字符串，需要自己组织url
	// return;
	// } else
	// {
	// Log.e(TAG, "request error");
	// sendMessage(UPLOAD_SERVER_ERROR_CODE, "上传失败：code=" + res);
	// return;
	// }
	// } catch (MalformedURLException e)
	// {
	// sendMessage(UPLOAD_SERVER_ERROR_CODE,
	// "上传失败：error=" + e.getMessage());
	// e.printStackTrace();
	// return;
	// } catch (IOException e)
	// {
	// sendMessage(UPLOAD_SERVER_ERROR_CODE,
	// "上传失败：error=" + e.getMessage());
	// e.printStackTrace();
	// return;
	// }
	// }

	// endregion

	// endregion
}
