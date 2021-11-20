/**
	This class will fetch the number of delivered vaccines grouped by supplier and draw the Chart
 */
class SuppliersVaccinesChart {
	
	constructor(canvasId, colorPalette) {
		this.colorPalette = colorPalette;
		this.canvasId = canvasId;

		this.darkModeOn = false;
		this.lastResponse = {};

		this.chart = new CovidChart(document.getElementById(this.canvasId));
		this.chart.setTitle("Dosi fornite");
	}
	
	setDarkMode(darkModeOn){
		this.darkModeOn = darkModeOn;
	}
	
	fetchData(fromToQueryParam) {
		let url = __URLS.VACCINES.PER_SUPPLIER + "?" + fromToQueryParam;
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
        let data = [];
        let arrColors = [];

		for (let supplier in this.lastResponse.deliveredPerSupplier) {
			
			data.push(this.lastResponse.deliveredPerSupplier[supplier]);
			arrLabels.push(supplier);
            arrColors.push(this.colorPalette[i++]);
		}
		
		const dataset = new CovidChartDataset("");
		dataset.setData(data);
		dataset.setColor(arrColors);
		
		this.chart.addCovidChartDataset(dataset);
		this.chart.setLabels(arrLabels);
		this.chart.drawChart(this.darkModeOn, "horizontalBar", undefined, false);
	}
}