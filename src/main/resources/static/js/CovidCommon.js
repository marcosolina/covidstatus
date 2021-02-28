/**
	Simple object to provid simple set of functions
 */
var CovidCommon = (function(CovidCommon){
	"use strict";
	
	if(typeof CovidCommon === "undefined"){
		CovidCommon = {};
	}
	
	
	var dateFormat = "dd/mm/yy";
	var darkModeOn = false;
	
	let nationalChart = {};
	let regionsChart = {};
	let provinceChart = {};
	let vaccinesChart = {};
	
	/*
		This array stores my color palette
	*/
	var provinceColorPalette = [];
	provinceColorPalette.push("rgb( 255, 0, 0, 1)");
	provinceColorPalette.push("rgb( 174, 67, 67, 1)");
	provinceColorPalette.push("rgb( 186, 124, 66, 1)");
	provinceColorPalette.push("rgb( 255, 123, 0, 1)");
	provinceColorPalette.push("rgb( 182, 176, 53, 1)");
	provinceColorPalette.push("rgb( 79, 255, 0, 1)");
	provinceColorPalette.push("rgb( 255, 242, 0, 1)");
	provinceColorPalette.push("rgb( 90, 163, 57, 1)");
	provinceColorPalette.push("rgb( 55, 98, 36, 1)");
	provinceColorPalette.push("rgb( 255, 104, 104, 1)");
	provinceColorPalette.push("rgb( 0, 255, 229, 1)");
	provinceColorPalette.push("rgb( 56, 180, 167, 1)");
	provinceColorPalette.push("rgb( 67, 141, 133 , 1)");
	provinceColorPalette.push("rgb( 0, 54, 255 , 1)");
	provinceColorPalette.push("rgb( 119, 148, 255 , 1)");
	provinceColorPalette.push("rgb( 48, 68, 144 , 1)");
	provinceColorPalette.push("rgb( 255, 169, 90, 1)");
	provinceColorPalette.push("rgb( 183, 0, 255 , 1)");
	provinceColorPalette.push("rgb( 101, 46, 122 , 1)");
	provinceColorPalette.push("rgb( 255, 0, 223 , 1)");
	provinceColorPalette.push("rgb( 0, 0, 0, 1)");

	
	
	/**
		Initialise the UI
	 */
	CovidCommon.init = function(){
		
		/*
			Setting up some jQuery elements and registering the event listeners
		*/
		$('[data-toggle="tooltip"]').tooltip();
		
		var dateFrom = $( "#dateFrom" ).datepicker({
							minDate: "24/02/2020",
						    changeMonth: true,
						    numberOfMonths: 1,
						    dateFormat: dateFormat
						  });
		var dateTo = $( "#dateTo" ).datepicker({
				            changeMonth: true,
				            numberOfMonths: 1,
				            dateFormat: dateFormat
				          });
			
		dateFrom.on( "change", function() {
			dateTo.datepicker( "option", "minDate", CovidCommon.getDate( this ) );
			CovidCommon.changeDates();
        });

		dateTo.on( "change", function() {
	        dateFrom.datepicker( "option", "maxDate", CovidCommon.getDate( this ) );
			CovidCommon.changeDates();
		});
		
		$("#btnTheme").click(CovidCommon.changeTheme);
		
		
		var i = 0;
		for(let region of __REGIONS_LIST){
			region.color= provinceColorPalette[i],
			__REGIONS_MAP[region.code] = region;
			i++;
		}
		
		nationalChart = new NationalChart("chartNational", "nationalDataCheckboxesWrapper", provinceColorPalette);
		regionsChart = new RegionsChart("chartRegions", "rowRegionsCheckboxes", "covidData", provinceColorPalette);
		provinceChart = new ProvinceChart("chartProvince", "rowProvince", "region", provinceColorPalette);
		
		let config = {
			regionCheckBoxesContainerId: "rowRegionVaccines",
			colorPalette: provinceColorPalette,
			idPersonCheckboxesWrapper: "vaccinesGivenCheckboxes",
			canvasIdGivenVaccinesPerRegion: "chartVaccinesDelivered",
			canvasIdSuppliersVaccines: "chartVaccinesSuppliers",
			canvasIdVaccinesPerPerson: "chartVaccinesGiven",
			canvasIdVaccinesPerAge: "chartVaccinesPerAge",
			convasIdVaccninsDoses: "chartVaccinesDoses"
		};
		
		vaccinesChart = new VaccinesChart(config);
		
	}
	
	/**
		It gets the Date() value of the element
	 */	
	CovidCommon.getDate = function(element) {
		var date;
		try {
			date = $.datepicker.parseDate( dateFormat, element.value );
		} catch( error ) {
			date = null;
		}
 
		return date;
    }

	CovidCommon.changeDates = function() {
		var from = $.datepicker.formatDate('yy-mm-dd', CovidCommon.getDate(document.getElementById("dateFrom")));
		var to = $.datepicker.formatDate('yy-mm-dd', CovidCommon.getDate(document.getElementById("dateTo")));
		
		nationalChart.fetchData(from, to);
		regionsChart.fetchData(from, to);
		provinceChart.fetchData(from, to);
		vaccinesChart.fetchData(from, to);
	}

	/**
		It changes the CSS theme
	*/
	CovidCommon.changeTheme = function() {
		darkModeOn = !darkModeOn;
		var selector = "select, span, input, body, a, .modal-content, .custom-control-label, .navbar";
		var govermentCellSelector = ".cella_rossa *, .cella_rossa *, .cella_rossa *";
		var className = "dark-background"; 
		if(darkModeOn){
			$(selector).addClass(className);
			$(govermentCellSelector).removeClass(className);
		}else{
			$(selector).removeClass(className);
		}
		
		nationalChart.setDarkMode(darkModeOn);
		regionsChart.setDarkMode(darkModeOn);
		provinceChart.setDarkMode(darkModeOn);
		vaccinesChart.setDarkMode(darkModeOn);
		
		nationalChart.drawChart();
		regionsChart.drawChart();
		provinceChart.drawChart();
		vaccinesChart.drawChart();
    }
	
	return CovidCommon;
})();

