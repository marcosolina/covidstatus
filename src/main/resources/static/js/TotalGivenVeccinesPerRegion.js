/**
	This class will fetch the number of vaccines given grouped by age range and draw the Chart
 */
class TotalGivenVeccinesPerRegion {
	
	constructor(canvasId, colorPalette) {
		this.canvasId = canvasId;
		this.colorPalette = colorPalette;
		

		this.darkModeOn = false;
		this.lastResponse = {};
		
		this.chart = new CovidChart(document.getElementById(this.canvasId));
        this.chart.setHideTextFromTooltip(true);
        this.chart.setTitle("% Popolazione Vaccinata per Regione");
        this.chart.setLabelSuffix(" %");
	}
	
	setDarkMode(darkModeOn){
		this.darkModeOn = darkModeOn;
	}
	
	fetchData(fromToQueryParam) {
		let url = __URLS.TOTALS.TOTALS_VACCINATED_PER_REGION;
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
        
        let data = this.lastResponse.vaccinatedPeople;

        let arrLabels = [];
        let valuesFirstDose = [];
        let valuesVaccinated = [];
        
        const datasetFirstDose = new CovidChartDataset("% 1a dose");
        const datasetVaccinated = new CovidChartDataset("% 2a Dose o Mono Dose");
        
        datasetFirstDose.setColor(this.colorPalette[1]);
        datasetVaccinated.setColor(this.colorPalette[2]);
        
        data.forEach(function(entry){
            arrLabels.push(__REGIONS_MAP[entry.regionCode].desc);
            valuesFirstDose.push(entry.firstDosePerc);
            valuesVaccinated.push(entry.vaccinatedPerc);
        }.bind(this));
        
        datasetFirstDose.setData(valuesFirstDose);
        datasetVaccinated.setData(valuesVaccinated);
        
        this.chart.addCovidChartDataset(datasetFirstDose);
        this.chart.addCovidChartDataset(datasetVaccinated);
        
        this.chart.setLabels(arrLabels);
        this.chart.drawChart(this.darkModeOn, "bar", true);
    }
}