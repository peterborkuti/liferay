# IE11 compat hook

IE11 causes issues with CKEditor. Here is a simple workaround, which sets IE into IE9 compat mode with this meta tag in html\common\themes\top_meta-ext.jsp:

```
<meta http-equiv="X-UA-Compatible" content="IE=9">
```
