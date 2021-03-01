class SuppliersVaccinesChart {
	//TODO
	constructor(canvasId, colorPalette) {
		this.colorPalette = colorPalette;
		this.canvasId = canvasId;

		this.darkModeOn = false;
		this.lastResponse = {};

		this.chart = new DoughnutChart(document.getElementById(this.canvasId));

		this.chart.setTitle("Dosi fornite");
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
				url: __URLS.URL_VACCINES_DELIVERED_DATA
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

		/*
			Doughnut chart to show the suppliers
		*/
		var i = 0;
		var arrLabels = [];
		for (let supplier in this.lastResponse.deliveredPerSupplier) {
			const dataset = new CovidChartDataset(supplier);
			dataset.setData(this.lastResponse.deliveredPerSupplier[supplier]);
			dataset.setColor(this.colorPalette[i++]);
			this.chart.addCovidChartDataset(dataset);
			arrLabels.push(supplier);
		}
		this.chart.setLabels(arrLabels);
		this.chart.drawChart(this.darkModeOn);
	}
}