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
	var dataRegionChart;
	var provinceColorPalette = [];
	
	var chartNational;
	var chartProvince;
	var chartRegion;
	var regionDropDownLastValue;
	
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
			CovidCommon.updateNationalData();
			CovidCommon.loadProvinceData();
			CovidCommon.loadRegionData();
        });

		dateTo.on( "change", function() {
	        dateFrom.datepicker( "option", "maxDate", CovidCommon.getDate( this ) );
			CovidCommon.updateNationalData();
			CovidCommon.loadProvinceData();
			CovidCommon.loadRegionData();
		});
		
		$("[type=checkbox]").change(CovidCommon.chartDataSwitchChanged);
		$("#region").change(CovidCommon.loadProvinceData);
		$("#covidData").change(CovidCommon.loadRegionData);
		
		
		/*
		 Activated the default checkboxes in the UI
		*/
		for(var prop in dataNationalChart){
			if (dataNationalChart.hasOwnProperty(prop)) {
			    $("#" + prop).prop("checked", dataNationalChart[prop].active);
			}
		}

		provinceColorPalette.push("rgb( 255, 0, 0, 1)");
		provinceColorPalette.push("rgb( 255, 104, 104, 1)");
		provinceColorPalette.push("rgb( 174, 67, 67, 1)");
		provinceColorPalette.push("rgb( 255, 123, 0, 1)");
		provinceColorPalette.push("rgb( 255, 169, 90, 1)");
		provinceColorPalette.push("rgb( 186, 124, 66, 1)");
		provinceColorPalette.push("rgb( 255, 242, 0, 1)");
		provinceColorPalette.push("rgb( 182, 176, 53, 1)");
		provinceColorPalette.push("rgb( 79, 255, 0, 1)");
		provinceColorPalette.push("rgb( 90, 163, 57, 1)");
		provinceColorPalette.push("rgb( 55, 98, 36, 1)");
		provinceColorPalette.push("rgb( 0, 255, 229, 1)");
		provinceColorPalette.push("rgb( 56, 180, 167, 1)");
		provinceColorPalette.push("rgb( 67, 141, 133 , 1)");
		provinceColorPalette.push("rgb( 0, 54, 255 , 1)");
		provinceColorPalette.push("rgb( 119, 148, 255 , 1)");
		provinceColorPalette.push("rgb( 48, 68, 144 , 1)");
		provinceColorPalette.push("rgb( 183, 0, 255 , 1)");
		provinceColorPalette.push("rgb( 101, 46, 122 , 1)");
		provinceColorPalette.push("rgb( 255, 0, 223 , 1)");
		provinceColorPalette.push("rgb( 0, 0, 0, 1)");
	}
	
	CovidCommon.loadProvinceData = function(){
		var from = $.datepicker.formatDate('yy-mm-dd', CovidCommon.getDate(document.getElementById("dateFrom")));
		var to = $.datepicker.formatDate('yy-mm-dd', CovidCommon.getDate(document.getElementById("dateTo")));
		var regionCode = $("#region").val();
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
	
	CovidCommon.loadRegionData = function(){
		var from = $.datepicker.formatDate('yy-mm-dd', CovidCommon.getDate(document.getElementById("dateFrom")));
		var to = $.datepicker.formatDate('yy-mm-dd', CovidCommon.getDate(document.getElementById("dateTo")));
		var covidData = $("#covidData").val();
		if(from != "" && to != ""){
			MarcoUtils.executeAjax({
				dataToPost: {
				    from: from,
				    to: to,
					covidData: covidData,
				},
				showLoading: true,
				url: "/Covid19Italy/getRegionData"
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
	CovidCommon.updateNationalData = function(){
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
				chartProvince.setTitle("Nuove infezioni nelle province di:");
			}
			if(chartRegion == undefined){
				chartRegion = new CovidChart(document.getElementById('chartRegions'));
			}
			
			chartNational.setLabels(response.arrDates);
			chartProvince.setLabels(response.arrDates);
			chartRegion.setLabels(response.arrDates);
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
					var tmpClone;
					var regionForProvinceVal = $("#region").val();
					if(regionDropDownLastValue == regionForProvinceVal){
						tmpClone = {...dataProvinceChart};//cloning the old data
					}
					regionDropDownLastValue = regionForProvinceVal;
					
					dataProvinceChart = response.provinceData;
					CovidCommon.createProvinceCheckboxes();
					
					if(tmpClone != undefined){
						setProvinceCheckboxesStatus(tmpClone);
					}
					
					CovidCommon.drawProvinceChart();
					break;
				case "REGIONAL":
					var createCheckboxes = dataRegionChart == undefined;
									
					for(var regionCode in dataRegionChart){
						response.regionData[regionCode].active = dataRegionChart[regionCode].active;
					}
					
					dataRegionChart = response.regionData;
					if(createCheckboxes){
						CovidCommon.createRegionCheckboxes();
					}
					
					CovidCommon.drawRegionChart();
					break;
				default:
					break;
			}
		}
	}
	
	function setProvinceCheckboxesStatus(dataProvinceStatus){
		for(var idCheckbox in dataProvinceStatus){
			$("#" + idCheckbox).prop("checked", dataProvinceStatus[idCheckbox].active);
			dataProvinceChart[idCheckbox].active = dataProvinceStatus[idCheckbox].active;
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
	
	CovidCommon.changeProvinceCheckboxes = function (){
		var jElement = $(this);
		var id = jElement.prop("id");
		
		dataProvinceChart[id].active = jElement.prop("checked");
		CovidCommon.drawProvinceChart();
	}
	
	CovidCommon.changeRegionCheckboxes = function (){
		var jElement = $(this);
		var id = jElement.prop("id");
		
		dataRegionChart[id].active = jElement.prop("checked");
		CovidCommon.drawRegionChart();
	}
	
	CovidCommon.createRegionCheckboxes = function(){
		var jRow = $("#rowRegion");
		jRow.empty();
		
		var strTmpl = '<div class="col-6 col-sm-4 col-md-3 col-lg-2 col-xl-2">' + 
						'<div class="custom-control custom-switch">' +
							'<input type="checkbox" class="custom-control-input" id="%regionId%">' +
							'<label class="custom-control-label switch-label" style="color: %color%" for="%regionId%">%label%</label>' + 
						'</div>' +
					  '</div>';	
		var i = 0;
		var arr = [];	
	
		for(var regionCode in dataRegionChart){
			var regionDetails = dataRegionChart[regionCode];
			regionDetails.active = false;
			var data = {
				regionId: regionCode,
				color: provinceColorPalette[i],
				label: regionDetails.label
			}
			arr.push(data);
			i++;
		}
		
		arr.sort((a, b) => {
			if(a.label < b.label){
				return -1
			} 
			return 1;
		});
		
		arr.forEach(el => {jRow.append(MarcoUtils.template(strTmpl, el));});
			
		jRow.find("input").change(CovidCommon.changeRegionCheckboxes);
		$(jRow.find("input").get(0)).prop("checked", true);
		$(jRow.find("input").get(0)).change();
	}

	CovidCommon.createProvinceCheckboxes = function(){
		var jRow = $("#rowProvince");
		jRow.empty();
		
		var strTmpl = '<div class="col-6 col-sm-4 col-md-3 col-lg-2 col-xl-2">' + 
						'<div class="custom-control custom-switch">' +
							'<input type="checkbox" class="custom-control-input" id="%provId%">' +
							'<label class="custom-control-label switch-label" style="color: %color%" for="%provId%">%label%</label>' + 
						'</div>' +
					  '</div>';	
		var i = 0;
		var arr = [];	
		
		for(var provinceCode in dataProvinceChart){
			var provinceDetails = dataProvinceChart[provinceCode];
			provinceDetails.active = false;
			var data = {
				provId: provinceCode,
				color: provinceColorPalette[i],
				label: provinceDetails.label
			}
			arr.push(data);
			i++;
		}

		arr.sort((a, b) => {
			if(a.label < b.label){
				return -1
			} 
			return 1;
		});
		
		arr.forEach(el => {jRow.append(MarcoUtils.template(strTmpl, el));});
		
		jRow.find("input").change(CovidCommon.changeProvinceCheckboxes);
		$(jRow.find("input").get(0)).prop("checked", true);
		$(jRow.find("input").get(0)).change();
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
			
			if(provinceDetails.active == true){
				chartProvince.addCovidChartDataset(dsProvince);
			}
			i++;
		}
		
		chartProvince.drawChart();
	}
	
	CovidCommon.drawRegionChart = function(){
		if(chartRegion == undefined){
			return;
		}
		
		chartRegion.clearDataSets();
		
		var i = 0;
		for(var regionCode in dataRegionChart){
			var regionDetails = dataRegionChart[regionCode];
			
			const dsRegion = new CovidChartDataset(regionDetails.label);
			dsRegion.setData(regionDetails.data);
			dsRegion.setColor(provinceColorPalette[i]);
			
			if(regionDetails.active == true){
				chartRegion.addCovidChartDataset(dsRegion);
			}
			i++;
		}
		
		chartRegion.drawChart();
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

