package eu.citadel.liferay.portlet.commons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;

import com.google.common.io.Files;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Validator;

import eu.citadel.converter.config.Config;
import eu.citadel.converter.data.dataset.Dataset;
import eu.citadel.converter.exceptions.DatasetException;
import eu.citadel.liferay.portlet.dto.MetadataDto;

/**
 * @author ttrapanese
 */
public class ConverterUtils {
	public static Map<String, String> getPropertiesMap(Class<?> clazz, String propFileName) throws IOException {
        Properties prop = new Properties();
        InputStream inputStream = clazz.getClassLoader().getResourceAsStream(propFileName);
        prop.load(inputStream);
        if (inputStream == null) {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }
 
        Map<String, String> ret = new HashMap<String, String>();
        Set<String> propertiesKeys = prop.stringPropertyNames();
        for (String k : propertiesKeys) {
			ret.put(k, prop.getProperty(k));
		}
        return ret;
    }
	
	public static Map<String, String> getUploadFolderPath(PortletRequest request){
		Map<String, String> folderPath = new LinkedHashMap<String, String>();
		folderPath.put("Upload", "Uploded file");
		folderPath.put(request.getRequestedSessionId(), "Session started in " + new Date());
		return folderPath;
	}
	
	public static Map<String, String> getGeneratedFolderPath(PortletRequest request){
		Map<String, String> folderPath = new LinkedHashMap<String, String>();
		folderPath.put("Generated", "Generated json file");
		folderPath.put(request.getRequestedSessionId(), "Session started in " + new Date());
		return folderPath;
	}
	
	public static List<MetadataDto> getTargetMetadata(List<MetadataDto> list) {
		String notClassifiedKey = Config.getDefaultCategory().keySet().toArray(new String[1])[0];
		
		List<MetadataDto> ret = new ArrayList<MetadataDto>();
		for (MetadataDto dto : list) {
			if(!dto.getCategory().containsKey(notClassifiedKey))
				ret.add(dto);
		}
		return ret;
	}
	
	public static String readableFileSize(long size) {
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static String reduceJson(String json, int size){
		int ind = -1;
		int count = 0;
		int braCount = 0;
		for (int i = 0; i < json.length()-3; i++) {
			if (count >= size) break;
			if (braCount == 1 && json.subSequence(i, i + 3).equals("},{")) {
				ind = i;
				count++;
			}
			if(json.charAt(i) == '[') 
				braCount++;
			if(json.charAt(i) == ']') 
				braCount--;
		}
		if(ind == -1) return json;
		return json.substring(0,ind) +"}]}}";
	}
	
	private static final String getBaseFolderPath(PortletRequest request){
		PortletConfig config = (PortletConfig)request.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		String uploadFolder = config.getInitParameter( ConverterConstants.INIT_PARAM_UPLOAD_FOLDER );
		if(Validator.isNull(uploadFolder)) uploadFolder = "./";
		uploadFolder += request.getPortletSession().getId() + "/";
		return uploadFolder;
	}

	private static final String getDestFilePath(PortletRequest request, String originalName){
		String tmp = getBaseFolderPath(request) + Files.getNameWithoutExtension(originalName);
		String ret = tmp;
		int i=1;
		while (new File(ret).exists()) {
			ret = tmp + "("+ i + ")";
			i++;
		}
		ret += "." + Files.getFileExtension(originalName);
		return ret;
	}

	private static final String getDestOriginalDocumentPath(PortletRequest request, String originalName){
		PortletConfig config = (PortletConfig)request.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		String ret = config.getInitParameter( ConverterConstants.INIT_PARAM_ORIGINAL_FOLDER);
		if(Validator.isNull(ret)) ret = "./";
		ret = getBaseFolderPath(request) + ret + originalName;
		return ret;
	}

	
	public static File uploadFile(PortletRequest request, File entryFile, String fileName) throws IOException {
		String destFilePath = getDestFilePath(request, fileName);
		File ret = new File(destFilePath);
		Files.createParentDirs(ret);
		Files.move(entryFile, ret);
		return ret;
	}	

	public static File uploadFile(PortletRequest request, Dataset ds, String fileName) throws IOException {
		String destFilePath = getDestFilePath(request, fileName);
		File ret = new File(destFilePath);
		Files.createParentDirs(ret);
		try {
			ds.saveAs(ret.toPath(), true);
		} catch (DatasetException e) {
			throw new IOException(e);
		}
		return ret;
	}	

	
	public static File uploadOriginalDocument(PortletRequest request, File entryFile, String fileName) throws IOException {
		String destFilePath = getDestOriginalDocumentPath(request, fileName);
		File ret = new File(destFilePath);
		File dir = ret.getParentFile();
		dir.mkdirs();
		Files.move(entryFile, ret);
		return ret;
	}
	
	public static List<File> getUploadedFileList(PortletRequest request) {
		PortletConfig config = (PortletConfig)request.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
		String tmp = config.getInitParameter( ConverterConstants.INIT_PARAM_ORIGINAL_FOLDER);
		if(Validator.isNull(tmp)) tmp = "./";
		tmp = getBaseFolderPath(request) + tmp;
		File[] arr = (new File(tmp)).listFiles();
		if(arr == null) return new ArrayList<File>();
		return Arrays.asList(arr);
	}
	
	public static final String getFileExt(File file){
		String extension = "";
		String fileName = file.getName();
		int i = fileName.lastIndexOf('.');
		int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

		if (i > p) {
		    extension = fileName.substring(i+1);
		}
		return extension;
	}
	
}
