/**
	This class will fetch the number of vaccines given grouped by age range and draw the Chart
 */
class VaccinesPerAgeChart {
	
	constructor(canvasId, colorPalette) {
		this.canvasId = canvasId;
		this.colorPalette = colorPalette;
		

		this.darkModeOn = false;
		this.lastResponse = {};
		
		this.chart = new CovidChart(document.getElementById(this.canvasId));
		this.chart.setTitle("Fasce di età vaccinate");
        this.chart.setHideTextFromTooltip(true);
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
		let valuesThirdDose = [];
		let valuesFourthDose = [];
        
        const datasetPopulation = new CovidChartDataset("Popolazione");
        const datasetFirstDose = new CovidChartDataset("Prima Dose");
        const datasetVaccinated = new CovidChartDataset("Vaccinati (Seconda Dose o Mono Dose)");
		const datasetThirdDose = new CovidChartDataset("Terza Dose (Addizionale / Booster)");
		const datasetFourthDose = new CovidChartDataset("Quarta Dose");
        
        datasetPopulation.setColor(this.colorPalette[0]);
        datasetFirstDose.setColor(this.colorPalette[1]);
        datasetVaccinated.setColor(this.colorPalette[2]);
		datasetThirdDose.setColor(this.colorPalette[3]);
		datasetFourthDose.setColor(this.colorPalette[4]);
        
        keysSorted.forEach(function(key){
            arrLabels.push(key);
            valuesPopulation.push(data[key].population);
            valuesFirstDose.push(data[key].firstDose);
            valuesVaccinated.push(data[key].secondDose + data[key].monoDose + data[key].doseAfterInfection);
			valuesThirdDose.push(data[key].thirdDose);
			valuesFourthDose.push(data[key].fourthDose);
        }.bind(this));
        
        datasetPopulation.setData(valuesPopulation);
        datasetFirstDose.setData(valuesFirstDose);
        datasetVaccinated.setData(valuesVaccinated);
		datasetThirdDose.setData(valuesThirdDose);
		datasetFourthDose.setData(valuesFourthDose);
        
        this.chart.addCovidChartDataset(datasetPopulation);
        this.chart.addCovidChartDataset(datasetFirstDose);
        this.chart.addCovidChartDataset(datasetVaccinated);
		this.chart.addCovidChartDataset(datasetThirdDose);
		this.chart.addCovidChartDataset(datasetFourthDose);
        
        this.chart.setLabels(arrLabels);
        this.chart.drawChart(this.darkModeOn, "horizontalBar", true, false);
	}
}