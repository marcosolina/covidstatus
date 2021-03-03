class VaccinesPerAgeChart {
	
	constructor(canvasId, colorPalette) {
		this.canvasId = canvasId;
		this.colorPalette = colorPalette;
		

		this.darkModeOn = false;
		this.lastResponse = {};
		
		this.chart = new DoughnutChart(document.getElementById(this.canvasId));
		this.chart.setTitle("Fasce di età vaccinate");
	}
	
	setDarkMode(darkModeOn){
		this.darkModeOn = darkModeOn;
	}

	fetchData(from, to) {
		if (from != "" && to != "") {
			MarcoUtils.executeAjax({
				dataToPost: {
					from: from,
					to: to
				},
				showLoading: true,
				url: __URLS.VACCINES.AGE
			}).then(this.dataRetrieved.bind(this));
		}
	}

	dataRetrieved(response) {
		if (response.status) {
			this.lastResponse = response;
			this.drawChart();
		}
	}

	drawChart() {
		this.chart.clearDataSets();
		
		let i = 0;
		let arrLabels = [];
		for (let ageRange in this.lastResponse.dataVaccinatedPerAge) {
			const dataset = new CovidChartDataset(ageRange);
			dataset.setData(this.lastResponse.dataVaccinatedPerAge[ageRange]);
			dataset.setColor(this.colorPalette[i++]);
			this.chart.addCovidChartDataset(dataset);
			arrLabels.push(ageRange);
		}
		this.chart.setLabels(arrLabels);
		this.chart.drawChart(this.darkModeOn);
	}
}