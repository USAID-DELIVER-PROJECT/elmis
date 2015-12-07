(function($) {

    $.fn.tableHeadFixer = function(param) {
        var defaults = {
            head: true,
            foot: false,
            left: 0,
            right: 0
        };

        var settings = $.extend({}, defaults, param);

        return this.each(function() {
            settings.table = this;
            settings.parent = $("<div></div>");
            setParent();

            if(settings.head == true)
                fixHead();

            if(settings.foot == true)
                fixFoot();

            if(settings.left > 0)
                fixLeft();

            if(settings.right > 0)
                fixRight();

            // self.setCorner();

            $(settings.parent).trigger("scroll");

            $(window).resize(function() {
                $(settings.parent).trigger("scroll");
            });
        });

        function setTable(table) {

        }


        function setParent() {
            var container = $(settings.table).parent();
            var parent = $(settings.parent);
            var table = $(settings.table);

            table.before(parent);
            parent.append(table);
            parent
                .css({
                    'width' : '100%',
                    'height' : container.css("height"),
                    'overflow' : 'scroll',
                    'max-height' : container.css("max-height"),
                    'min-height' : container.css("min-height"),
                    'max-width' : container.css('max-width'),
                    'min-width' : container.css('min-width')
                });

            parent.scroll(function() {
                var scrollWidth = parent[0].scrollWidth;
                var clientWidth = parent[0].clientWidth;
                var scrollHeight = parent[0].scrollHeight;
                var clientHeight = parent[0].clientHeight;
                var top = parent.scrollTop();
                var left = parent.scrollLeft();

                if(settings.head) {
                    this.find("thead tr > *").css("top", top - 1);
                    this.find("tfoot tr > *").css("bottom", scrollHeight - clientHeight - top );
                }

                if(settings.foot)
                    this.find("tfoot tr > *").css("bottom", scrollHeight - clientHeight - top-22);

                if(settings.left > 0)
                    settings.leftColumns.css("left", left);

                if(settings.right > 0)
                    settings.rightColumns.css("right", scrollWidth - clientWidth - left);
            }.bind(table));
        }

        function fixHead () {
            var thead = $(settings.table).find("thead");
            var tr = thead.find("tr");
            var cells = thead.find("tr > *");

            setBackground(cells);
            cells.css({
                'position' : 'relative',
            });
        }

        function fixFoot () {
            var tfoot = $(settings.table).find("tfoot");
            var tr = tfoot.find("tr");
            var cells = tfoot.find("tr > *");

            setBackground(cells);
            cells.css({
                'position' : 'relative'

            });
        }

        function fixLeft () {
            var table = $(settings.table);

            var fixColumn = settings.left;

            settings.leftColumns = $();

            for(var i = 1; i <= fixColumn; i++) {
                settings.leftColumns = settings.leftColumns
                    .add(table.find("tr > *:nth-child(" + i + ")"));

            }

            var column = settings.leftColumns;

            column.each(function(k, cell) {
                var cell = $(cell);
                setBackground(cell);

                if(cell[0].nodeName == 'TH')
                    cell.css({
                        'position' : 'relative',
                        'z-index': 15
                    });
                else
                    cell.css({
                        'position' : 'relative',
                        'z-index': 10
                    });

            });
        }

        function fixRight () {
            var table = $(settings.table);

            var fixColumn = settings.right;

            settings.rightColumns = $();

            for(var i = 1; i <= fixColumn; i++) {
                settings.rightColumns = settings.rightColumns
                    .add(table.find("tr > *:nth-last-child(" + i + ")"));
            }

            var column = settings.rightColumns;

            column.each(function(k, cell) {
                var cell = $(cell);

                setBackground(cell);
                cell.css({
                    'position' : 'relative'
                });
            });

        }

        function setBackground(elements) {
            elements.each(function(k, element) {
                var element = $(element);
                var parent = $(element).parent();

                var elementBackground = element.css("background-color");
                elementBackground = (elementBackground == "transparent" || elementBackground == "rgba(0, 0, 0, 0)") ? null : elementBackground;

                var parentBackground = parent.css("background-color");
                parentBackground = (parentBackground == "transparent" || parentBackground == "rgba(0, 0, 0, 0)") ? null : parentBackground;

                var background = parentBackground ? parentBackground : "white";
                background = elementBackground ? elementBackground : background;

                element.css("background-color", background);
            });
        }
    };

})(jQuery);