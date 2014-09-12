import com.liferay.portal.service.UserGroupLocalServiceUtil;

//The maximum number of rows loaded into memory
//if code runs slow, it can be higher
LIMIT = 100;

//if true script runs and only prints
//use this for testing
TEST = true;


ugCount = UserGroupLocalServiceUtil.getUserGroupsCount();

for (int i = 0; i < ugCount; i += LIMIT) {

	userGroups =  UserGroupLocalServiceUtil.getUserGroups(i, i + LIMIT);
	for (userGroup in userGroups) {
		if (TEST) {
			println("fake setting addedByLDAPImport to true for " + userGroup.getName());
		}
		else {
			print("setting addedByLDAPImport to true for " + userGroup.getName() + "...");
			userGroup.setAddedByLDAPImport(true);
			userGroup.persist();
			println("done");
		}
	}
}