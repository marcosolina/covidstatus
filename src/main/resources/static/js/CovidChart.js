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
		this.invertedAxes = false;
		this.oldDarkMode = false;
        this.hideTextFromTooltip = false;
        this.labelSuffix = "";
	}
	
	/**
		Param: string array -> It sets the array of the labels to use on the x axis
	 */	
	setLabels(arrLabels){
		this.arrLabels = arrLabels;
	}
    
    setHideTextFromTooltip(boolHide){
        this.hideTextFromTooltip = boolHide;
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
    
    setLabelSuffix(stringSuffix){
        this.labelSuffix = stringSuffix;
    }
	
	/**
		It draws the chart
	 */
	drawChart(darkModeOn, type, displayLegend){
		
		let fontColor = darkModeOn ? "#FFFFFF" : "#666666";
		let color = darkModeOn ? "rgba(255, 255, 255, 0.5)" : 'rgba(0, 0, 0, 0.1)';
		this.invertedAxes = type == "horizontalBar";
		
		var config = {
		    type: type || 'line',
		    data: {
		        labels: this.arrLabels,
		        datasets: this.arrDataSets
		    },
		    options:{
				title: {
					display: this.title != undefined ? true : false,
					text: this.title || '',
					fontColor: fontColor,
				},
				legend:{
					display: displayLegend || false,
                    labels:{
                        fontColor: fontColor
                    }
				},
				scales: {
	                yAxes: [{
	                    ticks: {
	                        fontColor: fontColor,
	                    },
						gridLines: {
							color: color,
						}
	                }],
	                xAxes: [{
	                    ticks: {
	                        fontColor: fontColor,
	                    },
						gridLines: {
							color: color,
						}
	                }]
	            },
		    	responsive: true,
		    	tooltips: {
					mode: 'index',
					intersect: false,
					itemSort: this.sortTooltip,
					callbacks: {
						label: this.formatToolTip.bind(this)
					}
				},

		    }
		    
		};
		
		if(this.chart != undefined){
			if(this.oldDarkMode == darkModeOn && 
				config.data.labels && 
				this.chart.data.labels && 
				this.chart.data.labels.length == config.data.labels.length &&
				this.chart.data.datasets.length == config.data.datasets.length){
					
				this.chart.data.labels.map((v, i) => config.data.labels[i]);
				this.chart.data.datasets.forEach((dataSet, i) => {
					dataSet.backgroundColor = config.data.datasets[i].backgroundColor;  
					dataSet.data = dataSet.data.map((v, j) => config.data.datasets[i].data[j]);
				});			
				this.chart.update();
				return;		
			}else{
				this.chart.destroy();
			}

		}
		this.oldDarkMode = darkModeOn;
		this.chart = new Chart(this.docElement, config);
	}
	
	formatToolTip(toolTip, data){
		let tmp = this.hideTextFromTooltip ? "" : data.datasets[toolTip.datasetIndex].label;
		
		let label = this.invertedAxes ? toolTip.xLabel : toolTip.yLabel;
		
		// If it is an Integer
		if(label % 1 === 0){
			tmp += ": " + label.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");
		}else{
			tmp += ": " + label;
		}
        
        if(this.hideTextFromTooltip){
            tmp = tmp.substring(1);
        }
        
        return tmp + this.labelSuffix;
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