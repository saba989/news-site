"use strict";
(function(ns, $, $document){
    $(document).ready(function () {
    $("#btnSubmit").click(function (event) { 
        let collectionName = $("#collectionName").val();
		let pagePath = $("#pagePath").val();
        let indexCheckbox = $("#indexCheckbox").prop('checked');

        var itemsLength = $("#sitePaths")[0].items.length;

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
                paths:links
            },
            success: function (data) {
                if(data == "OK")
                    showDialog("success", "Info", "Collection created successfully in Solr.");
                else
                    showDialog("error", "Error", "Failed while creating collection in Solr.");
            },
            error: function (e) {
                showDialog("error", "Error", "Failed while creating collection in Solr.");
            }
        });
    });

})
})(Granite.author, Granite.$, Granite.$(document));