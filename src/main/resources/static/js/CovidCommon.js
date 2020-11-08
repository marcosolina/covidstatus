/**
	Simple object to provid simple set of functions
 */
var CovidCommon = (function(CovidCommon){
	"use strict";
	
	if(typeof CovidCommon === "undefined"){
		CovidCommon = {};
	}
	
	/*
		Data Sets Objects
	*/
	
	const dsNewInfections = new CovidChartDataset("Nuove Infezioni");
	const dsTestExecuted = new CovidChartDataset("Test Eseguiti");
	const dsPercInfections = new CovidChartDataset("% Infetti su tamponi eseguiti");
	const dsCasualties = new CovidChartDataset("Decessi");
	const dsPercCasualties = new CovidChartDataset("% Decessi su infetti");
	const dsNewHospitalised = new CovidChartDataset("Nuovi Ricoveri");
	const dsNewIntensiveTherapy = new CovidChartDataset("Di cui in Terapia intensiva");
	const dsNewRecovered = new CovidChartDataset("Nuovi Dismessi/Guariti");
			
	/*
		Setting the color of the data sets
	*/
	dsNewInfections.setColor("rgb(255, 99, 99, 1)");
	dsTestExecuted.setColor("rgb(255, 175, 79, 1)");
	dsPercInfections.setColor("rgb(242, 17, 224, 1)");
	dsCasualties.setColor("rgb(31, 235, 255, 1)");
	dsPercCasualties.setColor("rgb(84, 149, 255, 1)");
	dsNewHospitalised.setColor("rgb(157, 140, 255, 1)");
	dsNewIntensiveTherapy.setColor("rgb(46, 22, 181, 1)");
	dsNewRecovered.setColor("rgb(75, 199, 50, 1)");
	
	var dateFormat = "dd/mm/yy";
	
	var dataNationalChart = {
		arrPercInfections: {active: true,},
		arrPercCasualties: {active: true,},
		arrNewInfections: {active: false,},
		arrNewTests: {active: false,},
		arrNewCasualties: {active: false,},
		arrNewHospitalized: {active: false,},
		arrNewIntensiveTherapy: {active: false,},
		arrNewRecovered: {active: false,},
	};
	
	var dataProvinceChart = {};
	
	var provinceColorPalette = [];
	
	var chartNational;
	var chartProvince;
	
	/**
		Initialise the UI
	 */
	CovidCommon.init = function(){
		
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
	          CovidCommon.updateDates();
        });

		dateTo.on( "change", function() {
	        dateFrom.datepicker( "option", "maxDate", CovidCommon.getDate( this ) );
	        CovidCommon.updateDates();
		});
		
		$("[type=checkbox]").change(CovidCommon.chartDataSwitchChanged);
		$("#region").change(CovidCommon.loadProvinceData);
		
		/*
		 Activated the default checkboxes in the UI
		*/
		for(var prop in dataNationalChart){
			if (dataNationalChart.hasOwnProperty(prop)) {
			    $("#" + prop).prop("checked", dataNationalChart[prop].active);
			}
		}
		
		provinceColorPalette.push("rgb( 64, 145, 108, 1)");
		provinceColorPalette.push("rgb( 157, 2, 8, 1)");
		provinceColorPalette.push("rgb( 232, 93, 4, 1)");
		provinceColorPalette.push("rgb( 255, 186, 8, 1)");
		provinceColorPalette.push("rgb( 247, 37, 133, 1)");
		provinceColorPalette.push("rgb( 114, 9, 183, 1)");
		provinceColorPalette.push("rgb( 52, 12, 163, 1)");
		provinceColorPalette.push("rgb( 67, 97, 238, 1)");
		provinceColorPalette.push("rgb( 76, 201, 240, 1)");
		provinceColorPalette.push("rgb( 239, 71, 111, 1)");
		provinceColorPalette.push("rgb( 255, 209, 102, 1)");
		provinceColorPalette.push("rgb( 6, 214, 160, 1)");
		provinceColorPalette.push("rgb( 17, 138, 178, 1)");
		provinceColorPalette.push("rgb( 7, 59, 76, 1)");
		provinceColorPalette.push("rgb( 112, 141, 129, 1)");
		provinceColorPalette.push("rgb( 73, 80, 87, 1)");
		
	}
	
	CovidCommon.loadProvinceData = function(){
		var from = $.datepicker.formatDate('yy-mm-dd', CovidCommon.getDate(document.getElementById("dateFrom")));
		var to = $.datepicker.formatDate('yy-mm-dd', CovidCommon.getDate(document.getElementById("dateTo")));
		var regionCode = $(this).val();
		if(from != "" && to != ""){
			MarcoUtils.executeAjax({
				dataToPost: {
				    from: from,
				    to: to,
					regionCode: regionCode,
				},
				showLoading: true,
				url: "/Covid19Italy/getProvinceData"
			}).then(CovidCommon.dataRetrieved);
		}
	}
	
	/**
		It manges the onchange event of the switches
	 */
	CovidCommon.chartDataSwitchChanged = function(){
		var jElement = $(this);
		var id = jElement.prop("id");
		
		dataNationalChart[id].active = jElement.prop("checked");
		CovidCommon.drawNationalChart();
	}
	
	/**
		It loads the data between the specified range
	 */
	CovidCommon.updateDates = function(){
		var from = $.datepicker.formatDate('yy-mm-dd', CovidCommon.getDate(document.getElementById("dateFrom")));
		var to = $.datepicker.formatDate('yy-mm-dd', CovidCommon.getDate(document.getElementById("dateTo")));
		if(from != "" && to != ""){
			MarcoUtils.executeAjax({
				dataToPost: {
				    from: from,
				    to: to
				},
				showLoading: true,
				url: "/Covid19Italy/getNationalData"
			}).then(CovidCommon.dataRetrieved);
		}
	}
	
	/**
		It sets the data retireved via the Ajax call
	 */
	CovidCommon.dataRetrieved = function(response){
		if(response.status){
			if(chartNational == undefined){
				chartNational = new CovidChart(document.getElementById('chartNational'));
			}
			if(chartProvince == undefined){
				chartProvince = new CovidChart(document.getElementById('chartProvince'));
			}
			chartNational.setLabels(response.arrDates);
			chartProvince.setLabels(response.arrDates);
			switch(response.dataType){
				case "NATIONAL":
					dataNationalChart.arrNewInfections.data = response.arrNewInfections;
					dataNationalChart.arrNewTests.data = response.arrNewTests;
					dataNationalChart.arrPercInfections.data = response.arrPercInfections;
					dataNationalChart.arrNewCasualties.data = response.arrNewCasualties;
					dataNationalChart.arrPercCasualties.data = response.arrPercCasualties;
					dataNationalChart.arrNewHospitalized.data = response.arrNewHospitalized;
					dataNationalChart.arrNewIntensiveTherapy.data = response.arrNewIntensiveTherapy;
					dataNationalChart.arrNewRecovered.data = response.arrNewRecovered;
					CovidCommon.drawNationalChart();
					break;
				case "PROVINCE":
					dataProvinceChart = response.provinceData;
					CovidCommon.drawProvinceChart();
					break;
				default:
					break;
			}
		}
	}
	
	/**
		IT gets the Date value of the element
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

	CovidCommon.drawProvinceChart = function(){
		if(chartProvince == undefined){
			return;
		}
		
		chartProvince.clearDataSets();
		
		var i = 0;
		for(var provinceCode in dataProvinceChart){
			var provinceDetails = dataProvinceChart[provinceCode];
			
			const dsProvince = new CovidChartDataset(provinceDetails.label);
			dsProvince.setData(provinceDetails.newInfections);
			dsProvince.setColor(provinceColorPalette[i]);
			
			chartProvince.addCovidChartDataset(dsProvince);
			i++;
		}
		
		chartProvince.drawChart();
	}

	/**
		It draws the charts
	 */
	CovidCommon.drawNationalChart = function(){
			if(chartNational == undefined){
				return;
			}
			/*
			* Preparing the datasets
			*/
			dsNewInfections.setData(dataNationalChart.arrNewInfections.data);
			dsTestExecuted.setData(dataNationalChart.arrNewTests.data);
			dsPercInfections.setData(dataNationalChart.arrPercInfections.data);
			dsCasualties.setData(dataNationalChart.arrNewCasualties.data);
			dsPercCasualties.setData(dataNationalChart.arrPercCasualties.data);
			dsNewHospitalised.setData(dataNationalChart.arrNewHospitalized.data);
			dsNewIntensiveTherapy.setData(dataNationalChart.arrNewIntensiveTherapy.data);
			dsNewRecovered.setData(dataNationalChart.arrNewRecovered.data);
			
			chartNational.clearDataSets();
			
			/**
				Set the data sets into the Chart
			 */
			if(dataNationalChart.arrNewInfections.active == true){
				chartNational.addCovidChartDataset(dsNewInfections);	
			}
			if(dataNationalChart.arrNewTests.active == true){
				chartNational.addCovidChartDataset(dsTestExecuted);	
			}
			if(dataNationalChart.arrPercInfections.active == true){
				chartNational.addCovidChartDataset(dsPercInfections);	
			}
			if(dataNationalChart.arrNewCasualties.active == true){
				chartNational.addCovidChartDataset(dsCasualties);	
			}
			if(dataNationalChart.arrPercCasualties.active == true){
				chartNational.addCovidChartDataset(dsPercCasualties);	
			}
			if(dataNationalChart.arrNewHospitalized.active == true){
				chartNational.addCovidChartDataset(dsNewHospitalised);	
			}
			if(dataNationalChart.arrNewIntensiveTherapy.active == true){
				chartNational.addCovidChartDataset(dsNewIntensiveTherapy);	
			}
			if(dataNationalChart.arrNewRecovered.active == true){
				chartNational.addCovidChartDataset(dsNewRecovered);	
			}
			
			chartNational.drawChart();
			
	}
	
	return CovidCommon;
})();

