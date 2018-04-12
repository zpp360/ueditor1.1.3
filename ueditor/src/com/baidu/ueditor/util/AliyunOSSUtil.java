package com.baidu.ueditor.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.StorageClass;

public class AliyunOSSUtil {
	private static Logger logger = Logger.getLogger(AliyunOSSUtil.class.getName());
	
	public static final ResourceBundle resource = ResourceBundle.getBundle("oss");
	
	static final String endpoint = resource.getString("endpoint");
	
	static final String accessKeyID = resource.getString("accessKeyID");
	
	static final String accessKeySecret = resource.getString("accessKeySecret");
	
	static final String bucketName = resource.getString("bucketName");
	
	public static final String imgHost = resource.getString("host");
	/**
	 * 本地上传图片使用的地址
	 */
	public static final String localHost = resource.getString("localHost");
	
	public static final boolean ossOpen = Boolean.valueOf(resource.getString("ossOpen"));
	/**
	 * 创建bucketName
	 * @param bucketName
	 *  权限	        Java SDK对应值
		私有读写	    CannedAccessControlList.Private
		公共读私有写	CannedAccessControlList.PublicRead
		公共读写	    CannedAccessControlList.PublicReadWrite
	 */
	public static Bucket createBucket(String bucketName){
		Bucket bucket = null;
		if(StringUtils.isNotBlank(bucketName)){
			OSSClient client = new OSSClient(endpoint, accessKeyID, accessKeySecret);
			CreateBucketRequest createBucketRequest= new CreateBucketRequest(bucketName);
			// 设置bucket权限为公共读，默认是私有读写
			createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
			// 设置bucket存储类型为低频访问类型，默认是标准类型
			createBucketRequest.setStorageClass(StorageClass.Standard);
			client.createBucket(createBucketRequest);
			client.shutdown();
		}
		return bucket;
	}
	/**
	 * 获取全部bucket
	 * @return
	 */
	public static List<Bucket> getAllBucket(){
		OSSClient client = new OSSClient(endpoint, accessKeyID, accessKeySecret);
		// 列举bucket
		List<Bucket> buckets = client.listBuckets();
		// 关闭client
		client.shutdown();
		return buckets;
	}
	/**
	 * 创建文件夹
	 * @param folderName
	 * @throws IOException 
	 */
	public static void createFolder(String folderName) throws IOException{
		if(StringUtils.isNotBlank(folderName)){
			if(!folderName.endsWith("/")){
				folderName += "/";
			}
			OSSClient client = new OSSClient(endpoint, accessKeyID, accessKeySecret);
			client.putObject(bucketName, folderName, new ByteArrayInputStream(new byte[0]));
			
			OSSObject object = client.getObject(bucketName, folderName);
			System.out.println("Size of the empty folder '" + object.getKey() + "' is " + 
					object.getObjectMetadata().getContentLength());
			object.getObjectContent().close();
			client.shutdown();
		}
	}
	/**
	 * 删除对象
	 * @param ducketName
	 * @param key
	 */
	public static void deleteFile(String ducketName,String key){
		if(StringUtils.isNotBlank(ducketName)&&StringUtils.isNotBlank(key)){
			OSSClient client = new OSSClient(endpoint, accessKeyID, accessKeySecret);
			client.deleteObject(ducketName, key);
			client.shutdown();
		}
	}
	/**
	 * 删除默认bucket下的对象
	 * @param key
	 */
	public static void deleteFile(String key){
		if(StringUtils.isNotBlank(key)){
			OSSClient client = new OSSClient(endpoint, accessKeyID, accessKeySecret);
			client.deleteObject(bucketName, key);
			client.shutdown();
		}
	}
	
