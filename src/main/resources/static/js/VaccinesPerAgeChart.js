/**
	This class will fetch the number of vaccines given grouped by age range and draw the Chart
 */
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
	
	fetchData(fromToQueryParam) {
		let url = __URLS.VACCINES.AGE + "?" + fromToQueryParam;
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
		
		let keysSorted = Object.keys(this.lastResponse.dataVaccinatedPerAge).sort(function(a, b) {
			if (a.charAt(0) < b.charAt(0)) {
				return -1;
			}
			if (a.charAt(0) > b.charAt(0)) {
				return 1;
			}
			return 0;
		});
		
		
		let arrLabels = [];
		keysSorted.forEach(function(key) {
			const dataset = new CovidChartDataset(key);
			dataset.setData(this.lastResponse.dataVaccinatedPerAge[key]);
			dataset.setColor(this.colorPalette[i++]);
			this.chart.addCovidChartDataset(dataset);
			arrLabels.push(key);
		}.bind(this));
		
		this.chart.setLabels(arrLabels);
		this.chart.drawChart(this.darkModeOn);
	}
}