# Component Lock

Component Lock is an AEM plugin that allows developers and administrators to restrict component authoring by user/group.  Restrictions are specified per principal, per resource type.  For each principal / resource type pairing, the permissions will either be `ALLOW`, `DENY` or `INHERIT` (the default).  Component permissions are evaluated in the following manner:

1. For each component resource type (e.g., a/b/c), determine the permission for the current user name.
2. If the permission is `INHERIT`, continue checking for each group for which the user is a member (starting first with direct memberships and moving out to indirect memberships, although the ordering beyond that is undefined).
3. If permissions for all groups are set to `INHERIT`, repeat steps 1-3 using the parent resource type (e.g, a/b/c -> a/b)

If the first non-`INHERIT` permission encountered is `DENY`, the authoring functionality will be blocked for that component.  If it is `ALLOW`, or if all ancestors are set to `INHERIT`, the authoring functionality will be displayed normally.  Inheriting permissions via the resource type parent (as opposed to the resource super type) enables administrators to set permissions for related groups of components with a single setting.

The permissions data is stored in the JCR at /etc/component-lock.  Children of this node correspond to user or group IDs, and descendant nodes contain the resource type tree.  For instance, the permission for user group "contributors" for resource type "a/b/c" can be found at /etc/content-lock/contributors/a/b/c.  This layout allows developers to vault configurations into an AEM instance as a part of the build's content package, and also allows administrators to transfer configurations between systems via the package manager.  In addition, configurations can be edited at runtime using the console at [/apps/component-lock/content/console.html](http://localhost:4502/apps/component-lock/content/console.html)