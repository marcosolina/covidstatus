/**
	This class uses the Chartjs library to create a Chart
 */
class DoughnutChart {
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
	drawChart(darkModeOn){
		var arrData = [];
		var labels = [];
		var colors = [];
		
		for(let dataSet of this.arrDataSets){
			arrData.push(dataSet.data);
			labels.push(dataSet.label);
			colors.push(dataSet.backgroundColor);
		}
		
		let fontColor = darkModeOn ? "#FFFFFF" : "#666666";
		
		var config = {
		    type: 'doughnut',
		    data: {
		        labels: labels,
		        datasets: [{
					data: arrData,
					backgroundColor: colors
				}]
		    },
		    options:{
				title: {
					display: this.title != undefined ? true : false,
					text: this.title || '',
					fontColor: fontColor
				},
				legend:{
					labels:{
						fontColor: fontColor
					}
				},
		    	responsive: true,
			tooltips: {
					callbacks: {
						label: this.formatToolTip 
					}
				},
		    }
		    
		};
		
		this.chart = new Chart(this.docElement, config);
	}
	
	formatToolTip(toolTip, data){
		return data.labels[toolTip.index] + ": " + data.datasets[0].data[toolTip.index].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");
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
