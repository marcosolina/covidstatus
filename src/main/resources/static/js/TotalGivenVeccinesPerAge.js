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
		this.chart.setTitle("Totale persone vaccinate per fasce di età");
	}
	
	setDarkMode(darkModeOn){
		this.darkModeOn = darkModeOn;
	}
	
	fetchData(fromToQueryParam) {
		let url = __URLS.TOTALS.TOTALS_AGE;
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
		let valuesPopulation = [];
        let valuesFirstDose = [];
        let valuesVaccinated = [];
		
		const datasetPopulation = new CovidChartDataset("Popolazione");
        const datasetFirstDose = new CovidChartDataset("Prima Dose");
        const datasetVaccinated = new CovidChartDataset("Vaccinati");
        
		datasetPopulation.setColor(this.colorPalette[0]);
        datasetFirstDose.setColor(this.colorPalette[1]);
        datasetVaccinated.setColor(this.colorPalette[2]);
		
		keysSorted.forEach(function(key){
			arrLabels.push(key);
			valuesPopulation.push(data[key].population);
            valuesFirstDose.push(data[key].firstDose);
            valuesVaccinated.push(data[key].completeVaccination);
		}.bind(this));
		
		datasetPopulation.setData(valuesPopulation);
        datasetFirstDose.setData(valuesFirstDose);
        datasetVaccinated.setData(valuesVaccinated);
        
		this.chart.addCovidChartDataset(datasetPopulation);
        this.chart.addCovidChartDataset(datasetFirstDose);
        this.chart.addCovidChartDataset(datasetVaccinated);
        
		this.chart.setLabels(arrLabels);
		this.chart.drawChart(this.darkModeOn, "horizontalBar", true);
	}
}