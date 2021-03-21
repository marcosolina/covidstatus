/**
	This class will fetch the number of given vaccines grouped by doses and draw the Chart
 */
class VaccinesDoseChart {
	
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

	fetchData(fromToQueryParam) {
		let url = __URLS.VACCINES.DOSE + "?" + fromToQueryParam;
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