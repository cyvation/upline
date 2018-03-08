window.onload = function() {
    $.ajaxSettings.async=false;
    var data = $.getJSON('/up/tree2');
    var jsonStr = data.responseJSON;

	var config = {
		id: "tg1",
		width: "600",
		renderTo: "div1",
		headerAlign: "left",
		headerHeight: "30",
		dataAlign: "left",
		indentation: "20",
		folderOpenIcon: "/up/js/treeGrid/images/folderOpen.png",
		folderCloseIcon: "/up/js/treeGrid/images/folderClose.png",
		defaultLeafIcon: "/up/js/treeGrid/images/defaultLeaf.gif",
		hoverRowBackground: "false",
		folderColumnIndex: "1",
		itemClick: "itemClickEvent",
		columns: [{
			headerText: "",
			headerAlign: "center",
			dataAlign: "center",
			width: "20"
		},
		{
			headerText: "历史记录",
			dataField: "name",
			headerAlign: "center",
			handler: "customOrgName",
            width: "400"
		},
		{
			headerText: "时间",
			dataField: "time",
			headerAlign: "center",
			dataAlign: "center",
			width: "200"
		},
		{
			headerText: "回滚",
            dataField: "revert",
			headerAlign: "center",
			dataAlign: "center",
			width: "50",
			handler: "customLook"
		}],
        data:jsonStr
	};
	var treeGrid = new TreeGrid(config);
	treeGrid.show()
}