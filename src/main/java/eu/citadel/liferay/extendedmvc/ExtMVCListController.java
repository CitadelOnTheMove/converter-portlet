package eu.citadel.liferay.extendedmvc;

import static com.liferay.portal.kernel.dao.search.SearchContainer.DEFAULT_CUR_PARAM;
import static com.liferay.portal.kernel.dao.search.SearchContainer.DEFAULT_DELTA;
import static com.liferay.portal.kernel.dao.search.SearchContainer.DEFAULT_DELTA_PARAM;
import static com.liferay.portal.kernel.dao.search.SearchContainer.DEFAULT_ORDER_BY_COL_PARAM;
import static com.liferay.portal.kernel.dao.search.SearchContainer.DEFAULT_ORDER_BY_TYPE_PARAM;
import static com.liferay.portal.kernel.dao.search.SearchContainer.DEFAULT_RESULTS_VAR;
import static com.liferay.portal.kernel.dao.search.SearchContainer.DEFAULT_TOTAL_VAR;

import java.util.List;

import javax.portlet.PortletRequest;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.util.ParamUtil;

/**
 * @author ttrapanese
 */
public class ExtMVCListController extends ExtMVCController {
	public static final String		DEFAULT_ORDER_VALUE			= "";
	public static final String		DEFAULT_TYPE_VALUE			= "desc";
	
	public int getStart(PortletRequest request) {
		return getDelta(request) * (getCur(request) - 1);
	}	
	
	public int getEnd(PortletRequest request) {
		return getDelta(request) * getCur(request);
	}	
	
	public int getDelta(PortletRequest request) {
		return ParamUtil.getInteger(request, DEFAULT_DELTA_PARAM, getDefaultDeltaValue());
	}	

	public int getCur(PortletRequest request) {
		return ParamUtil.getInteger(request, DEFAULT_CUR_PARAM, 1);
	}	

	public void setResult(PortletRequest request, List<?> result) {
		request.setAttribute(DEFAULT_RESULTS_VAR, result);
	}
	
	public void setTotal(PortletRequest request, int total) {
		request.setAttribute(DEFAULT_TOTAL_VAR, total);	
	}
	
	public String getOrderByColumn(PortletRequest request) {
		return ParamUtil.getString(request, getDefaultOrderByColumnKey(), getDefaultOrderByCol());
	}
	
	public String getOrderByType(PortletRequest request) {
		return ParamUtil.getString(request, getDefaultOrderByTypeKey(), getDefaultOrderByType());
	}
	
	public void setOrderByColumn(PortletRequest request, String column) {
		request.setAttribute(getDefaultOrderByColumnKey(), column);
	}
	
	public void setOrderByType(PortletRequest request, String type) {
		request.setAttribute(getDefaultOrderByTypeKey(), type);
	}	
	
	protected int getDefaultDeltaValue() {
		return DEFAULT_DELTA;
	}
	
	protected String getDefaultOrderByCol() {
		return DEFAULT_ORDER_VALUE;
	}
	
	protected String getDefaultOrderByType() {
		return SearchContainer.DEFAULT_VAR;
	}
	
	protected String getDefaultOrderByColumnKey() {
		return DEFAULT_ORDER_BY_COL_PARAM;
	}
	
	protected String getDefaultOrderByTypeKey() {
		return DEFAULT_ORDER_BY_TYPE_PARAM;
	}
}
