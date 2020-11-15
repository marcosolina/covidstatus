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
	
	/*
		The following objects are storing the National, Region and Province data
		retrieved with the different Ajax calls
	*/
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
	
	/*
		This array stores my color palette
	*/
	var provinceColorPalette = [];
	
	/*
		These will store the definition of the three different charts
	*/
	var chartNational;
	var chartProvince;
	var chartRegion;
	
	/*
		These property will hold the last value selected in the region drop down
	*/
	var regionDropDownLastValue;
	
	/**
		Initialise the UI
	 */
	CovidCommon.init = function(){
		
		/*
			Setting up some jQuery elements and registering the event listeners
		*/
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
			CovidCommon.loadNationalData();
			CovidCommon.loadProvinceData();
			CovidCommon.loadRegionData();
        });

		dateTo.on( "change", function() {
	        dateFrom.datepicker( "option", "maxDate", CovidCommon.getDate( this ) );
			CovidCommon.loadNationalData();
			CovidCommon.loadProvinceData();
			CovidCommon.loadRegionData();
		});
		
		$("[type=checkbox]").change(CovidCommon.updateNationalChart);
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

		/*
			Defining my color palette
		*/
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

		/*
			Setting up the charts
		*/		
		chartNational = new CovidChart(document.getElementById('chartNational'));
		chartProvince = new CovidChart(document.getElementById('chartProvince'));
		chartRegion = new CovidChart(document.getElementById('chartRegions'));
		chartProvince.setTitle("Nuove infezioni nelle province di:");
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
	
	/**
		It will load the new set of the Province data
	 */	
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
	
	/**
		It will load the new set of the Region data
	 */	
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
		It will load the new set of the National data
	 */
	CovidCommon.loadNationalData = function(){
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
		It manages the onchange event of the switches available in the National chart tab
	 */
	CovidCommon.updateNationalChart = function(){
		var jElement = $(this);
		var id = jElement.prop("id");
		
		dataNationalChart[id].active = jElement.prop("checked");
		CovidCommon.drawNationalChart();
	}
	
	/**
		It manages the onchange event of the switches available in the Province chart tab
	 */
	CovidCommon.updateProvinceChart = function (){
		var jElement = $(this);
		var id = jElement.prop("id");
		
		dataProvinceChart[id].active = jElement.prop("checked");
		CovidCommon.drawProvinceChart();
	}
	
	/**
		It manages the onchange event of the switches available in the Region chart tab
	 */
	CovidCommon.updateRegionChart = function (){
		var jElement = $(this);
		var id = jElement.prop("id");
		
		dataRegionChart[id].active = jElement.prop("checked");
		CovidCommon.drawRegionChart();
	}
	
	
	
	/**
		Call back function of the Ajax calls. It will update the data on the screen
	 */
	CovidCommon.dataRetrieved = function(response){
		if(response.status){
			
			/*
				Setting the new set of labels (X axis)
			*/
			chartNational.setLabels(response.arrDates);
			chartProvince.setLabels(response.arrDates);
			chartRegion.setLabels(response.arrDates);
			
			/*
				Updating the data of the specific data type
			*/
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
					/*
						If the user has just change the dates (same value in the region drop down)
						I need to store the staus of the checkboxes, update the data, and re-activate
						the previous selected checkboxes
					*/
					var tmpClone;
					var regionForProvinceVal = $("#region").val();
					if(regionDropDownLastValue == regionForProvinceVal){
						tmpClone = {...dataProvinceChart};//cloning the old data
					}
					regionDropDownLastValue = regionForProvinceVal;
					
					dataProvinceChart = response.provinceData;
					CovidCommon.createProvinceCheckboxes();
					
					if(tmpClone != undefined){
						// reactivate the checkbox
						setProvinceCheckboxesStatus(tmpClone);
					}
					
					CovidCommon.drawProvinceChart();
					break;
				case "REGIONAL":
					var createCheckboxes = dataRegionChart == undefined;
					
					/*
						In this case the number of checboxes for the region will be always the same,
						so I can directly set the active checkbox status reading it from the 
						current status 
					*/
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
	
	/**
		Private function to set the status of the checkboxes for the specific province
	 */	
	function setProvinceCheckboxesStatus(dataProvinceStatus){
		for(var idCheckbox in dataProvinceStatus){
			$("#" + idCheckbox).prop("checked", dataProvinceStatus[idCheckbox].active);
			dataProvinceChart[idCheckbox].active = dataProvinceStatus[idCheckbox].active;
		}
	}
	
	/**
		This function will dynamically create the required checkboxes for the Region chart
	 */
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
		
		/*
			Preparing the data objects to use with the html template
		*/
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
		
		/*
			Sorting the checkboxes
		*/
		arr.sort((a, b) => {
			if(a.label < b.label){
				return -1
			} 
			return 1;
		});
		
		/*
			Adding the sorted checkbox, setting the status and attaching the even listener.
			By default I will active the first checkbox so the user can automatically see
			some data
		*/
		arr.forEach(el => {jRow.append(MarcoUtils.template(strTmpl, el));});
		jRow.find("input").change(CovidCommon.updateRegionChart);
		$(jRow.find("input").get(0)).prop("checked", true);
		$(jRow.find("input").get(0)).change();
	}

	/**
		This function will dynamically create the required checkboxes for the Province chart
	 */
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
		
		/*
			Preparing the data objects to use with the html template
		*/
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

		/*
			Sorting the checkboxes
		*/
		arr.sort((a, b) => {
			if(a.label < b.label){
				return -1
			} 
			return 1;
		});
		
		/*
			Adding the sorted checkbox, setting the status and attaching the even listener.
			By default I will active the first checkbox so the user can automatically see
			some data
		*/
		arr.forEach(el => {jRow.append(MarcoUtils.template(strTmpl, el));});
		jRow.find("input").change(CovidCommon.updateProvinceChart);
		$(jRow.find("input").get(0)).prop("checked", true);
		$(jRow.find("input").get(0)).change();
	}

	/**
		This function will draw the Province chart on the screen
	 */
	CovidCommon.drawProvinceChart = function(){
		if(chartProvince == undefined){
			return;
		}
		
		chartProvince.clearDataSets();
		
		/*
			Checking which data the user has selected to be drawn
		*/
		var i = 0;
		for(var provinceCode in dataProvinceChart){
			var provinceDetails = dataProvinceChart[provinceCode];
			
			if(provinceDetails.active == true){
				/*
					If the user has selected data, I will create a ChartDataset and
					add it to the chart
				*/
				const dsProvince = new CovidChartDataset(provinceDetails.label);
				dsProvince.setData(provinceDetails.newInfections);
				dsProvince.setColor(provinceColorPalette[i]);
				chartProvince.addCovidChartDataset(dsProvince);
			}
			i++;
		}
		
		chartProvince.drawChart();
	}
	
	/**
		This function will draw the Region chart on the screen
	 */
	CovidCommon.drawRegionChart = function(){
		if(chartRegion == undefined){
			return;
		}
		
		chartRegion.clearDataSets();
		
		/*
			Checking which data the user has selected to be drawn
		*/
		var i = 0;
		for(var regionCode in dataRegionChart){
			var regionDetails = dataRegionChart[regionCode];
			
			if(regionDetails.active == true){
				/*
					If the user has selected data, I will create a ChartDataset and
					add it to the chart
				*/
				const dsRegion = new CovidChartDataset(regionDetails.label);
				dsRegion.setData(regionDetails.data);
				dsRegion.setColor(provinceColorPalette[i]);
				chartRegion.addCovidChartDataset(dsRegion);
			}
			i++;
		}
		
		chartRegion.drawChart();
	}

	/**
		This function will draw the Region chart on the screen
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

