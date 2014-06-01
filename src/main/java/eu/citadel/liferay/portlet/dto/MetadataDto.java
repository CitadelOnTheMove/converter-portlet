package eu.citadel.liferay.portlet.dto;

import java.util.HashMap;
import java.util.Map;

import com.liferay.portal.kernel.util.Validator;

import eu.citadel.converter.data.metadata.BasicMetadataUtils;

/**
 * @author ttrapanese
 */
public class MetadataDto {
	private Integer id;
	private String name;
	private String example;
	private Map<String, String> category;
	private Map<String, String> context;

	public MetadataDto(Integer id, String name, String example) {
		setId(id);
		setName(name);
		setExample(example);
		category= new HashMap<String, String>();
		context = new HashMap<String, String>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Map<String, String> getContext() {
		return context;
	}

	public void addContext(String key) {
		context.put(key, BasicMetadataUtils.getMap(BasicMetadataUtils.DEFAULT_CATEGORY).get(key));
	}

	public void setContext(Map<String, String> context) {
		this.context = context;
	}

	public Map<String, String> getCategory() {
		if (category == null || category.isEmpty()) {
			return BasicMetadataUtils.getMap(BasicMetadataUtils.DEFAULT_CATEGORY);
		}
		return category;
	}

	public void setCategory(String key) {
		if(Validator.isNotNull(key))
			this.category.put(key, BasicMetadataUtils.getMap(BasicMetadataUtils.DEFAULT_CATEGORY).get(key));
	}


}