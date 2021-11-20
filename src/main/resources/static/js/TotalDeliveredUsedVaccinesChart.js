/**
	This class will fetch the number of vaccines given grouped by age range and draw the Chart
 */
class TotalDeliveredUsedVaccinesChart {
	
	constructor(canvasId, labelsContainerId, colorPalette) {
		this.canvasId = canvasId;
		this.colorPalette = colorPalette;
		this.labelsContainerId = labelsContainerId;
		

		this.darkModeOn = false;
		this.lastResponse = {};
		
		this.chart = new DoughnutChart(document.getElementById(this.canvasId));
	}
	
	setDarkMode(darkModeOn){
		this.darkModeOn = darkModeOn;
	}
	
	fetchData(fromToQueryParam) {
		let url = __URLS.TOTALS.TOTALS_DELIVER_USED;
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
		
		let template = '<div class="col-12 col-sm-5 col-md-4 col-lg-5 col-xl-4">' +
							'<label style="color: %color%" >%label%</label>' +
						'</div>';
		let jContainer = $("#" + this.labelsContainerId);
		jContainer.empty();
		
		arrLabels.forEach(function(key, index) {
			const dataset = new CovidChartDataset(key);
			dataset.setData(arrData[index]);
			
			let color = this.colorPalette[index];
			
			jContainer.append(MarcoUtils.template(template, {
				label: key,
				color: color
			}));
			
			dataset.setColor(color);
			this.chart.addCovidChartDataset(dataset);
			arrLabels.push(key);
		}.bind(this));
		
		this.chart.setLabels(arrLabels);
		this.chart.drawChart(this.darkModeOn, false);
	}
}