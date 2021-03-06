/**
	Simple object to provid some basic common function
 */
var CovidCommon = (function(CovidCommon){
	"use strict";
	
	if(typeof CovidCommon === "undefined"){
		CovidCommon = {};
	}
	
	
	var dateFormat = "dd/mm/yy";
	var darkModeOn = false;
	
	var charts = {};// It will hold the charts objects instances 
	
	/*
		This array stores my color palette
	*/
	var colorPalette = [];
	colorPalette.push("rgb( 0, 54, 255 , 1)");// Blu
	colorPalette.push("rgb( 255, 0, 223 , 1)");// Rosa
	colorPalette.push("rgb( 15, 212, 192, 1)");// Verde Acqua
	colorPalette.push("rgb( 255, 123, 0, 1)");// Arancione
	colorPalette.push("rgb( 174, 67, 67, 1)");// Marrone
	colorPalette.push("rgb( 67, 141, 133 , 1)");// Verde Scuro
	colorPalette.push("rgb( 186, 124, 66, 1)");// Marrone Chiaro
	colorPalette.push("rgb( 182, 176, 53, 1)");// Oliva
	colorPalette.push("rgb( 79, 255, 0, 1)");// Verde evidenziatore
	colorPalette.push("rgb( 255, 169, 90, 1)");// Arancio chiaro
	colorPalette.push("rgb( 222, 212, 22, 1)");// Senape
	colorPalette.push("rgb( 90, 163, 57, 1)"); // Verde
	colorPalette.push("rgb( 183, 0, 255 , 1)");// Viola
	colorPalette.push("rgb( 55, 98, 36, 1)");// Verde molto scuro
	colorPalette.push("rgb( 255, 104, 104, 1)");// Rossino
	colorPalette.push("rgb( 119, 148, 255 , 1)");// Azzurro Violetto
	colorPalette.push("rgb( 48, 68, 144 , 1)");// Blu scuro
	colorPalette.push("rgb( 56, 180, 167, 1)");// Verde chiaro
	colorPalette.push("rgb( 101, 46, 122 , 1)");// Viola Scuro
	colorPalette.push("rgb( 255, 0, 0, 1)");// Rosso
	colorPalette.push("rgb( 0, 0, 0, 1)");// Nero

	
	
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
							changeYear: true,
						    numberOfMonths: 1,
						    dateFormat: dateFormat
						  });
		var dateTo = $( "#dateTo" ).datepicker({
				            changeMonth: true,
							changeYear: true, 
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
		
		/*
		* Creating a new region map including a color
		* from the color Palette
		*/
		var i = 0;
		for(let region of __REGIONS_LIST){
			region.color= colorPalette[i],
			__REGIONS_MAP[region.code] = region;
			i++;
		}
		
		/*
		* Creating an instance of the charts
		*/
		charts.nationalChart			= new NationalChart("chartNational", "nationalDataCheckboxesWrapper", colorPalette);
		charts.regionsChart				= new RegionsChart("chartRegions", "rowRegionsCheckboxes", "covidData", colorPalette);
		charts.provinceChart			= new ProvinceChart("chartProvince", "rowProvince", "region", colorPalette);
		charts.vaccinesPerPersonChart	= new VaccinesPerPersonChart("chartVaccinesGiven", "vaccinesGivenCheckboxes", colorPalette);
		charts.suppliersChart			= new SuppliersVaccinesChart("chartVaccinesSuppliers", colorPalette);
		charts.deliveredVaccines		= new DeliveredVaccinesChart("chartVaccinesDelivered", "rowRegionVaccines", colorPalette);
		charts.vaccinesDoses			= new VaccinesDoseChart("chartVaccinesDoses", colorPalette);
		charts.vaccinesPerAge			= new VaccinesPerAgeChart("chartVaccinesPerAge", colorPalette);
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

	/*
	* It is called when the user changes the date range.
	* It will loop through all the charts objects and 
	* request them to fecth the data for the new selected
	* date range
	*/
	CovidCommon.changeDates = function() {
		var from = $.datepicker.formatDate('yy-mm-dd', CovidCommon.getDate(document.getElementById("dateFrom")));
		var to = $.datepicker.formatDate('yy-mm-dd', CovidCommon.getDate(document.getElementById("dateTo")));
		
		for(let chartProp in charts){
			charts[chartProp].fetchData(from, to);
		}
	}

	/**
	* Switching the Themes
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
		
		for(let chartProp in charts){
			charts[chartProp].setDarkMode(darkModeOn);
			charts[chartProp].drawChart();
		}
    }
	
	return CovidCommon;
})();

