
$(document).ready(function() {

    window.CL.forms = window.CL.forms || {};
    window.CL.forms.refresh = function() {

        $('form[data-async-submit="true"]').submit(function (e) {
            e.preventDefault();
            var $this = $(this);
            var modal;
            if ($this.attr('data-wait-modal')) {
                var selector = $this.attr('data-wait-modal');
                modal = new CUI.Modal({element: selector, visible: true, type: 'info'});
            }
            $.ajax({
                type: $this.attr('method'),
                cache: false,
                url: $this.attr('action'),
                data: $this.serialize(),
                success: function (msg) {
                    if ($this.attr('data-success-refresh')) {
                        window.location.reload();
                    } else if ($this.attr('data-async-submit-target')) {
                        window.location.href = $this.attr('data-async-submit-target');
                    }
                },
                error: function (jqXHR) {
                    if ($this.attr('data-fail-modal')) {
                        if (modal) {
                            modal.hide();
                        }
                        var selector = $this.attr('data-fail-modal');
                        modal = new CUI.Modal({element: selector, visible: true, type: 'error'});
                    }
                }
            });
        });

        $('form').each(function () {
            var $form = $(this);
            var $toDisable = $form.find('[data-disable-on-invalid="true"]');
            if ($toDisable.length > 0) {
                $form.find('input').on('change keyup paste', function () {
                    $toDisable.prop("disabled", !$.validator.isValid($form));
                });
                $toDisable.prop("disabled", !$.validator.isValid($form));
            }
        });

        var enableOnSelection = function (inputName) {
            $('input[name="' + inputName + '"]').change(function () {
                checkSelection(inputName);
            });
            checkSelection(inputName);
        };
        var checkSelection = function (inputName) {
            var $inputs = $('input[name="' + inputName + '"]:checked');
            var $buttons = $('button[data-enabled-for-selection="' + inputName + '"]');
            $buttons.prop('disabled', $inputs.length == 0);
        };

        var names = [];
        $('button[data-enabled-for-selection]').each(function () {
            var name = $(this).attr('data-enabled-for-selection');
            if (name) {
                names.push(name);
            }
        });
        names = _.uniq(names);
        _.each(names, enableOnSelection)
    };

    window.CL.forms.refresh();

});