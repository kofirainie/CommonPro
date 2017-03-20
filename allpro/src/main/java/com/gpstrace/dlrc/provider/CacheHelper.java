package com.gpstrace.dlrc.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.util.Log;

/**
 * @缓存辅助类
 * @author YunZ
 *
 */
public class CacheHelper
{
	// region fields

	private static CacheHelper sInstance;
	private Context mContext;

	private static final int CACHE_TIME = 60000;// 部分缓存失效时间,默认1分钟

	// endregion

	// region public

	/**
	 * @单件模式
	 * @return
	 */
	public static CacheHelper getInstance()
	{
		if (sInstance == null)
		{
			sInstance = new CacheHelper();
		}
		return sInstance;
	}

	/**
	 * @设置应用程序Context
	 * @param mContext
	 */
	public void setContext(Context mContext)
	{
		this.mContext = mContext;
	}

	/**
	 * @保存序列对象
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file)
	{
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try
		{
			fos = mContext.openFileOutput(file, Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		} finally
		{
			try
			{
				oos.close();
			} catch (Exception e)
			{
			}
			try
			{
				fos.close();
			} catch (Exception e)
			{
			}
		}
	}

	/**
	 * @保存列表对象
	 * @param <T>
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public <T> boolean saveListObject(List<T> ser, String file)
	{
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try
		{
			fos = mContext.openFileOutput(file, Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		} finally
		{
			try
			{
				oos.close();
			} catch (Exception e)
			{
			}
			try
			{
				fos.close();
			} catch (Exception e)
			{
			}
		}
	}

	/**
	 * @读取序列对象
	 * @param file
	 * @param failureEnable
	 * @失效使能，true为检查是否失效，false为不检查是否失效
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file, Boolean failureEnable)
	{
		File cachefile = mContext.getFileStreamPath(file);

		if (!isExistDataCache(cachefile))
		{
			return null;
		}

		if (failureEnable && isCacheDataFailure(cachefile))// 判断是否失效
		{
			return null;
		}

		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try
		{
			fis = mContext.openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e)
		{
		} catch (Exception e)
		{
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException)
			{
				File data = mContext.getFileStreamPath(file);
				data.delete();
			}
		} finally
		{
			try
			{
				ois.close();
			} catch (Exception e)
			{
			}
			try
			{
				fis.close();
			} catch (Exception e)
			{
			}
		}
		return null;
	}

	/**
	 * @读取序列对象
	 * @param file
	 * @param failureEnable
	 * @失效使能，true为检查是否失效，false为不检查是否失效
	 * @param time
	 * @若failureEnable为true，time为设置的有效期，若failureEnable为false，time无效
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file, Boolean failureEnable, long time)
	{
		File cachefile = mContext.getFileStreamPath(file);

		if (!isExistDataCache(cachefile))
		{
			return null;
		}

		if (failureEnable && isCacheDataFailure(cachefile, time))// 判断是否失效
		{
			return null;
		}

		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try
		{
			fis = mContext.openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e)
		{
		} catch (Exception e)
		{
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException)
			{
				File data = mContext.getFileStreamPath(file);
				data.delete();
			}
		} finally
		{
			try
			{
				ois.close();
			} catch (Exception e)
			{
			}
			try
			{
				fis.close();
			} catch (Exception e)
			{
			}
		}
		return null;
	}

	/**
	 * @读取列表对象
	 * @param file
	 * @param failureEnable
	 * @失效使能，true为检查是否失效，false为不检查是否失效
	 * @return
	 */
	public <T> List<T> readListObject(String file, Boolean failureEnable)
	{
		File cachefile = mContext.getFileStreamPath(file);

		if (!isExistDataCache(cachefile))
		{
			return null;
		}

		if (failureEnable && isCacheDataFailure(cachefile))// 判断是否失效
		{
			return null;
		}

		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try
		{
			fis = mContext.openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (List<T>) ois.readObject();
		} catch (FileNotFoundException e)
		{
		} catch (Exception e)
		{
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException)
			{
				File data = mContext.getFileStreamPath(file);
				data.delete();
			}
		} finally
		{
			try
			{
				ois.close();
			} catch (Exception e)
			{
			}
			try
			{
				fis.close();
			} catch (Exception e)
			{
			}
		}
		return null;
	}

	/**
	 * @读取列表对象
	 * @param file
	 * @param failureEnable
	 * @失效使能，true为检查是否失效，false为不检查是否失效
	 * @param time
	 * @若failureEnable为true，time为设置的有效期，若failureEnable为false，time无效
	 * @return
	 */
	public <T> List<T> readListObject(String file, Boolean failureEnable,
			long time)
	{
		File cachefile = mContext.getFileStreamPath(file);

		if (!isExistDataCache(cachefile))
		{
			return null;
		}

		if (failureEnable && isCacheDataFailure(cachefile, time))// 判断是否失效
		{
			return null;
		}

		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try
		{
			fis = mContext.openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (List<T>) ois.readObject();
		} catch (FileNotFoundException e)
		{
		} catch (Exception e)
		{
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException)
			{
				File data = mContext.getFileStreamPath(file);
				data.delete();
			}
		} finally
		{
			try
			{
				ois.close();
			} catch (Exception e)
			{
			}
			try
			{
				fis.close();
			} catch (Exception e)
			{
			}
		}
		return null;
	}

	/**
	 * @读取列表对象,使用当天日期来作为过期判断
	 * @param file
	 * @return
	 */
	public <T> List<T> readListWithDayCheck(String file)
	{
		File cachefile = mContext.getFileStreamPath(file);

		if (!isExistDataCache(cachefile))
		{
			return null;
		}

		if (isCacheDataWithDayCheck(cachefile))// 判断是否失效
		{
			return null;
		}

		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try
		{
			fis = mContext.openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (List<T>) ois.readObject();
		} catch (FileNotFoundException e)
		{
		} catch (Exception e)
		{
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException)
			{
				File data = mContext.getFileStreamPath(file);
				data.delete();
			}
		} finally
		{
			try
			{
				ois.close();
			} catch (Exception e)
			{
			}
			try
			{
				fis.close();
			} catch (Exception e)
			{
			}
		}
		return null;
	}

	/**
	 * @判断缓存数据是否可读
	 * @param cachefile
	 * @param failureEnable
	 * @失效使能，true为检查是否失效，false为不检查是否失效
	 * @return
	 */
	public boolean isReadDataCache(String cachefile, Boolean failureEnable)
	{
		return readObject(cachefile, failureEnable) != null;
	}

	/**
	 * @清除APP缓存
	 */
	public void clearAppCache()
	{
		clearCacheFolder(mContext.getFilesDir(), System.currentTimeMillis());
	}

	/**
	 * @清除单个文件缓存
	 */
	public Boolean clearFileCache(String filePath)
	{
		File cachefile = mContext.getFileStreamPath(filePath);

		try
		{
			if (isExistDataCache(cachefile))
			{
				return cachefile.delete();
			} else
			{
				return true;
			}
		} catch (Exception e)
		{
			return false;
		}
	}
	
	// endregion

	// region private

	/**
	 * @判断缓存是否存在
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(File cachefile)
	{
		return cachefile.exists();
	}

	/**
	 * @判断缓存是否失效
	 * @param cachefile
	 * @return
	 * @true为失效，false为没失效
	 */
	private boolean isCacheDataFailure(File cachefile)
	{
		return (System.currentTimeMillis() - cachefile.lastModified()) > CACHE_TIME;
	}

	/**
	 * @判断缓存是否失效
	 * @param cachefile
	 * @param time
	 * @设定的有效期
	 * @return
	 * @true为失效，false为没失效
	 */
	private boolean isCacheDataFailure(File cachefile, long time)
	{
		return (System.currentTimeMillis() - cachefile.lastModified()) > time;
	}

	/**
	 * @检查缓存数据
	 * @param fileName
	 * @param time
	 * @return
	 * @true表示过期或者不存在，false表示没有过期
	 */
	public boolean checkCacheData(String fileName, long time)
	{
		File cachefile = mContext.getFileStreamPath(fileName);

		if (!isExistDataCache(cachefile))
		{
			return true;
		}

		return (System.currentTimeMillis() - cachefile.lastModified()) > time;
	}

	/**
	 * @判断缓存是否失效(使用零点作为判断标准)
	 * @param cachefile
	 * @return
	 */
	private boolean isCacheDataWithDayCheck(File cachefile)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(cachefile.lastModified());
		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);

		return System.currentTimeMillis() > calendar.getTime().getTime();
	}

	/**
	 * @清除缓存目录
	 * @param dir
	 * @目录
	 * @param numDays
	 * @当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime)
	{
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory())
		{
			try
			{
				for (File child : dir.listFiles())
				{
					if (child.isDirectory())
					{
						deletedFiles += clearCacheFolder(child, curTime);
					}
					if (child.lastModified() < curTime)
					{
						if (child.delete())
						{
							Log.e("CacheFile", child.getName());
							deletedFiles++;
						}
					}
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}
	// endregion
}
