package eu.citadel.liferay.portlet.converter.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.util.ParamUtil;

import eu.citadel.converter.data.dataset.CsvDataset;
import eu.citadel.converter.data.dataset.Dataset;
import eu.citadel.converter.data.dataset.DatasetType;
import eu.citadel.converter.data.dataset.ExcelDataset;
import eu.citadel.liferay.extendedmvc.ExtViewResult;
import eu.citadel.liferay.portlet.commons.ConverterUtils;
import eu.citadel.liferay.portlet.converter.ConverterPortlet;
import eu.citadel.liferay.portlet.converter.general.ConverterController;
import eu.citadel.liferay.portlet.dto.DatasetDto;

/**
 * @author ttrapanese
 */
/*Step 2*/
public class ContrChooseDataset extends ConverterController {
	private static final String JSP_MAIN_PATH 				= "/html/converter/chooseDataset.jsp";

	public static final String CONTR_PARAM_SELECTED_FILES 	= "contr_param_selected_files";

	@Override
	public ExtViewResult doView(RenderRequest request, RenderResponse renderResponse) throws IOException, PortletException {
		setResult(request, ConverterUtils.getUploadedFileList(request));
		return new ExtViewResult(ConverterPortlet.CONTR_CHOOSE_DATA);
	}
	
	@Override
	public String getViewPath(String viewKey, PortletRequest request, PortletResponse response) {
		return JSP_MAIN_PATH;
	}
	
	@Override
	public ExtViewResult nextStep(ActionRequest request,	ActionResponse actionResponse)  {
		String fileId 			= ParamUtil.getString(request, RowChecker.ROW_IDS);

		List<File> list = ConverterUtils.getUploadedFileList(request);
		if (list.size() <= 0) {
			return new ExtViewResult(ConverterPortlet.CONTR_CHOOSE_DATA);
		}

		File file = list.get(0);
		for (File f : list) {
			if(fileId.equals(f.getName())) {
				file = f;
				break;
			}
		}

		Path path = file.toPath();
		String dataType = DatasetType.detect(path);
		//Create dataType
		getLog(request).debug("selected mimeType: " + dataType);
		Dataset ds = null;
		if (dataType.equals(DatasetType.TYPE_EXCEL)) {
			ds = new ExcelDataset(path);
		} else if (dataType.equals(DatasetType.TYPE_CSV)) {
			ds = new CsvDataset(path);
		}
		
		DatasetDto dsDto = new DatasetDto();
		dsDto.setFile(file.getAbsolutePath());
		dsDto.setFileEntry(file);
		dsDto.setDataset(ds);
		setDatasetDto(request, dsDto);			
		
		return new ExtViewResult(ConverterPortlet.CONTR_INPUT_DETAIL);

	}

	@Override
	public ExtViewResult previousStep(ActionRequest actionRequest,	ActionResponse actionResponse) {
		return new ExtViewResult(ConverterPortlet.CONTR_SOURCE_DATA);
	}

}
