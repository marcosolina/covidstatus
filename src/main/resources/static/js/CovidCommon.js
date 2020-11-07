/**
	Simple object to provid simple set of functions
 */
var CovidCommon = (function(CovidCommon){
	"use strict";
	
	if(typeof CovidCommon === "undefined"){
		CovidCommon = {};
	}
	
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
	var chartNational;
	
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
		
		/*
		 Activated the default checkboxes in the UI
		*/
		for(var prop in dataNationalChart){
			if (dataNationalChart.hasOwnProperty(prop)) {
			    $("#" + prop).prop("checked", dataNationalChart[prop].active);
			}
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
			chartNational.setLabels(response.arrDates);
			dataNationalChart.arrNewInfections.data = response.arrNewInfections;
			dataNationalChart.arrNewTests.data = response.arrNewTests;
			dataNationalChart.arrPercInfections.data = response.arrPercInfections;
			dataNationalChart.arrNewCasualties.data = response.arrNewCasualties;
			dataNationalChart.arrPercCasualties.data = response.arrPercCasualties;
			dataNationalChart.arrNewHospitalized.data = response.arrNewHospitalized;
			dataNationalChart.arrNewIntensiveTherapy.data = response.arrNewIntensiveTherapy;
			dataNationalChart.arrNewRecovered.data = response.arrNewRecovered;
			CovidCommon.drawNationalChart();
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
			const dsNewInfections = new CovidChartDataset("Nuove Infezioni");
			dsNewInfections.setData(dataNationalChart.arrNewInfections.data);
			dsNewInfections.setColor("rgb(255, 99, 99, 1)");
			
			const dsTestExecuted = new CovidChartDataset("Test Eseguiti");
			dsTestExecuted.setData(dataNationalChart.arrNewTests.data);
			dsTestExecuted.setColor("rgb(255, 175, 79, 1)");
			
			const dsPercInfections = new CovidChartDataset("% Infetti su tamponi eseguiti");
			dsPercInfections.setData(dataNationalChart.arrPercInfections.data);
			dsPercInfections.setColor("rgb(242, 17, 224, 1)");
			
			const dsCasualties = new CovidChartDataset("Decessi");
			dsCasualties.setData(dataNationalChart.arrNewCasualties.data);
			dsCasualties.setColor("rgb(31, 235, 255, 1)");
			
			const dsPercCasualties = new CovidChartDataset("% Decessi su infetti");
			dsPercCasualties.setData(dataNationalChart.arrPercCasualties.data);
			dsPercCasualties.setColor("rgb(84, 149, 255, 1)");
			
			const dsNewHospitalised = new CovidChartDataset("Nuovi Ricoveri");
			dsNewHospitalised.setData(dataNationalChart.arrNewHospitalized.data);
			dsNewHospitalised.setColor("rgb(157, 140, 255, 1)");
			
			const dsNewIntensiveTherapy = new CovidChartDataset("Di cui in Terapia intensiva");
			dsNewIntensiveTherapy.setData(dataNationalChart.arrNewIntensiveTherapy);
			dsNewIntensiveTherapy.setColor("rgb(46, 22, 181, 1)");
			
			const dsNewRecovered = new CovidChartDataset("Nuovi Dismessi/Guariti");
			dsNewRecovered.setData(dataNationalChart.arrNewRecovered.data);
			dsNewRecovered.setColor("rgb(75, 199, 50, 1)");
			
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

