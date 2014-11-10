/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package eu.citadel.liferay.tag;

import java.util.List;

import javax.portlet.PortletURL;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTag;

import org.apache.commons.lang.StringUtils;

import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.dao.search.SearchEntry;
import com.liferay.portal.kernel.dao.search.TextSearchEntry;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.ui.SearchContainerColumnTag;
import com.liferay.taglib.ui.SearchContainerRowTag;

/**
 * @author ttrapanese
 */
public class SearchContainerColumnListTag<R> extends SearchContainerColumnTag
		implements BodyTag {

	public static final String ID_MAP_KEY = "xml_id_map_key";
	
	
	@Override
	public int doEndTag() {
		try {
			@SuppressWarnings("unchecked")
			SearchContainerRowTag<R> searchContainerRowTag = (SearchContainerRowTag<R>)findAncestorWithClass(this, SearchContainerRowTag.class);

			ResultRow resultRow = searchContainerRowTag.getRow();

			if (index <= -1) {
				List<SearchEntry> searchEntries = resultRow.getEntries();
				index = searchEntries.size();
			}

			if (resultRow.isRestricted()) {
				_href = null;
			}
			
			for(int i = 0; i < _list.size(); i++){
				String key = String.valueOf(i);
				Object tmp = _list.get(i);
				if(tmp == null) tmp = "";
				String val =  tmp.toString();

				if (_translate) {
					val = LanguageUtil.get(pageContext, val);
				}
				if (getLengthLimit() > 0) {
					val = StringUtils.abbreviate(val, getLengthLimit());
				}
				TextSearchEntry textSearchEntry = new TextSearchEntry();

				textSearchEntry.setAlign(getAlign());
				textSearchEntry.setColspan(getColspan());
				textSearchEntry.setCssClass(getCssClass());
				textSearchEntry.setHref((String)getHref());
				textSearchEntry.setName(val);
				textSearchEntry.setTarget(getTarget());
				textSearchEntry.setTitle(key);
				textSearchEntry.setValign(getValign());

				resultRow.addSearchEntry(index + i, textSearchEntry);
			}

			return EVAL_PAGE;
		}
		finally {
			index = -1;

			if (!ServerDetector.isResin()) {
				align = SearchEntry.DEFAULT_ALIGN;
				_buffer = null;
				colspan = SearchEntry.DEFAULT_COLSPAN;
				cssClass = SearchEntry.DEFAULT_CSS_CLASS;
				_href = null;
				name = null;
				_property = null;
				_sb = null;
				_target = null;
				_title = null;
				_translate = false;
				valign = SearchEntry.DEFAULT_VALIGN;
			}
		}
	}

	@Override
	public int doStartTag() throws JspException {
		@SuppressWarnings("unchecked")
		SearchContainerRowTag<R> searchContainerRowTag = (SearchContainerRowTag<R>) findAncestorWithClass(this, SearchContainerRowTag.class);

		if (searchContainerRowTag == null) {
			throw new JspTagException("Requires liferay-ui:search-container-row");
		}

		if (!searchContainerRowTag.isHeaderNamesAssigned()) {
			List<String> headerNames = searchContainerRowTag.getHeaderNames();
				if(getHeader() != null)
				{
					for (Object obj : getHeader()) {
						String key = "";
						if(obj != null) key = obj.toString();
						headerNames.add(key);
					}
			} else {
				for (int i = 0; i < _list.size(); i++) {
					String key = String.valueOf(i);
					headerNames.add(key);
				}
			}
		}

		if (Validator.isNotNull(_property)) {
			return SKIP_BODY;
		} else if (Validator.isNotNull(_buffer)) {
			_sb = new StringBuilder();

			pageContext.setAttribute(_buffer, _sb);

			return EVAL_BODY_INCLUDE;
		} else {
			return SKIP_BODY;
		}
	}

	
//	public static String formatName(String name) {
//		name = name.replace("_", " ");
//		name = StringUtil.upperCaseFirstLetter(name);
//		name = splitCamelCase(name);
//		return name;
//	}

//	private static String splitCamelCase(String s) {
//		   return s.replaceAll(
//		      String.format("%s|%s|%s",
//		         "(?<=[A-Z])(?=[A-Z][a-z])",
//		         "(?<=[^A-Z])(?=[A-Z])",
//		         "(?<=[A-Za-z])(?=[^A-Za-z])"
//		      ),
//		      " "
//		   );
//		}
	
	public String getBuffer() {
		return _buffer;
	}

	public Object getHref() {
		if (Validator.isNotNull(_href) && (_href instanceof PortletURL)) {
			_href = _href.toString();
		}

		return _href;
	}

	public String getProperty() {
		return _property;
	}

	public String getTarget() {
		return _target;
	}

	public String getTitle() {
		return _title;
	}

	public void setBuffer(String buffer) {
		_buffer = buffer;
	}

	public void setHref(Object href) {
		_href = href;
	}

	public void setProperty(String property) {
		_property = property;
	}

	public void setTarget(String target) {
		_target = target;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setTranslate(boolean translate) {
		_translate = translate;
	}


	public List<Object> getList() {
		return _list;
	}

	public void setList(List<Object> _list) {
		this._list = _list;
	}

	public List<Object> getHeader() {
		return _header;
	}

	public void setHeader(List<Object> _header) {
		this._header = _header;
	}

	public int getLengthLimit() {
		return _lengthLimit;
	}

	public void setLengthLimit(int _lengthLimit) {
		this._lengthLimit = _lengthLimit;
	}

	private String _buffer;
	private Object _href;
	private String _property;
	private StringBuilder _sb;
	private String _target;
	private String _title;
	private boolean _translate;
	private List<Object> _list;
	private List<Object> _header;
	private int _lengthLimit;

}