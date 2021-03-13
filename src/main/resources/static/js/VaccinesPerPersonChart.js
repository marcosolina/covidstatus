/**
	This class will fetch the number of vaccines given at the different
	type of people and draw the Chart
 */
class VaccinesPerPersonChart {
	constructor(canvasId, checkBoxesContainerId, colorPalette) {
		this.colorPalette = colorPalette;
		this.checkBoxesContainerId = checkBoxesContainerId;
		this.canvasId = canvasId;
		
		this.darkModeOn = false;
		this.lastResponse = {};
		this.checkBoxesDesc = {
			over: 			{code: "over",			desc: "Over 80"},
			nonospedali: 	{code: "nonospedali",	desc: "Personale non sanitario"},
			polizia: 		{code: "polizia",		desc: "Forze Armate"},
			scuole: 		{code: "scuole",		desc: "Personale Scolastico"},
			riposo: 		{code: "riposo",		desc: "Ospiti RSA"},
			uomini: 		{code: "uomini",		desc: "Uomini"},
			ospedali: 		{code: "ospedali",		desc: "Operatori Sanitari Sociosanitari"},
			donne: 			{code: "donne",			desc: "Donne"}
		};
		
		this.chart = new CovidChart(document.getElementById(this.canvasId));
		//this.chart.setTitle("Persone Vaccinate");
		
		this.addPersonsCheckboxes();
	}
	
	setDarkMode(darkModeOn){
		this.darkModeOn = darkModeOn;
	}
	
	/**
		It adds the checkboxes di select/de-select the person type to display the data for
	 */
	addPersonsCheckboxes() {
		var strTmpl = '<div class="col-6 col-sm-4 col-md-3 col-lg-2 col-xl-6">' +
							'<div class="custom-control custom-switch">' +
								'<input type="checkbox" class="custom-control-input" id="person-code-%code%">' +
								'<label id="label-person-code-%code%" class="custom-control-label switch-label" style="color: %color%" for="person-code-%code%">%desc%</label>' +
							'</div>' +
					 '</div>';

		let jContainer = $("#" + this.checkBoxesContainerId);
		
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
				body: {
					from: from,
					to: to
				},
				showLoading: true,
				url: __URLS.VACCINES.PEOPlE
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
		
		for (let person in this.lastResponse.dataVaccinatedPeople) {
			if ($("#person-code-" + person).prop("checked")) {
				var arr = this.lastResponse.dataVaccinatedPeople[person];
				const dataset = new CovidChartDataset(this.checkBoxesDesc[person].desc);
				dataset.setData(arr);
				dataset.setColor($("#label-person-code-" + person).css("color"));
				this.chart.addCovidChartDataset(dataset);
			}
		}
		this.chart.drawChart(this.darkModeOn);
	}
}