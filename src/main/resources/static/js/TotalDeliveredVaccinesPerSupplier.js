/**
	This class will fetch the number of vaccines given grouped by age range and draw the Chart
 */
class TotalDeliveredVaccinesPerSupplier {
	
	constructor(canvasId, colorPalette) {
		this.canvasId = canvasId;
		this.colorPalette = colorPalette;
		

		this.darkModeOn = false;
		this.lastResponse = {};
		
		this.chart = new DoughnutChart(document.getElementById(this.canvasId));
		this.chart.setTitle("Totale Forniture Vaccini");
	}
	
	setDarkMode(darkModeOn){
		this.darkModeOn = darkModeOn;
	}
	
	fetchData(fromToQueryParam) {
		let url = __URLS.TOTALS.TOTALS_DELIVERED;
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