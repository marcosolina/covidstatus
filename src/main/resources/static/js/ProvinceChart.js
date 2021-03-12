/**
	This class will fetch the data at Province level and draw the Chart
 */
class ProvinceChart {

	constructor(canvasId, checkBoxesContainerId, dropDownRegionsId, colorPalette) {
		this.canvasId = canvasId;
		this.checkBoxesContainerId = checkBoxesContainerId;
		this.colorPalette = colorPalette;
		this.dropDownRegionsId = dropDownRegionsId;

		this.darkModeOn = false;
		this.lastResponse = {};

		this.chart = new CovidChart(document.getElementById(canvasId));

		$("#" + this.dropDownRegionsId).change(this.changeRegion.bind(this));
	}
	
	setDarkMode(darkModeOn){
		this.darkModeOn = darkModeOn;
	}

	/**
		It adds the checkboxes used to select/de-select the provices to display the data for
	 */
	addCheckboxes() {
		var strTmpl = '<div class="col-6 col-sm-4 col-md-3 col-lg-2 col-xl-6">' +
							'<div class="custom-control custom-switch">' +
								'<input type="checkbox" class="custom-control-input" id="province-%code%">' +
								'<label id="label-province-%code%" class="custom-control-label switch-label" style="color: %color%" for="province-%code%">%label%</label>' +
							'</div>' +
						'</div>';
		let jContainer = $("#" + this.checkBoxesContainerId);
		jContainer.empty();

		let tmpMap = this.lastResponse.provinceData;
		let keysSorted = Object.keys(tmpMap).sort(function(a, b) {
			if (tmpMap[a].label < tmpMap[b].label) {
				return -1;
			}
			if (tmpMap[a].label > tmpMap[b].label) {
				return 1;
			}
			return 0;
		});

		let i = 0;
		let colors = this.colorPalette;
		keysSorted.forEach(function(key){
			let obj = {
				code: key,
				color: colors[i++],
				label: tmpMap[key].label
			}
			jContainer.append(MarcoUtils.template(strTmpl, obj));
		});

		jContainer.find("input").change(this.drawChart.bind(this));
		$(jContainer.find("input").get(0)).prop("checked", true);
	}

	changeRegion() {
		this.fetchData(this.lastFrom, this.lastTo);
	}

	fetchData(from, to) {
		this.lastFrom = from;
		this.lastTo = to;
		if(from != "" && to != ""){
			MarcoUtils.executeAjax({
				dataToPost: {
				    from: from,
				    to: to,
					regionCode: $("#" + this.dropDownRegionsId).val(),
				},
				showLoading: true,
				url: __URLS.INFECTIONS.PROVINCE_DATA
			}).then(this.dataRetrieved.bind(this));
		}
	}

	dataRetrieved(response) {
		if (response.status) {
			this.lastResponse = response;
			this.addCheckboxes();
			this.drawChart();
		}
	}

	drawChart() {
		this.chart.clearDataSets();
		this.chart.setLabels(this.lastResponse.arrDates);

		for (let province in this.lastResponse.provinceData) {

			if ($("#province-" + province).prop("checked")) {
				var arr = this.lastResponse.provinceData[province].newInfections;
				const dataset = new CovidChartDataset(this.lastResponse.provinceData[province].label);
				dataset.setData(arr);
				dataset.setColor($("#label-province-" + province).css("color"));
				this.chart.addCovidChartDataset(dataset);
			}
		}

		this.chart.drawChart(this.darkModeOn);
	}

}