$(document).ready(function() {

    window.CL = window.CL || {};
    window.CL.console = window.CL.console || {};


    var $select = new CUI.Autocomplete({ element:'#cl-console-user-select' });
    var baseUrl = $('#cl-console-user-select').attr('data-request-path');
    var $container = $('#cl-console-table-container');
    $select.on('selected', function(e) {
        loadTable(e.selectedValue)
    });

    function loadTable(user) {
        $.ajax({
            url: baseUrl + '.table.' + user + '.html'
        }).done(function(html) {
            $container.html(html);
            doQuery($query.val());
            window.CL.forms.refresh();
        });
    }

    function doQuery(text) {
        var regex = new RegExp(text, 'i');
        $container.find('tbody tr').each(function(index, e) {
            var $row = $(this);
            var match = false;
            $row.find('.js-query').each(function(index, e) {
                var html = $(this).html();
                if (html.match(regex)) {
                    match = true;
                }
            });
            $row.toggle(match);
        });
    }

    var $query = $('#cl-console-query');
    $query.keyup(function() {
        window.setTimeout(function() {
            doQuery($query.val())
        }, 1000)
    })
});