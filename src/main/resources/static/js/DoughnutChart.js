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
			//this.chart.destroy();
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
		
		if(this.chart != undefined){
			this.chart.options.title.fontColor = fontColor;
			this.chart.options.legend.labels.fontColor = fontColor;
			this.chart.data.labels.map((v, i) => config.data.labels[i]);
			this.chart.data.datasets.forEach((dataSet, i) => {
				dataSet.backgroundColor = dataSet.backgroundColor.map((v, j) => config.data.datasets[i].backgroundColor[j]);  
				dataSet.data = dataSet.data.map((v, j) => config.data.datasets[i].data[j]);
			});
			
			this.chart.update();			
		}else{
			this.chart = new Chart(this.docElement, config);
		}
	}
	
	formatToolTip(toolTip, data){
		return data.labels[toolTip.index] + ": " + data.datasets[0].data[toolTip.index].toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");
	}
}
