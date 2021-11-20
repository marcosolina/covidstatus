/**
	Simple object to provid some basic common function
 */
var CovidCommon = (function(CovidCommon) {
	"use strict";

	if (typeof CovidCommon === "undefined") {
		CovidCommon = {};
	}


	let dateFormat = "dd/mm/yy";
	let darkModeOn = false;
	let autoRefreshEnabled = true;
	let refreshInterval;
	let idRefreshCheckBox = "checkRefresh";

	let charts = {};// It will hold the charts objects instances
	let oldPortWidth = window.innerWidth; 

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
	CovidCommon.init = function() {



		/*
			Setting up some jQuery elements and registering the event listeners
		*/
		$('[data-toggle="tooltip"]').tooltip();

		var dateFrom = $("#dateFrom").datepicker({
			minDate: "24/02/2020",
			changeMonth: true,
			changeYear: true,
			numberOfMonths: 1,
			dateFormat: dateFormat
		});
		var dateTo = $("#dateTo").datepicker({
			changeMonth: true,
			changeYear: true,
			numberOfMonths: 1,
			dateFormat: dateFormat
		});

		dateFrom.on("change", function() {
			dateTo.datepicker("option", "minDate", CovidCommon.getDate(this));
			CovidCommon.changeDates();
		});

		dateTo.on("change", function() {
			dateFrom.datepicker("option", "maxDate", CovidCommon.getDate(this));
			CovidCommon.changeDates();
		});

		$("#btnTheme").click(CovidCommon.changeTheme);
		$("#" + idRefreshCheckBox).click(CovidCommon.autoRefreshData);
		$("#" + idRefreshCheckBox).prop("checked", autoRefreshEnabled);

		/*
		* Creating a new region map including a color
		* from the color Palette
		*/
		var i = 0;
		for (let region of __REGIONS_LIST) {
			region.color = colorPalette[i],
				__REGIONS_MAP[region.code] = region;
			i++;
		}

		CovidCommon.initChartObjects();
		CovidCommon.switchAccordionOrTabs(true);
		$(window).resize(function(){CovidCommon.switchAccordionOrTabs(false);});
	}
	
	/**
	* Creating an instance of the charts
	*/
	CovidCommon.initChartObjects = function() {
		charts.regionsChart = new RegionsChart("chartRegions", "rowRegionsCheckboxes", "covidData", colorPalette);
		charts.provinceChart = new ProvinceChart("chartProvince", "rowProvince", "region", colorPalette);
		charts.nationalChart = new NationalChart("chartNational", "nationalDataCheckboxesWrapper", colorPalette);
		charts.suppliersChart = new SuppliersVaccinesChart("chartVaccinesSuppliers", colorPalette);
		charts.deliveredVaccines = new DeliveredVaccinesChart("chartVaccinesDelivered", "rowRegionVaccines", colorPalette);
		charts.vaccinesDoses = new VaccinesDoseChart("chartVaccinesDoses", colorPalette);
		charts.vaccinesPerAge = new VaccinesPerAgeChart("chartVaccinesPerAge", colorPalette);
		charts.vaxTotalPerAge = new TotalGivenVeccinesPerAge("chartTotalsVaccinesGivenPerAge", "firstDosePerc", colorPalette);
		charts.vaxTotalPerRegion = new TotalGivenVeccinesPerRegion("vaccinatedPerRegion", colorPalette);
		charts.vaccinesDeliveredUsed = new TotalDeliveredUsedVaccinesChart("chartTotalsVaccinesUsedDelivered", colorPalette);
		charts.vaxDeliveredUsedPerReg = new TotalDeliveredUsedVaccinesPerRegionChart("chartTotalsVaccinesUsedDeliveredPerRegion", colorPalette);
        /*
        These data are not available anymore in the goverment repo. I will keep the code here just for reference, or in case they
        change their mind and restore these info
		charts.vaccinesPerPersonChart = new VaccinesPerPersonChart("chartVaccinesGiven", "vaccinesGivenCheckboxes", colorPalette);
        */
		charts.vaxTotalDeliveredPerSupplier = new TotalDeliveredVaccinesPerSupplier("chartTotalsVaccinesDeliveredPerSupplier", colorPalette);		
	}

	/**
		It gets the Date() value of the element
	 */
	CovidCommon.getDate = function(element) {
		var date;
		try {
			date = $.datepicker.parseDate(dateFormat, element.value);
		} catch (error) {
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

		if(from != "" && to != ""){
			let fromToQueryParam = MarcoUtils.template("&from=%from%&to=%to%", {from, to})
	
			for (let chartProp in charts) {
				charts[chartProp].fetchData(fromToQueryParam);
			}
		}
		CovidCommon.checkSyncStatus();
	}
	
	CovidCommon.checkSyncStatus = function (){
		let url = __URLS.UTILS.REFRESH_STATUS;
		MarcoUtils.executeAjax({type: "GET", url: url})
			.then(function(resp){
				if(resp.status){
					if(resp.refreshingData){
						let html = '<div class="alert alert-warning" role="alert">' +
									'Aggiornament dati in corso' +
									'&nbsp;<i class="fa fa-refresh fa-spin"></i>' +
								  '</div>'; 									
						$("#divLastUpdate").html(html);
					} else {
						let html = '<label>Ultimo Aggiornamento: ' + 
									resp.lastUpdate + '</label>'; 
						
						$("#divLastUpdate").html(html);
					}
				}
			});
	}	

	/**
	* Switching the Themes
	*/
	CovidCommon.changeTheme = function() {
		darkModeOn = !darkModeOn;
		var selector = "select, span, input, body, a, .modal-content, .custom-control-label, .navbar, .card, .btn";
		var govermentCellSelector = ".cella_rossa *, .cella_rossa *, .cella_rossa *";
		var className = "dark-background";
		if (darkModeOn) {
			$(selector).addClass(className);
			$(govermentCellSelector).removeClass(className);
		} else {
			$(selector).removeClass(className);
		}

		for (let chartProp in charts) {
			charts[chartProp].setDarkMode(darkModeOn);
			charts[chartProp].drawChart();
		}
	}

	CovidCommon.autoRefreshData = function() {
		autoRefreshEnabled = $("#" + idRefreshCheckBox).prop("checked");
		if (autoRefreshEnabled) {
			refreshInterval = setInterval(CovidCommon.changeDates, 10000);
		} else {
			if (refreshInterval) {
				clearInterval(refreshInterval);
			}
		}
	}
	
	CovidCommon.switchAccordionOrTabs = function(force){
		
		
			let viewPortWidth = window.innerWidth;
			let minimunSize = 576;
			let dontDoIt = (viewPortWidth < minimunSize && oldPortWidth < minimunSize) || (viewPortWidth >= minimunSize && oldPortWidth >= minimunSize);
			
			oldPortWidth = viewPortWidth;
			
			if(dontDoIt && !force){
				return;
			}
			
			let tabsBar = $("#tabsBars");
			let tabContent = $("#nav-tabContent");
			let jContainer = $("#divAccordion");
			
			if(viewPortWidth < minimunSize){
				var strTmpl = 
					'<div class="card">' +
	    				'<div class="card-header" id="header-%headerId%">' +
	      					'<h2 class="mb-0">' +
	        					'<button class="btn btn-block text-left" type="button" data-toggle="collapse" data-target="#collapse-%headerId%" aria-expanded="true" aria-controls="collapse-%headerId%">' +
	          						'%title%' +
	        					'</button>' +
	      					'</h2>' +
	    				'</div>' +
	    				'<div id="collapse-%headerId%" class="collapse" aria-labelledby="header-%headerId%" data-parent="#divAccordion">' +
	    					'<div class="card-body">' +
	      					'</div>' +
	    				'</div>' +
	  				'</div>';
				
				jContainer.empty();
				$(".nav-link").each(function(i) {
					let obj = {
						title: this.innerHTML,
						headerId: i
					};
					
					jContainer.append(MarcoUtils.template(strTmpl, obj));
					
					$("#nav-tabContent .container-fluid:eq(0)").appendTo("#collapse-" + i + " > .card-body");
				});
				
				tabsBar.hide();
				tabContent.hide();
			}else{
				
				$(".tab-pane").each(function(i){
					$(".card-body .container-fluid:eq(0)").appendTo(".tab-pane:eq(" + i + ")");
				});
				
				tabsBar.show();
				tabContent.show();
				jContainer.empty();
			}
		
	}

	return CovidCommon;
})();

