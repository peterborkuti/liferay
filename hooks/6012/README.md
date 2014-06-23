=== IE11 compat hook ===

IE11 causes issues with CKEditor. Here is a simple workaround, which sets IE into IE9 compat mode with this meta tag in html\common\themes\top_meta-ext.jsp:

```
<meta http-equiv="X-UA-Compatible" content="IE=9">
```

=== shutdown hook ===

Escaping the shutdown warning message

Customer originally got a shutdown hook + a shutdown portlet. The shutdown portlet is embedded into a theme
and polls the server in every 2 secs for a message.

The shutdown hook+portlet was developed one of my workmates and was not committed to 6.0.x,
but customer is happy with it.