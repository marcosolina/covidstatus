/**
	This class will fetch the data at national level and draw the Chart
 */
class NationalChart {

	constructor(canvasId, checkBoxesContainerId, colorPalette, totals) {
		this.canvasId = canvasId;
		this.checkBoxesContainerId = checkBoxesContainerId;
		this.colorPalette = colorPalette;
		this.totals = totals === true;
		
		this.darkModeOn = false;
		this.lastResponse = {};

		this.chart = new CovidChart(document.getElementById(canvasId));

		this.descData = {
			arrPercInfections: "% Infetti su tamponi eseguiti",
			arrPercCasualties: "% Decessi su infetti",
			arrNewTests: "Test Eseguiti",
			arrNewInfections: "Nuove Infezioni",
			arrNewCasualties: "Decessi",
			arrNewHospitalized: "Nuovi Ricoveri",
			arrNewIntensiveTherapy: "Ricoveri Terapia intensiva (Di Cui)",
			arrNewRecovered: "Nuovi Dismessi/Guariti"
		};

		this.addCheckboxes();
	}
	
	setDarkMode(darkModeOn){
		this.darkModeOn = darkModeOn;
	}

	/**
		Adds the checkboxes used to select/de-select the data to display
	 */
	addCheckboxes() {
		let template = '<div class="col-12 col-sm-6 col-md-4 col-lg-3 col-xl-6">' +
							'<div class="custom-control custom-switch">' +
								'<input type="checkbox" class="custom-control-input" id="%checkBoxId%">' +
								'<label class="custom-control-label switch-label" style="color: %color%" for="%checkBoxId%" id="label%checkBoxId%">%label%</label>' +
							'</div>' +
						'</div>';
		let jContainer = $("#" + this.checkBoxesContainerId);
		let index = 0;
		let prefix = this.totals ? "tot" : "";
		for (let dataType in this.descData) {
			jContainer.append(MarcoUtils.template(template, {
				checkBoxId: prefix + dataType,
				label: this.descData[dataType],
				color: this.colorPalette[index++]
			}));
		}

		jContainer.find("input").change(this.drawChart.bind(this));
		$(jContainer.find("input").get(0)).prop("checked", true);
		if(!this.totals){
			$(jContainer.find("input").get(1)).prop("checked", true);
		}
	}

	fetchData(fromToQueryParam) {
		let url;
		if(this.totals){
			url = __URLS.TOTALS.TOTALS_INFECTIONS_DATA;
		}else{
			url = __URLS.INFECTIONS.NATIONAL_DATA + "?" + fromToQueryParam;
		}
		MarcoUtils.executeAjax({type: "GET", url: url}).then(this.dataRetrieved.bind(this));
	}

	dataRetrieved(response) {
		if (response.status) {
			this.lastResponse = response;
			this.drawChart();
		}
	}

	drawChart() {
		this.chart.clearDataSets();
		this.chart.setLabels(this.lastResponse.arrDates);
		
		let prefix = this.totals ? "tot" : "";
		for (let dataType in this.descData) {
			if($("#" + prefix + dataType).prop("checked")){
				var arr = this.lastResponse[dataType];
				const dataset = new CovidChartDataset(this.descData[dataType]);
				dataset.setData(arr);
				dataset.setColor($("#label" + prefix + dataType).css("color"));
				this.chart.addCovidChartDataset(dataset);
			}
		}
		
		this.chart.drawChart(this.darkModeOn);
	}
}