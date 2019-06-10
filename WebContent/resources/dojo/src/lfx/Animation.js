/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.lfx.Animation");
dojo.require("dojo.lang.func");
dojo.lfx.Line = function (start, end) {
	this.start = start;
	this.end = end;
	if (dojo.lang.isArray(start)) {
		var diff = [];
		dojo.lang.forEach(this.start, function (s, i) {
			diff[i] = this.end[i] - s;
		}, this);
		this.getValue = function (n) {
			var res = [];
			dojo.lang.forEach(this.start, function (s, i) {
				res[i] = (diff[i] * n) + s;
			}, this);
			return res;
		};
	} else {
		var diff = end - start;
		this.getValue = function (n) {
			return (diff * n) + this.start;
		};
	}
};
if ((dojo.render.html.khtml) && (!dojo.render.html.safari)) {
	dojo.lfx.easeDefault = function (n) {
		return (parseFloat("0.5") + ((Math.sin((n + parseFloat("1.5")) * Math.PI)) / 2));
	};
} else {
	dojo.lfx.easeDefault = function (n) {
		return (0.5 + ((Math.sin((n + 1.5) * Math.PI)) / 2));
	};
}
dojo.lfx.easeIn = function (n) {
	return Math.pow(n, 3);
};
dojo.lfx.easeOut = function (n) {
	return (1 - Math.pow(1 - n, 3));
};
dojo.lfx.easeInOut = function (n) {
	return ((3 * Math.pow(n, 2)) - (2 * Math.pow(n, 3)));
};
dojo.lfx.IAnimation = function () {
};
dojo.lang.extend(dojo.lfx.IAnimation, {curve:null, duration:1000, easing:null, repeatCount:0, rate:10, handler:null, beforeBegin:null, onBegin:null, onAnimate:null, onEnd:null, onPlay:null, onPause:null, onStop:null, play:null, pause:null, stop:null, connect:function (evt, scope, newFunc) {
	if (!newFunc) {
		newFunc = scope;
		scope = this;
	}
	newFunc = dojo.lang.hitch(scope, newFunc);
	var oldFunc = this[evt] || function () {
	};
	this[evt] = function () {
		var ret = oldFunc.apply(this, arguments);
		newFunc.apply(this, arguments);
		return ret;
	};
	return this;
}, fire:function (evt, args) {
	if (this[evt]) {
		this[evt].apply(this, (args || []));
	}
	return this;
}, repeat:function (count) {
	this.repeatCount = count;
	return this;
}, _active:false, _paused:false});
dojo.lfx.Animation = function (handlers, duration, curve, easing, repeatCount, rate) {
	dojo.lfx.IAnimation.call(this);
	if (dojo.lang.isNumber(handlers) || (!handlers && duration.getValue)) {
		rate = repeatCount;
		repeatCount = easing;
		easing = curve;
		curve = duration;
		duration = handlers;
		handlers = null;
	} else {
		if (handlers.getValue || dojo.lang.isArray(handlers)) {
			rate = easing;
			repeatCount = curve;
			easing = duration;
			curve = handlers;
			duration = null;
			handlers = null;
		}
	}
	if (dojo.lang.isArray(curve)) {
		this.curve = new dojo.lfx.Line(curve[0], curve[1]);
	} else {
		this.curve = curve;
	}
	if (duration != null && duration > 0) {
		this.duration = duration;
	}
	if (repeatCount) {
		this.repeatCount = repeatCount;
	}
	if (rate) {
		this.rate = rate;
	}
	if (handlers) {
		dojo.lang.forEach(["handler", "beforeBegin", "onBegin", "onEnd", "onPlay", "onStop", "onAnimate"], function (item) {
			if (handlers[item]) {
				this.connect(item, handlers[item]);
			}
		}, this);
	}
	if (easing && dojo.lang.isFunction(easing)) {
		this.easing = easing;
	}
};
dojo.inherits(dojo.lfx.Animation, dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Animation, {_startTime:null, _endTime:null, _timer:null, _percent:0, _startRepeatCount:0, play:function (delay, gotoStart) {
	if (gotoStart) {
		clearTimeout(this._timer);
		this._active = false;
		this._paused = false;
		this._percent = 0;
	} else {
		if (this._active && !this._paused) {
			return this;
		}
	}
	this.fire("handler", ["beforeBegin"]);
	this.fire("beforeBegin");
	if (delay > 0) {
		setTimeout(dojo.lang.hitch(this, function () {
			this.play(null, gotoStart);
		}), delay);
		return this;
	}
	this._startTime = new Date().valueOf();
	if (this._paused) {
		this._startTime -= (this.duration * this._percent / 100);
	}
	this._endTime = this._startTime + this.duration;
	this._active = true;
	this._paused = false;
	var step = this._percent / 100;
	var value = this.curve.getValue(step);
	if (this._percent == 0) {
		if (!this._startRepeatCount) {
			this._startRepeatCount = this.repeatCount;
		}
		this.fire("handler", ["begin", value]);
		this.fire("onBegin", [value]);
	}
	this.fire("handler", ["play", value]);
	this.fire("onPlay", [value]);
	this._cycle();
	return this;
}, pause:function () {
	clearTimeout(this._timer);
	if (!this._active) {
		return this;
	}
	this._paused = true;
	var value = this.curve.getValue(this._percent / 100);
	this.fire("handler", ["pause", value]);
	this.fire("onPause", [value]);
	return this;
}, gotoPercent:function (pct, andPlay) {
	clearTimeout(this._timer);
	this._active = true;
	this._paused = true;
	this._percent = pct;
	if (andPlay) {
		this.play();
	}
	return this;
}, stop:function (gotoEnd) {
	clearTimeout(this._timer);
	var step = this._percent / 100;
	if (gotoEnd) {
		step = 1;
	}
	var value = this.curve.getValue(step);
	this.fire("handler", ["stop", value]);
	this.fire("onStop", [value]);
	this._active = false;
	this._paused = false;
	return this;
}, status:function () {
	if (this._active) {
		return this._paused ? "paused" : "playing";
	} else {
		return "stopped";
	}
	return this;
}, _cycle:function () {
	clearTimeout(this._timer);
	if (this._active) {
		var curr = new Date().valueOf();
		var step = (curr - this._startTime) / (this._endTime - this._startTime);
		if (step >= 1) {
			step = 1;
			this._percent = 100;
		} else {
			this._percent = step * 100;
		}
		if ((this.easing) && (dojo.lang.isFunction(this.easing))) {
			step = this.easing(step);
		}
		var value = this.curve.getValue(step);
		this.fire("handler", ["animate", value]);
		this.fire("onAnimate", [value]);
		if (step < 1) {
			this._timer = setTimeout(dojo.lang.hitch(this, "_cycle"), this.rate);
		} else {
			this._active = false;
			this.fire("handler", ["end"]);
			this.fire("onEnd");
			if (this.repeatCount > 0) {
				this.repeatCount--;
				this.play(null, true);
			} else {
				if (this.repeatCount == -1) {
					this.play(null, true);
				} else {
					if (this._startRepeatCount) {
						this.repeatCount = this._startRepeatCount;
						this._startRepeatCount = 0;
					}
				}
			}
		}
	}
	return this;
}});
dojo.lfx.Combine = function (animations) {
	dojo.lfx.IAnimation.call(this);
	this._anims = [];
	this._animsEnded = 0;
	var anims = arguments;
	if (anims.length == 1 && (dojo.lang.isArray(anims[0]) || dojo.lang.isArrayLike(anims[0]))) {
		anims = anims[0];
	}
	dojo.lang.forEach(anims, function (anim) {
		this._anims.push(anim);
		anim.connect("onEnd", dojo.lang.hitch(this, "_onAnimsEnded"));
	}, this);
};
dojo.inherits(dojo.lfx.Combine, dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Combine, {_animsEnded:0, play:function (delay, gotoStart) {
	if (!this._anims.length) {
		return this;
	}
	this.fire("beforeBegin");
	if (delay > 0) {
		setTimeout(dojo.lang.hitch(this, function () {
			this.play(null, gotoStart);
		}), delay);
		return this;
	}
	if (gotoStart || this._anims[0].percent == 0) {
		this.fire("onBegin");
	}
	this.fire("onPlay");
	this._animsCall("play", null, gotoStart);
	return this;
}, pause:function () {
	this.fire("onPause");
	this._animsCall("pause");
	return this;
}, stop:function (gotoEnd) {
	this.fire("onStop");
	this._animsCall("stop", gotoEnd);
	return this;
}, _onAnimsEnded:function () {
	this._animsEnded++;
	if (this._animsEnded >= this._anims.length) {
		this.fire("onEnd");
	}
	return this;
}, _animsCall:function (funcName) {
	var args = [];
	if (arguments.length > 1) {
		for (var i = 1; i < arguments.length; i++) {
			args.push(arguments[i]);
		}
	}
	var _this = this;
	dojo.lang.forEach(this._anims, function (anim) {
		anim[funcName](args);
	}, _this);
	return this;
}});
dojo.lfx.Chain = function (animations) {
	dojo.lfx.IAnimation.call(this);
	this._anims = [];
	this._currAnim = -1;
	var anims = arguments;
	if (anims.length == 1 && (dojo.lang.isArray(anims[0]) || dojo.lang.isArrayLike(anims[0]))) {
		anims = anims[0];
	}
	var _this = this;
	dojo.lang.forEach(anims, function (anim, i, anims_arr) {
		this._anims.push(anim);
		if (i < anims_arr.length - 1) {
			anim.connect("onEnd", dojo.lang.hitch(this, "_playNext"));
		} else {
			anim.connect("onEnd", dojo.lang.hitch(this, function () {
				this.fire("onEnd");
			}));
		}
	}, this);
};
dojo.inherits(dojo.lfx.Chain, dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Chain, {_currAnim:-1, play:function (delay, gotoStart) {
	if (!this._anims.length) {
		return this;
	}
	if (gotoStart || !this._anims[this._currAnim]) {
		this._currAnim = 0;
	}
	var currentAnimation = this._anims[this._currAnim];
	this.fire("beforeBegin");
	if (delay > 0) {
		setTimeout(dojo.lang.hitch(this, function () {
			this.play(null, gotoStart);
		}), delay);
		return this;
	}
	if (currentAnimation) {
		if (this._currAnim == 0) {
			this.fire("handler", ["begin", this._currAnim]);
			this.fire("onBegin", [this._currAnim]);
		}
		this.fire("onPlay", [this._currAnim]);
		currentAnimation.play(null, gotoStart);
	}
	return this;
}, pause:function () {
	if (this._anims[this._currAnim]) {
		this._anims[this._currAnim].pause();
		this.fire("onPause", [this._currAnim]);
	}
	return this;
}, playPause:function () {
	if (this._anims.length == 0) {
		return this;
	}
	if (this._currAnim == -1) {
		this._currAnim = 0;
	}
	var currAnim = this._anims[this._currAnim];
	if (currAnim) {
		if (!currAnim._active || currAnim._paused) {
			this.play();
		} else {
			this.pause();
		}
	}
	return this;
}, stop:function () {
	var currAnim = this._anims[this._currAnim];
	if (currAnim) {
		currAnim.stop();
		this.fire("onStop", [this._currAnim]);
	}
	return currAnim;
}, _playNext:function () {
	if (this._currAnim == -1 || this._anims.length == 0) {
		return this;
	}
	this._currAnim++;
	if (this._anims[this._currAnim]) {
		this._anims[this._currAnim].play(null, true);
	}
	return this;
}});
dojo.lfx.combine = function (animations) {
	var anims = arguments;
	if (dojo.lang.isArray(arguments[0])) {
		anims = arguments[0];
	}
	if (anims.length == 1) {
		return anims[0];
	}
	return new dojo.lfx.Combine(anims);
};
dojo.lfx.chain = function (animations) {
	var anims = arguments;
	if (dojo.lang.isArray(arguments[0])) {
		anims = arguments[0];
	}
	if (anims.length == 1) {
		return anims[0];
	}
	return new dojo.lfx.Chain(anims);
};

