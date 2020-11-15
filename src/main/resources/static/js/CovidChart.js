/**
	This class uses the Chartjs library to create a Chart
 */
class CovidChart {
	/**
		Param: DOM element -> where to draw the Chart. It must be of type "canvas"
	 */
	constructor(docElement){
		this.docElement = docElement;
		this.arrDataSets = [];
	}
	
	/**
		Param: string array -> It sets the array of the labels to use on the x axis
	 */	
	setLabels(arrLabels){
		this.arrLabels = arrLabels;
	}
	
	/**
		Param: CovidChartDataset -> It adds the dataset to the list of data to draw
	 */
	addCovidChartDataset(covidChartDataset){
		this.arrDataSets.push(covidChartDataset.getDataSet());
	}
	
	clearDataSets(){
		if(this.chart != undefined){
			this.arrDataSets = [];
			this.chart.destroy();
		}
	}
	
	/**
		Sets the title of the chart
	 */
	setTitle(stringTitle){
		this.title = stringTitle;
	}
	
	/**
		It draws the chart
	 */
	drawChart(){
		this.chart = new Chart(this.docElement, {
		    type: 'line',
		    data: {
		        labels: this.arrLabels,
		        datasets: this.arrDataSets
		    },
		    options:{
				title: {
					display: this.title != undefined ? true : false,
					text: this.title || ''
				},
				legend:{
					display: false
				},
		    	responsive: true,
		    	tooltips: {
					mode: 'index',
					intersect: false,
					itemSort: this.sortTooltip
				},

		    }
		    
		});
	}
	
	/**
		Function used to sort the info insed the tooltip
	 */
	sortTooltip(a, b, data){
		var labelA = data.datasets[a.datasetIndex].label;
		var labelB = data.datasets[b.datasetIndex].label;
		if(labelA < labelB){
			return -1;
		}
		return 1;
	}
}