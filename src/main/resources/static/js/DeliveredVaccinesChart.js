/**
 This class it is used to create the chart of the delivered vaccines per region
 */
class DeliveredVaccinesChart {
	
	constructor(canvasId, checkboxWrapperId, colorPalette) {
		this.canvasId = canvasId;
		this.checkboxWrapperId = checkboxWrapperId;
		this.colorPalette = colorPalette;

		this.darkModeOn = false;
		this.lastResponse = {};

		this.chart = new CovidChart(document.getElementById(this.canvasId));
		//this.chart.setTitle("Vaccini consegnati per regione");

		this.addRegionsCheckboxes();
	}

	setDarkMode(darkModeOn) {
		this.darkModeOn = darkModeOn;
	}

	/**
		It creates the checboxes used to select/un-select the region
	 */
	addRegionsCheckboxes() {
		var strTmpl = '<div class="col-6 col-sm-4 col-md-3 col-lg-2 col-xl-2">' +
			'<div class="custom-control custom-switch">' +
			'<input type="checkbox" class="custom-control-input" id="region-code-%code%">' +
			'<label id="label-region-code-%code%" class="custom-control-label switch-label" style="color: %color%" for="region-code-%code%">%desc%</label>' +
			'</div>' +
			'</div>';

		let jContainer = $("#" + this.checkboxWrapperId);

		let keysSorted = Object.keys(__REGIONS_MAP).sort(function(a, b) {
			if (__REGIONS_MAP[a].desc < __REGIONS_MAP[b].desc) {
				return -1;
			}
			if (__REGIONS_MAP[a].desc > __REGIONS_MAP[b].desc) {
				return 1;
			}
			return 0;
		});

		keysSorted.forEach(function(key) {
			jContainer.append(MarcoUtils.template(strTmpl, __REGIONS_MAP[key]));
		});

		jContainer.find("input").change(this.drawChart.bind(this));
		$(jContainer.find("input").get(0)).prop("checked", true);
	}


	fetchData(from, to) {
		if (from != "" && to != "") {
			MarcoUtils.executeAjax({
				dataToPost: {
					from: from,
					to: to
				},
				showLoading: true,
				url: __URLS.VACCINES.PER_REGION
			}).then(this.dataRetrieved.bind(this));
		}
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

		for (let region in this.lastResponse.deliveredPerRegion) {
			if ($("#region-code-" + region).prop("checked")) {
				var arr = this.lastResponse.deliveredPerRegion[region];
				const dataset = new CovidChartDataset(__REGIONS_MAP[region].desc);
				dataset.setData(arr);
				dataset.setColor($("#label-region-code-" + region).css("color"));
				this.chart.addCovidChartDataset(dataset);
			}
		}

		this.chart.drawChart(this.darkModeOn);

	}
}