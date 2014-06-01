
package eu.citadel.liferay.tag;

import javax.portlet.PortletResponse;

import com.liferay.portal.kernel.dao.search.RowChecker;

/**
 * @author ttrapanese
 */
public class MyRowChecker extends RowChecker {

	public MyRowChecker(PortletResponse portletResponse) {
		super(portletResponse);
	}

	@Override
	public String getAllRowsCheckBox() {
		return "";
	}
}
