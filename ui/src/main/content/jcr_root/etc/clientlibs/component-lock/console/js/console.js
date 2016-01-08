$(document).ready(function() {

    window.CL = window.CL || {};
    window.CL.console = window.CL.console || {};


    var $select = new CUI.Autocomplete({ element:'#cl-console-user-select' });
    var baseUrl = $('#cl-console-user-select').attr('data-request-path');
    var $container = $('#cl-console-table-container');
    var $visible = $('#cl-console-visible');
    var $total = $('#cl-console-total');
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
        var total = 0;
        var visible = 0;
        $container.find('tbody tr').each(function(index, e) {
            total++;
            var $row = $(this);
            var match = false;
            var queried = false;
            $row.find('.js-query').each(function(index, e) {
                queried = true;
                var html = $(this).html();
                if (html.match(regex)) {
                    match = true;
                }
            });
            if (match || !queried) {
                visible++;
            }
            $row.toggle(match || !queried);
        });
        $visible.text(visible);
        $total.text(total);
    }

    var $query = $('#cl-console-query');
    var timeoutHandle;
    $query.keyup(function() {
        if (timeoutHandle) {
            window.clearTimeout(timeoutHandle);
        }
        timeoutHandle = window.setTimeout(function() {
            doQuery($query.val())
        }, 1000)
    })
});