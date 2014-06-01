package eu.citadel.liferay.portlet.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.liferay.portal.kernel.util.Validator;

import eu.citadel.converter.data.dataset.DatasetType;
import eu.citadel.converter.data.metadata.BasicMetadata;
import eu.citadel.converter.data.metadata.BasicMetadataObj;
import eu.citadel.converter.data.metadata.BasicMetadataUtils;
import eu.citadel.converter.schema.obj.BasicSchemaObjAbstractValue;
import eu.citadel.converter.schema.obj.BasicSchemaObjAttributes;
import eu.citadel.converter.schema.obj.BasicSchemaObjElements;
import eu.citadel.converter.schema.obj.BasicSchemaObjValueInteger;
import eu.citadel.converter.schema.obj.BasicSchemaObjValueList;
import eu.citadel.converter.schema.obj.BasicSchemaObjValueNull;
import eu.citadel.converter.schema.obj.BasicSchemaObjValueObject;
import eu.citadel.converter.schema.obj.BasicSchemaObjValueString;
import eu.citadel.converter.transform.config.BasicTransformationConfig;
import eu.citadel.converter.transform.config.BasicTransformationConfigObj;
import eu.citadel.converter.transform.config.BasicTransformationConfigUtils;
import eu.citadel.liferay.portlet.dto.DatasetDto;
import eu.citadel.liferay.portlet.dto.MetadataDto;
import eu.citadel.liferay.portlet.dto.TransformationDto;

/**
 * @author ttrapanese
 */
public class JsonBuilder {
	private static BasicSchemaObjValueList fromListToBasicSchemaObjValueList(List<String> list){
		BasicSchemaObjValueList ret = new BasicSchemaObjValueList();
		List<BasicSchemaObjAbstractValue<?>> paramsList = Lists.newArrayList();
		for (String str : list) {
			if(Validator.isNull(str)) {
				paramsList.add(new BasicSchemaObjValueNull());
			} else {
				paramsList.add(new BasicSchemaObjValueString(str));
			}
		}
		ret.setValue(paramsList);
		return ret;
	}
	
	
	public static BasicMetadata metadataBuild(List<MetadataDto> metadataList, DatasetDto dataset) {
		BasicMetadataObj obj = new BasicMetadataObj();
		BasicSchemaObjElements elements = new BasicSchemaObjElements();
		// id = null
		BasicSchemaObjAttributes generalAttributes = new BasicSchemaObjAttributes();
		
		generalAttributes.put(BasicMetadataUtils.FIRST_ROW			, new BasicSchemaObjValueString(dataset.getFirstRowValue()));
		if(dataset.getDataset().getType().equals(DatasetType.TYPE_CSV)) {
			generalAttributes.put(BasicMetadataUtils.TYPE			, new BasicSchemaObjValueString(DatasetType.TYPE_CSV.toLowerCase()));
			generalAttributes.put(BasicMetadataUtils.CSV_DELIMITER	, new BasicSchemaObjValueString(dataset.getDelimiter()));		
			generalAttributes.put(BasicMetadataUtils.CSV_QUOTE		, new BasicSchemaObjValueString(dataset.getQuote()));
			generalAttributes.put(BasicMetadataUtils.CSV_NEWLINE	, new BasicSchemaObjValueString(dataset.getNewline()));
		} else if(dataset.getDataset().getType().equals(DatasetType.TYPE_EXCEL)) {
			generalAttributes.put(BasicMetadataUtils.TYPE			, new BasicSchemaObjValueString(DatasetType.TYPE_EXCEL.toLowerCase()));
		}
		elements.put(new BasicSchemaObjValueNull(), generalAttributes);

		for (MetadataDto meta : metadataList) {
			BasicSchemaObjAttributes row = new BasicSchemaObjAttributes();
			//CATEGORY
			String category = meta.getCategory().keySet().toArray(new String[1])[0];
			row.put(BasicMetadataUtils.CATEGORY, new BasicSchemaObjValueString(category));
			//CONTEXT
			BasicSchemaObjAbstractValue<?> contextBasicSchemaObj = new BasicSchemaObjValueNull();
			List<String> context = new ArrayList<String>(meta.getContext().keySet());
			if(context.size() == 1){
				contextBasicSchemaObj = new BasicSchemaObjValueString(context.get(0));
			}else if(context.size() > 1){
				contextBasicSchemaObj = fromListToBasicSchemaObjValueList(context);
			}
			row.put(BasicMetadataUtils.CONTEXT, contextBasicSchemaObj);
			elements.put(new BasicSchemaObjValueInteger(meta.getId()), row);
		}
		obj.setElements(elements);
		return new BasicMetadata(obj);
	}

	public static BasicTransformationConfig schemaBuild(List<MetadataDto> metadataList, List<TransformationDto> transfList) throws Exception {
		BasicSchemaObjElements elements = new BasicSchemaObjElements();
		//id = null
		BasicSchemaObjAttributes generalAttributes = new BasicSchemaObjAttributes();
		elements.put(new BasicSchemaObjValueNull(), generalAttributes);

		for (TransformationDto t : transfList) {
			BasicSchemaObjAttributes jsonItem = new BasicSchemaObjAttributes();
			//Source
			Map<String, BasicSchemaObjAbstractValue<?>> mapSource = new HashMap<>();			
			mapSource.put(BasicTransformationConfigUtils.DATATYPE, new BasicSchemaObjValueInteger(t.getId()));			
			BasicSchemaObjValueObject objSource = new BasicSchemaObjValueObject(mapSource);		
	
			//Target
			BasicSchemaObjAbstractValue<?> objTarget = getMetadata(t.getContent());	

			jsonItem.put(BasicTransformationConfigUtils.SOURCE, objSource);
			jsonItem.put(BasicTransformationConfigUtils.TARGET, objTarget);
			elements.put(new BasicSchemaObjValueInteger(t.getId()), jsonItem);
		}
		BasicTransformationConfigObj obj = new BasicTransformationConfigObj(elements);
		return new BasicTransformationConfig(obj);
	}

	
	private static BasicSchemaObjAbstractValue<?> getMetadataComponent(String item) throws Exception {
		BasicSchemaObjAbstractValue<?> obj1 = null;
		if (!item.contains("{plain_text}") ) {
			BasicSchemaObjValueInteger id1 = new BasicSchemaObjValueInteger(Integer.valueOf(item));
			Map<String, BasicSchemaObjAbstractValue<?>> map1 = new HashMap<>();			
			map1.put(BasicTransformationConfigUtils.METADATA, id1);			
			obj1 = new BasicSchemaObjValueObject(map1);	
		} else  {
			obj1 = new BasicSchemaObjValueString(item.replace("{plain_text}", ""));
		} 
		return obj1;
	}
	private static BasicSchemaObjAbstractValue<?> getMetadata(List<String> list) throws Exception {
		int nElements = list.size();
		switch (nElements) {
		case 0:
			return new BasicSchemaObjValueNull();
		case 1:
			return getMetadataComponent(list.get(0));
		default:
			//Piu colonne
			List<BasicSchemaObjAbstractValue<?>> listObj = new ArrayList<BasicSchemaObjAbstractValue<?>>(nElements);
			for (String item : list) {
				listObj.add(getMetadataComponent(item));
			}
			return new BasicSchemaObjValueList(listObj);
		}
	}
}