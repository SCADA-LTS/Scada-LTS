/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
dojo.require("dojo.widget.FloatingPane");

//
// Error handling
window.onerror = function mangoHandler(desc, page, line)  {
    BrowserDetect.init();
    if (checkCombo(BrowserDetect.browser, BrowserDetect.version, BrowserDetect.OS)) {
        MiscDwr.jsError(desc, page, line, BrowserDetect.browser, BrowserDetect.version, BrowserDetect.OS,
                window.location.href);
    }
    return false;
};

mango.header = {};
mango.header.onLoad = function() {
    if (dojo.render.html.ie)
        mango.header.evtVisualizer = new IEBlinker($("__header__alarmLevelDiv"), 500, 200);
    else
        mango.header.evtVisualizer = new ImageFader($("__header__alarmLevelDiv"), 75, .2);
    mango.longPoll.start();
};

function hMD(desc, source) {
    var c = $("headerMenuDescription");
    if (desc) {
        var bounds = getAbsoluteNodeBounds(source);
        c.innerHTML = desc;
        c.style.left = (bounds.x + 16) +"px";
        c.style.top = (bounds.y - 10) +"px";
        show(c);
    }
    else
        hide(c);
};

//
// Help
function help(documentId, source) {
    var widgetId = "documentationPane";
    var widget = dojo.widget.manager.getWidgetById(widgetId);
    if (widget)
        widget.closeWindow();

    var div = document.createElement("div");
    div.style.position = "absolute";
    div.style.width = "400px";
    div.style.height = "300px";
    if (source) {
        var bounds = getAbsoluteNodeBounds(source);
        div.style.left = (bounds.x + bounds.w) +"px";
        div.style.top = (bounds.y + bounds.h) +"px";
    }
    else {
        div.style.left = "10px";
        div.style.top = "10px";
    }
    
    document.body.appendChild(div);
    
    var params = {
            widgetId: widgetId, title: mango.i18n["js.help.loading"], constrainToContainer: false,
            displayCloseAction: true, displayMinimizeAction: false, displayMaximizeAction: false, resizable: true,
            hasShadow: true, iconSrc: "images/help_doc.png"};
    
    dojo.widget.createWidget("FloatingPane", params, div);
    helpImpl(documentId);
};

function helpImpl(documentId) {
    MiscDwr.getDocumentationItem(documentId, function(result) {
        var widgetId = "documentationPane";
        var widget = dojo.widget.manager.getWidgetById(widgetId);
        
        if (result.error) {
            widget.titleBarText.innerHTML = mango.i18n["js.help.error"];
            $("documentationPane_container").innerHTML = result.error;
        }
        else {
            widget.titleBarText.innerHTML = result.title;
            var content = result.content;
            if (result.relatedList && result.relatedList.length > 0) {
                content += "<p><b>"+ mango.i18n["js.help.related"] +"</b><br/>";
                for (var i=0; i<result.relatedList.length; i++)
                    content += "<a class='ptr' onclick='helpImpl(\""+ result.relatedList[i].id +"\");'>"+
                            result.relatedList[i].title +"</a><br/>";
                content += "</p>";
            }
            if (result.lastUpdated)
                content += "<p>"+ mango.i18n["js.help.lastUpdated"] +": "+ result.lastUpdated +"</p>";
            $("documentationPane_container").innerHTML = content;
        }
    });
};

//
// Sound related stuff
if (typeof(soundManager) != "undefined") {
    soundManager.debugMode = false;
    soundManager.onloadFinished = false;
    soundManager.onload = function() {
        soundManager.createSound({ id:'level1', url:'audio/information.mp3' });
        soundManager.createSound({ id:'level2', url:'audio/urgent.mp3' });
        soundManager.createSound({ id:'level3', url:'audio/critical.mp3' });
        soundManager.createSound({ id:'level4', url:'audio/lifesafety.mp3' });
        soundManager.onloadFinished = true;
    };
}

