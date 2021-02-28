class VaccinesChart {

	constructor(config) {
		this.regionCheckBoxesContainerId = config.regionCheckBoxesContainerId;
		this.colorPalette = config.colorPalette;
		this.idPersonCheckboxesWrapper = config.idPersonCheckboxesWrapper;
		this.canvasIdGivenVaccinesPerRegion = config.canvasIdGivenVaccinesPerRegion;
		this.canvasIdSuppliersVaccines = config.canvasIdSuppliersVaccines;
		this.canvasIdVaccinesPerPerson = config.canvasIdVaccinesPerPerson;
		this.canvasIdVaccinesPerAge = config.canvasIdVaccinesPerAge;
		this.convasIdVaccninsDoses = config.convasIdVaccninsDoses

		this.darkModeOn = false;
		this.lastResponse = {};
		this.checkBoxesDesc = {
			over: {code: "over", desc: "Over 80"},
			nonospedali: {code: "nonospedali", desc: "Personale non sanitario"},
			polizia: {code: "polizia", desc: "Forze Armate"},
			scuole: {code: "scuole", desc: "Personale Scolastico"},
			riposo: {code: "riposo", desc: "Ospiti RSA"},
			uomini: {code: "uomini", desc: "Uomini"},
			ospedali: {code: "ospedali", desc: "Operatori Sanitari Sociosanitari"},
			donne: {code: "donne", desc: "Donne"}
		};

		this.chartVaccinesDeliveredPerRegion = new CovidChart(document.getElementById(this.canvasIdGivenVaccinesPerRegion));
		this.chartVaccinesPerPerson = new CovidChart(document.getElementById(this.canvasIdVaccinesPerPerson));
		this.chartSuppliers = new DoughnutChart(document.getElementById(this.canvasIdSuppliersVaccines));
		this.chartVaccinesPerAge = new DoughnutChart(document.getElementById(this.canvasIdVaccinesPerAge));
		this.chartVaccinesDose = new DoughnutChart(document.getElementById(this.convasIdVaccninsDoses));

		this.chartVaccinesDeliveredPerRegion.setTitle("Vaccini consegnati per regione");
		this.chartVaccinesPerPerson.setTitle("Persone Vaccinate");
		this.chartSuppliers.setTitle("Dosi fornite");
		this.chartVaccinesPerAge.setTitle("Fasce di età vaccinate");
		this.chartVaccinesDose.setTitle("Dosi somministrate");

		this.addRegionsCheckboxes();
		this.addPersonsCheckboxes();
	}

	addRegionsCheckboxes() {
		var strTmpl = '<div class="col-6 col-sm-4 col-md-3 col-lg-2 col-xl-2">' +
			'<div class="custom-control custom-switch">' +
			'<input type="checkbox" class="custom-control-input" id="region-code-%code%">' +
			'<label id="label-region-code-%code%" class="custom-control-label switch-label" style="color: %color%" for="region-code-%code%">%desc%</label>' +
			'</div>' +
			'</div>';

		let jContainer = $("#" + this.regionCheckBoxesContainerId);

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

	addPersonsCheckboxes() {
		var strTmpl = '<div class="col-6 col-sm-4 col-md-3 col-lg-2 col-xl-2">' +
							'<div class="custom-control custom-switch">' +
								'<input type="checkbox" class="custom-control-input" id="person-code-%code%">' +
								'<label id="label-person-code-%code%" class="custom-control-label switch-label" style="color: %color%" for="person-code-%code%">%desc%</label>' +
							'</div>' +
					 '</div>';

		let jContainer = $("#" + this.idPersonCheckboxesWrapper);
		
		var tmpMap = this.checkBoxesDesc;
		let keysSorted = Object.keys(tmpMap).sort(function(a, b) {
			if (tmpMap[a].desc < tmpMap[b].desc) {
				return -1;
			}
			if (tmpMap[a].desc > tmpMap[b].desc) {
				return 1;
			}
			return 0;
		});

		for(let i = 0; i < keysSorted.length; i++){
			let tmp = this.checkBoxesDesc[keysSorted[i]];
			tmp.color = this.colorPalette[i]		
			jContainer.append(MarcoUtils.template(strTmpl, tmp));
		}

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
				url: __URLS.URL_VACCINES_DELIVERED_DATA
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
		this.chartVaccinesDeliveredPerRegion.clearDataSets();
		this.chartVaccinesPerPerson.clearDataSets();
		this.chartSuppliers.clearDataSets();
		this.chartVaccinesPerAge.clearDataSets();
		this.chartVaccinesDose.clearDataSets();

		this.chartVaccinesDeliveredPerRegion.setLabels(this.lastResponse.arrDates);

		/*
			Line Chart to display the number of vaccnes delivered per region
		*/
		for (let region in this.lastResponse.deliveredPerRegion) {
			if ($("#region-code-" + region).prop("checked")) {
				var arr = this.lastResponse.deliveredPerRegion[region];
				const dataset = new CovidChartDataset(__REGIONS_MAP[region].desc);
				dataset.setData(arr);
				dataset.setColor($("#label-region-code-" + region).css("color"));
				this.chartVaccinesDeliveredPerRegion.addCovidChartDataset(dataset);
			}
		}

		this.chartVaccinesDeliveredPerRegion.drawChart(this.darkModeOn);
		
		/*
			Line Chart to display the number of vaccnes giver per person
		*/
		this.chartVaccinesPerPerson.setLabels(this.lastResponse.arrDates);
		for (let person in this.lastResponse.dataVaccinatedPeople) {
			if ($("#person-code-" + person).prop("checked")) {
				var arr = this.lastResponse.dataVaccinatedPeople[person];
				const dataset = new CovidChartDataset(this.checkBoxesDesc[person].desc);
				dataset.setData(arr);
				dataset.setColor($("#label-person-code-" + person).css("color"));
				this.chartVaccinesPerPerson.addCovidChartDataset(dataset);
			}
		}
		this.chartVaccinesPerPerson.drawChart(this.darkModeOn);

		/*
			Doughnut chart to show the suppliers
		*/
		var i = 0;
		var arrLabels = [];
		for (let supplier in this.lastResponse.deliveredPerSupplier) {
			const dataset = new CovidChartDataset(supplier);
			dataset.setData(this.lastResponse.deliveredPerSupplier[supplier]);
			dataset.setColor(this.colorPalette[i++]);
			this.chartSuppliers.addCovidChartDataset(dataset);
			arrLabels.push(supplier);
		}
		this.chartSuppliers.setLabels(arrLabels);
		this.chartSuppliers.drawChart(this.darkModeOn);

		/*
			Doughnut chart to show number of doses
		*/

		i = 0;
		arrLabels = [];
		for (let shotNumber in this.lastResponse.dataShotNumber) {
			const dataset = new CovidChartDataset(shotNumber);
			dataset.setData(this.lastResponse.dataShotNumber[shotNumber]);
			dataset.setColor(this.colorPalette[i++]);
			this.chartVaccinesDose.addCovidChartDataset(dataset);
			arrLabels.push(shotNumber);
		}
		this.chartVaccinesDose.setLabels(arrLabels);
		this.chartVaccinesDose.drawChart(this.darkModeOn);

		/*
			Doughnut chart to show age range
		*/

		i = 0;
		arrLabels = [];
		for (let ageRange in this.lastResponse.dataVaccinatedPerAge) {
			const dataset = new CovidChartDataset(ageRange);
			dataset.setData(this.lastResponse.dataVaccinatedPerAge[ageRange]);
			dataset.setColor(this.colorPalette[i++]);
			this.chartVaccinesPerAge.addCovidChartDataset(dataset);
			arrLabels.push(ageRange);
		}
		this.chartVaccinesPerAge.setLabels(arrLabels);
		this.chartVaccinesPerAge.drawChart(this.darkModeOn);
	}
}