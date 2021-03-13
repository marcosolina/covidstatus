/**
	This class will fetch the number of vaccines given grouped by age range and draw the Chart
 */
class TotalDeliveredUsedVaccinesPerRegionChart {
	
	constructor(canvasId, colorPalette) {
		this.canvasId = canvasId;
		this.colorPalette = colorPalette;
		

		this.darkModeOn = false;
		this.lastResponse = {};
		
		this.chart = new CovidChart(document.getElementById(this.canvasId));
		this.chart.setTitle("Totale Vaccini Consegnati / Usati Per regione");
	}
	
	setDarkMode(darkModeOn){
		this.darkModeOn = darkModeOn;
	}

	fetchData(from, to) {
		MarcoUtils.executeAjax({type: "GET", url: __URLS.VACCINES.TOTALS_REGION})
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
		
		let keysSorted = Object.keys(__REGIONS_MAP).sort(function(a, b) {
			if (__REGIONS_MAP[a].desc < __REGIONS_MAP[b].desc){
				return -1;	
			}
			if (__REGIONS_MAP[a].desc > __REGIONS_MAP[b].desc){
				return 1;	
			}
			return 0;
		});
		
		let arrLabels = [];
		let arrDelivered = [];
		let arrUsed = [];
		
		keysSorted.forEach(function(key){
			arrLabels.push(__REGIONS_MAP[key].desc);
			arrDelivered.push(this.lastResponse.data[key].deliveredVaccines);
			arrUsed.push(this.lastResponse.data[key].givenVaccines);
		}.bind(this));
		
		const deliveredDataSet = new CovidChartDataset("Vaccini Consegnati");
		const giveDataSet = new CovidChartDataset("Vaccini Somministrati");
		
		deliveredDataSet.setColor(this.colorPalette[0]);
		giveDataSet.setColor(this.colorPalette[1]);
		
		deliveredDataSet.setData(arrDelivered);
		giveDataSet.setData(arrUsed);
		
		this.chart.addCovidChartDataset(deliveredDataSet);
		this.chart.addCovidChartDataset(giveDataSet);
		
		this.chart.setLabels(arrLabels);
		this.chart.drawChart(this.darkModeOn, "bar");
	}
}