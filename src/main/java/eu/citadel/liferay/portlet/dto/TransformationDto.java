package eu.citadel.liferay.portlet.dto;


import static eu.citadel.converter.data.datatype.BasicDatatypeUtils.DESCRIPTION;
import static eu.citadel.converter.data.datatype.BasicDatatypeUtils.FORMAT;
import static eu.citadel.converter.data.datatype.BasicDatatypeUtils.MANDATORY;
import static eu.citadel.converter.data.datatype.BasicDatatypeUtils.NAME;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.liferay.portal.kernel.util.UniqueList;

import eu.citadel.converter.schema.obj.BasicSchemaObjAbstractValue;
import eu.citadel.converter.schema.obj.BasicSchemaObjAttributes;
import eu.citadel.converter.schema.obj.BasicSchemaObjValueBoolean;
import eu.citadel.converter.schema.obj.BasicSchemaObjValueInteger;
import eu.citadel.converter.schema.obj.BasicSchemaObjValueList;
import eu.citadel.converter.schema.obj.BasicSchemaObjValueNull;
import eu.citadel.converter.schema.obj.BasicSchemaObjValueObject;
import eu.citadel.converter.schema.obj.BasicSchemaObjValueString;

/**
 * @author ttrapanese
 */
public class TransformationDto {
	private Integer id;
	private String name;
	private String description;
	private Boolean mandatory;
	private List<formatElement> format;
	private List<String> content;
	
	public TransformationDto(Entry<BasicSchemaObjAbstractValue<?>, BasicSchemaObjAttributes> entry, Locale pageContext) {
		content = new UniqueList<String>();
		setMandatory(true);
		BasicSchemaObjAbstractValue<?> idObj = entry.getKey();
		if (!(idObj instanceof BasicSchemaObjValueNull)) {
			
			BasicSchemaObjAttributes attrs = entry.getValue();
			
			BasicSchemaObjAbstractValue<?> tmp = idObj;
			if (tmp != null && tmp.getValue() != null && tmp instanceof BasicSchemaObjValueInteger) {
				setId((Integer)((BasicSchemaObjValueInteger) tmp).getValue());
			}

			tmp = attrs.get(NAME);
			if (tmp != null && tmp.getValue() != null && tmp instanceof BasicSchemaObjValueString) {
				setName(((BasicSchemaObjValueString) tmp).getValue());
			}

			tmp = attrs.get(DESCRIPTION);
			if (tmp != null && tmp.getValue() != null && tmp instanceof BasicSchemaObjValueString) {
				setDescription(((BasicSchemaObjValueString) tmp).getValue());
			}

			tmp = attrs.get(MANDATORY);
			if (tmp != null && tmp.getValue() != null && tmp instanceof BasicSchemaObjValueBoolean) {
				setMandatory(((BasicSchemaObjValueBoolean) tmp).getValue());
			}

			tmp = attrs.get(FORMAT);
			if (tmp != null && tmp.getValue() != null && tmp instanceof BasicSchemaObjValueObject){
				BasicSchemaObjAbstractValue<?> df = ((BasicSchemaObjValueObject)tmp).getValue().get("list");
				List<formatElement> myFrmtList = new ArrayList<formatElement>();
				if (df != null && df.getValue() != null && df instanceof BasicSchemaObjValueList) {
					List<BasicSchemaObjAbstractValue<?>> list = ((BasicSchemaObjValueList)df).getValue();
					for (BasicSchemaObjAbstractValue<?> listElement : list) {
						if (listElement != null && listElement.getValue() != null) {
							if (listElement instanceof BasicSchemaObjValueObject) {
								Map<String, BasicSchemaObjAbstractValue<?>> listKeyValue = ((BasicSchemaObjValueObject)listElement).getValue();
								Set<String> labels = listKeyValue.keySet();
								for (String label : labels) {
									BasicSchemaObjAbstractValue<?> valueObj = listKeyValue.get(label);
									if(valueObj != null && valueObj instanceof BasicSchemaObjValueString) {
											String value = ((BasicSchemaObjValueString)valueObj).getValue();
											myFrmtList.add(new formatElement(label, value));
										}
								}
							}else if (listElement instanceof BasicSchemaObjValueString) {
								String label = ((BasicSchemaObjValueString)listElement).getValue();
								String value = label;
								myFrmtList.add(new formatElement(label, value));
							}
						}						
					}
				}
				if(myFrmtList.size() > 0)
					setFormat(myFrmtList);
			}
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getMandatory() {
		return mandatory;
	}
	
	public Boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}

	public List<formatElement> getFormat() {
		return format;
	}

	public void setFormat(List<formatElement> format) {
		this.format = format;
	}
	
	public List<String> getContent() {
		return content;
	}

	public void setContent(List<String> content) {
		this.content = content;
	}
	
	public void addContent(String content) {
		this.content.add(content);
	}

	public class formatElement{
		private String label;
		private String value;
		public formatElement(String label, String value) {
			setLabel(label);
			setValue(value);
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
	public String getContentString(){
		if(getContent() == null)
			return "";
		StringBuilder ret = new StringBuilder();
		for (String str : getContent()) {
			ret.append(str);
			ret.append('#');
		}
		return ret.toString();
	}
}