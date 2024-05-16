/******************************************
 * FUScaBR - "Funções úteis para o ScadaBR"
 * License: MIT
 ******************************************/
 "use strict";

fuscabr.calendar = {
    start: function() {
        if (this.shouldCreateCalendars()) {
       		if (typeof flatpickr == "undefined") {
                this.loadFlatpickr();
            } else {
                this.localizeFlatpickr();
                this.createCalendars();
            }
        }
    },

    createCalendars: function() {
        // Create date-time calendars with Flatpickr library
        flatpickr(".fuscabr-calendar", { enableTime: true, altInput: true, dateFormat: "d/m/Y H:i:S" });
    },

    // Test if create calendars is needed
    shouldCreateCalendars: function() {
        for (var i of this.conf.rules) {
            for (var j of i.pages) {
                if (window.location.pathname.includes(j)) {
                    // Identify all calendar entries by a custom HTML class
					var inputs = document.querySelectorAll(i.inputSelector);
					for (var k of inputs) {
						k.classList.add("fuscabr-calendar");
					}
					// Return true. New calendars need to be created.
					if (inputs.length)
						return true;
                }
            }
        }
        
        // Return false if module doesn't need to create
        // new calendars
        return false;
    },

    // Load Flatpickr library
    loadFlatpickr: function() {
		if (!this.loadStarted) {
			this.loadStarted = true;
			
			var fpL10n = document.createElement("script");
			fpL10n.src = this.conf.flatpickrLocaleFile;
			fpL10n.onload = function() {
				fuscabr.calendar.start();
			};
			
			var fpCss = document.createElement("link");
			fpCss.href = this.conf.flatpickrCSSFile;
			fpCss.rel = "stylesheet";

			var fp = document.createElement("script");
			fp.src = this.conf.flatpickrFile;
			fp.onload =	function() {
				document.getElementById("fuscabr-modules").appendChild(fpL10n);
			};
			
			document.getElementById("fuscabr-modules").appendChild(fp);
			document.getElementById("fuscabr-modules").appendChild(fpCss);
		}
    },

    localizeFlatpickr: function() {
        var locale = fuscabr.common.conf.language;
        // Detect browser locale
        if (this.conf.detectBrowserLocale)
            locale = (navigator.language || navigator.userLanguage).replace(/-.*/, "");
        
        // Localize flatpickr (if given locale exists)
        if (flatpickr.l10ns[locale])
			flatpickr.localize(flatpickr.l10ns[locale]);
        else
            console.warn("Flatpickr locale not found. Using default locale (English).");
    },

    // Get module settings
    getSettings: function() {
		ajaxGetJson("resources/fuscabr/conf/modules/calendar.json", function(response) {
			fuscabr.calendar.conf = response;
			fuscabr.calendar.init();
		});
	},
	
	// Init module
	init: function() {
		if (!fuscabr.calendar.conf) {
			fuscabr.calendar.getSettings();
		} else {
			//console.info("Calendar module loaded.");
            fuscabr.calendar.start();
		}
	}

 }

fuscabr.calendar.init();
