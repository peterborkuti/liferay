import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;

//The maximum number of folders loaded into memory
//if code runs slow, it can be higher
LIMIT = 100;

//if true script runs and only prints, not resetting permissions
//use this for testing
TEST = true;

roleName = "Site Member";

//The bit value of default actionIds
//If a folder has an actionIds equals to this, will be resetting to newActionIds
actionIds = 29;
newActionIds = 1;

name = "com.liferay.portlet.documentlibrary.model.DLFolder";

//All the scopes in resourcepermission table
scopes = ResourceConstants.SCOPES;

//Assumes, that there are not too many companies compared to memory limits
companies = CompanyLocalServiceUtil.getCompanies();

for (company in companies) {
	long cId = company.getCompanyId();

	//Probably number of folders are too big for reading all the folders in one shot
	fCount = DLFolderLocalServiceUtil.getCompanyFoldersCount(cId);

	for (int i = 0; i < fCount; i += LIMIT) {
		folders = DLFolderLocalServiceUtil.getCompanyFolders(cId, i, i + LIMIT);

		for (folder in folders) {
			String fId = String.valueOf(folder.getFolderId());
			companyId = folder.getCompanyId();
			role = RoleLocalServiceUtil.getRole(companyId, roleName);
			roleId = -1;
			if (role != null) {
				roleId = role.getRoleId();
			}

			for (scope in scopes) {
				resPerms = 
					ResourcePermissionLocalServiceUtil.getResourcePermissions(
					cId, name, scope, fId);

				for (rp in resPerms) {

					if ((rp.getActionIds() == actionIds) && (rp.getRoleId() == roleId)) {
						println("Resetting: " + folder.getPath() + ", " + rp.getPrimaryKey());

						if (!TEST) {
							rp.setActionIds(newActionIds);
							ResourcePermissionLocalServiceUtil.updateResourcePermission(rp);
						}
					}
				}
			}
		}
	}
}