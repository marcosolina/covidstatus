/**
	This class will fetch the data at Region level and draw the Chart
 */
class RegionsChart {

	constructor(canvasId, checkBoxesContainerId, dropDownDataTypeId, colorPalette) {
		this.canvasId = canvasId;
		this.checkBoxesContainerId = checkBoxesContainerId;
		this.colorPalette = colorPalette;
		this.dropDownDataTypeId = dropDownDataTypeId;

		this.darkModeOn = false;
		this.lastResponse = {};

		this.chart = new CovidChart(document.getElementById(canvasId));

		this.addCheckboxes();
		$("#" + this.dropDownDataTypeId).change(this.changeDataType.bind(this));
	}
	
	setDarkMode(darkModeOn){
		this.darkModeOn = darkModeOn;
	}

	/*
		It adds the checkboxes to select/de-select the regions to display the data for
	*/
	addCheckboxes() {
		var strTmpl = '<div class="col-6 col-sm-4 col-md-3 col-lg-2 col-xl-6">' +
							'<div class="custom-control custom-switch">' +
								'<input type="checkbox" class="custom-control-input" id="region-chart-region-%code%">' +
								'<label id="label-region-chart-region-%code%" class="custom-control-label switch-label" style="color: %color%" for="region-chart-region-%code%">%desc%</label>' +
							'</div>' +
						'</div>';

		let jContainer = $("#" + this.checkBoxesContainerId);
		
		let keysSorted = Object.keys(__REGIONS_MAP).sort(function(a, b) {
			if (__REGIONS_MAP[a].desc < __REGIONS_MAP[b].desc){
				return -1;	
			}
			if (__REGIONS_MAP[a].desc > __REGIONS_MAP[b].desc){
				return 1;	
			}
			return 0;
		});

		keysSorted.forEach(function(key){
			jContainer.append(MarcoUtils.template(strTmpl, __REGIONS_MAP[key]));
		});

		jContainer.find("input").change(this.drawChart.bind(this));
		$(jContainer.find("input").get(0)).prop("checked", true);
	}

	changeDataType() {
		this.fetchData(this.lastFromToQueryParam);
	}
	
	fetchData(fromToQueryParam) {
		this.lastFromToQueryParam = fromToQueryParam;
		let url = __URLS.INFECTIONS.REGION_DATA + "?" + fromToQueryParam + "&dataType=" + $("#" + this.dropDownDataTypeId).val();
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
		this.chart.setLabels(this.lastResponse.arrDates);

		for (let region in this.lastResponse.regionData) {

			if ($("#region-chart-region-" + region).prop("checked")) {
				var arr = this.lastResponse.regionData[region].data;
				const dataset = new CovidChartDataset(__REGIONS_MAP[region].desc);
				dataset.setData(arr);
				dataset.setColor($("#label-region-chart-region-" + region).css("color"));
				this.chart.addCovidChartDataset(dataset);
			}
		}

		this.chart.drawChart(this.darkModeOn);
	}
}