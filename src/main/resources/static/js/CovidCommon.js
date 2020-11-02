var CovidCommon = (function(CovidCommon){
	"use strict";
	
	if(typeof CovidCommon === "undefined"){
		CovidCommon = {};
	}
	
	var dateFormat = "dd/mm/yy";
	var data = undefined;
	
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
	}
		
	CovidCommon.updateDates = function(){
		var from = $.datepicker.formatDate('yymmdd', CovidCommon.getDate(document.getElementById("dateFrom")));
		var to = $.datepicker.formatDate('yymmdd', CovidCommon.getDate(document.getElementById("dateTo")));
		if(from != "" && to != ""){
			var url ="/Covid19Italy/?from=" + from + "&to=" + to;
			window.open(url, "_self");
		}
	}
		
	CovidCommon.getDate = function(element) {
		var date;
		try {
			date = $.datepicker.parseDate( dateFormat, element.value );
		} catch( error ) {
			date = null;
		}
 
		return date;
    }

	CovidCommon.drawTheCharts = function(){
		/*
			* Preparing the datasets
			*/
			const dsNewInfections = new CovidChartDataset("Nuove Infezioni");
			dsNewInfections.setData(data.arrNewInfections);
			dsNewInfections.setColor("rgb(219, 53, 53, 1)");
			
			const dsTestExecuted = new CovidChartDataset("Test Eseguiti");
			dsTestExecuted.setData(data.arrNewTests);
			dsTestExecuted.setColor("rgb(53, 219, 108, 1)");
			
			const dsPercInfections = new CovidChartDataset("% Infetti su tamponi eseguiti");
			dsPercInfections.setData(data.arrPercInfections);
			dsPercInfections.setColor("rgb(25, 194, 255, 1)");
			
			const dsCasualties = new CovidChartDataset("Decessi");
			dsCasualties.setData(data.arrNewCasualties);
			dsCasualties.setColor("rgb(245, 152, 66, 1)");
			
			const dsPercCasualties = new CovidChartDataset("% Decessi su infetti");
			dsPercCasualties.setData(data.arrPercCasualties);
			dsPercCasualties.setColor("rgb(237, 78, 237, 1)");
			
			const dsNewHospitalised = new CovidChartDataset("Nuovi Ricoveri");
			dsNewHospitalised.setData(data.arrNewHospitalized);
			dsNewHospitalised.setColor("rgb(230, 218, 0, 1)");
			
			const dsNewIntensiveTherapy = new CovidChartDataset("Di cui in Terapia intensiva");
			dsNewIntensiveTherapy.setData(data.arrNewIntensiveTherapy);
			dsNewIntensiveTherapy.setColor("rgb(245, 161, 66, 1)");
			
			const dsNewRecovered = new CovidChartDataset("Nuovi Dismessi/Guariti");
			dsNewRecovered.setData(data.arrNewRecovered);
			dsNewRecovered.setColor("rgb(52, 235, 76, 1)");
			
			/*
				Prepare the charts
			*/
			const cTests = new CovidChart(document.getElementById('chartTests'));
			cTests.setLabels(data.arrDates);
			cTests.addCovidChartDataset(dsNewInfections);
			cTests.addCovidChartDataset(dsTestExecuted);
			
			const cInfections = new CovidChart(document.getElementById('chartInfected'));
			cInfections.setLabels(data.arrDates);
			cInfections.addCovidChartDataset(dsPercInfections);

			const cCasualties = new CovidChart(document.getElementById('chartCasualties'));
			cCasualties.setLabels(data.arrDates);
			cCasualties.addCovidChartDataset(dsCasualties);
			
			const cPercCasualties = new CovidChart(document.getElementById('chartPercCasualties'));
			cPercCasualties.setLabels(data.arrDates);
			cPercCasualties.addCovidChartDataset(dsPercCasualties);
			
			const cHospitalStatus = new CovidChart(document.getElementById('chartHospitalStatus'));
			cHospitalStatus.setLabels(data.arrDates);
			cHospitalStatus.addCovidChartDataset(dsNewInfections);
			cHospitalStatus.addCovidChartDataset(dsNewHospitalised);
			cHospitalStatus.addCovidChartDataset(dsNewIntensiveTherapy);
			cHospitalStatus.addCovidChartDataset(dsNewRecovered);
			
			/*
				Draw the charts
			*/
			cTests.drawChart();
			cInfections.drawChart();
			cCasualties.drawChart();
			cPercCasualties.drawChart();
			cHospitalStatus.drawChart();
	}
	
	return CovidCommon;
})();

