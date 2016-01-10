window.CL.siteadmin = {

    canEditProperties: function() {
        var grid = CQ.wcm.SiteAdmin.getActiveGrid();
        if (grid) {
            var selections = grid.getSelectionModel().getSelections();
            for (var i = 0; i < selections.length; i++) {
                var selection = selections[i];
                if (selection.json['cl:locked']) {
                    return false;
                }
            }
        }
        return true;
    }

};