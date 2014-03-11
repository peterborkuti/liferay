
package com.liferay.groupidcleaner.startupaction;

import java.util.List;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SimpleAction;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.BaseModel;
import com.liferay.portal.service.GroupLocalServiceUtil;

public class GroupidCleaner extends SimpleAction {

	public static final String CLASSNAME = GroupidCleaner.class.getSimpleName();

	public GroupidCleaner() {

		super();
	}

	private void cleanChunk(
		int start, int end, int count, String methodGetEntries,
		String classModel, String methodDeleteEntry, String classLocalSUFQDN,
		Class service, boolean dontDelete)
		throws Exception {

		_log.debug("cleanChunk: from " + start + " to " + end + ", count: " + count);

		List entries = null;
		long deletedCount = 0;
		long failedCount = 0;

		try {
			_log.debug("invoking " + methodGetEntries + " on " +
				classLocalSUFQDN);
			java.lang.reflect.Method method =
				service.getMethod(methodGetEntries, int.class, int.class);
			entries = (List) method.invoke(null, start, Math.min(count, end));
		}
		catch (Exception e) {
			_log.debug("Could not find entries.");
			e.printStackTrace();
			return;
		}

		_log.debug("Count of selected items: " + entries.size());
		_log.debug("");

		long groupId = 0;

		long id = 0;

		for (Object entry : entries) {

			// get ID
			BaseModel bm = (BaseModel) entry;
			id = (Long) (bm.getPrimaryKeyObj());
			// _log.debug("Checking id: " + id);

			// get groupId

			Class model = Class.forName(classModel);
			java.lang.reflect.Method getGroupMethod =
				model.getMethod("getGroupId");
			groupId = (Long) getGroupMethod.invoke(entry);

			if (groupId > 0) {
				try {
					if (GroupLocalServiceUtil.fetchGroup(groupId) == null) {
						_log.debug("Missing group id:" + groupId);
						if (!dontDelete) {
							try {
								java.lang.reflect.Method method =
									service.getMethod(
										methodDeleteEntry, long.class);
								method.invoke(null, id);
								deletedCount++;
							}
							catch (Exception e) {
								// TODO Auto-generated catch block
								_log.error("can not delete entry:" + id, e);
								failedCount++;
							}
						}
					}
				}
				catch (SystemException e) {
					// TODO Auto-generated catch block
					_log.error("Error when getting groupid:" + groupId);
				}
			}
		}

		_log.debug("");
		_log.debug("Number of deleted entries:" + deletedCount);
		_log.debug("Number of failed deletion of entries:" + failedCount);
		_log.debug("");

	}

	private void doDelete(
		String className, String packageName, boolean dontDelete, int chunks)
		throws Exception {

		String classNamePlural = "";
		if (className.endsWith("y")) {
			classNamePlural =
				className.substring(0, className.length() - 1) + "ies";
		}
		else {
			classNamePlural = className + "s";
		}

		String classLocalSU = className + "LocalServiceUtil";
		String methodCount = "get" + classNamePlural + "Count";
		String methodGetEntries = "get" + classNamePlural;
		String methodDeleteEntry = "delete" + className;
		String modelPackage = packageName + "model";
		String classModel = modelPackage + "." + className + "Model";

		String servicePackageName = packageName + "service";
		String classLocalSUFQDN = servicePackageName + "." + classLocalSU;

		Class<?> service = null;
		try {
			service = Class.forName(classLocalSUFQDN);
		}
		catch (ClassNotFoundException e1) {
			_log.error("class " + classLocalSUFQDN + " not found");
			return;
		}

		int count = 0;

		try {
			_log.debug("invoking " + methodCount + " on " + classLocalSUFQDN);
			java.lang.reflect.Method method = service.getMethod(methodCount);
			count = (Integer) method.invoke(null);
		}
		catch (Exception e) {
			_log.debug("Could not call method:" + methodCount);
			e.printStackTrace();
			return;
		}

		int chunkCount = (count / chunks) + 1;

		int j = 1;
		for (int i = 0; i < count; i += chunkCount) {
			_log.debug(className + " chunk:" + j);
			cleanChunk(
				i, i + chunkCount, count, methodGetEntries, classModel,
				methodDeleteEntry, classLocalSUFQDN, service, dontDelete);
			j++;

		}

		return;
	}

	@Override
	public void run(String[] arg0)
		throws ActionException {

		boolean dontDelete = true;

		dontDelete = GetterUtil.getBoolean(PropsUtil.get("dont.delete"), true);

		String entryprop =
			GetterUtil.getString(PropsUtil.get("cleaner.entries"), "");

		if (Validator.isNull(entryprop)) {
			_log.error("cleaner.properties is empty. There is no table to clean");
			return;
		}

		_log.debug("cleaner.entries:" + entryprop);

		int chunks = GetterUtil.getInteger(PropsUtil.get("cleaner.chunks"), 0);

		String[] entries = StringUtil.split(entryprop, ";");

		_log.debug(CLASSNAME + " hook started");
		_log.debug("");

		for (String entry : entries) {
			String[] parts = StringUtil.split(entry, ",");

			if (parts.length != 2) {
				_log.error("bad entry in cleaner.properties:" + entry);
				continue;
			}

			_log.debug("start deleting entries with missing groupid from " +
				parts[0]);

			String className = parts[0];
			String packageName = parts[1];

			try {
				doDelete(className, packageName, dontDelete, chunks);
			}
			catch (Exception e) {
				_log.error(e, e);
			}
		}

		_log.debug(CLASSNAME + " hook ended");

	}

	private static Logger _log = Logger.getLogger(GroupidCleaner.class);

}
