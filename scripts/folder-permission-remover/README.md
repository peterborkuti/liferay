== folder permission setter ==
sets all the folder's permission for site members, when it has the default permission

When adding a folder to Documents and Media, there are default permissions for some roles. It is mostly harmless,
but for site members, the permissions are very strong (ADD_FOLDER, ADD_SUBFOLDER, ADD_SHORTCUT, ADD_DOCUMENT).
If somebody does not set it to lower permissions when adding a folder, and creates many folder, resetting could be
very boring by hand.

Moreover, if somebody wants to reset the default behaviour, the documentlibrary.xml file should be changed in portal-impl.jar resource-actions directory.
However, after changing this file, the previously added folder's permissions wont change.

=== usage ===

The script resets permission for all the folders for site-member roles, which have actionIds==29. (ADD_FOLDER,ADD_SUBFOLDER,ADD_SHORTCUT, ADD_DOCUMENT,VIEW)
It can be used from control panel, Server Administration, Scripts.
Set Language to Groovy, copy-paste the script and push on Execute.
Customer has to backup its database and check the script if it works as intended.
By default it only prints the folder names which will be reset.
For real-resetting, TEST variable must be set to "false".

=== testing the script ===

check if it resets all folder's site-member role's permission
check if it resets only folders which has only add document, add subfolder, add shortcut and view permissions permissions
check if it does not reset other role's permissions
check if it does not reset a site-member permissions which originally does not have only the required permissions
check if it does not reset other resource's permission
=== additional info ===
changing the documentlibrary.xml solves the original issue, so after it the default permission will reset.
The attached script resets the stored folder's permissions.