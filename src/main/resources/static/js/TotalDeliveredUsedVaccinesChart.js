/**
	This class will fetch the number of vaccines given grouped by age range and draw the Chart
 */
class TotalDeliveredUsedVaccinesChart {
	
	constructor(canvasId, colorPalette) {
		this.canvasId = canvasId;
		this.colorPalette = colorPalette;
		

		this.darkModeOn = false;
		this.lastResponse = {};
		
		this.chart = new DoughnutChart(document.getElementById(this.canvasId));
		this.chart.setTitle("Totale Vaccini Consegnati / Usati a livello Nazionale");
	}
	
	setDarkMode(darkModeOn){
		this.darkModeOn = darkModeOn;
	}
	
	fetchData(fromToQueryParam) {
		let url = __URLS.VACCINES.TOTALS;
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
		
		let arrLabels = ["Totale Vaccini Ricevuti", "Totale Vaccini Utilizzati"];
		let arrData = [this.lastResponse.totalDeliveredVaccines, this.lastResponse.totalUsedVaccines];
		
		arrLabels.forEach(function(key, index) {
			const dataset = new CovidChartDataset(key);
			dataset.setData(arrData[index]);
			dataset.setColor(this.colorPalette[index]);
			this.chart.addCovidChartDataset(dataset);
			arrLabels.push(key);
		}.bind(this));
		
		this.chart.setLabels(arrLabels);
		this.chart.drawChart(this.darkModeOn);
	}
}