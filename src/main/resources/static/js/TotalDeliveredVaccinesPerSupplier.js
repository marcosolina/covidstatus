/**
	This class will fetch the number of vaccines given grouped by age range and draw the Chart
 */
class TotalDeliveredVaccinesPerSupplier {
	
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
		let url = __URLS.TOTALS.TOTALS_DELIVERED;
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
		
		let template = '<div class="col-12 col-sm-6 col-md-3 col-lg-3 col-xl-3">' +
							'<label style="color: %color%" >%label%</label>' +
						'</div>';
		let jContainer = $("#" + this.labelsContainerId);
		jContainer.empty();

		let i = 0;
		let arrLabels = [];
		for (let supplier in this.lastResponse.deliveredPerSupplier) {
			const dataset = new CovidChartDataset(supplier);
			dataset.setData(this.lastResponse.deliveredPerSupplier[supplier]);
			
			let color = this.colorPalette[i++];
			
			jContainer.append(MarcoUtils.template(template, {
				label: supplier,
				color: color
			}));
			
			dataset.setColor(color);
			this.chart.addCovidChartDataset(dataset);
			arrLabels.push(supplier);
		}
		this.chart.setLabels(arrLabels);
		this.chart.drawChart(this.darkModeOn, false);
	}
}