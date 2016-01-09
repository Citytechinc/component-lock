$(document).ready(function() {

    window.CL = window.CL || {};
    window.CL.console = window.CL.console || {};


    var $select = new CUI.Autocomplete({ element:'#cl-console-user-select' });
    var baseUrl = $('#cl-console-user-select').attr('data-request-path');
    var $container = $('#cl-console-table-container');
    var $visible = $('#cl-console-visible');
    var $total = $('#cl-console-total');
    var $waitIndicator = $('#cl-console-wait-indicator');

    $select.on('selected', function(e) {
        loadTable(e.selectedValue)
    });

    function loadTable(user) {
        $waitIndicator.toggleClass('loading', true);
        $.ajax({
            url: baseUrl + '.table.' + user + '.html'
        }).done(function(html) {
            $container.html(html);
            doQuery($query.val(), $toggle.prop('checked'));
            window.CL.forms.refresh();
            $waitIndicator.toggleClass('loading', false);
        });
    }

    function doQuery(text, hideDefault) {
        $waitIndicator.toggleClass('filtering', true);
        window.setTimeout(function () {
            doQueryDeferred(text, hideDefault);
            $waitIndicator.toggleClass('filtering', false);
        }, 10);
    }

    function doQueryDeferred(text, hideDefault) {
        var regex = new RegExp(text, 'i');
        var total = 0;
        var visible = 0;
        $container.find('tbody tr').each(function() {
            total++;
            var $row = $(this);
            var match = false;
            var queried = false;
            if (hideDefault) {
                match = !!$row.find('input:checked').val();
                queried = true;
            }
            if (hideDefault == match) {
                var rowText = $row.attr('data-query-text');
                if (text && text != '' && rowText && rowText != '') {
                    if (rowText.match(regex)) {
                        match = true;
                    }
                    queried = true;
                } else {
                    match = true;
                }
            }
            if (match || !queried) {
                visible++;
            }
            $row.toggle(match || !queried);
        });
        $visible.text(visible);
        $total.text(total);
    }

    var $query = $('#cl-console-query');
    var $toggle = $('#cl-console-toggle-default');
    var timeoutHandle;

    $query.keyup(function() {
        if (timeoutHandle) {
            window.clearTimeout(timeoutHandle);
        }
        timeoutHandle = window.setTimeout(function() {
            doQuery($query.val(), $toggle.prop('checked'))
        }, 500);
    });

    $toggle.change(function() {
        if (timeoutHandle) {
            window.clearTimeout(timeoutHandle);
        }
        doQuery($query.val(), $toggle.prop('checked'))
    });
});