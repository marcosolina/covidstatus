/**
	Simple object to provid simple set of functions
 */
var CovidCommon = (function(CovidCommon){
	"use strict";
	
	if(typeof CovidCommon === "undefined"){
		CovidCommon = {};
	}
	
	var dateFormat = "dd/mm/yy";
	var data = undefined;
	var dataOnTheChart = {};
	var chartPeople;
	
	/**
		Initialise the UI
	 */
	CovidCommon.init = function(dataMap){
		data = dataMap;
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
		
		chartPeople = new CovidChart(document.getElementById('chartPeople'));
		chartPeople.setLabels(data.arrDates);
	}
	
	CovidCommon.chartDataSwitchChanged = function(){
		var jElement = $(this);
		var id = jElement.prop("id");
		
		if(dataOnTheChart[id] == undefined){
			dataOnTheChart[id] = {};
		}
		
		dataOnTheChart[id].active = jElement.prop("checked");
		CovidCommon.drawTheCharts();
	}
	
	/**
		It loads the data between the specified range
	 */
	CovidCommon.updateDates = function(){
		var from = $.datepicker.formatDate('yymmdd', CovidCommon.getDate(document.getElementById("dateFrom")));
		var to = $.datepicker.formatDate('yymmdd', CovidCommon.getDate(document.getElementById("dateTo")));
		if(from != "" && to != ""){
			var url ="/Covid19Italy/?from=" + from + "&to=" + to;
			window.open(url, "_self");
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
	CovidCommon.drawTheCharts = function(){
			/*
			* Preparing the datasets
			*/
			const dsNewInfections = new CovidChartDataset("Nuove Infezioni");
			dsNewInfections.setData(data.arrNewInfections);
			dsNewInfections.setColor("rgb(255, 99, 99, 1)");
			
			const dsTestExecuted = new CovidChartDataset("Test Eseguiti");
			dsTestExecuted.setData(data.arrNewTests);
			dsTestExecuted.setColor("rgb(255, 175, 79, 1)");
			
			const dsPercInfections = new CovidChartDataset("% Infetti su tamponi eseguiti");
			dsPercInfections.setData(data.arrPercInfections);
			dsPercInfections.setColor("rgb(242, 17, 224, 1)");
			
			const dsCasualties = new CovidChartDataset("Decessi");
			dsCasualties.setData(data.arrNewCasualties);
			dsCasualties.setColor("rgb(31, 235, 255, 1)");
			
			const dsPercCasualties = new CovidChartDataset("% Decessi su infetti");
			dsPercCasualties.setData(data.arrPercCasualties);
			dsPercCasualties.setColor("rgb(84, 149, 255, 1)");
			
			const dsNewHospitalised = new CovidChartDataset("Nuovi Ricoveri");
			dsNewHospitalised.setData(data.arrNewHospitalized);
			dsNewHospitalised.setColor("rgb(157, 140, 255, 1)");
			
			const dsNewIntensiveTherapy = new CovidChartDataset("Di cui in Terapia intensiva");
			dsNewIntensiveTherapy.setData(data.arrNewIntensiveTherapy);
			dsNewIntensiveTherapy.setColor("rgb(46, 22, 181, 1)");
			
			const dsNewRecovered = new CovidChartDataset("Nuovi Dismessi/Guariti");
			dsNewRecovered.setData(data.arrNewRecovered);
			dsNewRecovered.setColor("rgb(75, 199, 50, 1)");
			
			
			chartPeople.clear();
			
			
			if(dataOnTheChart.arrNewInfections && dataOnTheChart.arrNewInfections.active == true){
				chartPeople.addCovidChartDataset(dsNewInfections);	
			}
			if(dataOnTheChart.arrNewTests && dataOnTheChart.arrNewTests.active == true){
				chartPeople.addCovidChartDataset(dsTestExecuted);	
			}
			if(dataOnTheChart.arrPercInfections && dataOnTheChart.arrPercInfections.active == true){
				chartPeople.addCovidChartDataset(dsPercInfections);	
			}
			if(dataOnTheChart.arrNewCasualties && dataOnTheChart.arrNewCasualties.active == true){
				chartPeople.addCovidChartDataset(dsCasualties);	
			}
			if(dataOnTheChart.arrPercCasualties && dataOnTheChart.arrPercCasualties.active == true){
				chartPeople.addCovidChartDataset(dsPercCasualties);	
			}
			if(dataOnTheChart.arrNewHospitalized && dataOnTheChart.arrNewHospitalized.active == true){
				chartPeople.addCovidChartDataset(dsNewHospitalised);	
			}
			if(dataOnTheChart.arrNewIntensiveTherapy && dataOnTheChart.arrNewIntensiveTherapy.active == true){
				chartPeople.addCovidChartDataset(dsNewIntensiveTherapy);	
			}
			if(dataOnTheChart.arrNewRecovered && dataOnTheChart.arrNewRecovered.active == true){
				chartPeople.addCovidChartDataset(dsNewRecovered);	
			}
			
			chartPeople.drawChart();
			
	}
	
	return CovidCommon;
})();

