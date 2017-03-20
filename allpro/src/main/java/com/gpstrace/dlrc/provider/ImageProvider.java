package com.gpstrace.dlrc.provider;

import com.gpstrace.dlrc.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.graphics.Bitmap;

public class ImageProvider
{
	public static ImageLoader Loader = ImageLoader.getInstance();// 图片加载器

	/**
	 * @图片参数（圆形加载）,圆形加载会造成OOM
	 * @加入旋转参数considerExifParams(true)
	 */
	public static DisplayImageOptions CircleOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.empty_photo)
			.showImageForEmptyUri(R.drawable.empty_photo)
			.showImageOnFail(R.drawable.empty_photo)
			.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
			.imageScaleType(ImageScaleType.EXACTLY).cacheOnDisk(true).build();

	/**
	 * @图片参数（普通直角加载）
	 * @加入旋转参数considerExifParams(true)
	 */
	public static DisplayImageOptions NormalOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.empty_photo)
			.showImageForEmptyUri(R.drawable.empty_photo)
			.showImageOnFail(R.drawable.empty_photo)
			.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
			.imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(true)
			.cacheOnDisk(true).build();

	/**
	 * @普通长方形封面图片参数（普通直角加载）
	 * @加入旋转参数considerExifParams(true)
	 */
	public static DisplayImageOptions NormalCoverOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.empty_cover)
			.showImageForEmptyUri(R.drawable.empty_cover)
			.showImageOnFail(R.drawable.empty_cover)
			.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
			.imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(true)
			.cacheOnDisk(true).build();
	
	/**
	 * @上传照片时压缩
	 * @加入旋转参数considerExifParams(true)
	 */
	public static DisplayImageOptions UploadOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.empty_photo)
			.showImageForEmptyUri(R.drawable.empty_photo)
			.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
			.cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY).build();

	/**
	 * @图片参数（普通直角加载）
	 * @带有淡入淡出动画
	 */
	public static DisplayImageOptions NormalOptionsWithFade = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.empty_photo)
			.showImageForEmptyUri(R.drawable.empty_photo)
			.showImageOnFail(R.drawable.empty_photo)
			.bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
			.cacheOnDisk(true).build();

	/**
	 * @图片参数（普通直角加载）
	 * @带有淡入淡出动画并且加入旋转校正
	 */
	public static DisplayImageOptions NormalOptionsWithFadeAndExif = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.empty_photo)
			.showImageForEmptyUri(R.drawable.empty_photo)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.showImageOnFail(R.drawable.empty_photo).considerExifParams(true)
			.cacheInMemory(true).cacheOnDisk(true).build();

	/**
	 * @图片参数（头像加载）
	 */
	public static DisplayImageOptions HeaderOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.user_img)
			.showImageForEmptyUri(R.drawable.user_img)
			.showImageOnFail(R.drawable.user_img).cacheInMemory(true)
			.cacheOnDisk(true).build();

	public static DisplayImageOptions NormalHeaderOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.user_img)
			.showImageForEmptyUri(R.drawable.user_img)
			.showImageOnFail(R.drawable.user_img)
			.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
			.imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(true)
			.cacheOnDisk(true).build();

	/**
	 * @图片参数（主页头像加载）
	 */
	public static DisplayImageOptions MainHeaderOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.user_img)
			.showImageForEmptyUri(R.drawable.user_img)
			.showImageOnFail(R.drawable.user_img).cacheInMemory(true)
			.cacheOnDisk(true).build();

	/**
	 * @图片参数（圆角加载）,圆形加载会造成OOM
	 * @加入旋转参数considerExifParams(true)
	 */
	public static DisplayImageOptions RadiusOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.empty_photo)
			.showImageForEmptyUri(R.drawable.empty_photo)
			.showImageOnFail(R.drawable.empty_photo)
			.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
			.displayer(new RoundedBitmapDisplayer(20))
			.imageScaleType(ImageScaleType.EXACTLY).cacheOnDisk(true).build();
	
	/**
	 * @试题图片参数（普通直角加载）
	 * @加入旋转参数considerExifParams(true)
	 */
	public static DisplayImageOptions QuestionOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.question_reload_tip_ico)
			.showImageForEmptyUri(R.drawable.question_reload_tip_ico)
			.showImageOnFail(R.drawable.question_reload_tip_ico)
			.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
			.imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(true)
			.cacheOnDisk(true).build();
}
