package com.baidu.ueditor.util;

import java.util.UUID;

/**
 * 系统名称：济南服务外包公共服务平台
 * 类名称：UuidUtil
 * @author 860616011
 */
public class UuidUtil {

	public static String get32UUID() {
		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "").toUpperCase();
		return uuid;
	}
}
