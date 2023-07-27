"use strict";

function showDialog(variant, headerText, contentText){
    var dialog = new Coral.Dialog().set({});
        dialog.variant = variant;
        dialog.header.innerHTML = headerText;
        dialog.content.innerHTML = contentText;
        dialog.footer.innerHTML =
            "<button is='coral-button' variant='default' coral-close>" + Granite.I18n.get("Ok") + "</button>";
        document.body.appendChild(dialog);
        dialog.show();
        $("button").click(function(){
            window.location.href =
             "/apps/ixm-aem/content/managecollection/configurations/manage-collection/content.html";
     });
}