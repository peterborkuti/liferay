<%@ include file="/init.jsp" %>

<portlet:defineObjects />

<liferay-theme:defineObjects />

<portlet:resourceURL var="resourceURL" id="findBrokenUrls"/>

<%
	int dlFileEntriesCount = DLFileEntryLocalServiceUtil.getFileEntriesCount();
%>

<p>This portlet will list the File Entry URLs which are broken.</p>
<p>Number of DlFileEntries :<%= dlFileEntriesCount %></p>
<p>

<aui:input title="Number of processed chunks. You can set it. If -1, process is finished" name="resultIndex" type="text" value="0" />
<aui:button title="start or continue the process" name="start" value="Start/Continue" />
<aui:button title="stop the process" name="stop" value="Stop" />

<div id="<portlet:namespace/>data"/>

<aui:script use="aui-base,aui-io-request,aui-datatable,aui-datataype,escape">
			var nameSpace="#<portlet:namespace/>";

			console.log(nameSpace + 'result');

			var resultIndexObj = A.one(nameSpace + 'resultIndex');
			var buttonStart = A.one(nameSpace + 'start');
			var buttonStop = A.one(nameSpace + 'stop');
			var data = [];
			var table = new A.DataTable.Base({
				columnset: [{ key : "url",
							allowHTML: true,
							formatter: function (o) {
								var value = A.Escape.html(o.value);
								return '<a href="' + value + '">' + value + '</a>';
							}}, "folder", "title"],
				recordset: data,
			}).plug(A.Plugin.DataTableScroll, {
				height: 200
			}).plug(A.Plugin.DataTableSort)
			.render(nameSpace + 'data');

			var isStopped = false;

			function showResponseData(jsonArray) {
				if (jsonArray) {
					Array.prototype.push.apply(data,jsonArray);
					table.set('recordset', data);
				}
			}

			function sendRequest(index) {
				if (!index || (index < 0)) {
					return;
				}

				if (isStopped) {
					return;
				}

				A.io.request(
					'<%= resourceURL.toString()%>',
					{
						after: {
							success: function(event, id, obj) {
								var responseData = this.get('responseData');
								console.log("success");
								console.log(responseData.index);
								showResponseData(responseData.files);
								resultIndexObj.set('value', responseData.index);
								sendRequest(responseData.index);
							},
							failure: function(event, id, obj) {
								console.log("failure")
							}
						},
						data: {
							'index': index
						},
						dataType: 'json'
					}
				);
			}

			function startProcess(event) {
				event.preventDefault();
				isStopped = false;
				console.log("startProcess");

				var index = resultIndexObj.get('value');

				sendRequest(index);
			}

			function stopProcess(event) {
				event.preventDefault();
				isStopped = true;
			}
			

			buttonStart.on('click', startProcess);
			buttonStop.on('click', stopProcess);

</aui:script>