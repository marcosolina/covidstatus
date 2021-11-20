/**
	This class will fetch the number of vaccines given grouped by age range and draw the Chart
 */
class TotalGivenVeccinesPerAge {
	
	constructor(canvasId, canvasIdPerc, colorPalette) {
		this.canvasId = canvasId;
        this.canvasIdPerc = canvasIdPerc;
		this.colorPalette = colorPalette;
		

		this.darkModeOn = false;
		this.lastResponse = {};
		
		this.chart = new CovidChart(document.getElementById(this.canvasId));
        this.chart2 = new CovidChart(document.getElementById(this.canvasIdPerc));
        
        this.chart.setHideTextFromTooltip(true);
        this.chart2.setHideTextFromTooltip(true);
        
		this.chart.setTitle("Totale persone vaccinate per fasce di età");
        this.chart2.setTitle("% Popolazione Vaccinata");
        this.chart2.setLabelSuffix(" %");
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

    drawPercChart() {
        this.chart2.clearDataSets();
        
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
        let valuesFirstDose = [];
        let valuesVaccinated = [];
		let valuesThirdDose = [];
        
        const datasetFirstDose = new CovidChartDataset("% 1a dose");
        const datasetVaccinated = new CovidChartDataset("% 2a Dose o Mono Dose");
		const datasetThirdDose = new CovidChartDataset("% 3a Dose (Addizionale / Booster)");
        
        datasetFirstDose.setColor(this.colorPalette[1]);
        datasetVaccinated.setColor(this.colorPalette[2]);
		datasetThirdDose.setColor(this.colorPalette[3]);
        
        keysSorted.forEach(function(key){
            arrLabels.push(key);
            valuesFirstDose.push(data[key].firstDosePerc);
            valuesVaccinated.push(data[key].vaccinatedPerc);
			valuesThirdDose.push(data[key].thirdDosePerc)
        }.bind(this));
        
        datasetFirstDose.setData(valuesFirstDose);
        datasetVaccinated.setData(valuesVaccinated);
		datasetThirdDose.setData(valuesThirdDose);
        
        this.chart2.addCovidChartDataset(datasetFirstDose);
        this.chart2.addCovidChartDataset(datasetVaccinated);
		this.chart2.addCovidChartDataset(datasetThirdDose);
        
        this.chart2.setLabels(arrLabels);
        this.chart2.drawChart(this.darkModeOn, "bar", true, false);
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
		
		const datasetPopulation = new CovidChartDataset("Popolazione");
        const datasetFirstDose = new CovidChartDataset("Prima Dose");
        const datasetVaccinated = new CovidChartDataset("Vaccinati (Seconda Dose o Mono Dose)");
		const datasetThirdDose = new CovidChartDataset("Terza Dose (Addizionale / Booster)");
        
		datasetPopulation.setColor(this.colorPalette[0]);
        datasetFirstDose.setColor(this.colorPalette[1]);
        datasetVaccinated.setColor(this.colorPalette[2]);
		datasetThirdDose.setColor(this.colorPalette[3]);
		
		keysSorted.forEach(function(key){
			arrLabels.push(key);
			valuesPopulation.push(data[key].population);
            valuesFirstDose.push(data[key].firstDose);
            valuesVaccinated.push(data[key].secondDose + data[key].monoDose + data[key].doseAfterInfection);
			valuesThirdDose.push(data[key].thirdDose);
		}.bind(this));
		
		datasetPopulation.setData(valuesPopulation);
        datasetFirstDose.setData(valuesFirstDose);
        datasetVaccinated.setData(valuesVaccinated);
		datasetThirdDose.setData(valuesThirdDose);
        
		this.chart.addCovidChartDataset(datasetPopulation);
        this.chart.addCovidChartDataset(datasetFirstDose);
        this.chart.addCovidChartDataset(datasetVaccinated);
		this.chart.addCovidChartDataset(datasetThirdDose);
        
		this.chart.setLabels(arrLabels);
		this.chart.drawChart(this.darkModeOn, "horizontalBar", true, false);
        
        this.drawPercChart();
	}
}