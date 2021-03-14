/**
	This class will fetch the number of vaccines given grouped by age range and draw the Chart
 */
class TotalGivenVeccinesPerAge {
	
	constructor(canvasId, colorPalette) {
		this.canvasId = canvasId;
		this.colorPalette = colorPalette;
		

		this.darkModeOn = false;
		this.lastResponse = {};
		
		this.chart = new CovidChart(document.getElementById(this.canvasId));
		this.chart.setTitle("Totale persone vaccinate per fasce ");
	}
	
	setDarkMode(darkModeOn){
		this.darkModeOn = darkModeOn;
	}

	fetchData(from, to) {
		MarcoUtils.executeAjax({type: "GET", url: __URLS.VACCINES.TOTALS_AGE})
			.then(this.dataRetrieved.bind(this));
	}

	dataRetrieved(response) {
		if (response.status) {
			this.lastResponse = response;
			this.drawChart();
		}
	}

	drawChart() {
		this.chart.clearDataSets();
		
		let data = this.lastResponse.dataVaccinatedPerAge;
		
		let keysSorted = Object.keys(data).sort(function(a, b) {
			if (a.charAt(0) < b.charAt(0)){
				return -1;	
			}
			if (a.charAt(0) > b.charAt(0)){
				return 1;	
			}
			return 0;
		});
		
		let arrLabels = [];
		let values = [];
		
		const dataSet = new CovidChartDataset("");
		dataSet.setColor(this.colorPalette[0]);
		
		keysSorted.forEach(function(key){
			arrLabels.push(key);
			values.push(data[key]);
		}.bind(this));
		
		dataSet.setData(values);
		this.chart.addCovidChartDataset(dataSet);
		this.chart.setLabels(arrLabels);
		this.chart.drawChart(this.darkModeOn, "horizontalBar");
	}
}