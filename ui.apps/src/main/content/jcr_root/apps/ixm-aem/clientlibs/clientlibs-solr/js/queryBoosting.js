"use strict";
(function(ns, $, $document){
    $(document).ready(function () {
    $("#btnSave").click(function (event) {

        var itemsLength = $("#boostMultifield")[0].items.length;

        const links = [];

        var map = new Object();
        for(i=0;i<itemsLength;i++){
            var fName = "./boostItems/item"+i+"/./fieldName";
			var bValue = "./boostItems/item"+i+"/./boostValue";

            var nameF = $('[name="'+fName+'"]').val();
			var valueB = $('[name="'+bValue+'"]').val();

            map[nameF] = valueB;
        }

        $.ajax({
            url:'/bin/solr/queryBoost',
            type: "GET",
            data: map,
            success: function () {
                showDialog("success", "Info", "Boost values have been set successfully.");
            },
            error: function (e) {
                showDialog("error", "Error", "Failed while saving boost values.");
            }
        });
    });

})
})(Granite.author, Granite.$, Granite.$(document));