"use strict";
(function(ns, $, $document){
    $(document).ready(function () {
        var params = document.location.search.substring(1).split('=');
        var path = params[1];
        var pagePaths = [];
        var collectionName = '';
		var indexCheckbox = false;

        $.ajax({
            url:'/bin/solr/editServlet',
            type: "GET",
            async: false,
            data:{
                path: path
            },
            success: function (res) {
                console.log(res);
                pagePaths = res.paths;
                collectionName = res.collectionName;
                indexCheckbox = res.index;
                console.log("success");
            },
            error: function (e) {
                console.log("failure");
            }
        });

		for(i=0;pagePaths!==null && i<pagePaths.length;i++){
            $("#editSitePaths")[0].items.add();
        }

        setTimeout(loadData, 0);


        function loadData() {
            for(i=0;pagePaths!==null && i<pagePaths.length;i++){
                addValue(pagePaths[i],i);
            }
        }

        function addValue(val,index){
			$('[name="'+"./pagePaths/item"+index+"/./pagePath"+'"]').val(val);

        }


	$("#editCollectionName").val(collectionName);
	$("#editIndex").prop('checked', (indexCheckbox === 'true'));

    $("#btnUpdate").click(function (event) {
        let collectionName = $("#editCollectionName").val();
        let indexCheckbox = $("#editIndex").prop('checked');

        var itemsLength = $("#editSitePaths")[0].items.length;

        const links = [];
        for(i=0;i<itemsLength;i++){
            var item = "./pagePaths/item"+i+"/./pagePath";
            links[i] = $('[name="'+item+'"]').val();
        }

        $.ajax({
            url:'/bin/solr',
            type: "POST",
            data: {
                collectionName:collectionName,
                index:indexCheckbox,
                paths:links,
            },
            success: function (data) {
                if(data == "OK")
                    showDialog("success", "Info", "Collection values have been updated successfully in Solr.");
                else
                    showDialog("error", "Error", "Failed while updating Collection values in Solr.");
            },
            error: function (e) {
                showDialog("error", "Error", "Failed while updating Collection values in Solr.");
            }
        });
    });

})
})(Granite.author, Granite.$, Granite.$(document));
