package eu.citadel.liferay.portlet.converter;

import static eu.citadel.liferay.extendedmvc.ExtMVCFactory.DEFAULT_CONTROLLER_KEY;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import eu.citadel.liferay.extendedmvc.ExtMVCFactory;
import eu.citadel.liferay.extendedmvc.ExtMVCPortlet;

/**
 * @author ttrapanese
 */
public class ConverterPortlet extends ExtMVCPortlet {
	private static Log _log = LogFactoryUtil.getLog(ConverterPortlet.class);
	
	public static final String CONTR_SOURCE_DATA 	= "sourceData";
	public static final String CONTR_CHOOSE_DATA 	= "chooseData";
	public static final String CONTR_INPUT_DETAIL 	= "inputDetail";
	public static final String CONTR_SEMANTIC_MATCH	= "semanticMatch";
	public static final String CONTR_CHOOSE_EXPORT 	= "chooseExport";
	public static final String CONTR_EXPORT_SCHEMA  = "exportSchema";
	public static final String CONTR_SAVE_FILE 		= "saveFile";

	private static ExtMVCFactory factory = null;
	static{
		factory = new ExtMVCFactory();
		factory.putController(DEFAULT_CONTROLLER_KEY, ContrSourceDataset.class);
		factory.putController(CONTR_SOURCE_DATA		, ContrSourceDataset.class);
		factory.putController(CONTR_CHOOSE_DATA		, ContrChooseDataset.class);
		factory.putController(CONTR_INPUT_DETAIL	, ContrInputDetail.class);
		factory.putController(CONTR_SEMANTIC_MATCH	, ContrSemanticMatch.class);
		factory.putController(CONTR_CHOOSE_EXPORT	, ContrChooseExport.class);
		factory.putController(CONTR_EXPORT_SCHEMA	, ContrExportSchema.class);
		factory.putController(CONTR_SAVE_FILE		, ContrSaveFile.class);
	}
	@Override
	public ExtMVCFactory getFactory() {
		return factory;
	}
	
	public static Log getLogger(){
		return _log;
	}
}
