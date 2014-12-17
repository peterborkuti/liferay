/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.brokenurlfinder.portlet;

import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.PortletResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.liferay.compat.util.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.documentlibrary.NoSuchFileException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFileEntryServiceUtil;

/**
 * @author Peter Borkuti
 */
public class BrokenUrlFinderPortlet  extends MVCPortlet {

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		_log.debug("serverResource called");
		try {
			String resourceID = resourceRequest.getResourceID();

			if (resourceID.equals("findBrokenUrls")) {
				findBrokenUrls(resourceRequest, resourceResponse);
			}
			else {
				super.serveResource(resourceRequest, resourceResponse);
			}
		}
		catch (Exception e) {
			throw new PortletException(e);
		}

	}

	protected void findBrokenUrls(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		int index = ParamUtil.getInteger(resourceRequest, "index");

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		jsonObject.put("success", Boolean.TRUE);

		if (index < 0) {
			jsonObject.put("index", -1);
			jsonObject.put("message", "process ended");
			writeJSON(resourceRequest, resourceResponse, jsonObject);
			return;
		}

		int dlFileEntriesCount = DLFileEntryLocalServiceUtil.getFileEntriesCount();

		if ((index * _CHUNK_SIZE) > dlFileEntriesCount) {
			jsonObject.put("index", -1);
			jsonObject.put("message", "process ended");
			writeJSON(resourceRequest, resourceResponse, jsonObject);
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		JSONArray filesJSONArray = getBrokenUrlsJSONObject(
			resourceResponse, themeDisplay, index);

		jsonObject.put("files", filesJSONArray);
		jsonObject.put("index", index + 1);

		writeJSON(resourceRequest, resourceResponse, jsonObject);
	}

	protected JSONArray getBrokenUrlsJSONObject(
		PortletResponse portletResponse, ThemeDisplay themeDisplay, int index)
	throws Exception {
		List<DLFileEntry> dlFileEntries = DLFileEntryLocalServiceUtil.getFileEntries(index * _CHUNK_SIZE, (index + 1) * _CHUNK_SIZE);

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (DLFileEntry dlFileEntry : dlFileEntries) {
			if (_TEST) {
				_log.debug("test file:" + dlFileEntry.getTitle());
				jsonArray.put(getFileJSONObject(themeDisplay, dlFileEntry));
			}

			try {
				DLFileEntryServiceUtil.getFileAsStream(dlFileEntry.getFileEntryId(), dlFileEntry.getVersion());
			}
			catch (NoSuchFileException nsfe) {
				_log.debug("broken file:" + dlFileEntry.getTitle());
				jsonArray.put(getFileJSONObject(themeDisplay, dlFileEntry));
			}
		}

		return jsonArray;
	}
	
	protected JSONObject getFileJSONObject(
		ThemeDisplay themeDisplay, DLFileEntry dlFileEntry)
	throws Exception {
		JSONObject JSONObject = JSONFactoryUtil.createJSONObject();
		StringBundler sb = new StringBundler(10);
		String url = "";

		sb.append(themeDisplay.getPortalURL());
		sb.append(themeDisplay.getPathContext());
		sb.append("/documents/");
		sb.append(dlFileEntry.getRepositoryId());
		sb.append(StringPool.SLASH);
		sb.append(dlFileEntry.getFolderId());
		sb.append(StringPool.SLASH);
		sb.append(HttpUtil.encodeURL(HtmlUtil.unescape(dlFileEntry.getTitle())));
		sb.append(StringPool.SLASH);
		sb.append(dlFileEntry.getUuid());
		sb.append("?version=");
		sb.append(dlFileEntry.getVersion());

		url = sb.toString();
		JSONObject.put("url", url);
		JSONObject.put("folder", dlFileEntry.getFolder().getName());
		JSONObject.put("title", dlFileEntry.getTitle());

		return JSONObject;
	}

	private static Log _log = LogFactoryUtil.getLog(BrokenUrlFinderPortlet.class);
	private static int _CHUNK_SIZE = 1000;
	private static boolean _TEST = false;

}