	/**
	 * 批量删除默认bucket下的object
	 * @param keys
	 * @return
	 */
	public static List<String> deleteByKeys(List<String> keys){
		if(keys!=null && keys.size()>0){
			OSSClient client = new OSSClient(endpoint, accessKeyID, accessKeySecret);
			DeleteObjectsResult deleteObjectsResult = client.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(keys));
			List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
			return deletedObjects;
		}
		return null;
	}
	
	/**
	 * 批量删除默认bucket下的object
	 * @param keys
	 * @return
	 */
	public static List<String> deleteByKeys(String bucketName, List<String> keys){
		if(StringUtils.isNotBlank(bucketName) && keys!=null && keys.size()>0){
			OSSClient client = new OSSClient(endpoint, accessKeyID, accessKeySecret);
			DeleteObjectsResult deleteObjectsResult = client.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(keys));
			List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
			return deletedObjects;
		}
		return null;
	}
	
	
	/**
	 * 上传文件
	* @Description: 
	* @author: 作者
	* @param path
	* @param prefix
	* @return
	* @throws FileNotFoundException
	 */
	public static String upload(String path) throws FileNotFoundException {
		return upload(new File(path));
	}
	
	/**
	 * 上传文件
	* @Description: 
	* @author: 作者
	* @param file
	* @param prefix
	* @return
	* @throws FileNotFoundException
	 */
	public static String upload(File file) throws FileNotFoundException {
		String fileName = file.getName();
		String key = getUUIDFileName(fileName);
		return upload(new FileInputStream(file), key);
	}
	/**
	 * 上传文件
	 * @Description:
	 * @author: 作者
	 * @param key
	 * @return
	 */
	public static String upload(File file, String key) throws FileNotFoundException {
		upload(new FileInputStream(file), key);
		return key;
	}
	
	/**
	 * 上传文件
	* @Description: 
	* @author: 作者
	* @param is
	* @param key
	* @return
	 */
	public static String upload(InputStream is, String key) {
		logger.info("上传文件[" + key + "]至OSS......");
		OSSClient client = new OSSClient(endpoint, accessKeyID, accessKeySecret);
		client.putObject(bucketName, key, is);
		client.shutdown();
		logger.info("上传文件至OSS成功");
		return key;
	}
	
	/**
	 * @param is
	 * @param fileName
	 * @param folder
	 * @return
	 * @throws IOException 
	 */
	public static String upload(MultipartFile mf,String folder) throws IOException{
		String key = getUUIDFileName(mf.getOriginalFilename());
		key = folder + key;
		upload(mf.getInputStream(), key);
		return key;
	}
	
	/**
	 * 上传网络图片
	 * @Description:
	 * @author: 作者
	 * @param client
	 * @param url
	 * @return
	 */
	public static String netImgUpload(String url) {
		String key = "";
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		OSSClient client = null;

		File file = null;

		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			StatusLine statusLine = httpResponse.getStatusLine();
			if (statusLine.getStatusCode() == 200) {
				Header[] header = httpResponse.getHeaders("Content-Type");
				if(header!=null && header.length>0){
					String contentType = header[0].getValue();
					if(contentType.startsWith("image")){
						String postfix = contentType.substring(contentType.indexOf("/")+1, contentType.length());
						String fileName = getUUIDFileName(postfix);
						file = new File(fileName+"."+postfix);
						FileOutputStream outputStream = new FileOutputStream(file);
						InputStream inputStream = httpResponse.getEntity()
								.getContent();
						byte b[] = new byte[2048];
						int j = 0;
						while ((j = inputStream.read(b)) != -1) {
							outputStream.write(b, 0, j);
							
						}
						outputStream.flush();
						outputStream.close();
						client = new OSSClient(endpoint, accessKeyID, accessKeySecret);
						key = upload(file);
					}
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
			client.shutdown();
			file.delete();
		}
		return key;
	}
	
	
	
	/**
	 * 获取UUID的filename
	* @Description: 
	* @author: 作者
	* @param fileName
	* @return
	 */
	private final static String getUUIDFileName(String fileName) {
		String uname = null;
		if (StringUtils.isNotBlank(fileName)) {
			int dotIndex = fileName.lastIndexOf(".");
			if (dotIndex == -1) {
				uname = UuidUtil.get32UUID();
			} else {
				uname =  UuidUtil.get32UUID() + "." + fileName.substring(dotIndex + 1, fileName.length()).toLowerCase();
			}
		}
		return DateUtil.getDay() + "_" + uname;
	}
	
	
	
	public static void main(String[] args) throws IOException {
		//getAllBucket();
		//createFolder("zheng/");
		String key = netImgUpload("https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=1624246531,228120280&fm=58&s=03DE38DAC6908D90F480FE420300F0F7&bpow=121&bpoh=75");
		System.out.println(key);
		
		
	}
	
}
