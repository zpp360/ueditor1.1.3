package com.baidu.ueditor.upload;

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.ActionMap;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;
import com.baidu.ueditor.util.AliyunOSSUtil;

import net.coobird.thumbnailator.Thumbnails;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public class BinaryUploader {

	public static final State save(HttpServletRequest request,
			Map<String, Object> conf) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//		FileItemStream fileStream = null;
		boolean isAjaxUpload = multipartRequest.getHeader( "X_Requested_With" ) != null;

		if (!ServletFileUpload.isMultipartContent(multipartRequest)) {
			return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
		}

//		ServletFileUpload upload = new ServletFileUpload(
//				new DiskFileItemFactory());

//        if ( isAjaxUpload ) {
//            upload.setHeaderEncoding( "UTF-8" );
//        }

		try {
//			FileItemIterator iterator = upload.getItemIterator(request);
			InputStream inputStream = null;
			MultipartFile mf = null;
			Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
			for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
				mf = entity.getValue();
				inputStream = mf.getInputStream();
			}
//			while (iterator.hasNext()) {
//				fileStream = iterator.next();
//				
//				if (!fileStream.isFormField())
//					break;
//				fileStream = null;
//			}

//			if (fileStream == null) {
//				return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
//			}
			if (inputStream == null) {
				return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
			}

			String savePath = (String) conf.get("savePath");
//			String originFileName = fileStream.getName();
			String originFileName = mf.getOriginalFilename();
			String suffix = FileType.getSuffixByFilename(originFileName);

			originFileName = originFileName.substring(0,
					originFileName.length() - suffix.length());
			savePath = savePath + suffix;

			long maxSize = ((Long) conf.get("maxSize")).longValue();

			if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
				return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
			}

			savePath = PathFormat.parse(savePath, originFileName);
			
			State storageState = null;
			
			if(AliyunOSSUtil.ossOpen){
//				InputStream is = fileStream.openStream();
				storageState = StorageManager.saveOSSFileByInputStream(inputStream,
						savePath, maxSize);
				inputStream.close();
				if (storageState.isSuccess()) {
					storageState.putInfo("url", AliyunOSSUtil.imgHost + PathFormat.format(savePath));
					storageState.putInfo("type", suffix);
					storageState.putInfo("original", originFileName + suffix);
				}
			}else{
//				String physicalPath = (String) conf.get("rootPath") + savePath;
				
//				InputStream is = fileStream.openStream();
				String physicalPath = (String)conf.get("filePath") + savePath;
				storageState = StorageManager.saveFileByInputStream(inputStream,
						physicalPath, maxSize);
				inputStream.close();
				//图片压缩
				String path = storageState.getInfo("path");
				if(conf.get("upload_type")!=null && ActionMap.UPLOAD_IMAGE == (int)conf.get("upload_type")){
					Double scale = (Double) conf.get("imageCompressScale");
					Double quality = (Double) conf.get("imageCompressQuality");
					Thumbnails.of(path) //
					.scale(scale) //
					.outputQuality(quality) //
					.toFile(path);
				}
				if (storageState.isSuccess()) {
					storageState.putInfo("url", PathFormat.format(savePath));
					storageState.putInfo("type", suffix);
					storageState.putInfo("original", originFileName + suffix);
				}
			} 
			return storageState;
		} catch (IOException e) {
			return new BaseState(false, AppInfo.IO_ERROR);
		}
	}

	private static boolean validType(String type, String[] allowTypes) {
		List<String> list = Arrays.asList(allowTypes);

		return list.contains(type);
	}
}
