package com.gpstrace.dlrc.provider;

import java.io.IOException;
import java.net.HttpURLConnection;

import com.gpstrace.dlrc.handler.AppHandler;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import android.content.Context;

/**
 * @function universal imageloader获取图片时,若需要cookie，需在application中进行配置添加此类
 * @author kofirainie
 * @date 2016-10-13
 */
public class CustomImageDownloader extends BaseImageDownloader {
	//universal image loader获取图片时,若需要cookie，
    // 需在application中进行配置添加此类。
    public CustomImageDownloader(Context context) {
        super(context);
    }

    @Override
    protected HttpURLConnection createConnection(String url, 
    		Object extra) throws IOException {
        // Super...
        HttpURLConnection connection = super.createConnection(url, extra);
        connection.setRequestProperty("Cookie", 
        		AppHandler.getInstance().getSessionValue());
        return connection;
    }
}