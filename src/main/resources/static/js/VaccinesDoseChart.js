class VaccinesDoseChart {
	//TODO
	constructor(canvasId, colorPalette) {
		this.canvasId = canvasId;
		this.colorPalette = colorPalette;

		this.darkModeOn = false;
		this.lastResponse = {};

		this.chart = new DoughnutChart(document.getElementById(this.canvasId));
		this.chart.setTitle("Dosi somministrate");
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
				url: __URLS.VACCINES.DOSE
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
		for (let shotNumber in this.lastResponse.dataShotNumber) {
			const dataset = new CovidChartDataset(shotNumber);
			dataset.setData(this.lastResponse.dataShotNumber[shotNumber]);
			dataset.setColor(this.colorPalette[i++]);
			this.chart.addCovidChartDataset(dataset);
			arrLabels.push(shotNumber);
		}
		this.chart.setLabels(arrLabels);
		this.chart.drawChart(this.darkModeOn);
	}
}