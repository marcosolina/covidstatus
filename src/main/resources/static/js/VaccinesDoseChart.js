/**
	This class will fetch the number of given vaccines grouped by doses and draw the Chart
 */
class VaccinesDoseChart {
	
	constructor(canvasId, colorPalette) {
		this.canvasId = canvasId;
		this.colorPalette = colorPalette;

		this.darkModeOn = false;
		this.lastResponse = {};

		//this.chart = new DoughnutChart(document.getElementById(this.canvasId));
        this.chart = new CovidChart(document.getElementById(this.canvasId));
		this.chart.setTitle("Dosi somministrate");
        
        this.arrBackGroundColors = [
            "rgba(255, 99, 132, 0.2)",
            "rgba(255, 159, 64, 0.2)",
            "rgba(255, 205, 86, 0.2)",
            "rgba(75, 192, 192, 0.2)",
            "rgba(54, 162, 235, 0.2)",
            "rgba(153, 102, 255, 0.2)",
            "rgba(201, 203, 207, 0.2)"
           ];
        this.arrBorderColor = [
            "rgb(255, 99, 132)", 
            "rgb(255, 159, 64)", 
            "rgb(255, 205, 86)", 
            "rgb(75, 192, 192)", 
            "rgb(54, 162, 235)", 
            "rgb(153, 102, 255)", 
            "rgb(201, 203, 207)"
            ];
	}
	
	setDarkMode(darkModeOn){
		this.darkModeOn = darkModeOn;
	}

	fetchData(fromToQueryParam) {
		let url = __URLS.VACCINES.DOSE + "?" + fromToQueryParam;
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
		let arrLabels = [];
        let data = [];
        let arrColors = [];
		
        for (let shotNumber in this.lastResponse.dataShotNumber) {
            data.push(this.lastResponse.dataShotNumber[shotNumber]);    
    		arrLabels.push(shotNumber);
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