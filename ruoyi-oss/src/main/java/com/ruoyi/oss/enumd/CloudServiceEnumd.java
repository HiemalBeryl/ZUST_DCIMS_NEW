package com.ruoyi.oss.enumd;

import com.ruoyi.oss.service.impl.AliyunCloudStorageServiceImpl;
import com.ruoyi.oss.service.impl.MinioCloudStorageServiceImpl;
import com.ruoyi.oss.service.impl.QiniuCloudStorageServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 云存储服务商枚举
 *
 * @author LionLi
 */
@Getter
@AllArgsConstructor
public enum CloudServiceEnumd {

	/**
	 * 七牛云
	 */
	QINIU("qiniu", QiniuCloudStorageServiceImpl.class),

	/**
	 * 阿里云
	 */
	ALIYUN("aliyun", AliyunCloudStorageServiceImpl.class),

	/**
	 * 腾讯云
	 */
	QCLOUD("qcloud", QiniuCloudStorageServiceImpl.class),

	/**
	 * minio
	 */
	MINIO("minio", MinioCloudStorageServiceImpl.class);

	private final String value;

	private final Class<?> serviceClass;

	public static Class<?> getServiceClass(String value) {
		for (CloudServiceEnumd clazz : values()) {
			if (clazz.getValue().equals(value)) {
				return clazz.getServiceClass();
			}
		}
		return null;
	}
}
