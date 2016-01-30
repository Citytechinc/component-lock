window.CL.siteadmin = {

    /**
     * A condition that prevents editing properties on denied page types.
     * This condition is specified at /apps/wcm/core/content/siteadmin/actions/properties
     * @returns {boolean}
     */
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