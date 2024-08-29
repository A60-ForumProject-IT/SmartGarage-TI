(function($){
	"use strict";
	var defaults = {
		formula: {},
		currency: "$",
		updateHidden: ""
	};

	var methods =
	{
		init: function(options){
			return this.each(function(){
				options = $.extend(false, defaults, options);
				$(this).data("cost-calculator-options", options);
				$(this).costCalculator("calculate");
			});
		},
		calculate : function(options){
			return this.each(function(){
				options = $(this).data("cost-calculator-options");
				var sum_array = options.formula.split("+");
				var mult_array;
				var sum = 0;
				var mult = 1;
				for(var i in sum_array)
				{
					mult_array = sum_array[i].split("*");
					if(mult_array.length>1)
					{
						mult = 1;
						for(var j in mult_array)
							mult = mult * (!isNaN($("#" + mult_array[j]).val()) ? $("#" + mult_array[j]).val() : (!isNaN(mult_array[j]) ? mult_array[j] : 0));
						sum = sum + mult;
					}
					else
						sum = sum + (!isNaN(parseFloat($("#" + sum_array[i]).val())) ? parseFloat($("#" + sum_array[i]).val()) : (!isNaN(parseFloat(sum_array[i])) ? parseFloat(sum_array[i]) : 0));
				}
				$(this).html(options.currency+sum.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,'));
				if(jQuery.type(options.updateHidden)=="object")
					options.updateHidden.val(options.currency+sum.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,'));
			});
		}
	};

	jQuery.fn.costCalculator = function(method){
		if(methods[method])
			return methods[method].apply(this, arguments);
		else if(typeof(method)==='object' || !method)
			return methods.init.apply(this, arguments);
	};
})(jQuery);