function SoundPlayer() {
    this.soundId;
    this.mute = false;
    this.timeoutId;
    var self = this;
    
    this.play = function(soundId) {
        this.stop();
        this.soundId = soundId;
        if (!this.mute)
        	this._repeat();
    };
    
    this.stop = function() {
        if (this.soundId) {
            var sid = this.soundId;
            this.soundId = null;
            this._stopRepeat(sid);
        }
    };
    
    this.isMute = function() {
        return this.mute;
    };
    
    this.setMute = function(muted) {
    	if (muted != this.mute) {
	        this.mute = muted;
	        if (this.soundId) {
		        if (muted)
		            this._stopRepeat(this.soundId);
		        else
		            this._repeat();
	        }
    	}
    };
    
    this._stopRepeat = function(sId) {
        soundManager.stop(sId);
        clearTimeout(this.timeoutId);
    };
    
    this._repeat = function() {
        if (soundManager.onloadFinished) {
            if (self.soundId && !self.mute) {
                var snd = soundManager.getSoundById(self.soundId);
                if (snd) {
                    if (snd.readyState == 0 || snd.readyState == 1) {
                        if (snd.readyState == 0)
                            // Load the sound
                            snd.load(snd.options);
                        // Wait for the sound to load.
                        setTimeout(self._repeat, 500);
                    }
                    else if (snd.readyState == 3)
                        // The sound exists, so play it.
                        soundManager.play(self.soundId, { onfinish: self._repeatDelay } );
                }
            }
        }
        else
            // Wait for the sound manager to load.
            setTimeout(self._repeat, 500);
    };
    
    this._repeatDelay = function() {
        if (self.soundId && !self.mute)
            self.timeoutId = setTimeout(self._repeat, 10000);
    };
};
mango.soundPlayer = new SoundPlayer();

//
// Browser detection
var BrowserDetect = {
    init: function () {
        this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
        this.version = this.searchVersion(navigator.userAgent)
            || this.searchVersion(navigator.appVersion)
            || "an unknown version";
        this.OS = this.searchString(this.dataOS) || "an unknown OS ("+ navigator.userAgent +")";
    },
    searchString: function (data) {
        for (var i=0;i<data.length;i++) {
            var dataString = data[i].string;
            var dataProp = data[i].prop;
            this.versionSearchString = data[i].versionSearch || data[i].identity;
            if (dataString) {
                if (dataString.indexOf(data[i].subString) != -1)
                    return data[i].identity;
            }
            else if (dataProp)
                return data[i].identity;
        }
    },
    searchVersion: function (dataString) {
        var index = dataString.indexOf(this.versionSearchString);
        if (index == -1) return;
        return parseFloat(dataString.substring(index+this.versionSearchString.length+1));
    },
    dataBrowser: [
        {
            string: navigator.userAgent,
            subString: "Chrome",
            identity: "Chrome"
        },
        {
            string: navigator.userAgent,
            subString: "OmniWeb",
            versionSearch: "OmniWeb/",
            identity: "OmniWeb"
        },
        {
            string: navigator.vendor,
            subString: "Apple",
            identity: "Safari",
            versionSearch: "Version"
        },
        {
            prop: window.opera,
            identity: "Opera"
        },
        {
            string: navigator.vendor,
            subString: "iCab",
            identity: "iCab"
        },
        {
            string: navigator.vendor,
            subString: "KDE",
            identity: "Konqueror"
        },
        {
            string: navigator.userAgent,
            subString: "Firefox",
            identity: "Firefox"
        },
        {
            string: navigator.vendor,
            subString: "Camino",
            identity: "Camino"
        },
        {		// for newer Netscapes (6+)
            string: navigator.userAgent,
            subString: "Netscape",
            identity: "Netscape"
        },
        {
            string: navigator.userAgent,
            subString: "MSIE",
            identity: "Explorer",
            versionSearch: "MSIE"
        },
        {
            string: navigator.userAgent,
            subString: "Gecko",
            identity: "Mozilla",
            versionSearch: "rv"
        },
        { 		// for older Netscapes (4-)
            string: navigator.userAgent,
            subString: "Mozilla",
            identity: "Netscape",
            versionSearch: "Mozilla"
        }
    ],
    dataOS : [
        {
            string: navigator.platform,
            subString: "Win",
            identity: "Windows"
        },
        {
            string: navigator.platform,
            subString: "Mac",
            identity: "Mac"
        },
        {
            string: navigator.userAgent,
            subString: "iPhone",
            identity: "iPhone/iPod"
        },
        {
            string: navigator.userAgent,
            subString: "Android",
            identity: "Android"
        },
        {
            string: navigator.platform,
            subString: "Linux",
            identity: "Linux"
        }
    ]
};

function checkCombo(browser, version, os) {
    if (browser == "Firefox" && version > 1)
        return true;
    if (browser == "Explorer" && version > 6 && os == "Windows")
        return true;
    if (browser == "Chrome")
        return true;
    if (browser == "Safari" && version > 3 && os == "Mac")
        return true;
    if (browser == "Safari" && version > 3 && os == "Android")
        return true;
    return false;
};