;(function(A, Liferay) {
	var LiferayAUI = Liferay.AUI;

	var COMBINE = LiferayAUI.getCombine();

	var GROUPS = AUI.defaults.groups;

	var PATH_COMBO = LiferayAUI.getComboPath();

	var PATH_JAVASCRIPT = LiferayAUI.getJavaScriptRootPath();

	var PATH_LIFERAY = PATH_JAVASCRIPT + '/liferay/';

	var PATH_MISC = PATH_JAVASCRIPT + '/misc/';

	var REGEX_DASH = /-/g;

	var STR_UNDERSCORE = '_';

	var addPlugin = function(config) {
		var group = config.group || 'liferay';
		var trigger = config.trigger;
		var name = config.name;

		delete config.group;
		delete config.name;

		var module = GROUPS[group].modules[trigger];

		var pluginObj = module.plugins;

		if (!pluginObj) {
			pluginObj = {};

			module.plugins = pluginObj;
		}

		pluginObj[name] = {
			condition: config
		};
	};

	var createLiferayModules = function() {
		var modules = {};

		var moduleList = {
			'asset-categories-selector': ['aui-debounce', 'aui-tree', 'liferay-asset-tags-selector'],
			'asset-tags-selector': ['array-extras', 'async-queue', 'aui-autocomplete', 'aui-dialog', 'aui-io-request', 'aui-live-search', 'aui-textboxlist', 'aui-form-textfield', 'datasource-cache', 'liferay-service-datasource', 'substitute'],
			'auto-fields': ['aui-base', 'aui-data-set', 'aui-io-request', 'aui-parse-content', 'aui-sortable', 'base', 'liferay-undo-manager'],
			'dockbar': ['aui-button-item', 'aui-io-plugin', 'aui-io-request', 'aui-overlay-context', 'aui-overlay-manager', 'event-touch', 'node-focusmanager'],
			'dynamic-select': ['aui-base'],
			'form': ['aui-base'],
			'form-navigator': ['aui-base'],
			'hudcrumbs': ['aui-base', 'plugin'],
			'icon': ['aui-base'],
			'input-move-boxes': ['aui-base', 'aui-toolbar'],
			'layout': ['aui-io-request', 'aui-portal-layout', 'aui-resize', 'dd'],
			'layout-configuration': ['aui-live-search', 'dd', 'liferay-layout'],
			'look-and-feel': ['aui-color-picker', 'aui-dialog', 'aui-io-request', 'aui-tabs-base'],
			'menu': ['aui-base', 'node-focusmanager', 'selector-css3'],
			'navigation': ['aui-form-combobox', 'aui-io-request', 'dd-constrain', 'event-touch', 'json-parse', 'node-event-simulate', 'overlay', 'selector-css3', 'sortable', 'substitute'],
			'navigation-interaction': ['node-focusmanager'],
			'notice': ['aui-base'],
			'panel': ['aui-base', 'aui-io-request'],
			'panel-floating': ['aui-paginator', 'liferay-panel', 'selector-css3'],
			'poller': ['aui-base', 'io', 'json'],
			'portlet-url': ['aui-base', 'aui-io-request', 'querystring-stringify-simple'],
			'ratings': ['aui-io-request', 'aui-rating', 'substitute'],
			'search-container': ['aui-base', 'selector-css3'],
			'session': ['aui-io', 'collection', 'cookie', 'liferay-notice', 'substitute'],
			'service-datasource': ['aui-base', 'datasource-local'],
			'shutdown': ['aui-base', 'collection', 'liferay-notice', 'liferay-poller', 'substitute'],
			'undo-manager': ['aui-data-set', 'base', 'substitute'],
			'upload': ['aui-base', 'aui-swf', 'collection', 'substitute', 'swfupload'],
			'util-list-fields': ['aui-base']
		};

		for (var i in moduleList) {
			modules['liferay-' + i] = {
				path: i.replace(REGEX_DASH, STR_UNDERSCORE) + '.js',
				requires: moduleList[i]
			};
		}

		return modules;
	};

	GROUPS.liferay = {
		base: PATH_LIFERAY,
		root: PATH_LIFERAY,
		combine: COMBINE,
		comboBase: PATH_COMBO,
		modules: createLiferayModules(),
		patterns: {
			'liferay-': {
				configFn: function(config) {
					var path = config.path;

					var nameRE = new RegExp(config.name + '/liferay-([A-Za-z0-9-]+)-min(\.js)');

					path = path.replace(nameRE, '$1$2');
					path = path.replace(REGEX_DASH, STR_UNDERSCORE);

					config.path = path;
				}
			}
		}
	};

	GROUPS.misc = {
		base: PATH_MISC,
		root: PATH_MISC,
		combine: COMBINE,
		comboBase: PATH_COMBO,
		modules: {
			swfupload: {
				path : 'swfupload/swfupload.js'
			},
			swfobject: {
				path: 'swfobject.js'
			}
		}
	};

	GROUPS.portal = {
		base: PATH_LIFERAY,
		combine: false,
		modules: {
			'portal-aui-lang': {
				requires: ['aui-calendar'],
				path: LiferayAUI.getLangPath()
			}
		}
	};

	addPlugin(
		{
			group: 'alloy',
			name: 'portal-aui-lang',
			test: function(A) {
				return true;
			},
			trigger: 'aui-calendar'
		}
	);
})(AUI(), Liferay);