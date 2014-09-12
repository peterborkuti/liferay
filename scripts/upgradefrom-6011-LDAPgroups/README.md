Whe upgrading from 6011, a new field added to UserGroups table:addedByLDAPImport.
It is for storing if a group is added by LDAP import to change it when a user's group is changed in LDAP.
If a group lives only in Liferay, changing on LDAP side will not affect that group in Liferay.

At upgrade, this field is set to "NO" (0, null, etc), because during the upgrade we do not have information about
the group is imported from LDAP or not.

The consequence of this is that when a group was imported from LDAP, after upgrade, portal will not change this user-group relation
according to changes in LDAP.

If all your groups are imported from LDAP, this dummy script will set the addedByLDAPImport field of all the groups to true.
