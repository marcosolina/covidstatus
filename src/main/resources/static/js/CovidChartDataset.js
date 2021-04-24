/**
	This class is used to represend a Dataset used with Chartjs
 */
class CovidChartDataset {
	/**
		Param: string -> Info label for this dataset
	 */
	constructor(label){
		this.label = label;
	}
	
	/**
		Param: array -> List of data
	 */
	setData(arrData){
		this.arrData = arrData;
	}
	
	/**
		Param: string -> rgb color string ex: "rgb(25, 194, 255, 1)"
	 */
	setColor(stringRgb){
		this.color = stringRgb;
	}
    
    setBorderColor(stringRgb){
        this.borderColor = stringRgb;
    }
	
	/**
		It returns the Chartjs ojbect that represents the data
	 */
	getDataSet(){
		return {
            label: this.label,
            data: this.arrData,
            borderColor: this.borderColor || this.color,
            backgroundColor: this.color,
            fill: false,
            pointRadius: 0
        };
	}
}