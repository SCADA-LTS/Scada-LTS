/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

/*
	This is a compiled version of Dojo, built for deployment and not for
	development. To get an editable version, please visit:

		http://dojotoolkit.org

	for documentation and information on getting the source.
*/

if(typeof dojo=="undefined"){
var dj_global=this;
var dj_currentContext=this;
function dj_undef(_1,_2){
return (typeof (_2||dj_currentContext)[_1]=="undefined");
}
if(dj_undef("djConfig",this)){
var djConfig={};
}
if(dj_undef("dojo",this)){
var dojo={};
}
dojo.global=function(){
return dj_currentContext;
};
dojo.locale=djConfig.locale;
dojo.version={major:0,minor:0,patch:0,flag:"dev",revision:Number("$Rev: 8617 $".match(/[0-9]+/)[0]),toString:function(){
with(dojo.version){
return major+"."+minor+"."+patch+flag+" ("+revision+")";
}
}};
dojo.evalProp=function(_3,_4,_5){
if((!_4)||(!_3)){
return undefined;
}
if(!dj_undef(_3,_4)){
return _4[_3];
}
return (_5?(_4[_3]={}):undefined);
};
dojo.parseObjPath=function(_6,_7,_8){
var _9=(_7||dojo.global());
var _a=_6.split(".");
var _b=_a.pop();
for(var i=0,l=_a.length;i<l&&_9;i++){
_9=dojo.evalProp(_a[i],_9,_8);
}
return {obj:_9,prop:_b};
};
dojo.evalObjPath=function(_e,_f){
if(typeof _e!="string"){
return dojo.global();
}
if(_e.indexOf(".")==-1){
return dojo.evalProp(_e,dojo.global(),_f);
}
var ref=dojo.parseObjPath(_e,dojo.global(),_f);
if(ref){
return dojo.evalProp(ref.prop,ref.obj,_f);
}
return null;
};
dojo.errorToString=function(_11){
if(!dj_undef("message",_11)){
return _11.message;
}else{
if(!dj_undef("description",_11)){
return _11.description;
}else{
return _11;
}
}
};
dojo.raise=function(_12,_13){
if(_13){
_12=_12+": "+dojo.errorToString(_13);
}else{
_12=dojo.errorToString(_12);
}
try{
if(djConfig.isDebug){
dojo.hostenv.println("FATAL exception raised: "+_12);
}
}
catch(e){
}
throw _13||Error(_12);
};
dojo.debug=function(){
};
dojo.debugShallow=function(obj){
};
dojo.profile={start:function(){
},end:function(){
},stop:function(){
},dump:function(){
}};
function dj_eval(_15){
return dj_global.eval?dj_global.eval(_15):eval(_15);
}
dojo.unimplemented=function(_16,_17){
var _18="'"+_16+"' not implemented";
if(_17!=null){
_18+=" "+_17;
}
dojo.raise(_18);
};
dojo.deprecated=function(_19,_1a,_1b){
var _1c="DEPRECATED: "+_19;
if(_1a){
_1c+=" "+_1a;
}
if(_1b){
_1c+=" -- will be removed in version: "+_1b;
}
dojo.debug(_1c);
};
dojo.render=(function(){
function vscaffold(_1d,_1e){
var tmp={capable:false,support:{builtin:false,plugin:false},prefixes:_1d};
for(var i=0;i<_1e.length;i++){
tmp[_1e[i]]=false;
}
return tmp;
}
return {name:"",ver:dojo.version,os:{win:false,linux:false,osx:false},html:vscaffold(["html"],["ie","opera","khtml","safari","moz"]),svg:vscaffold(["svg"],["corel","adobe","batik"]),vml:vscaffold(["vml"],["ie"]),swf:vscaffold(["Swf","Flash","Mm"],["mm"]),swt:vscaffold(["Swt"],["ibm"])};
})();
dojo.hostenv=(function(){
var _21={isDebug:false,allowQueryConfig:false,baseScriptUri:"",baseRelativePath:"",libraryScriptUri:"",iePreventClobber:false,ieClobberMinimal:true,preventBackButtonFix:true,delayMozLoadingFix:false,searchIds:[],parseWidgets:true};
if(typeof djConfig=="undefined"){
djConfig=_21;
}else{
for(var _22 in _21){
if(typeof djConfig[_22]=="undefined"){
djConfig[_22]=_21[_22];
}
}
}
return {name_:"(unset)",version_:"(unset)",getName:function(){
return this.name_;
},getVersion:function(){
return this.version_;
},getText:function(uri){
dojo.unimplemented("getText","uri="+uri);
}};
})();
dojo.hostenv.getBaseScriptUri=function(){
if(djConfig.baseScriptUri.length){
return djConfig.baseScriptUri;
}
var uri=new String(djConfig.libraryScriptUri||djConfig.baseRelativePath);
if(!uri){
dojo.raise("Nothing returned by getLibraryScriptUri(): "+uri);
}
var _25=uri.lastIndexOf("/");
djConfig.baseScriptUri=djConfig.baseRelativePath;
return djConfig.baseScriptUri;
};
(function(){
var _26={pkgFileName:"__package__",loading_modules_:{},loaded_modules_:{},addedToLoadingCount:[],removedFromLoadingCount:[],inFlightCount:0,modulePrefixes_:{dojo:{name:"dojo",value:"src"}},setModulePrefix:function(_27,_28){
this.modulePrefixes_[_27]={name:_27,value:_28};
},moduleHasPrefix:function(_29){
var mp=this.modulePrefixes_;
return Boolean(mp[_29]&&mp[_29].value);
},getModulePrefix:function(_2b){
if(this.moduleHasPrefix(_2b)){
return this.modulePrefixes_[_2b].value;
}
return _2b;
},getTextStack:[],loadUriStack:[],loadedUris:[],post_load_:false,modulesLoadedListeners:[],unloadListeners:[],loadNotifying:false};
for(var _2c in _26){
dojo.hostenv[_2c]=_26[_2c];
}
})();
dojo.hostenv.loadPath=function(_2d,_2e,cb){
var uri;
if(_2d.charAt(0)=="/"||_2d.match(/^\w+:/)){
uri=_2d;
}else{
uri=this.getBaseScriptUri()+_2d;
}
if(djConfig.cacheBust&&dojo.render.html.capable){
uri+="?"+String(djConfig.cacheBust).replace(/\W+/g,"");
}
try{
return !_2e?this.loadUri(uri,cb):this.loadUriAndCheck(uri,_2e,cb);
}
catch(e){
dojo.debug(e);
return false;
}
};
dojo.hostenv.loadUri=function(uri,cb){
if(this.loadedUris[uri]){
return true;
}
var _33=this.getText(uri,null,true);
if(!_33){
return false;
}
this.loadedUris[uri]=true;
if(cb){
_33="("+_33+")";
}
var _34=dj_eval(_33);
if(cb){
cb(_34);
}
return true;
};
dojo.hostenv.loadUriAndCheck=function(uri,_36,cb){
var ok=true;
try{
ok=this.loadUri(uri,cb);
}
catch(e){
dojo.debug("failed loading ",uri," with error: ",e);
}
return Boolean(ok&&this.findModule(_36,false));
};
dojo.loaded=function(){
};
dojo.unloaded=function(){
};
dojo.hostenv.loaded=function(){
this.loadNotifying=true;
this.post_load_=true;
var mll=this.modulesLoadedListeners;
for(var x=0;x<mll.length;x++){
mll[x]();
}
this.modulesLoadedListeners=[];
this.loadNotifying=false;
dojo.loaded();
};
dojo.hostenv.unloaded=function(){
var mll=this.unloadListeners;
while(mll.length){
(mll.pop())();
}
dojo.unloaded();
};
dojo.addOnLoad=function(obj,_3d){
var dh=dojo.hostenv;
if(arguments.length==1){
dh.modulesLoadedListeners.push(obj);
}else{
if(arguments.length>1){
dh.modulesLoadedListeners.push(function(){
obj[_3d]();
});
}
}
if(dh.post_load_&&dh.inFlightCount==0&&!dh.loadNotifying){
dh.callLoaded();
}
};
dojo.addOnUnload=function(obj,_40){
var dh=dojo.hostenv;
if(arguments.length==1){
dh.unloadListeners.push(obj);
}else{
if(arguments.length>1){
dh.unloadListeners.push(function(){
obj[_40]();
});
}
}
};
dojo.hostenv.modulesLoaded=function(){
if(this.post_load_){
return;
}
if(this.loadUriStack.length==0&&this.getTextStack.length==0){
if(this.inFlightCount>0){
dojo.debug("files still in flight!");
return;
}
dojo.hostenv.callLoaded();
}
};
dojo.hostenv.callLoaded=function(){
if(typeof setTimeout=="object"||(djConfig["useXDomain"]&&dojo.render.html.opera)){
setTimeout("dojo.hostenv.loaded();",0);
}else{
dojo.hostenv.loaded();
}
};
dojo.hostenv.getModuleSymbols=function(_42){
var _43=_42.split(".");
for(var i=_43.length;i>0;i--){
var _45=_43.slice(0,i).join(".");
if((i==1)&&!this.moduleHasPrefix(_45)){
_43[0]="../"+_43[0];
}else{
var _46=this.getModulePrefix(_45);
if(_46!=_45){
_43.splice(0,i,_46);
break;
}
}
}
return _43;
};
dojo.hostenv._global_omit_module_check=false;
dojo.hostenv.loadModule=function(_47,_48,_49){
if(!_47){
return;
}
_49=this._global_omit_module_check||_49;
var _4a=this.findModule(_47,false);
if(_4a){
return _4a;
}
if(dj_undef(_47,this.loading_modules_)){
this.addedToLoadingCount.push(_47);
}
this.loading_modules_[_47]=1;
var _4b=_47.replace(/\./g,"/")+".js";
var _4c=_47.split(".");
var _4d=this.getModuleSymbols(_47);
var _4e=((_4d[0].charAt(0)!="/")&&!_4d[0].match(/^\w+:/));
var _4f=_4d[_4d.length-1];
var ok;
if(_4f=="*"){
_47=_4c.slice(0,-1).join(".");
while(_4d.length){
_4d.pop();
_4d.push(this.pkgFileName);
_4b=_4d.join("/")+".js";
if(_4e&&_4b.charAt(0)=="/"){
_4b=_4b.slice(1);
}
ok=this.loadPath(_4b,!_49?_47:null);
if(ok){
break;
}
_4d.pop();
}
}else{
_4b=_4d.join("/")+".js";
_47=_4c.join(".");
var _51=!_49?_47:null;
ok=this.loadPath(_4b,_51);
if(!ok&&!_48){
_4d.pop();
while(_4d.length){
_4b=_4d.join("/")+".js";
ok=this.loadPath(_4b,_51);
if(ok){
break;
}
_4d.pop();
_4b=_4d.join("/")+"/"+this.pkgFileName+".js";
if(_4e&&_4b.charAt(0)=="/"){
_4b=_4b.slice(1);
}
ok=this.loadPath(_4b,_51);
if(ok){
break;
}
}
}
if(!ok&&!_49){
dojo.raise("Could not load '"+_47+"'; last tried '"+_4b+"'");
}
}
if(!_49&&!this["isXDomain"]){
_4a=this.findModule(_47,false);
if(!_4a){
dojo.raise("symbol '"+_47+"' is not defined after loading '"+_4b+"'");
}
}
return _4a;
};
dojo.hostenv.startPackage=function(_52){
var _53=String(_52);
var _54=_53;
var _55=_52.split(/\./);
if(_55[_55.length-1]=="*"){
_55.pop();
_54=_55.join(".");
}
var _56=dojo.evalObjPath(_54,true);
this.loaded_modules_[_53]=_56;
this.loaded_modules_[_54]=_56;
return _56;
};
dojo.hostenv.findModule=function(_57,_58){
var lmn=String(_57);
if(this.loaded_modules_[lmn]){
return this.loaded_modules_[lmn];
}
if(_58){
dojo.raise("no loaded module named '"+_57+"'");
}
return null;
};
dojo.kwCompoundRequire=function(_5a){
var _5b=_5a["common"]||[];
var _5c=_5a[dojo.hostenv.name_]?_5b.concat(_5a[dojo.hostenv.name_]||[]):_5b.concat(_5a["default"]||[]);
for(var x=0;x<_5c.length;x++){
var _5e=_5c[x];
if(_5e.constructor==Array){
dojo.hostenv.loadModule.apply(dojo.hostenv,_5e);
}else{
dojo.hostenv.loadModule(_5e);
}
}
};
dojo.require=function(_5f){
dojo.hostenv.loadModule.apply(dojo.hostenv,arguments);
};
dojo.requireIf=function(_60,_61){
var _62=arguments[0];
if((_62===true)||(_62=="common")||(_62&&dojo.render[_62].capable)){
var _63=[];
for(var i=1;i<arguments.length;i++){
_63.push(arguments[i]);
}
dojo.require.apply(dojo,_63);
}
};
dojo.requireAfterIf=dojo.requireIf;
dojo.provide=function(_65){
return dojo.hostenv.startPackage.apply(dojo.hostenv,arguments);
};
dojo.registerModulePath=function(_66,_67){
return dojo.hostenv.setModulePrefix(_66,_67);
};
if(djConfig["modulePaths"]){
for(var param in djConfig["modulePaths"]){
dojo.registerModulePath(param,djConfig["modulePaths"][param]);
}
}
dojo.setModulePrefix=function(_68,_69){
dojo.deprecated("dojo.setModulePrefix(\""+_68+"\", \""+_69+"\")","replaced by dojo.registerModulePath","0.5");
return dojo.registerModulePath(_68,_69);
};
dojo.exists=function(obj,_6b){
var p=_6b.split(".");
for(var i=0;i<p.length;i++){
if(!obj[p[i]]){
return false;
}
obj=obj[p[i]];
}
return true;
};
dojo.hostenv.normalizeLocale=function(_6e){
var _6f=_6e?_6e.toLowerCase():dojo.locale;
if(_6f=="root"){
_6f="ROOT";
}
return _6f;
};
dojo.hostenv.searchLocalePath=function(_70,_71,_72){
_70=dojo.hostenv.normalizeLocale(_70);
var _73=_70.split("-");
var _74=[];
for(var i=_73.length;i>0;i--){
_74.push(_73.slice(0,i).join("-"));
}
_74.push(false);
if(_71){
_74.reverse();
}
for(var j=_74.length-1;j>=0;j--){
var loc=_74[j]||"ROOT";
var _78=_72(loc);
if(_78){
break;
}
}
};
dojo.hostenv.localesGenerated=["ROOT","es-es","es","it-it","pt-br","de","fr-fr","zh-cn","pt","en-us","zh","fr","zh-tw","it","en-gb","xx","de-de","ko-kr","ja-jp","ko","en","ja"];
dojo.hostenv.registerNlsPrefix=function(){
dojo.registerModulePath("nls","nls");
};
dojo.hostenv.preloadLocalizations=function(){
if(dojo.hostenv.localesGenerated){
dojo.hostenv.registerNlsPrefix();
function preload(_79){
_79=dojo.hostenv.normalizeLocale(_79);
dojo.hostenv.searchLocalePath(_79,true,function(loc){
for(var i=0;i<dojo.hostenv.localesGenerated.length;i++){
if(dojo.hostenv.localesGenerated[i]==loc){
dojo["require"]("nls.dojo_"+loc);
return true;
}
}
return false;
});
}
preload();
var _7c=djConfig.extraLocale||[];
for(var i=0;i<_7c.length;i++){
preload(_7c[i]);
}
}
dojo.hostenv.preloadLocalizations=function(){
};
};
dojo.requireLocalization=function(_7e,_7f,_80,_81){
dojo.hostenv.preloadLocalizations();
var _82=dojo.hostenv.normalizeLocale(_80);
var _83=[_7e,"nls",_7f].join(".");
var _84="";
if(_81){
var _85=_81.split(",");
for(var i=0;i<_85.length;i++){
if(_82.indexOf(_85[i])==0){
if(_85[i].length>_84.length){
_84=_85[i];
}
}
}
if(!_84){
_84="ROOT";
}
}
var _87=_81?_84:_82;
var _88=dojo.hostenv.findModule(_83);
var _89=null;
if(_88){
if(djConfig.localizationComplete&&_88._built){
return;
}
var _8a=_87.replace("-","_");
var _8b=_83+"."+_8a;
_89=dojo.hostenv.findModule(_8b);
}
if(!_89){
_88=dojo.hostenv.startPackage(_83);
var _8c=dojo.hostenv.getModuleSymbols(_7e);
var _8d=_8c.concat("nls").join("/");
var _8e;
dojo.hostenv.searchLocalePath(_87,_81,function(loc){
var _90=loc.replace("-","_");
var _91=_83+"."+_90;
var _92=false;
if(!dojo.hostenv.findModule(_91)){
dojo.hostenv.startPackage(_91);
var _93=[_8d];
if(loc!="ROOT"){
_93.push(loc);
}
_93.push(_7f);
var _94=_93.join("/")+".js";
_92=dojo.hostenv.loadPath(_94,null,function(_95){
var _96=function(){
};
_96.prototype=_8e;
_88[_90]=new _96();
for(var j in _95){
_88[_90][j]=_95[j];
}
});
}else{
_92=true;
}
if(_92&&_88[_90]){
_8e=_88[_90];
}else{
_88[_90]=_8e;
}
if(_81){
return true;
}
});
}
if(_81&&_82!=_84){
_88[_82.replace("-","_")]=_88[_84.replace("-","_")];
}
};
(function(){
var _98=djConfig.extraLocale;
if(_98){
if(!_98 instanceof Array){
_98=[_98];
}
var req=dojo.requireLocalization;
dojo.requireLocalization=function(m,b,_9c,_9d){
req(m,b,_9c,_9d);
if(_9c){
return;
}
for(var i=0;i<_98.length;i++){
req(m,b,_98[i],_9d);
}
};
}
})();
}
if(typeof window!="undefined"){
(function(){
if(djConfig.allowQueryConfig){
var _9f=document.location.toString();
var _a0=_9f.split("?",2);
if(_a0.length>1){
var _a1=_a0[1];
var _a2=_a1.split("&");
for(var x in _a2){
var sp=_a2[x].split("=");
if((sp[0].length>9)&&(sp[0].substr(0,9)=="djConfig.")){
var opt=sp[0].substr(9);
try{
djConfig[opt]=eval(sp[1]);
}
catch(e){
djConfig[opt]=sp[1];
}
}
}
}
}
if(((djConfig["baseScriptUri"]=="")||(djConfig["baseRelativePath"]==""))&&(document&&document.getElementsByTagName)){
var _a6=document.getElementsByTagName("script");
var _a7=/(__package__|dojo|bootstrap1)\.js([\?\.]|$)/i;
for(var i=0;i<_a6.length;i++){
var src=_a6[i].getAttribute("src");
if(!src){
continue;
}
var m=src.match(_a7);
if(m){
var _ab=src.substring(0,m.index);
if(src.indexOf("bootstrap1")>-1){
_ab+="../";
}
if(!this["djConfig"]){
djConfig={};
}
if(djConfig["baseScriptUri"]==""){
djConfig["baseScriptUri"]=_ab;
}
if(djConfig["baseRelativePath"]==""){
djConfig["baseRelativePath"]=_ab;
}
break;
}
}
}
var dr=dojo.render;
var drh=dojo.render.html;
var drs=dojo.render.svg;
var dua=(drh.UA=navigator.userAgent);
var dav=(drh.AV=navigator.appVersion);
var t=true;
var f=false;
drh.capable=t;
drh.support.builtin=t;
dr.ver=parseFloat(drh.AV);
dr.os.mac=dav.indexOf("Macintosh")>=0;
dr.os.win=dav.indexOf("Windows")>=0;
dr.os.linux=dav.indexOf("X11")>=0;
drh.opera=dua.indexOf("Opera")>=0;
drh.khtml=(dav.indexOf("Konqueror")>=0)||(dav.indexOf("Safari")>=0);
drh.safari=dav.indexOf("Safari")>=0;
var _b3=dua.indexOf("Gecko");
drh.mozilla=drh.moz=(_b3>=0)&&(!drh.khtml);
if(drh.mozilla){
drh.geckoVersion=dua.substring(_b3+6,_b3+14);
}
drh.ie=(document.all)&&(!drh.opera);
drh.ie50=drh.ie&&dav.indexOf("MSIE 5.0")>=0;
drh.ie55=drh.ie&&dav.indexOf("MSIE 5.5")>=0;
drh.ie60=drh.ie&&dav.indexOf("MSIE 6.0")>=0;
drh.ie70=drh.ie&&dav.indexOf("MSIE 7.0")>=0;
var cm=document["compatMode"];
drh.quirks=(cm=="BackCompat")||(cm=="QuirksMode")||drh.ie55||drh.ie50;
dojo.locale=dojo.locale||(drh.ie?navigator.userLanguage:navigator.language).toLowerCase();
dr.vml.capable=drh.ie;
drs.capable=f;
drs.support.plugin=f;
drs.support.builtin=f;
var _b5=window["document"];
var tdi=_b5["implementation"];
if((tdi)&&(tdi["hasFeature"])&&(tdi.hasFeature("org.w3c.dom.svg","1.0"))){
drs.capable=t;
drs.support.builtin=t;
drs.support.plugin=f;
}
if(drh.safari){
var tmp=dua.split("AppleWebKit/")[1];
var ver=parseFloat(tmp.split(" ")[0]);
if(ver>=420){
drs.capable=t;
drs.support.builtin=t;
drs.support.plugin=f;
}
}else{
}
})();
dojo.hostenv.startPackage("dojo.hostenv");
dojo.render.name=dojo.hostenv.name_="browser";
dojo.hostenv.searchIds=[];
dojo.hostenv._XMLHTTP_PROGIDS=["Msxml2.XMLHTTP","Microsoft.XMLHTTP","Msxml2.XMLHTTP.4.0"];
dojo.hostenv.getXmlhttpObject=function(){
var _b9=null;
var _ba=null;
try{
_b9=new XMLHttpRequest();
}
catch(e){
}
if(!_b9){
for(var i=0;i<3;++i){
var _bc=dojo.hostenv._XMLHTTP_PROGIDS[i];
try{
_b9=new ActiveXObject(_bc);
}
catch(e){
_ba=e;
}
if(_b9){
dojo.hostenv._XMLHTTP_PROGIDS=[_bc];
break;
}
}
}
if(!_b9){
return dojo.raise("XMLHTTP not available",_ba);
}
return _b9;
};
dojo.hostenv._blockAsync=false;
dojo.hostenv.getText=function(uri,_be,_bf){
if(!_be){
this._blockAsync=true;
}
var _c0=this.getXmlhttpObject();
function isDocumentOk(_c1){
var _c2=_c1["status"];
return Boolean((!_c2)||((200<=_c2)&&(300>_c2))||(_c2==304));
}
if(_be){
var _c3=this,_c4=null,gbl=dojo.global();
var xhr=dojo.evalObjPath("dojo.io.XMLHTTPTransport");
_c0.onreadystatechange=function(){
if(_c4){
gbl.clearTimeout(_c4);
_c4=null;
}
if(_c3._blockAsync||(xhr&&xhr._blockAsync)){
_c4=gbl.setTimeout(function(){
_c0.onreadystatechange.apply(this);
},10);
}else{
if(4==_c0.readyState){
if(isDocumentOk(_c0)){
_be(_c0.responseText);
}
}
}
};
}
_c0.open("GET",uri,_be?true:false);
try{
_c0.send(null);
if(_be){
return null;
}
if(!isDocumentOk(_c0)){
var err=Error("Unable to load "+uri+" status:"+_c0.status);
err.status=_c0.status;
err.responseText=_c0.responseText;
throw err;
}
}
catch(e){
this._blockAsync=false;
if((_bf)&&(!_be)){
return null;
}else{
throw e;
}
}
this._blockAsync=false;
return _c0.responseText;
};
dojo.hostenv.defaultDebugContainerId="dojoDebug";
dojo.hostenv._println_buffer=[];
dojo.hostenv._println_safe=false;
dojo.hostenv.println=function(_c8){
if(!dojo.hostenv._println_safe){
dojo.hostenv._println_buffer.push(_c8);
}else{
try{
var _c9=document.getElementById(djConfig.debugContainerId?djConfig.debugContainerId:dojo.hostenv.defaultDebugContainerId);
if(!_c9){
_c9=dojo.body();
}
var div=document.createElement("div");
div.appendChild(document.createTextNode(_c8));
_c9.appendChild(div);
}
catch(e){
try{
document.write("<div>"+_c8+"</div>");
}
catch(e2){
window.status=_c8;
}
}
}
};
dojo.addOnLoad(function(){
dojo.hostenv._println_safe=true;
while(dojo.hostenv._println_buffer.length>0){
dojo.hostenv.println(dojo.hostenv._println_buffer.shift());
}
});
function dj_addNodeEvtHdlr(_cb,_cc,fp){
var _ce=_cb["on"+_cc]||function(){
};
_cb["on"+_cc]=function(){
fp.apply(_cb,arguments);
_ce.apply(_cb,arguments);
};
return true;
}
dojo.hostenv._djInitFired=false;
function dj_load_init(e){
dojo.hostenv._djInitFired=true;
var _d0=(e&&e.type)?e.type.toLowerCase():"load";
if(arguments.callee.initialized||(_d0!="domcontentloaded"&&_d0!="load")){
return;
}
arguments.callee.initialized=true;
if(typeof (_timer)!="undefined"){
clearInterval(_timer);
delete _timer;
}
var _d1=function(){
if(dojo.render.html.ie){
dojo.hostenv.makeWidgets();
}
};
if(dojo.hostenv.inFlightCount==0){
_d1();
dojo.hostenv.modulesLoaded();
}else{
dojo.hostenv.modulesLoadedListeners.unshift(_d1);
}
}
if(document.addEventListener){
if(dojo.render.html.opera||(dojo.render.html.moz&&(djConfig["enableMozDomContentLoaded"]===true))){
document.addEventListener("DOMContentLoaded",dj_load_init,null);
}
window.addEventListener("load",dj_load_init,null);
}
if(dojo.render.html.ie&&dojo.render.os.win){
document.attachEvent("onreadystatechange",function(e){
if(document.readyState=="complete"){
dj_load_init();
}
});
}
if(/(WebKit|khtml)/i.test(navigator.userAgent)){
var _timer=setInterval(function(){
if(/loaded|complete/.test(document.readyState)){
dj_load_init();
}
},10);
}
if(dojo.render.html.ie){
dj_addNodeEvtHdlr(window,"beforeunload",function(){
dojo.hostenv._unloading=true;
window.setTimeout(function(){
dojo.hostenv._unloading=false;
},0);
});
}
dj_addNodeEvtHdlr(window,"unload",function(){
dojo.hostenv.unloaded();
if((!dojo.render.html.ie)||(dojo.render.html.ie&&dojo.hostenv._unloading)){
dojo.hostenv.unloaded();
}
});
dojo.hostenv.makeWidgets=function(){
var _d3=[];
if(djConfig.searchIds&&djConfig.searchIds.length>0){
_d3=_d3.concat(djConfig.searchIds);
}
if(dojo.hostenv.searchIds&&dojo.hostenv.searchIds.length>0){
_d3=_d3.concat(dojo.hostenv.searchIds);
}
if((djConfig.parseWidgets)||(_d3.length>0)){
if(dojo.evalObjPath("dojo.widget.Parse")){
var _d4=new dojo.xml.Parse();
if(_d3.length>0){
for(var x=0;x<_d3.length;x++){
var _d6=document.getElementById(_d3[x]);
if(!_d6){
continue;
}
var _d7=_d4.parseElement(_d6,null,true);
dojo.widget.getParser().createComponents(_d7);
}
}else{
if(djConfig.parseWidgets){
var _d7=_d4.parseElement(dojo.body(),null,true);
dojo.widget.getParser().createComponents(_d7);
}
}
}
}
};
dojo.addOnLoad(function(){
if(!dojo.render.html.ie){
dojo.hostenv.makeWidgets();
}
});
try{
if(dojo.render.html.ie){
document.namespaces.add("v","urn:schemas-microsoft-com:vml");
document.createStyleSheet().addRule("v\\:*","behavior:url(#default#VML)");
}
}
catch(e){
}
dojo.hostenv.writeIncludes=function(){
};
if(!dj_undef("document",this)){
dj_currentDocument=this.document;
}
dojo.doc=function(){
return dj_currentDocument;
};
dojo.body=function(){
return dojo.doc().body||dojo.doc().getElementsByTagName("body")[0];
};
dojo.byId=function(id,doc){
if((id)&&((typeof id=="string")||(id instanceof String))){
if(!doc){
doc=dj_currentDocument;
}
var ele=doc.getElementById(id);
if(ele&&(ele.id!=id)&&doc.all){
ele=null;
eles=doc.all[id];
if(eles){
if(eles.length){
for(var i=0;i<eles.length;i++){
if(eles[i].id==id){
ele=eles[i];
break;
}
}
}else{
ele=eles;
}
}
}
return ele;
}
return id;
};
dojo.setContext=function(_dc,_dd){
dj_currentContext=_dc;
dj_currentDocument=_dd;
};
dojo._fireCallback=function(_de,_df,_e0){
if((_df)&&((typeof _de=="string")||(_de instanceof String))){
_de=_df[_de];
}
return (_df?_de.apply(_df,_e0||[]):_de());
};
dojo.withGlobal=function(_e1,_e2,_e3,_e4){
var _e5;
var _e6=dj_currentContext;
var _e7=dj_currentDocument;
try{
dojo.setContext(_e1,_e1.document);
_e5=dojo._fireCallback(_e2,_e3,_e4);
}
finally{
dojo.setContext(_e6,_e7);
}
return _e5;
};
dojo.withDoc=function(_e8,_e9,_ea,_eb){
var _ec;
var _ed=dj_currentDocument;
try{
dj_currentDocument=_e8;
_ec=dojo._fireCallback(_e9,_ea,_eb);
}
finally{
dj_currentDocument=_ed;
}
return _ec;
};
}
dojo.requireIf((djConfig["isDebug"]||djConfig["debugAtAllCosts"]),"dojo.debug");
dojo.requireIf(djConfig["debugAtAllCosts"]&&!window.widget&&!djConfig["useXDomain"],"dojo.browser_debug");
dojo.requireIf(djConfig["debugAtAllCosts"]&&!window.widget&&djConfig["useXDomain"],"dojo.browser_debug_xd");
dojo.provide("dojo.lang.common");
dojo.lang.inherits=function(_ee,_ef){
if(!dojo.lang.isFunction(_ef)){
dojo.raise("dojo.inherits: superclass argument ["+_ef+"] must be a function (subclass: ["+_ee+"']");
}
_ee.prototype=new _ef();
_ee.prototype.constructor=_ee;
_ee.superclass=_ef.prototype;
_ee["super"]=_ef.prototype;
};
dojo.lang._mixin=function(obj,_f1){
var _f2={};
for(var x in _f1){
if((typeof _f2[x]=="undefined")||(_f2[x]!=_f1[x])){
obj[x]=_f1[x];
}
}
if(dojo.render.html.ie&&(typeof (_f1["toString"])=="function")&&(_f1["toString"]!=obj["toString"])&&(_f1["toString"]!=_f2["toString"])){
obj.toString=_f1.toString;
}
return obj;
};
dojo.lang.mixin=function(obj,_f5){
for(var i=1,l=arguments.length;i<l;i++){
dojo.lang._mixin(obj,arguments[i]);
}
return obj;
};
dojo.lang.extend=function(_f8,_f9){
for(var i=1,l=arguments.length;i<l;i++){
dojo.lang._mixin(_f8.prototype,arguments[i]);
}
return _f8;
};
dojo.inherits=dojo.lang.inherits;
dojo.mixin=dojo.lang.mixin;
dojo.extend=dojo.lang.extend;
dojo.lang.find=function(_fc,_fd,_fe,_ff){
if(!dojo.lang.isArrayLike(_fc)&&dojo.lang.isArrayLike(_fd)){
dojo.deprecated("dojo.lang.find(value, array)","use dojo.lang.find(array, value) instead","0.5");
var temp=_fc;
_fc=_fd;
_fd=temp;
}
var _101=dojo.lang.isString(_fc);
if(_101){
_fc=_fc.split("");
}
if(_ff){
var step=-1;
var i=_fc.length-1;
var end=-1;
}else{
var step=1;
var i=0;
var end=_fc.length;
}
if(_fe){
while(i!=end){
if(_fc[i]===_fd){
return i;
}
i+=step;
}
}else{
while(i!=end){
if(_fc[i]==_fd){
return i;
}
i+=step;
}
}
return -1;
};
dojo.lang.indexOf=dojo.lang.find;
dojo.lang.findLast=function(_105,_106,_107){
return dojo.lang.find(_105,_106,_107,true);
};
dojo.lang.lastIndexOf=dojo.lang.findLast;
dojo.lang.inArray=function(_108,_109){
return dojo.lang.find(_108,_109)>-1;
};
dojo.lang.isObject=function(it){
if(typeof it=="undefined"){
return false;
}
return (typeof it=="object"||it===null||dojo.lang.isArray(it)||dojo.lang.isFunction(it));
};
dojo.lang.isArray=function(it){
return (it&&it instanceof Array||typeof it=="array");
};
dojo.lang.isArrayLike=function(it){
if((!it)||(dojo.lang.isUndefined(it))){
return false;
}
if(dojo.lang.isString(it)){
return false;
}
if(dojo.lang.isFunction(it)){
return false;
}
if(dojo.lang.isArray(it)){
return true;
}
if((it.tagName)&&(it.tagName.toLowerCase()=="form")){
return false;
}
if(dojo.lang.isNumber(it.length)&&isFinite(it.length)){
return true;
}
return false;
};
dojo.lang.isFunction=function(it){
return (it instanceof Function||typeof it=="function");
};
(function(){
if((dojo.render.html.capable)&&(dojo.render.html["safari"])){
dojo.lang.isFunction=function(it){
if((typeof (it)=="function")&&(it=="[object NodeList]")){
return false;
}
return (it instanceof Function||typeof it=="function");
};
}
})();
dojo.lang.isString=function(it){
return (typeof it=="string"||it instanceof String);
};
dojo.lang.isAlien=function(it){
if(!it){
return false;
}
return !dojo.lang.isFunction(it)&&/\{\s*\[native code\]\s*\}/.test(String(it));
};
dojo.lang.isBoolean=function(it){
return (it instanceof Boolean||typeof it=="boolean");
};
dojo.lang.isNumber=function(it){
return (it instanceof Number||typeof it=="number");
};
dojo.lang.isUndefined=function(it){
return ((typeof (it)=="undefined")&&(it==undefined));
};
dojo.provide("dojo.lang.array");
dojo.lang.mixin(dojo.lang,{has:function(obj,name){
try{
return typeof obj[name]!="undefined";
}
catch(e){
return false;
}
},isEmpty:function(obj){
if(dojo.lang.isObject(obj)){
var tmp={};
var _118=0;
for(var x in obj){
if(obj[x]&&(!tmp[x])){
_118++;
break;
}
}
return _118==0;
}else{
if(dojo.lang.isArrayLike(obj)||dojo.lang.isString(obj)){
return obj.length==0;
}
}
},map:function(arr,obj,_11c){
var _11d=dojo.lang.isString(arr);
if(_11d){
arr=arr.split("");
}
if(dojo.lang.isFunction(obj)&&(!_11c)){
_11c=obj;
obj=dj_global;
}else{
if(dojo.lang.isFunction(obj)&&_11c){
var _11e=obj;
obj=_11c;
_11c=_11e;
}
}
if(Array.map){
var _11f=Array.map(arr,_11c,obj);
}else{
var _11f=[];
for(var i=0;i<arr.length;++i){
_11f.push(_11c.call(obj,arr[i]));
}
}
if(_11d){
return _11f.join("");
}else{
return _11f;
}
},reduce:function(arr,_122,obj,_124){
var _125=_122;
if(arguments.length==2){
_124=_122;
_125=arr[0];
arr=arr.slice(1);
}else{
if(arguments.length==3){
if(dojo.lang.isFunction(obj)){
_124=obj;
obj=null;
}
}else{
if(dojo.lang.isFunction(obj)){
var tmp=_124;
_124=obj;
obj=tmp;
}
}
}
var ob=obj||dj_global;
dojo.lang.map(arr,function(val){
_125=_124.call(ob,_125,val);
});
return _125;
},forEach:function(_129,_12a,_12b){
if(dojo.lang.isString(_129)){
_129=_129.split("");
}
if(Array.forEach){
Array.forEach(_129,_12a,_12b);
}else{
if(!_12b){
_12b=dj_global;
}
for(var i=0,l=_129.length;i<l;i++){
_12a.call(_12b,_129[i],i,_129);
}
}
},_everyOrSome:function(_12e,arr,_130,_131){
if(dojo.lang.isString(arr)){
arr=arr.split("");
}
if(Array.every){
return Array[_12e?"every":"some"](arr,_130,_131);
}else{
if(!_131){
_131=dj_global;
}
for(var i=0,l=arr.length;i<l;i++){
var _134=_130.call(_131,arr[i],i,arr);
if(_12e&&!_134){
return false;
}else{
if((!_12e)&&(_134)){
return true;
}
}
}
return Boolean(_12e);
}
},every:function(arr,_136,_137){
return this._everyOrSome(true,arr,_136,_137);
},some:function(arr,_139,_13a){
return this._everyOrSome(false,arr,_139,_13a);
},filter:function(arr,_13c,_13d){
var _13e=dojo.lang.isString(arr);
if(_13e){
arr=arr.split("");
}
var _13f;
if(Array.filter){
_13f=Array.filter(arr,_13c,_13d);
}else{
if(!_13d){
if(arguments.length>=3){
dojo.raise("thisObject doesn't exist!");
}
_13d=dj_global;
}
_13f=[];
for(var i=0;i<arr.length;i++){
if(_13c.call(_13d,arr[i],i,arr)){
_13f.push(arr[i]);
}
}
}
if(_13e){
return _13f.join("");
}else{
return _13f;
}
},unnest:function(){
var out=[];
for(var i=0;i<arguments.length;i++){
if(dojo.lang.isArrayLike(arguments[i])){
var add=dojo.lang.unnest.apply(this,arguments[i]);
out=out.concat(add);
}else{
out.push(arguments[i]);
}
}
return out;
},toArray:function(_144,_145){
var _146=[];
for(var i=_145||0;i<_144.length;i++){
_146.push(_144[i]);
}
return _146;
}});
dojo.provide("dojo.lang.extras");
dojo.lang.setTimeout=function(func,_149){
var _14a=window,_14b=2;
if(!dojo.lang.isFunction(func)){
_14a=func;
func=_149;
_149=arguments[2];
_14b++;
}
if(dojo.lang.isString(func)){
func=_14a[func];
}
var args=[];
for(var i=_14b;i<arguments.length;i++){
args.push(arguments[i]);
}
return dojo.global().setTimeout(function(){
func.apply(_14a,args);
},_149);
};
dojo.lang.clearTimeout=function(_14e){
dojo.global().clearTimeout(_14e);
};
dojo.lang.getNameInObj=function(ns,item){
if(!ns){
ns=dj_global;
}
for(var x in ns){
if(ns[x]===item){
return new String(x);
}
}
return null;
};
dojo.lang.shallowCopy=function(obj,deep){
var i,ret;
if(obj===null){
return null;
}
if(dojo.lang.isObject(obj)){
ret=new obj.constructor();
for(i in obj){
if(dojo.lang.isUndefined(ret[i])){
ret[i]=deep?dojo.lang.shallowCopy(obj[i],deep):obj[i];
}
}
}else{
if(dojo.lang.isArray(obj)){
ret=[];
for(i=0;i<obj.length;i++){
ret[i]=deep?dojo.lang.shallowCopy(obj[i],deep):obj[i];
}
}else{
ret=obj;
}
}
return ret;
};
dojo.lang.firstValued=function(){
for(var i=0;i<arguments.length;i++){
if(typeof arguments[i]!="undefined"){
return arguments[i];
}
}
return undefined;
};
dojo.lang.getObjPathValue=function(_157,_158,_159){
with(dojo.parseObjPath(_157,_158,_159)){
return dojo.evalProp(prop,obj,_159);
}
};
dojo.lang.setObjPathValue=function(_15a,_15b,_15c,_15d){
dojo.deprecated("dojo.lang.setObjPathValue","use dojo.parseObjPath and the '=' operator","0.6");
if(arguments.length<4){
_15d=true;
}
with(dojo.parseObjPath(_15a,_15c,_15d)){
if(obj&&(_15d||(prop in obj))){
obj[prop]=_15b;
}
}
};
dojo.provide("dojo.lang.declare");
dojo.lang.declare=function(_15e,_15f,init,_161){
if((dojo.lang.isFunction(_161))||((!_161)&&(!dojo.lang.isFunction(init)))){
var temp=_161;
_161=init;
init=temp;
}
var _163=[];
if(dojo.lang.isArray(_15f)){
_163=_15f;
_15f=_163.shift();
}
if(!init){
init=dojo.evalObjPath(_15e,false);
if((init)&&(!dojo.lang.isFunction(init))){
init=null;
}
}
var ctor=dojo.lang.declare._makeConstructor();
var scp=(_15f?_15f.prototype:null);
if(scp){
scp.prototyping=true;
ctor.prototype=new _15f();
scp.prototyping=false;
}
ctor.superclass=scp;
ctor.mixins=_163;
for(var i=0,l=_163.length;i<l;i++){
dojo.lang.extend(ctor,_163[i].prototype);
}
ctor.prototype.initializer=null;
ctor.prototype.declaredClass=_15e;
if(dojo.lang.isArray(_161)){
dojo.lang.extend.apply(dojo.lang,[ctor].concat(_161));
}else{
dojo.lang.extend(ctor,(_161)||{});
}
dojo.lang.extend(ctor,dojo.lang.declare._common);
ctor.prototype.constructor=ctor;
ctor.prototype.initializer=(ctor.prototype.initializer)||(init)||(function(){
});
var _168=dojo.parseObjPath(_15e,null,true);
_168.obj[_168.prop]=ctor;
return ctor;
};
dojo.lang.declare._makeConstructor=function(){
return function(){
var self=this._getPropContext();
var s=self.constructor.superclass;
if((s)&&(s.constructor)){
if(s.constructor==arguments.callee){
this._inherited("constructor",arguments);
}else{
this._contextMethod(s,"constructor",arguments);
}
}
var ms=(self.constructor.mixins)||([]);
for(var i=0,m;(m=ms[i]);i++){
(((m.prototype)&&(m.prototype.initializer))||(m)).apply(this,arguments);
}
if((!this.prototyping)&&(self.initializer)){
self.initializer.apply(this,arguments);
}
};
};
dojo.lang.declare._common={_getPropContext:function(){
return (this.___proto||this);
},_contextMethod:function(_16e,_16f,args){
var _171,_172=this.___proto;
this.___proto=_16e;
try{
_171=_16e[_16f].apply(this,(args||[]));
}
catch(e){
throw e;
}
finally{
this.___proto=_172;
}
return _171;
},_inherited:function(prop,args){
var p=this._getPropContext();
do{
if((!p.constructor)||(!p.constructor.superclass)){
return;
}
p=p.constructor.superclass;
}while(!(prop in p));
return (dojo.lang.isFunction(p[prop])?this._contextMethod(p,prop,args):p[prop]);
},inherited:function(prop,args){
dojo.deprecated("'inherited' method is dangerous, do not up-call! 'inherited' is slated for removal in 0.5; name your super class (or use superclass property) instead.","0.5");
this._inherited(prop,args);
}};
dojo.declare=dojo.lang.declare;
dojo.provide("dojo.lang.func");
dojo.lang.hitch=function(_178,_179){
var args=[];
for(var x=2;x<arguments.length;x++){
args.push(arguments[x]);
}
var fcn=(dojo.lang.isString(_179)?_178[_179]:_179)||function(){
};
return function(){
var ta=args.concat([]);
for(var x=0;x<arguments.length;x++){
ta.push(arguments[x]);
}
return fcn.apply(_178,ta);
};
};
dojo.lang.anonCtr=0;
dojo.lang.anon={};
dojo.lang.nameAnonFunc=function(_17f,_180,_181){
var nso=(_180||dojo.lang.anon);
if((_181)||((dj_global["djConfig"])&&(djConfig["slowAnonFuncLookups"]==true))){
for(var x in nso){
try{
if(nso[x]===_17f){
return x;
}
}
catch(e){
}
}
}
var ret="__"+dojo.lang.anonCtr++;
while(typeof nso[ret]!="undefined"){
ret="__"+dojo.lang.anonCtr++;
}
nso[ret]=_17f;
return ret;
};
dojo.lang.forward=function(_185){
return function(){
return this[_185].apply(this,arguments);
};
};
dojo.lang.curry=function(_186,func){
var _188=[];
_186=_186||dj_global;
if(dojo.lang.isString(func)){
func=_186[func];
}
for(var x=2;x<arguments.length;x++){
_188.push(arguments[x]);
}
var _18a=(func["__preJoinArity"]||func.length)-_188.length;
function gather(_18b,_18c,_18d){
var _18e=_18d;
var _18f=_18c.slice(0);
for(var x=0;x<_18b.length;x++){
_18f.push(_18b[x]);
}
_18d=_18d-_18b.length;
if(_18d<=0){
var res=func.apply(_186,_18f);
_18d=_18e;
return res;
}else{
return function(){
return gather(arguments,_18f,_18d);
};
}
}
return gather([],_188,_18a);
};
dojo.lang.curryArguments=function(_192,func,args,_195){
var _196=[];
var x=_195||0;
for(x=_195;x<args.length;x++){
_196.push(args[x]);
}
return dojo.lang.curry.apply(dojo.lang,[_192,func].concat(_196));
};
dojo.lang.tryThese=function(){
for(var x=0;x<arguments.length;x++){
try{
if(typeof arguments[x]=="function"){
var ret=(arguments[x]());
if(ret){
return ret;
}
}
}
catch(e){
dojo.debug(e);
}
}
};
dojo.lang.delayThese=function(farr,cb,_19c,_19d){
if(!farr.length){
if(typeof _19d=="function"){
_19d();
}
return;
}
if((typeof _19c=="undefined")&&(typeof cb=="number")){
_19c=cb;
cb=function(){
};
}else{
if(!cb){
cb=function(){
};
if(!_19c){
_19c=0;
}
}
}
setTimeout(function(){
(farr.shift())();
cb();
dojo.lang.delayThese(farr,cb,_19c,_19d);
},_19c);
};
dojo.provide("dojo.event.common");
dojo.event=new function(){
this._canTimeout=dojo.lang.isFunction(dj_global["setTimeout"])||dojo.lang.isAlien(dj_global["setTimeout"]);
function interpolateArgs(args,_19f){
var dl=dojo.lang;
var ao={srcObj:dj_global,srcFunc:null,adviceObj:dj_global,adviceFunc:null,aroundObj:null,aroundFunc:null,adviceType:(args.length>2)?args[0]:"after",precedence:"last",once:false,delay:null,rate:0,adviceMsg:false,maxCalls:-1};
switch(args.length){
case 0:
return;
case 1:
return;
case 2:
ao.srcFunc=args[0];
ao.adviceFunc=args[1];
break;
case 3:
if((dl.isObject(args[0]))&&(dl.isString(args[1]))&&(dl.isString(args[2]))){
ao.adviceType="after";
ao.srcObj=args[0];
ao.srcFunc=args[1];
ao.adviceFunc=args[2];
}else{
if((dl.isString(args[1]))&&(dl.isString(args[2]))){
ao.srcFunc=args[1];
ao.adviceFunc=args[2];
}else{
if((dl.isObject(args[0]))&&(dl.isString(args[1]))&&(dl.isFunction(args[2]))){
ao.adviceType="after";
ao.srcObj=args[0];
ao.srcFunc=args[1];
var _1a2=dl.nameAnonFunc(args[2],ao.adviceObj,_19f);
ao.adviceFunc=_1a2;
}else{
if((dl.isFunction(args[0]))&&(dl.isObject(args[1]))&&(dl.isString(args[2]))){
ao.adviceType="after";
ao.srcObj=dj_global;
var _1a2=dl.nameAnonFunc(args[0],ao.srcObj,_19f);
ao.srcFunc=_1a2;
ao.adviceObj=args[1];
ao.adviceFunc=args[2];
}
}
}
}
break;
case 4:
if((dl.isObject(args[0]))&&(dl.isObject(args[2]))){
ao.adviceType="after";
ao.srcObj=args[0];
ao.srcFunc=args[1];
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((dl.isString(args[0]))&&(dl.isString(args[1]))&&(dl.isObject(args[2]))){
ao.adviceType=args[0];
ao.srcObj=dj_global;
ao.srcFunc=args[1];
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((dl.isString(args[0]))&&(dl.isFunction(args[1]))&&(dl.isObject(args[2]))){
ao.adviceType=args[0];
ao.srcObj=dj_global;
var _1a2=dl.nameAnonFunc(args[1],dj_global,_19f);
ao.srcFunc=_1a2;
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((dl.isString(args[0]))&&(dl.isObject(args[1]))&&(dl.isString(args[2]))&&(dl.isFunction(args[3]))){
ao.srcObj=args[1];
ao.srcFunc=args[2];
var _1a2=dl.nameAnonFunc(args[3],dj_global,_19f);
ao.adviceObj=dj_global;
ao.adviceFunc=_1a2;
}else{
if(dl.isObject(args[1])){
ao.srcObj=args[1];
ao.srcFunc=args[2];
ao.adviceObj=dj_global;
ao.adviceFunc=args[3];
}else{
if(dl.isObject(args[2])){
ao.srcObj=dj_global;
ao.srcFunc=args[1];
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
ao.srcObj=ao.adviceObj=ao.aroundObj=dj_global;
ao.srcFunc=args[1];
ao.adviceFunc=args[2];
ao.aroundFunc=args[3];
}
}
}
}
}
}
break;
case 6:
ao.srcObj=args[1];
ao.srcFunc=args[2];
ao.adviceObj=args[3];
ao.adviceFunc=args[4];
ao.aroundFunc=args[5];
ao.aroundObj=dj_global;
break;
default:
ao.srcObj=args[1];
ao.srcFunc=args[2];
ao.adviceObj=args[3];
ao.adviceFunc=args[4];
ao.aroundObj=args[5];
ao.aroundFunc=args[6];
ao.once=args[7];
ao.delay=args[8];
ao.rate=args[9];
ao.adviceMsg=args[10];
ao.maxCalls=(!isNaN(parseInt(args[11])))?args[11]:-1;
break;
}
if(dl.isFunction(ao.aroundFunc)){
var _1a2=dl.nameAnonFunc(ao.aroundFunc,ao.aroundObj,_19f);
ao.aroundFunc=_1a2;
}
if(dl.isFunction(ao.srcFunc)){
ao.srcFunc=dl.getNameInObj(ao.srcObj,ao.srcFunc);
}
if(dl.isFunction(ao.adviceFunc)){
ao.adviceFunc=dl.getNameInObj(ao.adviceObj,ao.adviceFunc);
}
if((ao.aroundObj)&&(dl.isFunction(ao.aroundFunc))){
ao.aroundFunc=dl.getNameInObj(ao.aroundObj,ao.aroundFunc);
}
if(!ao.srcObj){
dojo.raise("bad srcObj for srcFunc: "+ao.srcFunc);
}
if(!ao.adviceObj){
dojo.raise("bad adviceObj for adviceFunc: "+ao.adviceFunc);
}
if(!ao.adviceFunc){
dojo.debug("bad adviceFunc for srcFunc: "+ao.srcFunc);
dojo.debugShallow(ao);
}
return ao;
}
this.connect=function(){
if(arguments.length==1){
var ao=arguments[0];
}else{
var ao=interpolateArgs(arguments,true);
}
if(dojo.lang.isString(ao.srcFunc)&&(ao.srcFunc.toLowerCase()=="onkey")){
if(dojo.render.html.ie){
ao.srcFunc="onkeydown";
this.connect(ao);
}
ao.srcFunc="onkeypress";
}
if(dojo.lang.isArray(ao.srcObj)&&ao.srcObj!=""){
var _1a4={};
for(var x in ao){
_1a4[x]=ao[x];
}
var mjps=[];
dojo.lang.forEach(ao.srcObj,function(src){
if((dojo.render.html.capable)&&(dojo.lang.isString(src))){
src=dojo.byId(src);
}
_1a4.srcObj=src;
mjps.push(dojo.event.connect.call(dojo.event,_1a4));
});
return mjps;
}
var mjp=dojo.event.MethodJoinPoint.getForMethod(ao.srcObj,ao.srcFunc);
if(ao.adviceFunc){
var mjp2=dojo.event.MethodJoinPoint.getForMethod(ao.adviceObj,ao.adviceFunc);
}
mjp.kwAddAdvice(ao);
return mjp;
};
this.log=function(a1,a2){
var _1ac;
if((arguments.length==1)&&(typeof a1=="object")){
_1ac=a1;
}else{
_1ac={srcObj:a1,srcFunc:a2};
}
_1ac.adviceFunc=function(){
var _1ad=[];
for(var x=0;x<arguments.length;x++){
_1ad.push(arguments[x]);
}
dojo.debug("("+_1ac.srcObj+")."+_1ac.srcFunc,":",_1ad.join(", "));
};
this.kwConnect(_1ac);
};
this.connectBefore=function(){
var args=["before"];
for(var i=0;i<arguments.length;i++){
args.push(arguments[i]);
}
return this.connect.apply(this,args);
};
this.connectAround=function(){
var args=["around"];
for(var i=0;i<arguments.length;i++){
args.push(arguments[i]);
}
return this.connect.apply(this,args);
};
this.connectOnce=function(){
var ao=interpolateArgs(arguments,true);
ao.once=true;
return this.connect(ao);
};
this.connectRunOnce=function(){
var ao=interpolateArgs(arguments,true);
ao.maxCalls=1;
return this.connect(ao);
};
this._kwConnectImpl=function(_1b5,_1b6){
var fn=(_1b6)?"disconnect":"connect";
if(typeof _1b5["srcFunc"]=="function"){
_1b5.srcObj=_1b5["srcObj"]||dj_global;
var _1b8=dojo.lang.nameAnonFunc(_1b5.srcFunc,_1b5.srcObj,true);
_1b5.srcFunc=_1b8;
}
if(typeof _1b5["adviceFunc"]=="function"){
_1b5.adviceObj=_1b5["adviceObj"]||dj_global;
var _1b8=dojo.lang.nameAnonFunc(_1b5.adviceFunc,_1b5.adviceObj,true);
_1b5.adviceFunc=_1b8;
}
_1b5.srcObj=_1b5["srcObj"]||dj_global;
_1b5.adviceObj=_1b5["adviceObj"]||_1b5["targetObj"]||dj_global;
_1b5.adviceFunc=_1b5["adviceFunc"]||_1b5["targetFunc"];
return dojo.event[fn](_1b5);
};
this.kwConnect=function(_1b9){
return this._kwConnectImpl(_1b9,false);
};
this.disconnect=function(){
if(arguments.length==1){
var ao=arguments[0];
}else{
var ao=interpolateArgs(arguments,true);
}
if(!ao.adviceFunc){
return;
}
if(dojo.lang.isString(ao.srcFunc)&&(ao.srcFunc.toLowerCase()=="onkey")){
if(dojo.render.html.ie){
ao.srcFunc="onkeydown";
this.disconnect(ao);
}
ao.srcFunc="onkeypress";
}
if(!ao.srcObj[ao.srcFunc]){
return null;
}
var mjp=dojo.event.MethodJoinPoint.getForMethod(ao.srcObj,ao.srcFunc,true);
mjp.removeAdvice(ao.adviceObj,ao.adviceFunc,ao.adviceType,ao.once);
return mjp;
};
this.kwDisconnect=function(_1bc){
return this._kwConnectImpl(_1bc,true);
};
};
dojo.event.MethodInvocation=function(_1bd,obj,args){
this.jp_=_1bd;
this.object=obj;
this.args=[];
for(var x=0;x<args.length;x++){
this.args[x]=args[x];
}
this.around_index=-1;
};
dojo.event.MethodInvocation.prototype.proceed=function(){
this.around_index++;
if(this.around_index>=this.jp_.around.length){
return this.jp_.object[this.jp_.methodname].apply(this.jp_.object,this.args);
}else{
var ti=this.jp_.around[this.around_index];
var mobj=ti[0]||dj_global;
var meth=ti[1];
return mobj[meth].call(mobj,this);
}
};
dojo.event.MethodJoinPoint=function(obj,_1c5){
this.object=obj||dj_global;
this.methodname=_1c5;
this.methodfunc=this.object[_1c5];
this.squelch=false;
};
dojo.event.MethodJoinPoint.getForMethod=function(obj,_1c7){
if(!obj){
obj=dj_global;
}
var ofn=obj[_1c7];
if(!ofn){
ofn=obj[_1c7]=function(){
};
if(!obj[_1c7]){
dojo.raise("Cannot set do-nothing method on that object "+_1c7);
}
}else{
if((typeof ofn!="function")&&(!dojo.lang.isFunction(ofn))&&(!dojo.lang.isAlien(ofn))){
return null;
}
}
var _1c9=_1c7+"$joinpoint";
var _1ca=_1c7+"$joinpoint$method";
var _1cb=obj[_1c9];
if(!_1cb){
var _1cc=false;
if(dojo.event["browser"]){
if((obj["attachEvent"])||(obj["nodeType"])||(obj["addEventListener"])){
_1cc=true;
dojo.event.browser.addClobberNodeAttrs(obj,[_1c9,_1ca,_1c7]);
}
}
var _1cd=ofn.length;
obj[_1ca]=ofn;
_1cb=obj[_1c9]=new dojo.event.MethodJoinPoint(obj,_1ca);
if(!_1cc){
obj[_1c7]=function(){
return _1cb.run.apply(_1cb,arguments);
};
}else{
obj[_1c7]=function(){
var args=[];
if(!arguments.length){
var evt=null;
try{
if(obj.ownerDocument){
evt=obj.ownerDocument.parentWindow.event;
}else{
if(obj.documentElement){
evt=obj.documentElement.ownerDocument.parentWindow.event;
}else{
if(obj.event){
evt=obj.event;
}else{
evt=window.event;
}
}
}
}
catch(e){
evt=window.event;
}
if(evt){
args.push(dojo.event.browser.fixEvent(evt,this));
}
}else{
for(var x=0;x<arguments.length;x++){
if((x==0)&&(dojo.event.browser.isEvent(arguments[x]))){
args.push(dojo.event.browser.fixEvent(arguments[x],this));
}else{
args.push(arguments[x]);
}
}
}
return _1cb.run.apply(_1cb,args);
};
}
obj[_1c7].__preJoinArity=_1cd;
}
return _1cb;
};
dojo.lang.extend(dojo.event.MethodJoinPoint,{squelch:false,unintercept:function(){
this.object[this.methodname]=this.methodfunc;
this.before=[];
this.after=[];
this.around=[];
},disconnect:dojo.lang.forward("unintercept"),run:function(){
var obj=this.object||dj_global;
var args=arguments;
var _1d3=[];
for(var x=0;x<args.length;x++){
_1d3[x]=args[x];
}
var _1d5=function(marr){
if(!marr){
dojo.debug("Null argument to unrollAdvice()");
return;
}
var _1d7=marr[0]||dj_global;
var _1d8=marr[1];
if(!_1d7[_1d8]){
dojo.raise("function \""+_1d8+"\" does not exist on \""+_1d7+"\"");
}
var _1d9=marr[2]||dj_global;
var _1da=marr[3];
var msg=marr[6];
var _1dc=marr[7];
if(_1dc>-1){
if(_1dc==0){
return;
}
marr[7]--;
}
var _1dd;
var to={args:[],jp_:this,object:obj,proceed:function(){
return _1d7[_1d8].apply(_1d7,to.args);
}};
to.args=_1d3;
var _1df=parseInt(marr[4]);
var _1e0=((!isNaN(_1df))&&(marr[4]!==null)&&(typeof marr[4]!="undefined"));
if(marr[5]){
var rate=parseInt(marr[5]);
var cur=new Date();
var _1e3=false;
if((marr["last"])&&((cur-marr.last)<=rate)){
if(dojo.event._canTimeout){
if(marr["delayTimer"]){
clearTimeout(marr.delayTimer);
}
var tod=parseInt(rate*2);
var mcpy=dojo.lang.shallowCopy(marr);
marr.delayTimer=setTimeout(function(){
mcpy[5]=0;
_1d5(mcpy);
},tod);
}
return;
}else{
marr.last=cur;
}
}
if(_1da){
_1d9[_1da].call(_1d9,to);
}else{
if((_1e0)&&((dojo.render.html)||(dojo.render.svg))){
dj_global["setTimeout"](function(){
if(msg){
_1d7[_1d8].call(_1d7,to);
}else{
_1d7[_1d8].apply(_1d7,args);
}
},_1df);
}else{
if(msg){
_1d7[_1d8].call(_1d7,to);
}else{
_1d7[_1d8].apply(_1d7,args);
}
}
}
};
var _1e6=function(){
if(this.squelch){
try{
return _1d5.apply(this,arguments);
}
catch(e){
dojo.debug(e);
}
}else{
return _1d5.apply(this,arguments);
}
};
if((this["before"])&&(this.before.length>0)){
dojo.lang.forEach(this.before.concat(new Array()),_1e6);
}
var _1e7;
try{
if((this["around"])&&(this.around.length>0)){
var mi=new dojo.event.MethodInvocation(this,obj,args);
_1e7=mi.proceed();
}else{
if(this.methodfunc){
_1e7=this.object[this.methodname].apply(this.object,args);
}
}
}
catch(e){
if(!this.squelch){
dojo.debug(e,"when calling",this.methodname,"on",this.object,"with arguments",args);
dojo.raise(e);
}
}
if((this["after"])&&(this.after.length>0)){
dojo.lang.forEach(this.after.concat(new Array()),_1e6);
}
return (this.methodfunc)?_1e7:null;
},getArr:function(kind){
var type="after";
if((typeof kind=="string")&&(kind.indexOf("before")!=-1)){
type="before";
}else{
if(kind=="around"){
type="around";
}
}
if(!this[type]){
this[type]=[];
}
return this[type];
},kwAddAdvice:function(args){
this.addAdvice(args["adviceObj"],args["adviceFunc"],args["aroundObj"],args["aroundFunc"],args["adviceType"],args["precedence"],args["once"],args["delay"],args["rate"],args["adviceMsg"],args["maxCalls"]);
},addAdvice:function(_1ec,_1ed,_1ee,_1ef,_1f0,_1f1,once,_1f3,rate,_1f5,_1f6){
var arr=this.getArr(_1f0);
if(!arr){
dojo.raise("bad this: "+this);
}
var ao=[_1ec,_1ed,_1ee,_1ef,_1f3,rate,_1f5,_1f6];
if(once){
if(this.hasAdvice(_1ec,_1ed,_1f0,arr)>=0){
return;
}
}
if(_1f1=="first"){
arr.unshift(ao);
}else{
arr.push(ao);
}
},hasAdvice:function(_1f9,_1fa,_1fb,arr){
if(!arr){
arr=this.getArr(_1fb);
}
var ind=-1;
for(var x=0;x<arr.length;x++){
var aao=(typeof _1fa=="object")?(new String(_1fa)).toString():_1fa;
var a1o=(typeof arr[x][1]=="object")?(new String(arr[x][1])).toString():arr[x][1];
if((arr[x][0]==_1f9)&&(a1o==aao)){
ind=x;
}
}
return ind;
},removeAdvice:function(_201,_202,_203,once){
var arr=this.getArr(_203);
var ind=this.hasAdvice(_201,_202,_203,arr);
if(ind==-1){
return false;
}
while(ind!=-1){
arr.splice(ind,1);
if(once){
break;
}
ind=this.hasAdvice(_201,_202,_203,arr);
}
return true;
}});
dojo.provide("dojo.event.topic");
dojo.event.topic=new function(){
this.topics={};
this.getTopic=function(_207){
if(!this.topics[_207]){
this.topics[_207]=new this.TopicImpl(_207);
}
return this.topics[_207];
};
this.registerPublisher=function(_208,obj,_20a){
var _208=this.getTopic(_208);
_208.registerPublisher(obj,_20a);
};
this.subscribe=function(_20b,obj,_20d){
var _20b=this.getTopic(_20b);
_20b.subscribe(obj,_20d);
};
this.unsubscribe=function(_20e,obj,_210){
var _20e=this.getTopic(_20e);
_20e.unsubscribe(obj,_210);
};
this.destroy=function(_211){
this.getTopic(_211).destroy();
delete this.topics[_211];
};
this.publishApply=function(_212,args){
var _212=this.getTopic(_212);
_212.sendMessage.apply(_212,args);
};
this.publish=function(_214,_215){
var _214=this.getTopic(_214);
var args=[];
for(var x=1;x<arguments.length;x++){
args.push(arguments[x]);
}
_214.sendMessage.apply(_214,args);
};
};
dojo.event.topic.TopicImpl=function(_218){
this.topicName=_218;
this.subscribe=function(_219,_21a){
var tf=_21a||_219;
var to=(!_21a)?dj_global:_219;
return dojo.event.kwConnect({srcObj:this,srcFunc:"sendMessage",adviceObj:to,adviceFunc:tf});
};
this.unsubscribe=function(_21d,_21e){
var tf=(!_21e)?_21d:_21e;
var to=(!_21e)?null:_21d;
return dojo.event.kwDisconnect({srcObj:this,srcFunc:"sendMessage",adviceObj:to,adviceFunc:tf});
};
this._getJoinPoint=function(){
return dojo.event.MethodJoinPoint.getForMethod(this,"sendMessage");
};
this.setSquelch=function(_221){
this._getJoinPoint().squelch=_221;
};
this.destroy=function(){
this._getJoinPoint().disconnect();
};
this.registerPublisher=function(_222,_223){
dojo.event.connect(_222,_223,this,"sendMessage");
};
this.sendMessage=function(_224){
};
};
dojo.provide("dojo.event.browser");
dojo._ie_clobber=new function(){
this.clobberNodes=[];
function nukeProp(node,prop){
try{
node[prop]=null;
}
catch(e){
}
try{
delete node[prop];
}
catch(e){
}
try{
node.removeAttribute(prop);
}
catch(e){
}
}
this.clobber=function(_227){
var na;
var tna;
if(_227){
tna=_227.all||_227.getElementsByTagName("*");
na=[_227];
for(var x=0;x<tna.length;x++){
if(tna[x]["__doClobber__"]){
na.push(tna[x]);
}
}
}else{
try{
window.onload=null;
}
catch(e){
}
na=(this.clobberNodes.length)?this.clobberNodes:document.all;
}
tna=null;
var _22b={};
for(var i=na.length-1;i>=0;i=i-1){
var el=na[i];
try{
if(el&&el["__clobberAttrs__"]){
for(var j=0;j<el.__clobberAttrs__.length;j++){
nukeProp(el,el.__clobberAttrs__[j]);
}
nukeProp(el,"__clobberAttrs__");
nukeProp(el,"__doClobber__");
}
}
catch(e){
}
}
na=null;
};
};
if(dojo.render.html.ie){
dojo.addOnUnload(function(){
dojo._ie_clobber.clobber();
try{
if((dojo["widget"])&&(dojo.widget["manager"])){
dojo.widget.manager.destroyAll();
}
}
catch(e){
}
if(dojo.widget){
for(var name in dojo.widget._templateCache){
if(dojo.widget._templateCache[name].node){
dojo.dom.destroyNode(dojo.widget._templateCache[name].node);
dojo.widget._templateCache[name].node=null;
delete dojo.widget._templateCache[name].node;
}
}
}
try{
window.onload=null;
}
catch(e){
}
try{
window.onunload=null;
}
catch(e){
}
dojo._ie_clobber.clobberNodes=[];
});
}
dojo.event.browser=new function(){
var _230=0;
this.normalizedEventName=function(_231){
switch(_231){
case "CheckboxStateChange":
case "DOMAttrModified":
case "DOMMenuItemActive":
case "DOMMenuItemInactive":
case "DOMMouseScroll":
case "DOMNodeInserted":
case "DOMNodeRemoved":
case "RadioStateChange":
return _231;
break;
default:
var lcn=_231.toLowerCase();
return (lcn.indexOf("on")==0)?lcn.substr(2):lcn;
break;
}
};
this.clean=function(node){
if(dojo.render.html.ie){
dojo._ie_clobber.clobber(node);
}
};
this.addClobberNode=function(node){
if(!dojo.render.html.ie){
return;
}
if(!node["__doClobber__"]){
node.__doClobber__=true;
dojo._ie_clobber.clobberNodes.push(node);
node.__clobberAttrs__=[];
}
};
this.addClobberNodeAttrs=function(node,_236){
if(!dojo.render.html.ie){
return;
}
this.addClobberNode(node);
for(var x=0;x<_236.length;x++){
node.__clobberAttrs__.push(_236[x]);
}
};
this.removeListener=function(node,_239,fp,_23b){
if(!_23b){
var _23b=false;
}
_239=dojo.event.browser.normalizedEventName(_239);
if(_239=="key"){
if(dojo.render.html.ie){
this.removeListener(node,"onkeydown",fp,_23b);
}
_239="keypress";
}
if(node.removeEventListener){
node.removeEventListener(_239,fp,_23b);
}
};
this.addListener=function(node,_23d,fp,_23f,_240){
if(!node){
return;
}
if(!_23f){
var _23f=false;
}
_23d=dojo.event.browser.normalizedEventName(_23d);
if(_23d=="key"){
if(dojo.render.html.ie){
this.addListener(node,"onkeydown",fp,_23f,_240);
}
_23d="keypress";
}
if(!_240){
var _241=function(evt){
if(!evt){
evt=window.event;
}
var ret=fp(dojo.event.browser.fixEvent(evt,this));
if(_23f){
dojo.event.browser.stopEvent(evt);
}
return ret;
};
}else{
_241=fp;
}
if(node.addEventListener){
node.addEventListener(_23d,_241,_23f);
return _241;
}else{
_23d="on"+_23d;
if(typeof node[_23d]=="function"){
var _244=node[_23d];
node[_23d]=function(e){
_244(e);
return _241(e);
};
}else{
node[_23d]=_241;
}
if(dojo.render.html.ie){
this.addClobberNodeAttrs(node,[_23d]);
}
return _241;
}
};
this.isEvent=function(obj){
return (typeof obj!="undefined")&&(obj)&&(typeof Event!="undefined")&&(obj.eventPhase);
};
this.currentEvent=null;
this.callListener=function(_247,_248){
if(typeof _247!="function"){
dojo.raise("listener not a function: "+_247);
}
dojo.event.browser.currentEvent.currentTarget=_248;
return _247.call(_248,dojo.event.browser.currentEvent);
};
this._stopPropagation=function(){
dojo.event.browser.currentEvent.cancelBubble=true;
};
this._preventDefault=function(){
dojo.event.browser.currentEvent.returnValue=false;
};
this.keys={KEY_BACKSPACE:8,KEY_TAB:9,KEY_CLEAR:12,KEY_ENTER:13,KEY_SHIFT:16,KEY_CTRL:17,KEY_ALT:18,KEY_PAUSE:19,KEY_CAPS_LOCK:20,KEY_ESCAPE:27,KEY_SPACE:32,KEY_PAGE_UP:33,KEY_PAGE_DOWN:34,KEY_END:35,KEY_HOME:36,KEY_LEFT_ARROW:37,KEY_UP_ARROW:38,KEY_RIGHT_ARROW:39,KEY_DOWN_ARROW:40,KEY_INSERT:45,KEY_DELETE:46,KEY_HELP:47,KEY_LEFT_WINDOW:91,KEY_RIGHT_WINDOW:92,KEY_SELECT:93,KEY_NUMPAD_0:96,KEY_NUMPAD_1:97,KEY_NUMPAD_2:98,KEY_NUMPAD_3:99,KEY_NUMPAD_4:100,KEY_NUMPAD_5:101,KEY_NUMPAD_6:102,KEY_NUMPAD_7:103,KEY_NUMPAD_8:104,KEY_NUMPAD_9:105,KEY_NUMPAD_MULTIPLY:106,KEY_NUMPAD_PLUS:107,KEY_NUMPAD_ENTER:108,KEY_NUMPAD_MINUS:109,KEY_NUMPAD_PERIOD:110,KEY_NUMPAD_DIVIDE:111,KEY_F1:112,KEY_F2:113,KEY_F3:114,KEY_F4:115,KEY_F5:116,KEY_F6:117,KEY_F7:118,KEY_F8:119,KEY_F9:120,KEY_F10:121,KEY_F11:122,KEY_F12:123,KEY_F13:124,KEY_F14:125,KEY_F15:126,KEY_NUM_LOCK:144,KEY_SCROLL_LOCK:145};
this.revKeys=[];
for(var key in this.keys){
this.revKeys[this.keys[key]]=key;
}
this.fixEvent=function(evt,_24b){
if(!evt){
if(window["event"]){
evt=window.event;
}
}
if((evt["type"])&&(evt["type"].indexOf("key")==0)){
evt.keys=this.revKeys;
for(var key in this.keys){
evt[key]=this.keys[key];
}
if(evt["type"]=="keydown"&&dojo.render.html.ie){
switch(evt.keyCode){
case evt.KEY_SHIFT:
case evt.KEY_CTRL:
case evt.KEY_ALT:
case evt.KEY_CAPS_LOCK:
case evt.KEY_LEFT_WINDOW:
case evt.KEY_RIGHT_WINDOW:
case evt.KEY_SELECT:
case evt.KEY_NUM_LOCK:
case evt.KEY_SCROLL_LOCK:
case evt.KEY_NUMPAD_0:
case evt.KEY_NUMPAD_1:
case evt.KEY_NUMPAD_2:
case evt.KEY_NUMPAD_3:
case evt.KEY_NUMPAD_4:
case evt.KEY_NUMPAD_5:
case evt.KEY_NUMPAD_6:
case evt.KEY_NUMPAD_7:
case evt.KEY_NUMPAD_8:
case evt.KEY_NUMPAD_9:
case evt.KEY_NUMPAD_PERIOD:
break;
case evt.KEY_NUMPAD_MULTIPLY:
case evt.KEY_NUMPAD_PLUS:
case evt.KEY_NUMPAD_ENTER:
case evt.KEY_NUMPAD_MINUS:
case evt.KEY_NUMPAD_DIVIDE:
break;
case evt.KEY_PAUSE:
case evt.KEY_TAB:
case evt.KEY_BACKSPACE:
case evt.KEY_ENTER:
case evt.KEY_ESCAPE:
case evt.KEY_PAGE_UP:
case evt.KEY_PAGE_DOWN:
case evt.KEY_END:
case evt.KEY_HOME:
case evt.KEY_LEFT_ARROW:
case evt.KEY_UP_ARROW:
case evt.KEY_RIGHT_ARROW:
case evt.KEY_DOWN_ARROW:
case evt.KEY_INSERT:
case evt.KEY_DELETE:
case evt.KEY_F1:
case evt.KEY_F2:
case evt.KEY_F3:
case evt.KEY_F4:
case evt.KEY_F5:
case evt.KEY_F6:
case evt.KEY_F7:
case evt.KEY_F8:
case evt.KEY_F9:
case evt.KEY_F10:
case evt.KEY_F11:
case evt.KEY_F12:
case evt.KEY_F12:
case evt.KEY_F13:
case evt.KEY_F14:
case evt.KEY_F15:
case evt.KEY_CLEAR:
case evt.KEY_HELP:
evt.key=evt.keyCode;
break;
default:
if(evt.ctrlKey||evt.altKey){
var _24d=evt.keyCode;
if(_24d>=65&&_24d<=90&&evt.shiftKey==false){
_24d+=32;
}
if(_24d>=1&&_24d<=26&&evt.ctrlKey){
_24d+=96;
}
evt.key=String.fromCharCode(_24d);
}
}
}else{
if(evt["type"]=="keypress"){
if(dojo.render.html.opera){
if(evt.which==0){
evt.key=evt.keyCode;
}else{
if(evt.which>0){
switch(evt.which){
case evt.KEY_SHIFT:
case evt.KEY_CTRL:
case evt.KEY_ALT:
case evt.KEY_CAPS_LOCK:
case evt.KEY_NUM_LOCK:
case evt.KEY_SCROLL_LOCK:
break;
case evt.KEY_PAUSE:
case evt.KEY_TAB:
case evt.KEY_BACKSPACE:
case evt.KEY_ENTER:
case evt.KEY_ESCAPE:
evt.key=evt.which;
break;
default:
var _24d=evt.which;
if((evt.ctrlKey||evt.altKey||evt.metaKey)&&(evt.which>=65&&evt.which<=90&&evt.shiftKey==false)){
_24d+=32;
}
evt.key=String.fromCharCode(_24d);
}
}
}
}else{
if(dojo.render.html.ie){
if(!evt.ctrlKey&&!evt.altKey&&evt.keyCode>=evt.KEY_SPACE){
evt.key=String.fromCharCode(evt.keyCode);
}
}else{
if(dojo.render.html.safari){
switch(evt.keyCode){
case 25:
evt.key=evt.KEY_TAB;
evt.shift=true;
break;
case 63232:
evt.key=evt.KEY_UP_ARROW;
break;
case 63233:
evt.key=evt.KEY_DOWN_ARROW;
break;
case 63234:
evt.key=evt.KEY_LEFT_ARROW;
break;
case 63235:
evt.key=evt.KEY_RIGHT_ARROW;
break;
case 63236:
evt.key=evt.KEY_F1;
break;
case 63237:
evt.key=evt.KEY_F2;
break;
case 63238:
evt.key=evt.KEY_F3;
break;
case 63239:
evt.key=evt.KEY_F4;
break;
case 63240:
evt.key=evt.KEY_F5;
break;
case 63241:
evt.key=evt.KEY_F6;
break;
case 63242:
evt.key=evt.KEY_F7;
break;
case 63243:
evt.key=evt.KEY_F8;
break;
case 63244:
evt.key=evt.KEY_F9;
break;
case 63245:
evt.key=evt.KEY_F10;
break;
case 63246:
evt.key=evt.KEY_F11;
break;
case 63247:
evt.key=evt.KEY_F12;
break;
case 63250:
evt.key=evt.KEY_PAUSE;
break;
case 63272:
evt.key=evt.KEY_DELETE;
break;
case 63273:
evt.key=evt.KEY_HOME;
break;
case 63275:
evt.key=evt.KEY_END;
break;
case 63276:
evt.key=evt.KEY_PAGE_UP;
break;
case 63277:
evt.key=evt.KEY_PAGE_DOWN;
break;
case 63302:
evt.key=evt.KEY_INSERT;
break;
case 63248:
case 63249:
case 63289:
break;
default:
evt.key=evt.charCode>=evt.KEY_SPACE?String.fromCharCode(evt.charCode):evt.keyCode;
}
}else{
evt.key=evt.charCode>0?String.fromCharCode(evt.charCode):evt.keyCode;
}
}
}
}
}
}
if(dojo.render.html.ie){
if(!evt.target){
evt.target=evt.srcElement;
}
if(!evt.currentTarget){
evt.currentTarget=(_24b?_24b:evt.srcElement);
}
if(!evt.layerX){
evt.layerX=evt.offsetX;
}
if(!evt.layerY){
evt.layerY=evt.offsetY;
}
var doc=(evt.srcElement&&evt.srcElement.ownerDocument)?evt.srcElement.ownerDocument:document;
var _24f=((dojo.render.html.ie55)||(doc["compatMode"]=="BackCompat"))?doc.body:doc.documentElement;
if(!evt.pageX){
evt.pageX=evt.clientX+(_24f.scrollLeft||0);
}
if(!evt.pageY){
evt.pageY=evt.clientY+(_24f.scrollTop||0);
}
if(evt.type=="mouseover"){
evt.relatedTarget=evt.fromElement;
}
if(evt.type=="mouseout"){
evt.relatedTarget=evt.toElement;
}
this.currentEvent=evt;
evt.callListener=this.callListener;
evt.stopPropagation=this._stopPropagation;
evt.preventDefault=this._preventDefault;
}
return evt;
};
this.stopEvent=function(evt){
if(window.event){
evt.cancelBubble=true;
evt.returnValue=false;
}else{
evt.preventDefault();
evt.stopPropagation();
}
};
};
dojo.kwCompoundRequire({common:["dojo.event.common","dojo.event.topic"],browser:["dojo.event.browser"],dashboard:["dojo.event.browser"]});
dojo.provide("dojo.event.*");
dojo.provide("dojo.string.common");
dojo.string.trim=function(str,wh){
if(!str.replace){
return str;
}
if(!str.length){
return str;
}
var re=(wh>0)?(/^\s+/):(wh<0)?(/\s+$/):(/^\s+|\s+$/g);
return str.replace(re,"");
};
dojo.string.trimStart=function(str){
return dojo.string.trim(str,1);
};
dojo.string.trimEnd=function(str){
return dojo.string.trim(str,-1);
};
dojo.string.repeat=function(str,_257,_258){
var out="";
for(var i=0;i<_257;i++){
out+=str;
if(_258&&i<_257-1){
out+=_258;
}
}
return out;
};
dojo.string.pad=function(str,len,c,dir){
var out=String(str);
if(!c){
c="0";
}
if(!dir){
dir=1;
}
while(out.length<len){
if(dir>0){
out=c+out;
}else{
out+=c;
}
}
return out;
};
dojo.string.padLeft=function(str,len,c){
return dojo.string.pad(str,len,c,1);
};
dojo.string.padRight=function(str,len,c){
return dojo.string.pad(str,len,c,-1);
};
dojo.provide("dojo.string.extras");
dojo.string.substituteParams=function(_266,hash){
var map=(typeof hash=="object")?hash:dojo.lang.toArray(arguments,1);
return _266.replace(/\%\{(\w+)\}/g,function(_269,key){
if(typeof (map[key])!="undefined"&&map[key]!=null){
return map[key];
}
dojo.raise("Substitution not found: "+key);
});
};
dojo.string.capitalize=function(str){
if(!dojo.lang.isString(str)){
return "";
}
if(arguments.length==0){
str=this;
}
var _26c=str.split(" ");
for(var i=0;i<_26c.length;i++){
_26c[i]=_26c[i].charAt(0).toUpperCase()+_26c[i].substring(1);
}
return _26c.join(" ");
};
dojo.string.isBlank=function(str){
if(!dojo.lang.isString(str)){
return true;
}
return (dojo.string.trim(str).length==0);
};
dojo.string.encodeAscii=function(str){
if(!dojo.lang.isString(str)){
return str;
}
var ret="";
var _271=escape(str);
var _272,re=/%u([0-9A-F]{4})/i;
while((_272=_271.match(re))){
var num=Number("0x"+_272[1]);
var _275=escape("&#"+num+";");
ret+=_271.substring(0,_272.index)+_275;
_271=_271.substring(_272.index+_272[0].length);
}
ret+=_271.replace(/\+/g,"%2B");
return ret;
};
dojo.string.escape=function(type,str){
var args=dojo.lang.toArray(arguments,1);
switch(type.toLowerCase()){
case "xml":
case "html":
case "xhtml":
return dojo.string.escapeXml.apply(this,args);
case "sql":
return dojo.string.escapeSql.apply(this,args);
case "regexp":
case "regex":
return dojo.string.escapeRegExp.apply(this,args);
case "javascript":
case "jscript":
case "js":
return dojo.string.escapeJavaScript.apply(this,args);
case "ascii":
return dojo.string.encodeAscii.apply(this,args);
default:
return str;
}
};
dojo.string.escapeXml=function(str,_27a){
str=str.replace(/&/gm,"&amp;").replace(/</gm,"&lt;").replace(/>/gm,"&gt;").replace(/"/gm,"&quot;");
if(!_27a){
str=str.replace(/'/gm,"&#39;");
}
return str;
};
dojo.string.escapeSql=function(str){
return str.replace(/'/gm,"''");
};
dojo.string.escapeRegExp=function(str){
return str.replace(/\\/gm,"\\\\").replace(/([\f\b\n\t\r[\^$|?*+(){}])/gm,"\\$1");
};
dojo.string.escapeJavaScript=function(str){
return str.replace(/(["'\f\b\n\t\r])/gm,"\\$1");
};
dojo.string.escapeString=function(str){
return ("\""+str.replace(/(["\\])/g,"\\$1")+"\"").replace(/[\f]/g,"\\f").replace(/[\b]/g,"\\b").replace(/[\n]/g,"\\n").replace(/[\t]/g,"\\t").replace(/[\r]/g,"\\r");
};
dojo.string.summary=function(str,len){
if(!len||str.length<=len){
return str;
}
return str.substring(0,len).replace(/\.+$/,"")+"...";
};
dojo.string.endsWith=function(str,end,_283){
if(_283){
str=str.toLowerCase();
end=end.toLowerCase();
}
if((str.length-end.length)<0){
return false;
}
return str.lastIndexOf(end)==str.length-end.length;
};
dojo.string.endsWithAny=function(str){
for(var i=1;i<arguments.length;i++){
if(dojo.string.endsWith(str,arguments[i])){
return true;
}
}
return false;
};
dojo.string.startsWith=function(str,_287,_288){
if(_288){
str=str.toLowerCase();
_287=_287.toLowerCase();
}
return str.indexOf(_287)==0;
};
dojo.string.startsWithAny=function(str){
for(var i=1;i<arguments.length;i++){
if(dojo.string.startsWith(str,arguments[i])){
return true;
}
}
return false;
};
dojo.string.has=function(str){
for(var i=1;i<arguments.length;i++){
if(str.indexOf(arguments[i])>-1){
return true;
}
}
return false;
};
dojo.string.normalizeNewlines=function(text,_28e){
if(_28e=="\n"){
text=text.replace(/\r\n/g,"\n");
text=text.replace(/\r/g,"\n");
}else{
if(_28e=="\r"){
text=text.replace(/\r\n/g,"\r");
text=text.replace(/\n/g,"\r");
}else{
text=text.replace(/([^\r])\n/g,"$1\r\n").replace(/\r([^\n])/g,"\r\n$1");
}
}
return text;
};
dojo.string.splitEscaped=function(str,_290){
var _291=[];
for(var i=0,_293=0;i<str.length;i++){
if(str.charAt(i)=="\\"){
i++;
continue;
}
if(str.charAt(i)==_290){
_291.push(str.substring(_293,i));
_293=i+1;
}
}
_291.push(str.substr(_293));
return _291;
};
dojo.provide("dojo.string");
dojo.provide("dojo.io.common");
dojo.io.transports=[];
dojo.io.hdlrFuncNames=["load","error","timeout"];
dojo.io.Request=function(url,_295,_296,_297){
if((arguments.length==1)&&(arguments[0].constructor==Object)){
this.fromKwArgs(arguments[0]);
}else{
this.url=url;
if(_295){
this.mimetype=_295;
}
if(_296){
this.transport=_296;
}
if(arguments.length>=4){
this.changeUrl=_297;
}
}
};
dojo.lang.extend(dojo.io.Request,{url:"",mimetype:"text/plain",method:"GET",content:undefined,transport:undefined,changeUrl:undefined,formNode:undefined,sync:false,bindSuccess:false,useCache:false,preventCache:false,jsonFilter:function(_298){
if((this.mimetype=="text/json-comment-filtered")||(this.mimetype=="application/json-comment-filtered")){
var _299=_298.indexOf("/*");
var _29a=_298.lastIndexOf("*/");
if((_299==-1)||(_29a==-1)){
dojo.debug("your JSON wasn't comment filtered!");
return "";
}
return _298.substring(_299+2,_29a);
}
dojo.debug("please consider using a mimetype of text/json-comment-filtered to avoid potential security issues with JSON endpoints");
return _298;
},load:function(type,data,_29d,_29e){
},error:function(type,_2a0,_2a1,_2a2){
},timeout:function(type,_2a4,_2a5,_2a6){
},handle:function(type,data,_2a9,_2aa){
},timeoutSeconds:0,abort:function(){
},fromKwArgs:function(_2ab){
if(_2ab["url"]){
_2ab.url=_2ab.url.toString();
}
if(_2ab["formNode"]){
_2ab.formNode=dojo.byId(_2ab.formNode);
}
if(!_2ab["method"]&&_2ab["formNode"]&&_2ab["formNode"].method){
_2ab.method=_2ab["formNode"].method;
}
if(!_2ab["handle"]&&_2ab["handler"]){
_2ab.handle=_2ab.handler;
}
if(!_2ab["load"]&&_2ab["loaded"]){
_2ab.load=_2ab.loaded;
}
if(!_2ab["changeUrl"]&&_2ab["changeURL"]){
_2ab.changeUrl=_2ab.changeURL;
}
_2ab.encoding=dojo.lang.firstValued(_2ab["encoding"],djConfig["bindEncoding"],"");
_2ab.sendTransport=dojo.lang.firstValued(_2ab["sendTransport"],djConfig["ioSendTransport"],false);
var _2ac=dojo.lang.isFunction;
for(var x=0;x<dojo.io.hdlrFuncNames.length;x++){
var fn=dojo.io.hdlrFuncNames[x];
if(_2ab[fn]&&_2ac(_2ab[fn])){
continue;
}
if(_2ab["handle"]&&_2ac(_2ab["handle"])){
_2ab[fn]=_2ab.handle;
}
}
dojo.lang.mixin(this,_2ab);
}});
dojo.io.Error=function(msg,type,num){
this.message=msg;
this.type=type||"unknown";
this.number=num||0;
};
dojo.io.transports.addTransport=function(name){
this.push(name);
this[name]=dojo.io[name];
};
dojo.io.bind=function(_2b3){
if(!(_2b3 instanceof dojo.io.Request)){
try{
_2b3=new dojo.io.Request(_2b3);
}
catch(e){
dojo.debug(e);
}
}
var _2b4="";
if(_2b3["transport"]){
_2b4=_2b3["transport"];
if(!this[_2b4]){
dojo.io.sendBindError(_2b3,"No dojo.io.bind() transport with name '"+_2b3["transport"]+"'.");
return _2b3;
}
if(!this[_2b4].canHandle(_2b3)){
dojo.io.sendBindError(_2b3,"dojo.io.bind() transport with name '"+_2b3["transport"]+"' cannot handle this type of request.");
return _2b3;
}
}else{
for(var x=0;x<dojo.io.transports.length;x++){
var tmp=dojo.io.transports[x];
if((this[tmp])&&(this[tmp].canHandle(_2b3))){
_2b4=tmp;
break;
}
}
if(_2b4==""){
dojo.io.sendBindError(_2b3,"None of the loaded transports for dojo.io.bind()"+" can handle the request.");
return _2b3;
}
}
this[_2b4].bind(_2b3);
_2b3.bindSuccess=true;
return _2b3;
};
dojo.io.sendBindError=function(_2b7,_2b8){
if((typeof _2b7.error=="function"||typeof _2b7.handle=="function")&&(typeof setTimeout=="function"||typeof setTimeout=="object")){
var _2b9=new dojo.io.Error(_2b8);
setTimeout(function(){
_2b7[(typeof _2b7.error=="function")?"error":"handle"]("error",_2b9,null,_2b7);
},50);
}else{
dojo.raise(_2b8);
}
};
dojo.io.queueBind=function(_2ba){
if(!(_2ba instanceof dojo.io.Request)){
try{
_2ba=new dojo.io.Request(_2ba);
}
catch(e){
dojo.debug(e);
}
}
var _2bb=_2ba.load;
_2ba.load=function(){
dojo.io._queueBindInFlight=false;
var ret=_2bb.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
var _2bd=_2ba.error;
_2ba.error=function(){
dojo.io._queueBindInFlight=false;
var ret=_2bd.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
dojo.io._bindQueue.push(_2ba);
dojo.io._dispatchNextQueueBind();
return _2ba;
};
dojo.io._dispatchNextQueueBind=function(){
if(!dojo.io._queueBindInFlight){
dojo.io._queueBindInFlight=true;
if(dojo.io._bindQueue.length>0){
dojo.io.bind(dojo.io._bindQueue.shift());
}else{
dojo.io._queueBindInFlight=false;
}
}
};
dojo.io._bindQueue=[];
dojo.io._queueBindInFlight=false;
dojo.io.argsFromMap=function(map,_2c0,last){
var enc=/utf/i.test(_2c0||"")?encodeURIComponent:dojo.string.encodeAscii;
var _2c3=[];
var _2c4=new Object();
for(var name in map){
var _2c6=function(elt){
var val=enc(name)+"="+enc(elt);
_2c3[(last==name)?"push":"unshift"](val);
};
if(!_2c4[name]){
var _2c9=map[name];
if(dojo.lang.isArray(_2c9)){
dojo.lang.forEach(_2c9,_2c6);
}else{
_2c6(_2c9);
}
}
}
return _2c3.join("&");
};
dojo.io.setIFrameSrc=function(_2ca,src,_2cc){
try{
var r=dojo.render.html;
if(!_2cc){
if(r.safari){
_2ca.location=src;
}else{
frames[_2ca.name].location=src;
}
}else{
var idoc;
if(r.ie){
idoc=_2ca.contentWindow.document;
}else{
if(r.safari){
idoc=_2ca.document;
}else{
idoc=_2ca.contentWindow;
}
}
if(!idoc){
_2ca.location=src;
return;
}else{
idoc.location.replace(src);
}
}
}
catch(e){
dojo.debug(e);
dojo.debug("setIFrameSrc: "+e);
}
};
dojo.provide("dojo.dom");
dojo.dom.ELEMENT_NODE=1;
dojo.dom.ATTRIBUTE_NODE=2;
dojo.dom.TEXT_NODE=3;
dojo.dom.CDATA_SECTION_NODE=4;
dojo.dom.ENTITY_REFERENCE_NODE=5;
dojo.dom.ENTITY_NODE=6;
dojo.dom.PROCESSING_INSTRUCTION_NODE=7;
dojo.dom.COMMENT_NODE=8;
dojo.dom.DOCUMENT_NODE=9;
dojo.dom.DOCUMENT_TYPE_NODE=10;
dojo.dom.DOCUMENT_FRAGMENT_NODE=11;
dojo.dom.NOTATION_NODE=12;
dojo.dom.dojoml="http://www.dojotoolkit.org/2004/dojoml";
dojo.dom.xmlns={svg:"http://www.w3.org/2000/svg",smil:"http://www.w3.org/2001/SMIL20/",mml:"http://www.w3.org/1998/Math/MathML",cml:"http://www.xml-cml.org",xlink:"http://www.w3.org/1999/xlink",xhtml:"http://www.w3.org/1999/xhtml",xul:"http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul",xbl:"http://www.mozilla.org/xbl",fo:"http://www.w3.org/1999/XSL/Format",xsl:"http://www.w3.org/1999/XSL/Transform",xslt:"http://www.w3.org/1999/XSL/Transform",xi:"http://www.w3.org/2001/XInclude",xforms:"http://www.w3.org/2002/01/xforms",saxon:"http://icl.com/saxon",xalan:"http://xml.apache.org/xslt",xsd:"http://www.w3.org/2001/XMLSchema",dt:"http://www.w3.org/2001/XMLSchema-datatypes",xsi:"http://www.w3.org/2001/XMLSchema-instance",rdf:"http://www.w3.org/1999/02/22-rdf-syntax-ns#",rdfs:"http://www.w3.org/2000/01/rdf-schema#",dc:"http://purl.org/dc/elements/1.1/",dcq:"http://purl.org/dc/qualifiers/1.0","soap-env":"http://schemas.xmlsoap.org/soap/envelope/",wsdl:"http://schemas.xmlsoap.org/wsdl/",AdobeExtensions:"http://ns.adobe.com/AdobeSVGViewerExtensions/3.0/"};
dojo.dom.isNode=function(wh){
if(typeof Element=="function"){
try{
return wh instanceof Element;
}
catch(e){
}
}else{
return wh&&!isNaN(wh.nodeType);
}
};
dojo.dom.getUniqueId=function(){
var _2d0=dojo.doc();
do{
var id="dj_unique_"+(++arguments.callee._idIncrement);
}while(_2d0.getElementById(id));
return id;
};
dojo.dom.getUniqueId._idIncrement=0;
dojo.dom.firstElement=dojo.dom.getFirstChildElement=function(_2d2,_2d3){
var node=_2d2.firstChild;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.nextSibling;
}
if(_2d3&&node&&node.tagName&&node.tagName.toLowerCase()!=_2d3.toLowerCase()){
node=dojo.dom.nextElement(node,_2d3);
}
return node;
};
dojo.dom.lastElement=dojo.dom.getLastChildElement=function(_2d5,_2d6){
var node=_2d5.lastChild;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.previousSibling;
}
if(_2d6&&node&&node.tagName&&node.tagName.toLowerCase()!=_2d6.toLowerCase()){
node=dojo.dom.prevElement(node,_2d6);
}
return node;
};
dojo.dom.nextElement=dojo.dom.getNextSiblingElement=function(node,_2d9){
if(!node){
return null;
}
do{
node=node.nextSibling;
}while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE);
if(node&&_2d9&&_2d9.toLowerCase()!=node.tagName.toLowerCase()){
return dojo.dom.nextElement(node,_2d9);
}
return node;
};
dojo.dom.prevElement=dojo.dom.getPreviousSiblingElement=function(node,_2db){
if(!node){
return null;
}
if(_2db){
_2db=_2db.toLowerCase();
}
do{
node=node.previousSibling;
}while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE);
if(node&&_2db&&_2db.toLowerCase()!=node.tagName.toLowerCase()){
return dojo.dom.prevElement(node,_2db);
}
return node;
};
dojo.dom.moveChildren=function(_2dc,_2dd,trim){
var _2df=0;
if(trim){
while(_2dc.hasChildNodes()&&_2dc.firstChild.nodeType==dojo.dom.TEXT_NODE){
_2dc.removeChild(_2dc.firstChild);
}
while(_2dc.hasChildNodes()&&_2dc.lastChild.nodeType==dojo.dom.TEXT_NODE){
_2dc.removeChild(_2dc.lastChild);
}
}
while(_2dc.hasChildNodes()){
_2dd.appendChild(_2dc.firstChild);
_2df++;
}
return _2df;
};
dojo.dom.copyChildren=function(_2e0,_2e1,trim){
var _2e3=_2e0.cloneNode(true);
return this.moveChildren(_2e3,_2e1,trim);
};
dojo.dom.replaceChildren=function(node,_2e5){
var _2e6=[];
if(dojo.render.html.ie){
for(var i=0;i<node.childNodes.length;i++){
_2e6.push(node.childNodes[i]);
}
}
dojo.dom.removeChildren(node);
node.appendChild(_2e5);
for(var i=0;i<_2e6.length;i++){
dojo.dom.destroyNode(_2e6[i]);
}
};
dojo.dom.removeChildren=function(node){
var _2e9=node.childNodes.length;
while(node.hasChildNodes()){
dojo.dom.removeNode(node.firstChild);
}
return _2e9;
};
dojo.dom.replaceNode=function(node,_2eb){
return node.parentNode.replaceChild(_2eb,node);
};
dojo.dom.destroyNode=function(node){
if(node.parentNode){
node=dojo.dom.removeNode(node);
}
if(node.nodeType!=3){
if(dojo.evalObjPath("dojo.event.browser.clean",false)){
dojo.event.browser.clean(node);
}
if(dojo.render.html.ie){
node.outerHTML="";
}
}
};
dojo.dom.removeNode=function(node){
if(node&&node.parentNode){
return node.parentNode.removeChild(node);
}
};
dojo.dom.getAncestors=function(node,_2ef,_2f0){
var _2f1=[];
var _2f2=(_2ef&&(_2ef instanceof Function||typeof _2ef=="function"));
while(node){
if(!_2f2||_2ef(node)){
_2f1.push(node);
}
if(_2f0&&_2f1.length>0){
return _2f1[0];
}
node=node.parentNode;
}
if(_2f0){
return null;
}
return _2f1;
};
dojo.dom.getAncestorsByTag=function(node,tag,_2f5){
tag=tag.toLowerCase();
return dojo.dom.getAncestors(node,function(el){
return ((el.tagName)&&(el.tagName.toLowerCase()==tag));
},_2f5);
};
dojo.dom.getFirstAncestorByTag=function(node,tag){
return dojo.dom.getAncestorsByTag(node,tag,true);
};
dojo.dom.isDescendantOf=function(node,_2fa,_2fb){
if(_2fb&&node){
node=node.parentNode;
}
while(node){
if(node==_2fa){
return true;
}
node=node.parentNode;
}
return false;
};
dojo.dom.innerXML=function(node){
if(node.innerXML){
return node.innerXML;
}else{
if(node.xml){
return node.xml;
}else{
if(typeof XMLSerializer!="undefined"){
return (new XMLSerializer()).serializeToString(node);
}
}
}
};
dojo.dom.createDocument=function(){
var doc=null;
var _2fe=dojo.doc();
if(!dj_undef("ActiveXObject")){
var _2ff=["MSXML2","Microsoft","MSXML","MSXML3"];
for(var i=0;i<_2ff.length;i++){
try{
doc=new ActiveXObject(_2ff[i]+".XMLDOM");
}
catch(e){
}
if(doc){
break;
}
}
}else{
if((_2fe.implementation)&&(_2fe.implementation.createDocument)){
doc=_2fe.implementation.createDocument("","",null);
}
}
return doc;
};
dojo.dom.createDocumentFromText=function(str,_302){
if(!_302){
_302="text/xml";
}
if(!dj_undef("DOMParser")){
var _303=new DOMParser();
return _303.parseFromString(str,_302);
}else{
if(!dj_undef("ActiveXObject")){
var _304=dojo.dom.createDocument();
if(_304){
_304.async=false;
_304.loadXML(str);
return _304;
}else{
dojo.debug("toXml didn't work?");
}
}else{
var _305=dojo.doc();
if(_305.createElement){
var tmp=_305.createElement("xml");
tmp.innerHTML=str;
if(_305.implementation&&_305.implementation.createDocument){
var _307=_305.implementation.createDocument("foo","",null);
for(var i=0;i<tmp.childNodes.length;i++){
_307.importNode(tmp.childNodes.item(i),true);
}
return _307;
}
return ((tmp.document)&&(tmp.document.firstChild?tmp.document.firstChild:tmp));
}
}
}
return null;
};
dojo.dom.prependChild=function(node,_30a){
if(_30a.firstChild){
_30a.insertBefore(node,_30a.firstChild);
}else{
_30a.appendChild(node);
}
return true;
};
dojo.dom.insertBefore=function(node,ref,_30d){
if((_30d!=true)&&(node===ref||node.nextSibling===ref)){
return false;
}
var _30e=ref.parentNode;
_30e.insertBefore(node,ref);
return true;
};
dojo.dom.insertAfter=function(node,ref,_311){
var pn=ref.parentNode;
if(ref==pn.lastChild){
if((_311!=true)&&(node===ref)){
return false;
}
pn.appendChild(node);
}else{
return this.insertBefore(node,ref.nextSibling,_311);
}
return true;
};
dojo.dom.insertAtPosition=function(node,ref,_315){
if((!node)||(!ref)||(!_315)){
return false;
}
switch(_315.toLowerCase()){
case "before":
return dojo.dom.insertBefore(node,ref);
case "after":
return dojo.dom.insertAfter(node,ref);
case "first":
if(ref.firstChild){
return dojo.dom.insertBefore(node,ref.firstChild);
}else{
ref.appendChild(node);
return true;
}
break;
default:
ref.appendChild(node);
return true;
}
};
dojo.dom.insertAtIndex=function(node,_317,_318){
var _319=_317.childNodes;
if(!_319.length||_319.length==_318){
_317.appendChild(node);
return true;
}
if(_318==0){
return dojo.dom.prependChild(node,_317);
}
return dojo.dom.insertAfter(node,_319[_318-1]);
};
dojo.dom.textContent=function(node,text){
if(arguments.length>1){
var _31c=dojo.doc();
dojo.dom.replaceChildren(node,_31c.createTextNode(text));
return text;
}else{
if(node.textContent!=undefined){
return node.textContent;
}
var _31d="";
if(node==null){
return _31d;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
_31d+=dojo.dom.textContent(node.childNodes[i]);
break;
case 3:
case 2:
case 4:
_31d+=node.childNodes[i].nodeValue;
break;
default:
break;
}
}
return _31d;
}
};
dojo.dom.hasParent=function(node){
return Boolean(node&&node.parentNode&&dojo.dom.isNode(node.parentNode));
};
dojo.dom.isTag=function(node){
if(node&&node.tagName){
for(var i=1;i<arguments.length;i++){
if(node.tagName==String(arguments[i])){
return String(arguments[i]);
}
}
}
return "";
};
dojo.dom.setAttributeNS=function(elem,_323,_324,_325){
if(elem==null||((elem==undefined)&&(typeof elem=="undefined"))){
dojo.raise("No element given to dojo.dom.setAttributeNS");
}
if(!((elem.setAttributeNS==undefined)&&(typeof elem.setAttributeNS=="undefined"))){
elem.setAttributeNS(_323,_324,_325);
}else{
var _326=elem.ownerDocument;
var _327=_326.createNode(2,_324,_323);
_327.nodeValue=_325;
elem.setAttributeNode(_327);
}
};
dojo.provide("dojo.undo.browser");
try{
if((!djConfig["preventBackButtonFix"])&&(!dojo.hostenv.post_load_)){
document.write("<iframe style='border: 0px; width: 1px; height: 1px; position: absolute; bottom: 0px; right: 0px; visibility: visible;' name='djhistory' id='djhistory' src='"+(djConfig["dojoIframeHistoryUrl"]||dojo.hostenv.getBaseScriptUri()+"iframe_history.html")+"'></iframe>");
}
}
catch(e){
}
if(dojo.render.html.opera){
dojo.debug("Opera is not supported with dojo.undo.browser, so back/forward detection will not work.");
}
dojo.undo.browser={initialHref:(!dj_undef("window"))?window.location.href:"",initialHash:(!dj_undef("window"))?window.location.hash:"",moveForward:false,historyStack:[],forwardStack:[],historyIframe:null,bookmarkAnchor:null,locationTimer:null,setInitialState:function(args){
this.initialState=this._createState(this.initialHref,args,this.initialHash);
},addToHistory:function(args){
this.forwardStack=[];
var hash=null;
var url=null;
if(!this.historyIframe){
if(djConfig["useXDomain"]&&!djConfig["dojoIframeHistoryUrl"]){
dojo.debug("dojo.undo.browser: When using cross-domain Dojo builds,"+" please save iframe_history.html to your domain and set djConfig.dojoIframeHistoryUrl"+" to the path on your domain to iframe_history.html");
}
this.historyIframe=window.frames["djhistory"];
}
if(!this.bookmarkAnchor){
this.bookmarkAnchor=document.createElement("a");
dojo.body().appendChild(this.bookmarkAnchor);
this.bookmarkAnchor.style.display="none";
}
if(args["changeUrl"]){
hash="#"+((args["changeUrl"]!==true)?args["changeUrl"]:(new Date()).getTime());
if(this.historyStack.length==0&&this.initialState.urlHash==hash){
this.initialState=this._createState(url,args,hash);
return;
}else{
if(this.historyStack.length>0&&this.historyStack[this.historyStack.length-1].urlHash==hash){
this.historyStack[this.historyStack.length-1]=this._createState(url,args,hash);
return;
}
}
this.changingUrl=true;
setTimeout("window.location.href = '"+hash+"'; dojo.undo.browser.changingUrl = false;",1);
this.bookmarkAnchor.href=hash;
if(dojo.render.html.ie){
url=this._loadIframeHistory();
var _32c=args["back"]||args["backButton"]||args["handle"];
var tcb=function(_32e){
if(window.location.hash!=""){
setTimeout("window.location.href = '"+hash+"';",1);
}
_32c.apply(this,[_32e]);
};
if(args["back"]){
args.back=tcb;
}else{
if(args["backButton"]){
args.backButton=tcb;
}else{
if(args["handle"]){
args.handle=tcb;
}
}
}
var _32f=args["forward"]||args["forwardButton"]||args["handle"];
var tfw=function(_331){
if(window.location.hash!=""){
window.location.href=hash;
}
if(_32f){
_32f.apply(this,[_331]);
}
};
if(args["forward"]){
args.forward=tfw;
}else{
if(args["forwardButton"]){
args.forwardButton=tfw;
}else{
if(args["handle"]){
args.handle=tfw;
}
}
}
}else{
if(dojo.render.html.moz){
if(!this.locationTimer){
this.locationTimer=setInterval("dojo.undo.browser.checkLocation();",200);
}
}
}
}else{
url=this._loadIframeHistory();
}
this.historyStack.push(this._createState(url,args,hash));
},checkLocation:function(){
if(!this.changingUrl){
var hsl=this.historyStack.length;
if((window.location.hash==this.initialHash||window.location.href==this.initialHref)&&(hsl==1)){
this.handleBackButton();
return;
}
if(this.forwardStack.length>0){
if(this.forwardStack[this.forwardStack.length-1].urlHash==window.location.hash){
this.handleForwardButton();
return;
}
}
if((hsl>=2)&&(this.historyStack[hsl-2])){
if(this.historyStack[hsl-2].urlHash==window.location.hash){
this.handleBackButton();
return;
}
}
}
},iframeLoaded:function(evt,_334){
if(!dojo.render.html.opera){
var _335=this._getUrlQuery(_334.href);
if(_335==null){
if(this.historyStack.length==1){
this.handleBackButton();
}
return;
}
if(this.moveForward){
this.moveForward=false;
return;
}
if(this.historyStack.length>=2&&_335==this._getUrlQuery(this.historyStack[this.historyStack.length-2].url)){
this.handleBackButton();
}else{
if(this.forwardStack.length>0&&_335==this._getUrlQuery(this.forwardStack[this.forwardStack.length-1].url)){
this.handleForwardButton();
}
}
}
},handleBackButton:function(){
var _336=this.historyStack.pop();
if(!_336){
return;
}
var last=this.historyStack[this.historyStack.length-1];
if(!last&&this.historyStack.length==0){
last=this.initialState;
}
if(last){
if(last.kwArgs["back"]){
last.kwArgs["back"]();
}else{
if(last.kwArgs["backButton"]){
last.kwArgs["backButton"]();
}else{
if(last.kwArgs["handle"]){
last.kwArgs.handle("back");
}
}
}
}
this.forwardStack.push(_336);
},handleForwardButton:function(){
var last=this.forwardStack.pop();
if(!last){
return;
}
if(last.kwArgs["forward"]){
last.kwArgs.forward();
}else{
if(last.kwArgs["forwardButton"]){
last.kwArgs.forwardButton();
}else{
if(last.kwArgs["handle"]){
last.kwArgs.handle("forward");
}
}
}
this.historyStack.push(last);
},_createState:function(url,args,hash){
return {"url":url,"kwArgs":args,"urlHash":hash};
},_getUrlQuery:function(url){
var _33d=url.split("?");
if(_33d.length<2){
return null;
}else{
return _33d[1];
}
},_loadIframeHistory:function(){
var url=(djConfig["dojoIframeHistoryUrl"]||dojo.hostenv.getBaseScriptUri()+"iframe_history.html")+"?"+(new Date()).getTime();
this.moveForward=true;
dojo.io.setIFrameSrc(this.historyIframe,url,false);
return url;
}};
dojo.provide("dojo.io.BrowserIO");
if(!dj_undef("window")){
dojo.io.checkChildrenForFile=function(node){
var _340=false;
var _341=node.getElementsByTagName("input");
dojo.lang.forEach(_341,function(_342){
if(_340){
return;
}
if(_342.getAttribute("type")=="file"){
_340=true;
}
});
return _340;
};
dojo.io.formHasFile=function(_343){
return dojo.io.checkChildrenForFile(_343);
};
dojo.io.updateNode=function(node,_345){
node=dojo.byId(node);
var args=_345;
if(dojo.lang.isString(_345)){
args={url:_345};
}
args.mimetype="text/html";
args.load=function(t,d,e){
while(node.firstChild){
dojo.dom.destroyNode(node.firstChild);
}
node.innerHTML=d;
};
dojo.io.bind(args);
};
dojo.io.formFilter=function(node){
var type=(node.type||"").toLowerCase();
return !node.disabled&&node.name&&!dojo.lang.inArray(["file","submit","image","reset","button"],type);
};
dojo.io.encodeForm=function(_34c,_34d,_34e){
if((!_34c)||(!_34c.tagName)||(!_34c.tagName.toLowerCase()=="form")){
dojo.raise("Attempted to encode a non-form element.");
}
if(!_34e){
_34e=dojo.io.formFilter;
}
var enc=/utf/i.test(_34d||"")?encodeURIComponent:dojo.string.encodeAscii;
var _350=[];
for(var i=0;i<_34c.elements.length;i++){
var elm=_34c.elements[i];
if(!elm||elm.tagName.toLowerCase()=="fieldset"||!_34e(elm)){
continue;
}
var name=enc(elm.name);
var type=elm.type.toLowerCase();
if(type=="select-multiple"){
for(var j=0;j<elm.options.length;j++){
if(elm.options[j].selected){
_350.push(name+"="+enc(elm.options[j].value));
}
}
}else{
if(dojo.lang.inArray(["radio","checkbox"],type)){
if(elm.checked){
_350.push(name+"="+enc(elm.value));
}
}else{
_350.push(name+"="+enc(elm.value));
}
}
}
var _356=_34c.getElementsByTagName("input");
for(var i=0;i<_356.length;i++){
var _357=_356[i];
if(_357.type.toLowerCase()=="image"&&_357.form==_34c&&_34e(_357)){
var name=enc(_357.name);
_350.push(name+"="+enc(_357.value));
_350.push(name+".x=0");
_350.push(name+".y=0");
}
}
return _350.join("&")+"&";
};
dojo.io.FormBind=function(args){
this.bindArgs={};
if(args&&args.formNode){
this.init(args);
}else{
if(args){
this.init({formNode:args});
}
}
};
dojo.lang.extend(dojo.io.FormBind,{form:null,bindArgs:null,clickedButton:null,init:function(args){
var form=dojo.byId(args.formNode);
if(!form||!form.tagName||form.tagName.toLowerCase()!="form"){
throw new Error("FormBind: Couldn't apply, invalid form");
}else{
if(this.form==form){
return;
}else{
if(this.form){
throw new Error("FormBind: Already applied to a form");
}
}
}
dojo.lang.mixin(this.bindArgs,args);
this.form=form;
this.connect(form,"onsubmit","submit");
for(var i=0;i<form.elements.length;i++){
var node=form.elements[i];
if(node&&node.type&&dojo.lang.inArray(["submit","button"],node.type.toLowerCase())){
this.connect(node,"onclick","click");
}
}
var _35d=form.getElementsByTagName("input");
for(var i=0;i<_35d.length;i++){
var _35e=_35d[i];
if(_35e.type.toLowerCase()=="image"&&_35e.form==form){
this.connect(_35e,"onclick","click");
}
}
},onSubmit:function(form){
return true;
},submit:function(e){
e.preventDefault();
if(this.onSubmit(this.form)){
dojo.io.bind(dojo.lang.mixin(this.bindArgs,{formFilter:dojo.lang.hitch(this,"formFilter")}));
}
},click:function(e){
var node=e.currentTarget;
if(node.disabled){
return;
}
this.clickedButton=node;
},formFilter:function(node){
var type=(node.type||"").toLowerCase();
var _365=false;
if(node.disabled||!node.name){
_365=false;
}else{
if(dojo.lang.inArray(["submit","button","image"],type)){
if(!this.clickedButton){
this.clickedButton=node;
}
_365=node==this.clickedButton;
}else{
_365=!dojo.lang.inArray(["file","submit","reset","button"],type);
}
}
return _365;
},connect:function(_366,_367,_368){
if(dojo.evalObjPath("dojo.event.connect")){
dojo.event.connect(_366,_367,this,_368);
}else{
var fcn=dojo.lang.hitch(this,_368);
_366[_367]=function(e){
if(!e){
e=window.event;
}
if(!e.currentTarget){
e.currentTarget=e.srcElement;
}
if(!e.preventDefault){
e.preventDefault=function(){
window.event.returnValue=false;
};
}
fcn(e);
};
}
}});
dojo.io.XMLHTTPTransport=new function(){
var _36b=this;
var _36c={};
this.useCache=false;
this.preventCache=false;
function getCacheKey(url,_36e,_36f){
return url+"|"+_36e+"|"+_36f.toLowerCase();
}
function addToCache(url,_371,_372,http){
_36c[getCacheKey(url,_371,_372)]=http;
}
function getFromCache(url,_375,_376){
return _36c[getCacheKey(url,_375,_376)];
}
this.clearCache=function(){
_36c={};
};
function doLoad(_377,http,url,_37a,_37b){
if(((http.status>=200)&&(http.status<300))||(http.status==304)||(http.status==1223)||(location.protocol=="file:"&&(http.status==0||http.status==undefined))||(location.protocol=="chrome:"&&(http.status==0||http.status==undefined))){
var ret;
if(_377.method.toLowerCase()=="head"){
var _37d=http.getAllResponseHeaders();
ret={};
ret.toString=function(){
return _37d;
};
var _37e=_37d.split(/[\r\n]+/g);
for(var i=0;i<_37e.length;i++){
var pair=_37e[i].match(/^([^:]+)\s*:\s*(.+)$/i);
if(pair){
ret[pair[1]]=pair[2];
}
}
}else{
if(_377.mimetype=="text/javascript"){
try{
ret=dj_eval(http.responseText);
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=null;
}
}else{
if(_377.mimetype.substr(0,9)=="text/json"||_377.mimetype.substr(0,16)=="application/json"){
try{
ret=dj_eval("("+_377.jsonFilter(http.responseText)+")");
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=false;
}
}else{
if((_377.mimetype=="application/xml")||(_377.mimetype=="text/xml")){
ret=http.responseXML;
if(!ret||typeof ret=="string"||!http.getResponseHeader("Content-Type")){
ret=dojo.dom.createDocumentFromText(http.responseText);
}
}else{
ret=http.responseText;
}
}
}
}
if(_37b){
addToCache(url,_37a,_377.method,http);
}
_377[(typeof _377.load=="function")?"load":"handle"]("load",ret,http,_377);
}else{
var _381=new dojo.io.Error("XMLHttpTransport Error: "+http.status+" "+http.statusText);
_377[(typeof _377.error=="function")?"error":"handle"]("error",_381,http,_377);
}
}
function setHeaders(http,_383){
if(_383["headers"]){
for(var _384 in _383["headers"]){
if(_384.toLowerCase()=="content-type"&&!_383["contentType"]){
_383["contentType"]=_383["headers"][_384];
}else{
http.setRequestHeader(_384,_383["headers"][_384]);
}
}
}
}
this.inFlight=[];
this.inFlightTimer=null;
this.startWatchingInFlight=function(){
if(!this.inFlightTimer){
this.inFlightTimer=setTimeout("dojo.io.XMLHTTPTransport.watchInFlight();",10);
}
};
this.watchInFlight=function(){
var now=null;
if(!dojo.hostenv._blockAsync&&!_36b._blockAsync){
for(var x=this.inFlight.length-1;x>=0;x--){
try{
var tif=this.inFlight[x];
if(!tif||tif.http._aborted||!tif.http.readyState){
this.inFlight.splice(x,1);
continue;
}
if(4==tif.http.readyState){
this.inFlight.splice(x,1);
doLoad(tif.req,tif.http,tif.url,tif.query,tif.useCache);
}else{
if(tif.startTime){
if(!now){
now=(new Date()).getTime();
}
if(tif.startTime+(tif.req.timeoutSeconds*1000)<now){
if(typeof tif.http.abort=="function"){
tif.http.abort();
}
this.inFlight.splice(x,1);
tif.req[(typeof tif.req.timeout=="function")?"timeout":"handle"]("timeout",null,tif.http,tif.req);
}
}
}
}
catch(e){
try{
var _388=new dojo.io.Error("XMLHttpTransport.watchInFlight Error: "+e);
tif.req[(typeof tif.req.error=="function")?"error":"handle"]("error",_388,tif.http,tif.req);
}
catch(e2){
dojo.debug("XMLHttpTransport error callback failed: "+e2);
}
}
}
}
clearTimeout(this.inFlightTimer);
if(this.inFlight.length==0){
this.inFlightTimer=null;
return;
}
this.inFlightTimer=setTimeout("dojo.io.XMLHTTPTransport.watchInFlight();",10);
};
var _389=dojo.hostenv.getXmlhttpObject()?true:false;
this.canHandle=function(_38a){
var mlc=_38a["mimetype"].toLowerCase()||"";
return _389&&((dojo.lang.inArray(["text/plain","text/html","application/xml","text/xml","text/javascript"],mlc))||(mlc.substr(0,9)=="text/json"||mlc.substr(0,16)=="application/json"))&&!(_38a["formNode"]&&dojo.io.formHasFile(_38a["formNode"]));
};
this.multipartBoundary="45309FFF-BD65-4d50-99C9-36986896A96F";
this.bind=function(_38c){
if(!_38c["url"]){
if(!_38c["formNode"]&&(_38c["backButton"]||_38c["back"]||_38c["changeUrl"]||_38c["watchForURL"])&&(!djConfig.preventBackButtonFix)){
dojo.deprecated("Using dojo.io.XMLHTTPTransport.bind() to add to browser history without doing an IO request","Use dojo.undo.browser.addToHistory() instead.","0.4");
dojo.undo.browser.addToHistory(_38c);
return true;
}
}
var url=_38c.url;
var _38e="";
if(_38c["formNode"]){
var ta=_38c.formNode.getAttribute("action");
if((ta)&&(!_38c["url"])){
url=ta;
}
var tp=_38c.formNode.getAttribute("method");
if((tp)&&(!_38c["method"])){
_38c.method=tp;
}
_38e+=dojo.io.encodeForm(_38c.formNode,_38c.encoding,_38c["formFilter"]);
}
if(url.indexOf("#")>-1){
dojo.debug("Warning: dojo.io.bind: stripping hash values from url:",url);
url=url.split("#")[0];
}
if(_38c["file"]){
_38c.method="post";
}
if(!_38c["method"]){
_38c.method="get";
}
if(_38c.method.toLowerCase()=="get"){
_38c.multipart=false;
}else{
if(_38c["file"]){
_38c.multipart=true;
}else{
if(!_38c["multipart"]){
_38c.multipart=false;
}
}
}
if(_38c["backButton"]||_38c["back"]||_38c["changeUrl"]){
dojo.undo.browser.addToHistory(_38c);
}
var _391=_38c["content"]||{};
if(_38c.sendTransport){
_391["dojo.transport"]="xmlhttp";
}
do{
if(_38c.postContent){
_38e=_38c.postContent;
break;
}
if(_391){
_38e+=dojo.io.argsFromMap(_391,_38c.encoding);
}
if(_38c.method.toLowerCase()=="get"||!_38c.multipart){
break;
}
var t=[];
if(_38e.length){
var q=_38e.split("&");
for(var i=0;i<q.length;++i){
if(q[i].length){
var p=q[i].split("=");
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+p[0]+"\"","",p[1]);
}
}
}
if(_38c.file){
if(dojo.lang.isArray(_38c.file)){
for(var i=0;i<_38c.file.length;++i){
var o=_38c.file[i];
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}else{
var o=_38c.file;
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}
if(t.length){
t.push("--"+this.multipartBoundary+"--","");
_38e=t.join("\r\n");
}
}while(false);
var _397=_38c["sync"]?false:true;
var _398=_38c["preventCache"]||(this.preventCache==true&&_38c["preventCache"]!=false);
var _399=_38c["useCache"]==true||(this.useCache==true&&_38c["useCache"]!=false);
if(!_398&&_399){
var _39a=getFromCache(url,_38e,_38c.method);
if(_39a){
doLoad(_38c,_39a,url,_38e,false);
return;
}
}
var http=dojo.hostenv.getXmlhttpObject(_38c);
var _39c=false;
if(_397){
var _39d=this.inFlight.push({"req":_38c,"http":http,"url":url,"query":_38e,"useCache":_399,"startTime":_38c.timeoutSeconds?(new Date()).getTime():0});
this.startWatchingInFlight();
}else{
_36b._blockAsync=true;
}
if(_38c.method.toLowerCase()=="post"){
if(!_38c.user){
http.open("POST",url,_397);
}else{
http.open("POST",url,_397,_38c.user,_38c.password);
}
setHeaders(http,_38c);
http.setRequestHeader("Content-Type",_38c.multipart?("multipart/form-data; boundary="+this.multipartBoundary):(_38c.contentType||"application/x-www-form-urlencoded"));
try{
http.send(_38e);
}
catch(e){
if(typeof http.abort=="function"){
http.abort();
}
doLoad(_38c,{status:404},url,_38e,_399);
}
}else{
var _39e=url;
if(_38e!=""){
_39e+=(_39e.indexOf("?")>-1?"&":"?")+_38e;
}
if(_398){
_39e+=(dojo.string.endsWithAny(_39e,"?","&")?"":(_39e.indexOf("?")>-1?"&":"?"))+"dojo.preventCache="+new Date().valueOf();
}
if(!_38c.user){
http.open(_38c.method.toUpperCase(),_39e,_397);
}else{
http.open(_38c.method.toUpperCase(),_39e,_397,_38c.user,_38c.password);
}
setHeaders(http,_38c);
try{
http.send(null);
}
catch(e){
if(typeof http.abort=="function"){
http.abort();
}
doLoad(_38c,{status:404},url,_38e,_399);
}
}
if(!_397){
doLoad(_38c,http,url,_38e,_399);
_36b._blockAsync=false;
}
_38c.abort=function(){
try{
http._aborted=true;
}
catch(e){
}
return http.abort();
};
return;
};
dojo.io.transports.addTransport("XMLHTTPTransport");
};
}
dojo.provide("dojo.io.cookie");
dojo.io.cookie.setCookie=function(name,_3a0,days,path,_3a3,_3a4){
var _3a5=-1;
if((typeof days=="number")&&(days>=0)){
var d=new Date();
d.setTime(d.getTime()+(days*24*60*60*1000));
_3a5=d.toGMTString();
}
_3a0=escape(_3a0);
document.cookie=name+"="+_3a0+";"+(_3a5!=-1?" expires="+_3a5+";":"")+(path?"path="+path:"")+(_3a3?"; domain="+_3a3:"")+(_3a4?"; secure":"");
};
dojo.io.cookie.set=dojo.io.cookie.setCookie;
dojo.io.cookie.getCookie=function(name){
var idx=document.cookie.lastIndexOf(name+"=");
if(idx==-1){
return null;
}
var _3a9=document.cookie.substring(idx+name.length+1);
var end=_3a9.indexOf(";");
if(end==-1){
end=_3a9.length;
}
_3a9=_3a9.substring(0,end);
_3a9=unescape(_3a9);
return _3a9;
};
dojo.io.cookie.get=dojo.io.cookie.getCookie;
dojo.io.cookie.deleteCookie=function(name){
dojo.io.cookie.setCookie(name,"-",0);
};
dojo.io.cookie.setObjectCookie=function(name,obj,days,path,_3b0,_3b1,_3b2){
if(arguments.length==5){
_3b2=_3b0;
_3b0=null;
_3b1=null;
}
var _3b3=[],_3b4,_3b5="";
if(!_3b2){
_3b4=dojo.io.cookie.getObjectCookie(name);
}
if(days>=0){
if(!_3b4){
_3b4={};
}
for(var prop in obj){
if(obj[prop]==null){
delete _3b4[prop];
}else{
if((typeof obj[prop]=="string")||(typeof obj[prop]=="number")){
_3b4[prop]=obj[prop];
}
}
}
prop=null;
for(var prop in _3b4){
_3b3.push(escape(prop)+"="+escape(_3b4[prop]));
}
_3b5=_3b3.join("&");
}
dojo.io.cookie.setCookie(name,_3b5,days,path,_3b0,_3b1);
};
dojo.io.cookie.getObjectCookie=function(name){
var _3b8=null,_3b9=dojo.io.cookie.getCookie(name);
if(_3b9){
_3b8={};
var _3ba=_3b9.split("&");
for(var i=0;i<_3ba.length;i++){
var pair=_3ba[i].split("=");
var _3bd=pair[1];
if(isNaN(_3bd)){
_3bd=unescape(pair[1]);
}
_3b8[unescape(pair[0])]=_3bd;
}
}
return _3b8;
};
dojo.io.cookie.isSupported=function(){
if(typeof navigator.cookieEnabled!="boolean"){
dojo.io.cookie.setCookie("__TestingYourBrowserForCookieSupport__","CookiesAllowed",90,null);
var _3be=dojo.io.cookie.getCookie("__TestingYourBrowserForCookieSupport__");
navigator.cookieEnabled=(_3be=="CookiesAllowed");
if(navigator.cookieEnabled){
this.deleteCookie("__TestingYourBrowserForCookieSupport__");
}
}
return navigator.cookieEnabled;
};
if(!dojo.io.cookies){
dojo.io.cookies=dojo.io.cookie;
}
dojo.kwCompoundRequire({common:["dojo.io.common"],rhino:["dojo.io.RhinoIO"],browser:["dojo.io.BrowserIO","dojo.io.cookie"],dashboard:["dojo.io.BrowserIO","dojo.io.cookie"]});
dojo.provide("dojo.io.*");
dojo.provide("dojo.io.BrowserIO");
if(!dj_undef("window")){
dojo.io.checkChildrenForFile=function(node){
var _3c0=false;
var _3c1=node.getElementsByTagName("input");
dojo.lang.forEach(_3c1,function(_3c2){
if(_3c0){
return;
}
if(_3c2.getAttribute("type")=="file"){
_3c0=true;
}
});
return _3c0;
};
dojo.io.formHasFile=function(_3c3){
return dojo.io.checkChildrenForFile(_3c3);
};
dojo.io.updateNode=function(node,_3c5){
node=dojo.byId(node);
var args=_3c5;
if(dojo.lang.isString(_3c5)){
args={url:_3c5};
}
args.mimetype="text/html";
args.load=function(t,d,e){
while(node.firstChild){
dojo.dom.destroyNode(node.firstChild);
}
node.innerHTML=d;
};
dojo.io.bind(args);
};
dojo.io.formFilter=function(node){
var type=(node.type||"").toLowerCase();
return !node.disabled&&node.name&&!dojo.lang.inArray(["file","submit","image","reset","button"],type);
};
dojo.io.encodeForm=function(_3cc,_3cd,_3ce){
if((!_3cc)||(!_3cc.tagName)||(!_3cc.tagName.toLowerCase()=="form")){
dojo.raise("Attempted to encode a non-form element.");
}
if(!_3ce){
_3ce=dojo.io.formFilter;
}
var enc=/utf/i.test(_3cd||"")?encodeURIComponent:dojo.string.encodeAscii;
var _3d0=[];
for(var i=0;i<_3cc.elements.length;i++){
var elm=_3cc.elements[i];
if(!elm||elm.tagName.toLowerCase()=="fieldset"||!_3ce(elm)){
continue;
}
var name=enc(elm.name);
var type=elm.type.toLowerCase();
if(type=="select-multiple"){
for(var j=0;j<elm.options.length;j++){
if(elm.options[j].selected){
_3d0.push(name+"="+enc(elm.options[j].value));
}
}
}else{
if(dojo.lang.inArray(["radio","checkbox"],type)){
if(elm.checked){
_3d0.push(name+"="+enc(elm.value));
}
}else{
_3d0.push(name+"="+enc(elm.value));
}
}
}
var _3d6=_3cc.getElementsByTagName("input");
for(var i=0;i<_3d6.length;i++){
var _3d7=_3d6[i];
if(_3d7.type.toLowerCase()=="image"&&_3d7.form==_3cc&&_3ce(_3d7)){
var name=enc(_3d7.name);
_3d0.push(name+"="+enc(_3d7.value));
_3d0.push(name+".x=0");
_3d0.push(name+".y=0");
}
}
return _3d0.join("&")+"&";
};
dojo.io.FormBind=function(args){
this.bindArgs={};
if(args&&args.formNode){
this.init(args);
}else{
if(args){
this.init({formNode:args});
}
}
};
dojo.lang.extend(dojo.io.FormBind,{form:null,bindArgs:null,clickedButton:null,init:function(args){
var form=dojo.byId(args.formNode);
if(!form||!form.tagName||form.tagName.toLowerCase()!="form"){
throw new Error("FormBind: Couldn't apply, invalid form");
}else{
if(this.form==form){
return;
}else{
if(this.form){
throw new Error("FormBind: Already applied to a form");
}
}
}
dojo.lang.mixin(this.bindArgs,args);
this.form=form;
this.connect(form,"onsubmit","submit");
for(var i=0;i<form.elements.length;i++){
var node=form.elements[i];
if(node&&node.type&&dojo.lang.inArray(["submit","button"],node.type.toLowerCase())){
this.connect(node,"onclick","click");
}
}
var _3dd=form.getElementsByTagName("input");
for(var i=0;i<_3dd.length;i++){
var _3de=_3dd[i];
if(_3de.type.toLowerCase()=="image"&&_3de.form==form){
this.connect(_3de,"onclick","click");
}
}
},onSubmit:function(form){
return true;
},submit:function(e){
e.preventDefault();
if(this.onSubmit(this.form)){
dojo.io.bind(dojo.lang.mixin(this.bindArgs,{formFilter:dojo.lang.hitch(this,"formFilter")}));
}
},click:function(e){
var node=e.currentTarget;
if(node.disabled){
return;
}
this.clickedButton=node;
},formFilter:function(node){
var type=(node.type||"").toLowerCase();
var _3e5=false;
if(node.disabled||!node.name){
_3e5=false;
}else{
if(dojo.lang.inArray(["submit","button","image"],type)){
if(!this.clickedButton){
this.clickedButton=node;
}
_3e5=node==this.clickedButton;
}else{
_3e5=!dojo.lang.inArray(["file","submit","reset","button"],type);
}
}
return _3e5;
},connect:function(_3e6,_3e7,_3e8){
if(dojo.evalObjPath("dojo.event.connect")){
dojo.event.connect(_3e6,_3e7,this,_3e8);
}else{
var fcn=dojo.lang.hitch(this,_3e8);
_3e6[_3e7]=function(e){
if(!e){
e=window.event;
}
if(!e.currentTarget){
e.currentTarget=e.srcElement;
}
if(!e.preventDefault){
e.preventDefault=function(){
window.event.returnValue=false;
};
}
fcn(e);
};
}
}});
dojo.io.XMLHTTPTransport=new function(){
var _3eb=this;
var _3ec={};
this.useCache=false;
this.preventCache=false;
function getCacheKey(url,_3ee,_3ef){
return url+"|"+_3ee+"|"+_3ef.toLowerCase();
}
function addToCache(url,_3f1,_3f2,http){
_3ec[getCacheKey(url,_3f1,_3f2)]=http;
}
function getFromCache(url,_3f5,_3f6){
return _3ec[getCacheKey(url,_3f5,_3f6)];
}
this.clearCache=function(){
_3ec={};
};
function doLoad(_3f7,http,url,_3fa,_3fb){
if(((http.status>=200)&&(http.status<300))||(http.status==304)||(http.status==1223)||(location.protocol=="file:"&&(http.status==0||http.status==undefined))||(location.protocol=="chrome:"&&(http.status==0||http.status==undefined))){
var ret;
if(_3f7.method.toLowerCase()=="head"){
var _3fd=http.getAllResponseHeaders();
ret={};
ret.toString=function(){
return _3fd;
};
var _3fe=_3fd.split(/[\r\n]+/g);
for(var i=0;i<_3fe.length;i++){
var pair=_3fe[i].match(/^([^:]+)\s*:\s*(.+)$/i);
if(pair){
ret[pair[1]]=pair[2];
}
}
}else{
if(_3f7.mimetype=="text/javascript"){
try{
ret=dj_eval(http.responseText);
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=null;
}
}else{
if(_3f7.mimetype.substr(0,9)=="text/json"||_3f7.mimetype.substr(0,16)=="application/json"){
try{
ret=dj_eval("("+_3f7.jsonFilter(http.responseText)+")");
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=false;
}
}else{
if((_3f7.mimetype=="application/xml")||(_3f7.mimetype=="text/xml")){
ret=http.responseXML;
if(!ret||typeof ret=="string"||!http.getResponseHeader("Content-Type")){
ret=dojo.dom.createDocumentFromText(http.responseText);
}
}else{
ret=http.responseText;
}
}
}
}
if(_3fb){
addToCache(url,_3fa,_3f7.method,http);
}
_3f7[(typeof _3f7.load=="function")?"load":"handle"]("load",ret,http,_3f7);
}else{
var _401=new dojo.io.Error("XMLHttpTransport Error: "+http.status+" "+http.statusText);
_3f7[(typeof _3f7.error=="function")?"error":"handle"]("error",_401,http,_3f7);
}
}
function setHeaders(http,_403){
if(_403["headers"]){
for(var _404 in _403["headers"]){
if(_404.toLowerCase()=="content-type"&&!_403["contentType"]){
_403["contentType"]=_403["headers"][_404];
}else{
http.setRequestHeader(_404,_403["headers"][_404]);
}
}
}
}
this.inFlight=[];
this.inFlightTimer=null;
this.startWatchingInFlight=function(){
if(!this.inFlightTimer){
this.inFlightTimer=setTimeout("dojo.io.XMLHTTPTransport.watchInFlight();",10);
}
};
this.watchInFlight=function(){
var now=null;
if(!dojo.hostenv._blockAsync&&!_3eb._blockAsync){
for(var x=this.inFlight.length-1;x>=0;x--){
try{
var tif=this.inFlight[x];
if(!tif||tif.http._aborted||!tif.http.readyState){
this.inFlight.splice(x,1);
continue;
}
if(4==tif.http.readyState){
this.inFlight.splice(x,1);
doLoad(tif.req,tif.http,tif.url,tif.query,tif.useCache);
}else{
if(tif.startTime){
if(!now){
now=(new Date()).getTime();
}
if(tif.startTime+(tif.req.timeoutSeconds*1000)<now){
if(typeof tif.http.abort=="function"){
tif.http.abort();
}
this.inFlight.splice(x,1);
tif.req[(typeof tif.req.timeout=="function")?"timeout":"handle"]("timeout",null,tif.http,tif.req);
}
}
}
}
catch(e){
try{
var _408=new dojo.io.Error("XMLHttpTransport.watchInFlight Error: "+e);
tif.req[(typeof tif.req.error=="function")?"error":"handle"]("error",_408,tif.http,tif.req);
}
catch(e2){
dojo.debug("XMLHttpTransport error callback failed: "+e2);
}
}
}
}
clearTimeout(this.inFlightTimer);
if(this.inFlight.length==0){
this.inFlightTimer=null;
return;
}
this.inFlightTimer=setTimeout("dojo.io.XMLHTTPTransport.watchInFlight();",10);
};
var _409=dojo.hostenv.getXmlhttpObject()?true:false;
this.canHandle=function(_40a){
var mlc=_40a["mimetype"].toLowerCase()||"";
return _409&&((dojo.lang.inArray(["text/plain","text/html","application/xml","text/xml","text/javascript"],mlc))||(mlc.substr(0,9)=="text/json"||mlc.substr(0,16)=="application/json"))&&!(_40a["formNode"]&&dojo.io.formHasFile(_40a["formNode"]));
};
this.multipartBoundary="45309FFF-BD65-4d50-99C9-36986896A96F";
this.bind=function(_40c){
if(!_40c["url"]){
if(!_40c["formNode"]&&(_40c["backButton"]||_40c["back"]||_40c["changeUrl"]||_40c["watchForURL"])&&(!djConfig.preventBackButtonFix)){
dojo.deprecated("Using dojo.io.XMLHTTPTransport.bind() to add to browser history without doing an IO request","Use dojo.undo.browser.addToHistory() instead.","0.4");
dojo.undo.browser.addToHistory(_40c);
return true;
}
}
var url=_40c.url;
var _40e="";
if(_40c["formNode"]){
var ta=_40c.formNode.getAttribute("action");
if((ta)&&(!_40c["url"])){
url=ta;
}
var tp=_40c.formNode.getAttribute("method");
if((tp)&&(!_40c["method"])){
_40c.method=tp;
}
_40e+=dojo.io.encodeForm(_40c.formNode,_40c.encoding,_40c["formFilter"]);
}
if(url.indexOf("#")>-1){
dojo.debug("Warning: dojo.io.bind: stripping hash values from url:",url);
url=url.split("#")[0];
}
if(_40c["file"]){
_40c.method="post";
}
if(!_40c["method"]){
_40c.method="get";
}
if(_40c.method.toLowerCase()=="get"){
_40c.multipart=false;
}else{
if(_40c["file"]){
_40c.multipart=true;
}else{
if(!_40c["multipart"]){
_40c.multipart=false;
}
}
}
if(_40c["backButton"]||_40c["back"]||_40c["changeUrl"]){
dojo.undo.browser.addToHistory(_40c);
}
var _411=_40c["content"]||{};
if(_40c.sendTransport){
_411["dojo.transport"]="xmlhttp";
}
do{
if(_40c.postContent){
_40e=_40c.postContent;
break;
}
if(_411){
_40e+=dojo.io.argsFromMap(_411,_40c.encoding);
}
if(_40c.method.toLowerCase()=="get"||!_40c.multipart){
break;
}
var t=[];
if(_40e.length){
var q=_40e.split("&");
for(var i=0;i<q.length;++i){
if(q[i].length){
var p=q[i].split("=");
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+p[0]+"\"","",p[1]);
}
}
}
if(_40c.file){
if(dojo.lang.isArray(_40c.file)){
for(var i=0;i<_40c.file.length;++i){
var o=_40c.file[i];
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}else{
var o=_40c.file;
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}
if(t.length){
t.push("--"+this.multipartBoundary+"--","");
_40e=t.join("\r\n");
}
}while(false);
var _417=_40c["sync"]?false:true;
var _418=_40c["preventCache"]||(this.preventCache==true&&_40c["preventCache"]!=false);
var _419=_40c["useCache"]==true||(this.useCache==true&&_40c["useCache"]!=false);
if(!_418&&_419){
var _41a=getFromCache(url,_40e,_40c.method);
if(_41a){
doLoad(_40c,_41a,url,_40e,false);
return;
}
}
var http=dojo.hostenv.getXmlhttpObject(_40c);
var _41c=false;
if(_417){
var _41d=this.inFlight.push({"req":_40c,"http":http,"url":url,"query":_40e,"useCache":_419,"startTime":_40c.timeoutSeconds?(new Date()).getTime():0});
this.startWatchingInFlight();
}else{
_3eb._blockAsync=true;
}
if(_40c.method.toLowerCase()=="post"){
if(!_40c.user){
http.open("POST",url,_417);
}else{
http.open("POST",url,_417,_40c.user,_40c.password);
}
setHeaders(http,_40c);
http.setRequestHeader("Content-Type",_40c.multipart?("multipart/form-data; boundary="+this.multipartBoundary):(_40c.contentType||"application/x-www-form-urlencoded"));
try{
http.send(_40e);
}
catch(e){
if(typeof http.abort=="function"){
http.abort();
}
doLoad(_40c,{status:404},url,_40e,_419);
}
}else{
var _41e=url;
if(_40e!=""){
_41e+=(_41e.indexOf("?")>-1?"&":"?")+_40e;
}
if(_418){
_41e+=(dojo.string.endsWithAny(_41e,"?","&")?"":(_41e.indexOf("?")>-1?"&":"?"))+"dojo.preventCache="+new Date().valueOf();
}
if(!_40c.user){
http.open(_40c.method.toUpperCase(),_41e,_417);
}else{
http.open(_40c.method.toUpperCase(),_41e,_417,_40c.user,_40c.password);
}
setHeaders(http,_40c);
try{
http.send(null);
}
catch(e){
if(typeof http.abort=="function"){
http.abort();
}
doLoad(_40c,{status:404},url,_40e,_419);
}
}
if(!_417){
doLoad(_40c,http,url,_40e,_419);
_3eb._blockAsync=false;
}
_40c.abort=function(){
try{
http._aborted=true;
}
catch(e){
}
return http.abort();
};
return;
};
dojo.io.transports.addTransport("XMLHTTPTransport");
};
}
dojo.provide("dojo.AdapterRegistry");
dojo.AdapterRegistry=function(_41f){
this.pairs=[];
this.returnWrappers=_41f||false;
};
dojo.lang.extend(dojo.AdapterRegistry,{register:function(name,_421,wrap,_423,_424){
var type=(_424)?"unshift":"push";
this.pairs[type]([name,_421,wrap,_423]);
},match:function(){
for(var i=0;i<this.pairs.length;i++){
var pair=this.pairs[i];
if(pair[1].apply(this,arguments)){
if((pair[3])||(this.returnWrappers)){
return pair[2];
}else{
return pair[2].apply(this,arguments);
}
}
}
throw new Error("No match found");
},unregister:function(name){
for(var i=0;i<this.pairs.length;i++){
var pair=this.pairs[i];
if(pair[0]==name){
this.pairs.splice(i,1);
return true;
}
}
return false;
}});
dojo.provide("dojo.json");
dojo.json={jsonRegistry:new dojo.AdapterRegistry(),register:function(name,_42c,wrap,_42e){
dojo.json.jsonRegistry.register(name,_42c,wrap,_42e);
},evalJson:function(json){
try{
return eval("("+json+")");
}
catch(e){
dojo.debug(e);
return json;
}
},serialize:function(o){
var _431=typeof (o);
if(_431=="undefined"){
return "undefined";
}else{
if((_431=="number")||(_431=="boolean")){
return o+"";
}else{
if(o===null){
return "null";
}
}
}
if(_431=="string"){
return dojo.string.escapeString(o);
}
var me=arguments.callee;
var _433;
if(typeof (o.__json__)=="function"){
_433=o.__json__();
if(o!==_433){
return me(_433);
}
}
if(typeof (o.json)=="function"){
_433=o.json();
if(o!==_433){
return me(_433);
}
}
if(_431!="function"&&typeof (o.length)=="number"){
var res=[];
for(var i=0;i<o.length;i++){
var val=me(o[i]);
if(typeof (val)!="string"){
val="undefined";
}
res.push(val);
}
return "["+res.join(",")+"]";
}
try{
window.o=o;
_433=dojo.json.jsonRegistry.match(o);
return me(_433);
}
catch(e){
}
if(_431=="function"){
return null;
}
res=[];
for(var k in o){
var _438;
if(typeof (k)=="number"){
_438="\""+k+"\"";
}else{
if(typeof (k)=="string"){
_438=dojo.string.escapeString(k);
}else{
continue;
}
}
val=me(o[k]);
if(typeof (val)!="string"){
continue;
}
res.push(_438+":"+val);
}
return "{"+res.join(",")+"}";
}};
dojo.provide("dojo.html.common");
dojo.lang.mixin(dojo.html,dojo.dom);
dojo.html.body=function(){
dojo.deprecated("dojo.html.body() moved to dojo.body()","0.5");
return dojo.body();
};
dojo.html.getEventTarget=function(evt){
if(!evt){
evt=dojo.global().event||{};
}
var t=(evt.srcElement?evt.srcElement:(evt.target?evt.target:null));
while((t)&&(t.nodeType!=1)){
t=t.parentNode;
}
return t;
};
dojo.html.getViewport=function(){
var _43b=dojo.global();
var _43c=dojo.doc();
var w=0;
var h=0;
if(dojo.render.html.mozilla){
w=_43c.documentElement.clientWidth;
h=_43b.innerHeight;
}else{
if(!dojo.render.html.opera&&_43b.innerWidth){
w=_43b.innerWidth;
h=_43b.innerHeight;
}else{
if(!dojo.render.html.opera&&dojo.exists(_43c,"documentElement.clientWidth")){
var w2=_43c.documentElement.clientWidth;
if(!w||w2&&w2<w){
w=w2;
}
h=_43c.documentElement.clientHeight;
}else{
if(dojo.body().clientWidth){
w=dojo.body().clientWidth;
h=dojo.body().clientHeight;
}
}
}
}
return {width:w,height:h};
};
dojo.html.getScroll=function(){
var _440=dojo.global();
var _441=dojo.doc();
var top=_440.pageYOffset||_441.documentElement.scrollTop||dojo.body().scrollTop||0;
var left=_440.pageXOffset||_441.documentElement.scrollLeft||dojo.body().scrollLeft||0;
return {top:top,left:left,offset:{x:left,y:top}};
};
dojo.html.getParentByType=function(node,type){
var _446=dojo.doc();
var _447=dojo.byId(node);
type=type.toLowerCase();
while((_447)&&(_447.nodeName.toLowerCase()!=type)){
if(_447==(_446["body"]||_446["documentElement"])){
return null;
}
_447=_447.parentNode;
}
return _447;
};
dojo.html.getAttribute=function(node,attr){
node=dojo.byId(node);
if((!node)||(!node.getAttribute)){
return null;
}
var ta=typeof attr=="string"?attr:new String(attr);
var v=node.getAttribute(ta.toUpperCase());
if((v)&&(typeof v=="string")&&(v!="")){
return v;
}
if(v&&v.value){
return v.value;
}
if((node.getAttributeNode)&&(node.getAttributeNode(ta))){
return (node.getAttributeNode(ta)).value;
}else{
if(node.getAttribute(ta)){
return node.getAttribute(ta);
}else{
if(node.getAttribute(ta.toLowerCase())){
return node.getAttribute(ta.toLowerCase());
}
}
}
return null;
};
dojo.html.hasAttribute=function(node,attr){
return dojo.html.getAttribute(dojo.byId(node),attr)?true:false;
};
dojo.html.getCursorPosition=function(e){
e=e||dojo.global().event;
var _44f={x:0,y:0};
if(e.pageX||e.pageY){
_44f.x=e.pageX;
_44f.y=e.pageY;
}else{
var de=dojo.doc().documentElement;
var db=dojo.body();
_44f.x=e.clientX+((de||db)["scrollLeft"])-((de||db)["clientLeft"]);
_44f.y=e.clientY+((de||db)["scrollTop"])-((de||db)["clientTop"]);
}
return _44f;
};
dojo.html.isTag=function(node){
node=dojo.byId(node);
if(node&&node.tagName){
for(var i=1;i<arguments.length;i++){
if(node.tagName.toLowerCase()==String(arguments[i]).toLowerCase()){
return String(arguments[i]).toLowerCase();
}
}
}
return "";
};
if(dojo.render.html.ie&&!dojo.render.html.ie70){
if(window.location.href.substr(0,6).toLowerCase()!="https:"){
(function(){
var _454=dojo.doc().createElement("script");
_454.src="javascript:'dojo.html.createExternalElement=function(doc, tag){ return doc.createElement(tag); }'";
dojo.doc().getElementsByTagName("head")[0].appendChild(_454);
})();
}
}else{
dojo.html.createExternalElement=function(doc,tag){
return doc.createElement(tag);
};
}
dojo.html._callDeprecated=function(_457,_458,args,_45a,_45b){
dojo.deprecated("dojo.html."+_457,"replaced by dojo.html."+_458+"("+(_45a?"node, {"+_45a+": "+_45a+"}":"")+")"+(_45b?"."+_45b:""),"0.5");
var _45c=[];
if(_45a){
var _45d={};
_45d[_45a]=args[1];
_45c.push(args[0]);
_45c.push(_45d);
}else{
_45c=args;
}
var ret=dojo.html[_458].apply(dojo.html,args);
if(_45b){
return ret[_45b];
}else{
return ret;
}
};
dojo.html.getViewportWidth=function(){
return dojo.html._callDeprecated("getViewportWidth","getViewport",arguments,null,"width");
};
dojo.html.getViewportHeight=function(){
return dojo.html._callDeprecated("getViewportHeight","getViewport",arguments,null,"height");
};
dojo.html.getViewportSize=function(){
return dojo.html._callDeprecated("getViewportSize","getViewport",arguments);
};
dojo.html.getScrollTop=function(){
return dojo.html._callDeprecated("getScrollTop","getScroll",arguments,null,"top");
};
dojo.html.getScrollLeft=function(){
return dojo.html._callDeprecated("getScrollLeft","getScroll",arguments,null,"left");
};
dojo.html.getScrollOffset=function(){
return dojo.html._callDeprecated("getScrollOffset","getScroll",arguments,null,"offset");
};
dojo.provide("dojo.uri.Uri");
dojo.uri=new function(){
this.dojoUri=function(uri){
return new dojo.uri.Uri(dojo.hostenv.getBaseScriptUri(),uri);
};
this.moduleUri=function(_460,uri){
var loc=dojo.hostenv.getModuleSymbols(_460).join("/");
if(!loc){
return null;
}
if(loc.lastIndexOf("/")!=loc.length-1){
loc+="/";
}
var _463=loc.indexOf(":");
var _464=loc.indexOf("/");
if(loc.charAt(0)!="/"&&(_463==-1||_463>_464)){
loc=dojo.hostenv.getBaseScriptUri()+loc;
}
return new dojo.uri.Uri(loc,uri);
};
this.Uri=function(){
var uri=arguments[0];
for(var i=1;i<arguments.length;i++){
if(!arguments[i]){
continue;
}
var _467=new dojo.uri.Uri(arguments[i].toString());
var _468=new dojo.uri.Uri(uri.toString());
if((_467.path=="")&&(_467.scheme==null)&&(_467.authority==null)&&(_467.query==null)){
if(_467.fragment!=null){
_468.fragment=_467.fragment;
}
_467=_468;
}else{
if(_467.scheme==null){
_467.scheme=_468.scheme;
if(_467.authority==null){
_467.authority=_468.authority;
if(_467.path.charAt(0)!="/"){
var path=_468.path.substring(0,_468.path.lastIndexOf("/")+1)+_467.path;
var segs=path.split("/");
for(var j=0;j<segs.length;j++){
if(segs[j]=="."){
if(j==segs.length-1){
segs[j]="";
}else{
segs.splice(j,1);
j--;
}
}else{
if(j>0&&!(j==1&&segs[0]=="")&&segs[j]==".."&&segs[j-1]!=".."){
if(j==segs.length-1){
segs.splice(j,1);
segs[j-1]="";
}else{
segs.splice(j-1,2);
j-=2;
}
}
}
}
_467.path=segs.join("/");
}
}
}
}
uri="";
if(_467.scheme!=null){
uri+=_467.scheme+":";
}
if(_467.authority!=null){
uri+="//"+_467.authority;
}
uri+=_467.path;
if(_467.query!=null){
uri+="?"+_467.query;
}
if(_467.fragment!=null){
uri+="#"+_467.fragment;
}
}
this.uri=uri.toString();
var _46c="^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?$";
var r=this.uri.match(new RegExp(_46c));
this.scheme=r[2]||(r[1]?"":null);
this.authority=r[4]||(r[3]?"":null);
this.path=r[5];
this.query=r[7]||(r[6]?"":null);
this.fragment=r[9]||(r[8]?"":null);
if(this.authority!=null){
_46c="^((([^:]+:)?([^@]+))@)?([^:]*)(:([0-9]+))?$";
r=this.authority.match(new RegExp(_46c));
this.user=r[3]||null;
this.password=r[4]||null;
this.host=r[5];
this.port=r[7]||null;
}
this.toString=function(){
return this.uri;
};
};
};
dojo.provide("dojo.html.style");
dojo.html.getClass=function(node){
node=dojo.byId(node);
if(!node){
return "";
}
var cs="";
if(node.className){
cs=node.className;
}else{
if(dojo.html.hasAttribute(node,"class")){
cs=dojo.html.getAttribute(node,"class");
}
}
return cs.replace(/^\s+|\s+$/g,"");
};
dojo.html.getClasses=function(node){
var c=dojo.html.getClass(node);
return (c=="")?[]:c.split(/\s+/g);
};
dojo.html.hasClass=function(node,_473){
return (new RegExp("(^|\\s+)"+_473+"(\\s+|$)")).test(dojo.html.getClass(node));
};
dojo.html.prependClass=function(node,_475){
_475+=" "+dojo.html.getClass(node);
return dojo.html.setClass(node,_475);
};
dojo.html.addClass=function(node,_477){
if(dojo.html.hasClass(node,_477)){
return false;
}
_477=(dojo.html.getClass(node)+" "+_477).replace(/^\s+|\s+$/g,"");
return dojo.html.setClass(node,_477);
};
dojo.html.setClass=function(node,_479){
node=dojo.byId(node);
var cs=new String(_479);
try{
if(typeof node.className=="string"){
node.className=cs;
}else{
if(node.setAttribute){
node.setAttribute("class",_479);
node.className=cs;
}else{
return false;
}
}
}
catch(e){
dojo.debug("dojo.html.setClass() failed",e);
}
return true;
};
dojo.html.removeClass=function(node,_47c,_47d){
try{
if(!_47d){
var _47e=dojo.html.getClass(node).replace(new RegExp("(^|\\s+)"+_47c+"(\\s+|$)"),"$1$2");
}else{
var _47e=dojo.html.getClass(node).replace(_47c,"");
}
dojo.html.setClass(node,_47e);
}
catch(e){
dojo.debug("dojo.html.removeClass() failed",e);
}
return true;
};
dojo.html.replaceClass=function(node,_480,_481){
dojo.html.removeClass(node,_481);
dojo.html.addClass(node,_480);
};
dojo.html.classMatchType={ContainsAll:0,ContainsAny:1,IsOnly:2};
dojo.html.getElementsByClass=function(_482,_483,_484,_485,_486){
_486=false;
var _487=dojo.doc();
_483=dojo.byId(_483)||_487;
var _488=_482.split(/\s+/g);
var _489=[];
if(_485!=1&&_485!=2){
_485=0;
}
var _48a=new RegExp("(\\s|^)(("+_488.join(")|(")+"))(\\s|$)");
var _48b=_488.join(" ").length;
var _48c=[];
if(!_486&&_487.evaluate){
var _48d=".//"+(_484||"*")+"[contains(";
if(_485!=dojo.html.classMatchType.ContainsAny){
_48d+="concat(' ',@class,' '), ' "+_488.join(" ') and contains(concat(' ',@class,' '), ' ")+" ')";
if(_485==2){
_48d+=" and string-length(@class)="+_48b+"]";
}else{
_48d+="]";
}
}else{
_48d+="concat(' ',@class,' '), ' "+_488.join(" ') or contains(concat(' ',@class,' '), ' ")+" ')]";
}
var _48e=_487.evaluate(_48d,_483,null,XPathResult.ANY_TYPE,null);
var _48f=_48e.iterateNext();
while(_48f){
try{
_48c.push(_48f);
_48f=_48e.iterateNext();
}
catch(e){
break;
}
}
return _48c;
}else{
if(!_484){
_484="*";
}
_48c=_483.getElementsByTagName(_484);
var node,i=0;
outer:
while(node=_48c[i++]){
var _492=dojo.html.getClasses(node);
if(_492.length==0){
continue outer;
}
var _493=0;
for(var j=0;j<_492.length;j++){
if(_48a.test(_492[j])){
if(_485==dojo.html.classMatchType.ContainsAny){
_489.push(node);
continue outer;
}else{
_493++;
}
}else{
if(_485==dojo.html.classMatchType.IsOnly){
continue outer;
}
}
}
if(_493==_488.length){
if((_485==dojo.html.classMatchType.IsOnly)&&(_493==_492.length)){
_489.push(node);
}else{
if(_485==dojo.html.classMatchType.ContainsAll){
_489.push(node);
}
}
}
}
return _489;
}
};
dojo.html.getElementsByClassName=dojo.html.getElementsByClass;
dojo.html.toCamelCase=function(_495){
var arr=_495.split("-"),cc=arr[0];
for(var i=1;i<arr.length;i++){
cc+=arr[i].charAt(0).toUpperCase()+arr[i].substring(1);
}
return cc;
};
dojo.html.toSelectorCase=function(_499){
return _499.replace(/([A-Z])/g,"-$1").toLowerCase();
};
if(dojo.render.html.ie){
dojo.html.getComputedStyle=function(node,_49b,_49c){
node=dojo.byId(node);
if(!node||!node.currentStyle){
return _49c;
}
return node.currentStyle[dojo.html.toCamelCase(_49b)];
};
dojo.html.getComputedStyles=function(node){
return node.currentStyle;
};
}else{
dojo.html.getComputedStyle=function(node,_49f,_4a0){
node=dojo.byId(node);
if(!node||!node.style){
return _4a0;
}
var s=document.defaultView.getComputedStyle(node,null);
return (s&&s[dojo.html.toCamelCase(_49f)])||"";
};
dojo.html.getComputedStyles=function(node){
return document.defaultView.getComputedStyle(node,null);
};
}
dojo.html.getStyleProperty=function(node,_4a4){
node=dojo.byId(node);
return (node&&node.style?node.style[dojo.html.toCamelCase(_4a4)]:undefined);
};
dojo.html.getStyle=function(node,_4a6){
var _4a7=dojo.html.getStyleProperty(node,_4a6);
return (_4a7?_4a7:dojo.html.getComputedStyle(node,_4a6));
};
dojo.html.setStyle=function(node,_4a9,_4aa){
node=dojo.byId(node);
if(node&&node.style){
var _4ab=dojo.html.toCamelCase(_4a9);
node.style[_4ab]=_4aa;
}
};
dojo.html.setStyleText=function(_4ac,text){
try{
_4ac.style.cssText=text;
}
catch(e){
_4ac.setAttribute("style",text);
}
};
dojo.html.copyStyle=function(_4ae,_4af){
if(!_4af.style.cssText){
_4ae.setAttribute("style",_4af.getAttribute("style"));
}else{
_4ae.style.cssText=_4af.style.cssText;
}
dojo.html.addClass(_4ae,dojo.html.getClass(_4af));
};
dojo.html.getUnitValue=function(node,_4b1,_4b2){
var s=dojo.html.getComputedStyle(node,_4b1);
if((!s)||((s=="auto")&&(_4b2))){
return {value:0,units:"px"};
}
var _4b4=s.match(/(\-?[\d.]+)([a-z%]*)/i);
if(!_4b4){
return dojo.html.getUnitValue.bad;
}
return {value:Number(_4b4[1]),units:_4b4[2].toLowerCase()};
};
dojo.html.getUnitValue.bad={value:NaN,units:""};
if(dojo.render.html.ie){
dojo.html.toPixelValue=function(_4b5,_4b6){
if(!_4b6){
return 0;
}
if(_4b6.slice(-2)=="px"){
return parseFloat(_4b6);
}
var _4b7=0;
with(_4b5){
var _4b8=style.left;
var _4b9=runtimeStyle.left;
runtimeStyle.left=currentStyle.left;
try{
style.left=_4b6||0;
_4b7=style.pixelLeft;
style.left=_4b8;
runtimeStyle.left=_4b9;
}
catch(e){
}
}
return _4b7;
};
}else{
dojo.html.toPixelValue=function(_4ba,_4bb){
return (_4bb&&(_4bb.slice(-2)=="px")?parseFloat(_4bb):0);
};
}
dojo.html.getPixelValue=function(node,_4bd,_4be){
return dojo.html.toPixelValue(node,dojo.html.getComputedStyle(node,_4bd));
};
dojo.html.setPositivePixelValue=function(node,_4c0,_4c1){
if(isNaN(_4c1)){
return false;
}
node.style[_4c0]=Math.max(0,_4c1)+"px";
return true;
};
dojo.html.styleSheet=null;
dojo.html.insertCssRule=function(_4c2,_4c3,_4c4){
if(!dojo.html.styleSheet){
if(document.createStyleSheet){
dojo.html.styleSheet=document.createStyleSheet();
}else{
if(document.styleSheets[0]){
dojo.html.styleSheet=document.styleSheets[0];
}else{
return null;
}
}
}
if(arguments.length<3){
if(dojo.html.styleSheet.cssRules){
_4c4=dojo.html.styleSheet.cssRules.length;
}else{
if(dojo.html.styleSheet.rules){
_4c4=dojo.html.styleSheet.rules.length;
}else{
return null;
}
}
}
if(dojo.html.styleSheet.insertRule){
var rule=_4c2+" { "+_4c3+" }";
return dojo.html.styleSheet.insertRule(rule,_4c4);
}else{
if(dojo.html.styleSheet.addRule){
return dojo.html.styleSheet.addRule(_4c2,_4c3,_4c4);
}else{
return null;
}
}
};
dojo.html.removeCssRule=function(_4c6){
if(!dojo.html.styleSheet){
dojo.debug("no stylesheet defined for removing rules");
return false;
}
if(dojo.render.html.ie){
if(!_4c6){
_4c6=dojo.html.styleSheet.rules.length;
dojo.html.styleSheet.removeRule(_4c6);
}
}else{
if(document.styleSheets[0]){
if(!_4c6){
_4c6=dojo.html.styleSheet.cssRules.length;
}
dojo.html.styleSheet.deleteRule(_4c6);
}
}
return true;
};
dojo.html._insertedCssFiles=[];
dojo.html.insertCssFile=function(URI,doc,_4c9,_4ca){
if(!URI){
return;
}
if(!doc){
doc=document;
}
var _4cb=dojo.hostenv.getText(URI,false,_4ca);
if(_4cb===null){
return;
}
_4cb=dojo.html.fixPathsInCssText(_4cb,URI);
if(_4c9){
var idx=-1,node,ent=dojo.html._insertedCssFiles;
for(var i=0;i<ent.length;i++){
if((ent[i].doc==doc)&&(ent[i].cssText==_4cb)){
idx=i;
node=ent[i].nodeRef;
break;
}
}
if(node){
var _4d0=doc.getElementsByTagName("style");
for(var i=0;i<_4d0.length;i++){
if(_4d0[i]==node){
return;
}
}
dojo.html._insertedCssFiles.shift(idx,1);
}
}
var _4d1=dojo.html.insertCssText(_4cb,doc);
dojo.html._insertedCssFiles.push({"doc":doc,"cssText":_4cb,"nodeRef":_4d1});
if(_4d1&&djConfig.isDebug){
_4d1.setAttribute("dbgHref",URI);
}
return _4d1;
};
dojo.html.insertCssText=function(_4d2,doc,URI){
if(!_4d2){
return;
}
if(!doc){
doc=document;
}
if(URI){
_4d2=dojo.html.fixPathsInCssText(_4d2,URI);
}
var _4d5=doc.createElement("style");
_4d5.setAttribute("type","text/css");
var head=doc.getElementsByTagName("head")[0];
if(!head){
dojo.debug("No head tag in document, aborting styles");
return;
}else{
head.appendChild(_4d5);
}
if(_4d5.styleSheet){
var _4d7=function(){
try{
_4d5.styleSheet.cssText=_4d2;
}
catch(e){
dojo.debug(e);
}
};
if(_4d5.styleSheet.disabled){
setTimeout(_4d7,10);
}else{
_4d7();
}
}else{
var _4d8=doc.createTextNode(_4d2);
_4d5.appendChild(_4d8);
}
return _4d5;
};
dojo.html.fixPathsInCssText=function(_4d9,URI){
if(!_4d9||!URI){
return;
}
var _4db,str="",url="",_4de="[\\t\\s\\w\\(\\)\\/\\.\\\\'\"-:#=&?~]+";
var _4df=new RegExp("url\\(\\s*("+_4de+")\\s*\\)");
var _4e0=/(file|https?|ftps?):\/\//;
regexTrim=new RegExp("^[\\s]*(['\"]?)("+_4de+")\\1[\\s]*?$");
if(dojo.render.html.ie55||dojo.render.html.ie60){
var _4e1=new RegExp("AlphaImageLoader\\((.*)src=['\"]("+_4de+")['\"]");
while(_4db=_4e1.exec(_4d9)){
url=_4db[2].replace(regexTrim,"$2");
if(!_4e0.exec(url)){
url=(new dojo.uri.Uri(URI,url).toString());
}
str+=_4d9.substring(0,_4db.index)+"AlphaImageLoader("+_4db[1]+"src='"+url+"'";
_4d9=_4d9.substr(_4db.index+_4db[0].length);
}
_4d9=str+_4d9;
str="";
}
while(_4db=_4df.exec(_4d9)){
url=_4db[1].replace(regexTrim,"$2");
if(!_4e0.exec(url)){
url=(new dojo.uri.Uri(URI,url).toString());
}
str+=_4d9.substring(0,_4db.index)+"url("+url+")";
_4d9=_4d9.substr(_4db.index+_4db[0].length);
}
return str+_4d9;
};
dojo.html.setActiveStyleSheet=function(_4e2){
var i=0,a,els=dojo.doc().getElementsByTagName("link");
while(a=els[i++]){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("title")){
a.disabled=true;
if(a.getAttribute("title")==_4e2){
a.disabled=false;
}
}
}
};
dojo.html.getActiveStyleSheet=function(){
var i=0,a,els=dojo.doc().getElementsByTagName("link");
while(a=els[i++]){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("title")&&!a.disabled){
return a.getAttribute("title");
}
}
return null;
};
dojo.html.getPreferredStyleSheet=function(){
var i=0,a,els=dojo.doc().getElementsByTagName("link");
while(a=els[i++]){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("rel").indexOf("alt")==-1&&a.getAttribute("title")){
return a.getAttribute("title");
}
}
return null;
};
dojo.html.applyBrowserClass=function(node){
var drh=dojo.render.html;
var _4ee={dj_ie:drh.ie,dj_ie55:drh.ie55,dj_ie6:drh.ie60,dj_ie7:drh.ie70,dj_iequirks:drh.ie&&drh.quirks,dj_opera:drh.opera,dj_opera8:drh.opera&&(Math.floor(dojo.render.version)==8),dj_opera9:drh.opera&&(Math.floor(dojo.render.version)==9),dj_khtml:drh.khtml,dj_safari:drh.safari,dj_gecko:drh.mozilla};
for(var p in _4ee){
if(_4ee[p]){
dojo.html.addClass(node,p);
}
}
};
dojo.kwCompoundRequire({common:["dojo.html.common","dojo.html.style"]});
dojo.provide("dojo.html.*");
dojo.provide("dojo.html.display");
dojo.html._toggle=function(node,_4f1,_4f2){
node=dojo.byId(node);
_4f2(node,!_4f1(node));
return _4f1(node);
};
dojo.html.show=function(node){
node=dojo.byId(node);
if(dojo.html.getStyleProperty(node,"display")=="none"){
dojo.html.setStyle(node,"display",(node.dojoDisplayCache||""));
node.dojoDisplayCache=undefined;
}
};
dojo.html.hide=function(node){
node=dojo.byId(node);
if(typeof node["dojoDisplayCache"]=="undefined"){
var d=dojo.html.getStyleProperty(node,"display");
if(d!="none"){
node.dojoDisplayCache=d;
}
}
dojo.html.setStyle(node,"display","none");
};
dojo.html.setShowing=function(node,_4f7){
dojo.html[(_4f7?"show":"hide")](node);
};
dojo.html.isShowing=function(node){
return (dojo.html.getStyleProperty(node,"display")!="none");
};
dojo.html.toggleShowing=function(node){
return dojo.html._toggle(node,dojo.html.isShowing,dojo.html.setShowing);
};
dojo.html.displayMap={tr:"",td:"",th:"",img:"inline",span:"inline",input:"inline",button:"inline"};
dojo.html.suggestDisplayByTagName=function(node){
node=dojo.byId(node);
if(node&&node.tagName){
var tag=node.tagName.toLowerCase();
return (tag in dojo.html.displayMap?dojo.html.displayMap[tag]:"block");
}
};
dojo.html.setDisplay=function(node,_4fd){
dojo.html.setStyle(node,"display",((_4fd instanceof String||typeof _4fd=="string")?_4fd:(_4fd?dojo.html.suggestDisplayByTagName(node):"none")));
};
dojo.html.isDisplayed=function(node){
return (dojo.html.getComputedStyle(node,"display")!="none");
};
dojo.html.toggleDisplay=function(node){
return dojo.html._toggle(node,dojo.html.isDisplayed,dojo.html.setDisplay);
};
dojo.html.setVisibility=function(node,_501){
dojo.html.setStyle(node,"visibility",((_501 instanceof String||typeof _501=="string")?_501:(_501?"visible":"hidden")));
};
dojo.html.isVisible=function(node){
return (dojo.html.getComputedStyle(node,"visibility")!="hidden");
};
dojo.html.toggleVisibility=function(node){
return dojo.html._toggle(node,dojo.html.isVisible,dojo.html.setVisibility);
};
dojo.html.setOpacity=function(node,_505,_506){
node=dojo.byId(node);
var h=dojo.render.html;
if(!_506){
if(_505>=1){
if(h.ie){
dojo.html.clearOpacity(node);
return;
}else{
_505=0.999999;
}
}else{
if(_505<0){
_505=0;
}
}
}
if(h.ie){
if(node.nodeName.toLowerCase()=="tr"){
var tds=node.getElementsByTagName("td");
for(var x=0;x<tds.length;x++){
tds[x].style.filter="Alpha(Opacity="+_505*100+")";
}
}
node.style.filter="Alpha(Opacity="+_505*100+")";
}else{
if(h.moz){
node.style.opacity=_505;
node.style.MozOpacity=_505;
}else{
if(h.safari){
node.style.opacity=_505;
node.style.KhtmlOpacity=_505;
}else{
node.style.opacity=_505;
}
}
}
};
dojo.html.clearOpacity=function(node){
node=dojo.byId(node);
var ns=node.style;
var h=dojo.render.html;
if(h.ie){
try{
if(node.filters&&node.filters.alpha){
ns.filter="";
}
}
catch(e){
}
}else{
if(h.moz){
ns.opacity=1;
ns.MozOpacity=1;
}else{
if(h.safari){
ns.opacity=1;
ns.KhtmlOpacity=1;
}else{
ns.opacity=1;
}
}
}
};
dojo.html.getOpacity=function(node){
node=dojo.byId(node);
var h=dojo.render.html;
if(h.ie){
var opac=(node.filters&&node.filters.alpha&&typeof node.filters.alpha.opacity=="number"?node.filters.alpha.opacity:100)/100;
}else{
var opac=node.style.opacity||node.style.MozOpacity||node.style.KhtmlOpacity||1;
}
return opac>=0.999999?1:Number(opac);
};
dojo.provide("dojo.html.layout");
dojo.html.sumAncestorProperties=function(node,prop){
node=dojo.byId(node);
if(!node){
return 0;
}
var _512=0;
while(node){
if(dojo.html.getComputedStyle(node,"position")=="fixed"){
return 0;
}
var val=node[prop];
if(val){
_512+=val-0;
if(node==dojo.body()){
break;
}
}
node=node.parentNode;
}
return _512;
};
dojo.html.setStyleAttributes=function(node,_515){
node=dojo.byId(node);
var _516=_515.replace(/(;)?\s*$/,"").split(";");
for(var i=0;i<_516.length;i++){
var _518=_516[i].split(":");
var name=_518[0].replace(/\s*$/,"").replace(/^\s*/,"").toLowerCase();
var _51a=_518[1].replace(/\s*$/,"").replace(/^\s*/,"");
switch(name){
case "opacity":
dojo.html.setOpacity(node,_51a);
break;
case "content-height":
dojo.html.setContentBox(node,{height:_51a});
break;
case "content-width":
dojo.html.setContentBox(node,{width:_51a});
break;
case "outer-height":
dojo.html.setMarginBox(node,{height:_51a});
break;
case "outer-width":
dojo.html.setMarginBox(node,{width:_51a});
break;
default:
node.style[dojo.html.toCamelCase(name)]=_51a;
}
}
};
dojo.html.boxSizing={MARGIN_BOX:"margin-box",BORDER_BOX:"border-box",PADDING_BOX:"padding-box",CONTENT_BOX:"content-box"};
dojo.html.getAbsolutePosition=dojo.html.abs=function(node,_51c,_51d){
node=dojo.byId(node,node.ownerDocument);
var ret={x:0,y:0};
var bs=dojo.html.boxSizing;
if(!_51d){
_51d=bs.CONTENT_BOX;
}
var _520=2;
var _521;
switch(_51d){
case bs.MARGIN_BOX:
_521=3;
break;
case bs.BORDER_BOX:
_521=2;
break;
case bs.PADDING_BOX:
default:
_521=1;
break;
case bs.CONTENT_BOX:
_521=0;
break;
}
var h=dojo.render.html;
var db=document["body"]||document["documentElement"];
if(h.ie){
with(node.getBoundingClientRect()){
ret.x=left-2;
ret.y=top-2;
}
}else{
if(document.getBoxObjectFor){
_520=1;
try{
var bo=document.getBoxObjectFor(node);
ret.x=bo.x-dojo.html.sumAncestorProperties(node,"scrollLeft");
ret.y=bo.y-dojo.html.sumAncestorProperties(node,"scrollTop");
}
catch(e){
}
}else{
if(node["offsetParent"]){
var _525;
if((h.safari)&&(node.style.getPropertyValue("position")=="absolute")&&(node.parentNode==db)){
_525=db;
}else{
_525=db.parentNode;
}
if(node.parentNode!=db){
var nd=node;
if(dojo.render.html.opera){
nd=db;
}
ret.x-=dojo.html.sumAncestorProperties(nd,"scrollLeft");
ret.y-=dojo.html.sumAncestorProperties(nd,"scrollTop");
}
var _527=node;
do{
var n=_527["offsetLeft"];
if(!h.opera||n>0){
ret.x+=isNaN(n)?0:n;
}
var m=_527["offsetTop"];
ret.y+=isNaN(m)?0:m;
_527=_527.offsetParent;
}while((_527!=_525)&&(_527!=null));
}else{
if(node["x"]&&node["y"]){
ret.x+=isNaN(node.x)?0:node.x;
ret.y+=isNaN(node.y)?0:node.y;
}
}
}
}
if(_51c){
var _52a=dojo.html.getScroll();
ret.y+=_52a.top;
ret.x+=_52a.left;
}
var _52b=[dojo.html.getPaddingExtent,dojo.html.getBorderExtent,dojo.html.getMarginExtent];
if(_520>_521){
for(var i=_521;i<_520;++i){
ret.y+=_52b[i](node,"top");
ret.x+=_52b[i](node,"left");
}
}else{
if(_520<_521){
for(var i=_521;i>_520;--i){
ret.y-=_52b[i-1](node,"top");
ret.x-=_52b[i-1](node,"left");
}
}
}
ret.top=ret.y;
ret.left=ret.x;
return ret;
};
dojo.html.isPositionAbsolute=function(node){
return (dojo.html.getComputedStyle(node,"position")=="absolute");
};
dojo.html._sumPixelValues=function(node,_52f,_530){
var _531=0;
for(var x=0;x<_52f.length;x++){
_531+=dojo.html.getPixelValue(node,_52f[x],_530);
}
return _531;
};
dojo.html.getMargin=function(node){
return {width:dojo.html._sumPixelValues(node,["margin-left","margin-right"],(dojo.html.getComputedStyle(node,"position")=="absolute")),height:dojo.html._sumPixelValues(node,["margin-top","margin-bottom"],(dojo.html.getComputedStyle(node,"position")=="absolute"))};
};
dojo.html.getBorder=function(node){
return {width:dojo.html.getBorderExtent(node,"left")+dojo.html.getBorderExtent(node,"right"),height:dojo.html.getBorderExtent(node,"top")+dojo.html.getBorderExtent(node,"bottom")};
};
dojo.html.getBorderExtent=function(node,side){
return (dojo.html.getStyle(node,"border-"+side+"-style")=="none"?0:dojo.html.getPixelValue(node,"border-"+side+"-width"));
};
dojo.html.getMarginExtent=function(node,side){
return dojo.html._sumPixelValues(node,["margin-"+side],dojo.html.isPositionAbsolute(node));
};
dojo.html.getPaddingExtent=function(node,side){
return dojo.html._sumPixelValues(node,["padding-"+side],true);
};
dojo.html.getPadding=function(node){
return {width:dojo.html._sumPixelValues(node,["padding-left","padding-right"],true),height:dojo.html._sumPixelValues(node,["padding-top","padding-bottom"],true)};
};
dojo.html.getPadBorder=function(node){
var pad=dojo.html.getPadding(node);
var _53e=dojo.html.getBorder(node);
return {width:pad.width+_53e.width,height:pad.height+_53e.height};
};
dojo.html.getBoxSizing=function(node){
var h=dojo.render.html;
var bs=dojo.html.boxSizing;
if(((h.ie)||(h.opera))&&node.nodeName.toLowerCase()!="img"){
var cm=document["compatMode"];
if((cm=="BackCompat")||(cm=="QuirksMode")){
return bs.BORDER_BOX;
}else{
return bs.CONTENT_BOX;
}
}else{
if(arguments.length==0){
node=document.documentElement;
}
var _543;
if(!h.ie){
_543=dojo.html.getStyle(node,"-moz-box-sizing");
if(!_543){
_543=dojo.html.getStyle(node,"box-sizing");
}
}
return (_543?_543:bs.CONTENT_BOX);
}
};
dojo.html.isBorderBox=function(node){
return (dojo.html.getBoxSizing(node)==dojo.html.boxSizing.BORDER_BOX);
};
dojo.html.getBorderBox=function(node){
node=dojo.byId(node);
return {width:node.offsetWidth,height:node.offsetHeight};
};
dojo.html.getPaddingBox=function(node){
var box=dojo.html.getBorderBox(node);
var _548=dojo.html.getBorder(node);
return {width:box.width-_548.width,height:box.height-_548.height};
};
dojo.html.getContentBox=function(node){
node=dojo.byId(node);
var _54a=dojo.html.getPadBorder(node);
return {width:node.offsetWidth-_54a.width,height:node.offsetHeight-_54a.height};
};
dojo.html.setContentBox=function(node,args){
node=dojo.byId(node);
var _54d=0;
var _54e=0;
var isbb=dojo.html.isBorderBox(node);
var _550=(isbb?dojo.html.getPadBorder(node):{width:0,height:0});
var ret={};
if(typeof args.width!="undefined"){
_54d=args.width+_550.width;
ret.width=dojo.html.setPositivePixelValue(node,"width",_54d);
}
if(typeof args.height!="undefined"){
_54e=args.height+_550.height;
ret.height=dojo.html.setPositivePixelValue(node,"height",_54e);
}
return ret;
};
dojo.html.getMarginBox=function(node){
var _553=dojo.html.getBorderBox(node);
var _554=dojo.html.getMargin(node);
return {width:_553.width+_554.width,height:_553.height+_554.height};
};
dojo.html.setMarginBox=function(node,args){
node=dojo.byId(node);
var _557=0;
var _558=0;
var isbb=dojo.html.isBorderBox(node);
var _55a=(!isbb?dojo.html.getPadBorder(node):{width:0,height:0});
var _55b=dojo.html.getMargin(node);
var ret={};
if(typeof args.width!="undefined"){
_557=args.width-_55a.width;
_557-=_55b.width;
ret.width=dojo.html.setPositivePixelValue(node,"width",_557);
}
if(typeof args.height!="undefined"){
_558=args.height-_55a.height;
_558-=_55b.height;
ret.height=dojo.html.setPositivePixelValue(node,"height",_558);
}
return ret;
};
dojo.html.getElementBox=function(node,type){
var bs=dojo.html.boxSizing;
switch(type){
case bs.MARGIN_BOX:
return dojo.html.getMarginBox(node);
case bs.BORDER_BOX:
return dojo.html.getBorderBox(node);
case bs.PADDING_BOX:
return dojo.html.getPaddingBox(node);
case bs.CONTENT_BOX:
default:
return dojo.html.getContentBox(node);
}
};
dojo.html.toCoordinateObject=dojo.html.toCoordinateArray=function(_560,_561,_562){
if(_560 instanceof Array||typeof _560=="array"){
dojo.deprecated("dojo.html.toCoordinateArray","use dojo.html.toCoordinateObject({left: , top: , width: , height: }) instead","0.5");
while(_560.length<4){
_560.push(0);
}
while(_560.length>4){
_560.pop();
}
var ret={left:_560[0],top:_560[1],width:_560[2],height:_560[3]};
}else{
if(!_560.nodeType&&!(_560 instanceof String||typeof _560=="string")&&("width" in _560||"height" in _560||"left" in _560||"x" in _560||"top" in _560||"y" in _560)){
var ret={left:_560.left||_560.x||0,top:_560.top||_560.y||0,width:_560.width||0,height:_560.height||0};
}else{
var node=dojo.byId(_560);
var pos=dojo.html.abs(node,_561,_562);
var _566=dojo.html.getMarginBox(node);
var ret={left:pos.left,top:pos.top,width:_566.width,height:_566.height};
}
}
ret.x=ret.left;
ret.y=ret.top;
return ret;
};
dojo.html.setMarginBoxWidth=dojo.html.setOuterWidth=function(node,_568){
return dojo.html._callDeprecated("setMarginBoxWidth","setMarginBox",arguments,"width");
};
dojo.html.setMarginBoxHeight=dojo.html.setOuterHeight=function(){
return dojo.html._callDeprecated("setMarginBoxHeight","setMarginBox",arguments,"height");
};
dojo.html.getMarginBoxWidth=dojo.html.getOuterWidth=function(){
return dojo.html._callDeprecated("getMarginBoxWidth","getMarginBox",arguments,null,"width");
};
dojo.html.getMarginBoxHeight=dojo.html.getOuterHeight=function(){
return dojo.html._callDeprecated("getMarginBoxHeight","getMarginBox",arguments,null,"height");
};
dojo.html.getTotalOffset=function(node,type,_56b){
return dojo.html._callDeprecated("getTotalOffset","getAbsolutePosition",arguments,null,type);
};
dojo.html.getAbsoluteX=function(node,_56d){
return dojo.html._callDeprecated("getAbsoluteX","getAbsolutePosition",arguments,null,"x");
};
dojo.html.getAbsoluteY=function(node,_56f){
return dojo.html._callDeprecated("getAbsoluteY","getAbsolutePosition",arguments,null,"y");
};
dojo.html.totalOffsetLeft=function(node,_571){
return dojo.html._callDeprecated("totalOffsetLeft","getAbsolutePosition",arguments,null,"left");
};
dojo.html.totalOffsetTop=function(node,_573){
return dojo.html._callDeprecated("totalOffsetTop","getAbsolutePosition",arguments,null,"top");
};
dojo.html.getMarginWidth=function(node){
return dojo.html._callDeprecated("getMarginWidth","getMargin",arguments,null,"width");
};
dojo.html.getMarginHeight=function(node){
return dojo.html._callDeprecated("getMarginHeight","getMargin",arguments,null,"height");
};
dojo.html.getBorderWidth=function(node){
return dojo.html._callDeprecated("getBorderWidth","getBorder",arguments,null,"width");
};
dojo.html.getBorderHeight=function(node){
return dojo.html._callDeprecated("getBorderHeight","getBorder",arguments,null,"height");
};
dojo.html.getPaddingWidth=function(node){
return dojo.html._callDeprecated("getPaddingWidth","getPadding",arguments,null,"width");
};
dojo.html.getPaddingHeight=function(node){
return dojo.html._callDeprecated("getPaddingHeight","getPadding",arguments,null,"height");
};
dojo.html.getPadBorderWidth=function(node){
return dojo.html._callDeprecated("getPadBorderWidth","getPadBorder",arguments,null,"width");
};
dojo.html.getPadBorderHeight=function(node){
return dojo.html._callDeprecated("getPadBorderHeight","getPadBorder",arguments,null,"height");
};
dojo.html.getBorderBoxWidth=dojo.html.getInnerWidth=function(){
return dojo.html._callDeprecated("getBorderBoxWidth","getBorderBox",arguments,null,"width");
};
dojo.html.getBorderBoxHeight=dojo.html.getInnerHeight=function(){
return dojo.html._callDeprecated("getBorderBoxHeight","getBorderBox",arguments,null,"height");
};
dojo.html.getContentBoxWidth=dojo.html.getContentWidth=function(){
return dojo.html._callDeprecated("getContentBoxWidth","getContentBox",arguments,null,"width");
};
dojo.html.getContentBoxHeight=dojo.html.getContentHeight=function(){
return dojo.html._callDeprecated("getContentBoxHeight","getContentBox",arguments,null,"height");
};
dojo.html.setContentBoxWidth=dojo.html.setContentWidth=function(node,_57d){
return dojo.html._callDeprecated("setContentBoxWidth","setContentBox",arguments,"width");
};
dojo.html.setContentBoxHeight=dojo.html.setContentHeight=function(node,_57f){
return dojo.html._callDeprecated("setContentBoxHeight","setContentBox",arguments,"height");
};
dojo.provide("dojo.html.util");
dojo.html.getElementWindow=function(_580){
return dojo.html.getDocumentWindow(_580.ownerDocument);
};
dojo.html.getDocumentWindow=function(doc){
if(dojo.render.html.safari&&!doc._parentWindow){
var fix=function(win){
win.document._parentWindow=win;
for(var i=0;i<win.frames.length;i++){
fix(win.frames[i]);
}
};
fix(window.top);
}
if(dojo.render.html.ie&&window!==document.parentWindow&&!doc._parentWindow){
doc.parentWindow.execScript("document._parentWindow = window;","Javascript");
var win=doc._parentWindow;
doc._parentWindow=null;
return win;
}
return doc._parentWindow||doc.parentWindow||doc.defaultView;
};
dojo.html.gravity=function(node,e){
node=dojo.byId(node);
var _588=dojo.html.getCursorPosition(e);
with(dojo.html){
var _589=getAbsolutePosition(node,true);
var bb=getBorderBox(node);
var _58b=_589.x+(bb.width/2);
var _58c=_589.y+(bb.height/2);
}
with(dojo.html.gravity){
return ((_588.x<_58b?WEST:EAST)|(_588.y<_58c?NORTH:SOUTH));
}
};
dojo.html.gravity.NORTH=1;
dojo.html.gravity.SOUTH=1<<1;
dojo.html.gravity.EAST=1<<2;
dojo.html.gravity.WEST=1<<3;
dojo.html.overElement=function(_58d,e){
_58d=dojo.byId(_58d);
var _58f=dojo.html.getCursorPosition(e);
var bb=dojo.html.getBorderBox(_58d);
var _591=dojo.html.getAbsolutePosition(_58d,true,dojo.html.boxSizing.BORDER_BOX);
var top=_591.y;
var _593=top+bb.height;
var left=_591.x;
var _595=left+bb.width;
return (_58f.x>=left&&_58f.x<=_595&&_58f.y>=top&&_58f.y<=_593);
};
dojo.html.renderedTextContent=function(node){
node=dojo.byId(node);
var _597="";
if(node==null){
return _597;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
var _599="unknown";
try{
_599=dojo.html.getStyle(node.childNodes[i],"display");
}
catch(E){
}
switch(_599){
case "block":
case "list-item":
case "run-in":
case "table":
case "table-row-group":
case "table-header-group":
case "table-footer-group":
case "table-row":
case "table-column-group":
case "table-column":
case "table-cell":
case "table-caption":
_597+="\n";
_597+=dojo.html.renderedTextContent(node.childNodes[i]);
_597+="\n";
break;
case "none":
break;
default:
if(node.childNodes[i].tagName&&node.childNodes[i].tagName.toLowerCase()=="br"){
_597+="\n";
}else{
_597+=dojo.html.renderedTextContent(node.childNodes[i]);
}
break;
}
break;
case 3:
case 2:
case 4:
var text=node.childNodes[i].nodeValue;
var _59b="unknown";
try{
_59b=dojo.html.getStyle(node,"text-transform");
}
catch(E){
}
switch(_59b){
case "capitalize":
var _59c=text.split(" ");
for(var i=0;i<_59c.length;i++){
_59c[i]=_59c[i].charAt(0).toUpperCase()+_59c[i].substring(1);
}
text=_59c.join(" ");
break;
case "uppercase":
text=text.toUpperCase();
break;
case "lowercase":
text=text.toLowerCase();
break;
default:
break;
}
switch(_59b){
case "nowrap":
break;
case "pre-wrap":
break;
case "pre-line":
break;
case "pre":
break;
default:
text=text.replace(/\s+/," ");
if(/\s$/.test(_597)){
text.replace(/^\s/,"");
}
break;
}
_597+=text;
break;
default:
break;
}
}
return _597;
};
dojo.html.createNodesFromText=function(txt,trim){
if(trim){
txt=txt.replace(/^\s+|\s+$/g,"");
}
var tn=dojo.doc().createElement("div");
tn.style.visibility="hidden";
dojo.body().appendChild(tn);
var _5a0="none";
if((/^<t[dh][\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table><tbody><tr>"+txt+"</tr></tbody></table>";
_5a0="cell";
}else{
if((/^<tr[\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table><tbody>"+txt+"</tbody></table>";
_5a0="row";
}else{
if((/^<(thead|tbody|tfoot)[\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table>"+txt+"</table>";
_5a0="section";
}
}
}
tn.innerHTML=txt;
if(tn["normalize"]){
tn.normalize();
}
var _5a1=null;
switch(_5a0){
case "cell":
_5a1=tn.getElementsByTagName("tr")[0];
break;
case "row":
_5a1=tn.getElementsByTagName("tbody")[0];
break;
case "section":
_5a1=tn.getElementsByTagName("table")[0];
break;
default:
_5a1=tn;
break;
}
var _5a2=[];
for(var x=0;x<_5a1.childNodes.length;x++){
_5a2.push(_5a1.childNodes[x].cloneNode(true));
}
tn.style.display="none";
dojo.html.destroyNode(tn);
return _5a2;
};
dojo.html.placeOnScreen=function(node,_5a5,_5a6,_5a7,_5a8,_5a9,_5aa){
if(_5a5 instanceof Array||typeof _5a5=="array"){
_5aa=_5a9;
_5a9=_5a8;
_5a8=_5a7;
_5a7=_5a6;
_5a6=_5a5[1];
_5a5=_5a5[0];
}
if(_5a9 instanceof String||typeof _5a9=="string"){
_5a9=_5a9.split(",");
}
if(!isNaN(_5a7)){
_5a7=[Number(_5a7),Number(_5a7)];
}else{
if(!(_5a7 instanceof Array||typeof _5a7=="array")){
_5a7=[0,0];
}
}
var _5ab=dojo.html.getScroll().offset;
var view=dojo.html.getViewport();
node=dojo.byId(node);
var _5ad=node.style.display;
node.style.display="";
var bb=dojo.html.getBorderBox(node);
var w=bb.width;
var h=bb.height;
node.style.display=_5ad;
if(!(_5a9 instanceof Array||typeof _5a9=="array")){
_5a9=["TL"];
}
var _5b1,_5b2,_5b3=Infinity,_5b4;
for(var _5b5=0;_5b5<_5a9.length;++_5b5){
var _5b6=_5a9[_5b5];
var _5b7=true;
var tryX=_5a5-(_5b6.charAt(1)=="L"?0:w)+_5a7[0]*(_5b6.charAt(1)=="L"?1:-1);
var tryY=_5a6-(_5b6.charAt(0)=="T"?0:h)+_5a7[1]*(_5b6.charAt(0)=="T"?1:-1);
if(_5a8){
tryX-=_5ab.x;
tryY-=_5ab.y;
}
if(tryX<0){
tryX=0;
_5b7=false;
}
if(tryY<0){
tryY=0;
_5b7=false;
}
var x=tryX+w;
if(x>view.width){
x=view.width-w;
_5b7=false;
}else{
x=tryX;
}
x=Math.max(_5a7[0],x)+_5ab.x;
var y=tryY+h;
if(y>view.height){
y=view.height-h;
_5b7=false;
}else{
y=tryY;
}
y=Math.max(_5a7[1],y)+_5ab.y;
if(_5b7){
_5b1=x;
_5b2=y;
_5b3=0;
_5b4=_5b6;
break;
}else{
var dist=Math.pow(x-tryX-_5ab.x,2)+Math.pow(y-tryY-_5ab.y,2);
if(_5b3>dist){
_5b3=dist;
_5b1=x;
_5b2=y;
_5b4=_5b6;
}
}
}
if(!_5aa){
node.style.left=_5b1+"px";
node.style.top=_5b2+"px";
}
return {left:_5b1,top:_5b2,x:_5b1,y:_5b2,dist:_5b3,corner:_5b4};
};
dojo.html.placeOnScreenPoint=function(node,_5be,_5bf,_5c0,_5c1){
dojo.deprecated("dojo.html.placeOnScreenPoint","use dojo.html.placeOnScreen() instead","0.5");
return dojo.html.placeOnScreen(node,_5be,_5bf,_5c0,_5c1,["TL","TR","BL","BR"]);
};
dojo.html.placeOnScreenAroundElement=function(node,_5c3,_5c4,_5c5,_5c6,_5c7){
var best,_5c9=Infinity;
_5c3=dojo.byId(_5c3);
var _5ca=_5c3.style.display;
_5c3.style.display="";
var mb=dojo.html.getElementBox(_5c3,_5c5);
var _5cc=mb.width;
var _5cd=mb.height;
var _5ce=dojo.html.getAbsolutePosition(_5c3,true,_5c5);
_5c3.style.display=_5ca;
for(var _5cf in _5c6){
var pos,_5d1,_5d2;
var _5d3=_5c6[_5cf];
_5d1=_5ce.x+(_5cf.charAt(1)=="L"?0:_5cc);
_5d2=_5ce.y+(_5cf.charAt(0)=="T"?0:_5cd);
pos=dojo.html.placeOnScreen(node,_5d1,_5d2,_5c4,true,_5d3,true);
if(pos.dist==0){
best=pos;
break;
}else{
if(_5c9>pos.dist){
_5c9=pos.dist;
best=pos;
}
}
}
if(!_5c7){
node.style.left=best.left+"px";
node.style.top=best.top+"px";
}
return best;
};
dojo.html.scrollIntoView=function(node){
if(!node){
return;
}
if(dojo.render.html.ie){
if(dojo.html.getBorderBox(node.parentNode).height<=node.parentNode.scrollHeight){
node.scrollIntoView(false);
}
}else{
if(dojo.render.html.mozilla){
node.scrollIntoView(false);
}else{
var _5d5=node.parentNode;
var _5d6=_5d5.scrollTop+dojo.html.getBorderBox(_5d5).height;
var _5d7=node.offsetTop+dojo.html.getMarginBox(node).height;
if(_5d6<_5d7){
_5d5.scrollTop+=(_5d7-_5d6);
}else{
if(_5d5.scrollTop>node.offsetTop){
_5d5.scrollTop-=(_5d5.scrollTop-node.offsetTop);
}
}
}
}
};
dojo.provide("dojo.lfx.shadow");
dojo.lfx.shadow=function(node){
this.shadowPng=dojo.uri.moduleUri("dojo.html","images/shadow");
this.shadowThickness=8;
this.shadowOffset=15;
this.init(node);
};
dojo.extend(dojo.lfx.shadow,{init:function(node){
this.node=node;
this.pieces={};
var x1=-1*this.shadowThickness;
var y0=this.shadowOffset;
var y1=this.shadowOffset+this.shadowThickness;
this._makePiece("tl","top",y0,"left",x1);
this._makePiece("l","top",y1,"left",x1,"scale");
this._makePiece("tr","top",y0,"left",0);
this._makePiece("r","top",y1,"left",0,"scale");
this._makePiece("bl","top",0,"left",x1);
this._makePiece("b","top",0,"left",0,"crop");
this._makePiece("br","top",0,"left",0);
},_makePiece:function(name,_5de,_5df,_5e0,_5e1,_5e2){
var img;
var url=this.shadowPng+name.toUpperCase()+".png";
if(dojo.render.html.ie55||dojo.render.html.ie60){
img=dojo.doc().createElement("div");
img.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+url+"'"+(_5e2?", sizingMethod='"+_5e2+"'":"")+")";
}else{
img=dojo.doc().createElement("img");
img.src=url;
}
img.style.position="absolute";
img.style[_5de]=_5df+"px";
img.style[_5e0]=_5e1+"px";
img.style.width=this.shadowThickness+"px";
img.style.height=this.shadowThickness+"px";
this.pieces[name]=img;
this.node.appendChild(img);
},size:function(_5e5,_5e6){
var _5e7=_5e6-(this.shadowOffset+this.shadowThickness+1);
if(_5e7<0){
_5e7=0;
}
if(_5e6<1){
_5e6=1;
}
if(_5e5<1){
_5e5=1;
}
with(this.pieces){
l.style.height=_5e7+"px";
r.style.height=_5e7+"px";
b.style.width=(_5e5-1)+"px";
bl.style.top=(_5e6-1)+"px";
b.style.top=(_5e6-1)+"px";
br.style.top=(_5e6-1)+"px";
tr.style.left=(_5e5-1)+"px";
r.style.left=(_5e5-1)+"px";
br.style.left=(_5e5-1)+"px";
}
}});
dojo.provide("dojo.dnd.DragAndDrop");
dojo.declare("dojo.dnd.DragSource",null,{type:"",onDragEnd:function(evt){
},onDragStart:function(evt){
},onSelected:function(evt){
},unregister:function(){
dojo.dnd.dragManager.unregisterDragSource(this);
},reregister:function(){
dojo.dnd.dragManager.registerDragSource(this);
}});
dojo.declare("dojo.dnd.DragObject",null,{type:"",register:function(){
var dm=dojo.dnd.dragManager;
if(dm["registerDragObject"]){
dm.registerDragObject(this);
}
},onDragStart:function(evt){
},onDragMove:function(evt){
},onDragOver:function(evt){
},onDragOut:function(evt){
},onDragEnd:function(evt){
},onDragLeave:dojo.lang.forward("onDragOut"),onDragEnter:dojo.lang.forward("onDragOver"),ondragout:dojo.lang.forward("onDragOut"),ondragover:dojo.lang.forward("onDragOver")});
dojo.declare("dojo.dnd.DropTarget",null,{acceptsType:function(type){
if(!dojo.lang.inArray(this.acceptedTypes,"*")){
if(!dojo.lang.inArray(this.acceptedTypes,type)){
return false;
}
}
return true;
},accepts:function(_5f2){
if(!dojo.lang.inArray(this.acceptedTypes,"*")){
for(var i=0;i<_5f2.length;i++){
if(!dojo.lang.inArray(this.acceptedTypes,_5f2[i].type)){
return false;
}
}
}
return true;
},unregister:function(){
dojo.dnd.dragManager.unregisterDropTarget(this);
},onDragOver:function(evt){
},onDragOut:function(evt){
},onDragMove:function(evt){
},onDropStart:function(evt){
},onDrop:function(evt){
},onDropEnd:function(){
}},function(){
this.acceptedTypes=[];
});
dojo.dnd.DragEvent=function(){
this.dragSource=null;
this.dragObject=null;
this.target=null;
this.eventStatus="success";
};
dojo.declare("dojo.dnd.DragManager",null,{selectedSources:[],dragObjects:[],dragSources:[],registerDragSource:function(_5f9){
},dropTargets:[],registerDropTarget:function(_5fa){
},lastDragTarget:null,currentDragTarget:null,onKeyDown:function(){
},onMouseOut:function(){
},onMouseMove:function(){
},onMouseUp:function(){
}});
dojo.provide("dojo.dnd.HtmlDragManager");
dojo.declare("dojo.dnd.HtmlDragManager",dojo.dnd.DragManager,{disabled:false,nestedTargets:false,mouseDownTimer:null,dsCounter:0,dsPrefix:"dojoDragSource",dropTargetDimensions:[],currentDropTarget:null,previousDropTarget:null,_dragTriggered:false,selectedSources:[],dragObjects:[],dragSources:[],dropTargets:[],currentX:null,currentY:null,lastX:null,lastY:null,mouseDownX:null,mouseDownY:null,threshold:7,dropAcceptable:false,cancelEvent:function(e){
e.stopPropagation();
e.preventDefault();
},registerDragSource:function(ds){
if(ds["domNode"]){
var dp=this.dsPrefix;
var _5fe=dp+"Idx_"+(this.dsCounter++);
ds.dragSourceId=_5fe;
this.dragSources[_5fe]=ds;
ds.domNode.setAttribute(dp,_5fe);
if(dojo.render.html.ie){
dojo.event.browser.addListener(ds.domNode,"ondragstart",this.cancelEvent);
}
}
},unregisterDragSource:function(ds){
if(ds["domNode"]){
var dp=this.dsPrefix;
var _601=ds.dragSourceId;
delete ds.dragSourceId;
delete this.dragSources[_601];
ds.domNode.setAttribute(dp,null);
if(dojo.render.html.ie){
dojo.event.browser.removeListener(ds.domNode,"ondragstart",this.cancelEvent);
}
}
},registerDropTarget:function(dt){
this.dropTargets.push(dt);
},unregisterDropTarget:function(dt){
var _604=dojo.lang.find(this.dropTargets,dt,true);
if(_604>=0){
this.dropTargets.splice(_604,1);
}
},getDragSource:function(e){
var tn=e.target;
if(tn===dojo.body()){
return;
}
var ta=dojo.html.getAttribute(tn,this.dsPrefix);
while((!ta)&&(tn)){
tn=tn.parentNode;
if((!tn)||(tn===dojo.body())){
return;
}
ta=dojo.html.getAttribute(tn,this.dsPrefix);
}
return this.dragSources[ta];
},onKeyDown:function(e){
},onMouseDown:function(e){
if(this.disabled){
return;
}
if(dojo.render.html.ie){
if(e.button!=1){
return;
}
}else{
if(e.which!=1){
return;
}
}
var _60a=e.target.nodeType==dojo.html.TEXT_NODE?e.target.parentNode:e.target;
if(dojo.html.isTag(_60a,"button","textarea","input","select","option")){
return;
}
var ds=this.getDragSource(e);
if(!ds){
return;
}
if(!dojo.lang.inArray(this.selectedSources,ds)){
this.selectedSources.push(ds);
ds.onSelected();
}
this.mouseDownX=e.pageX;
this.mouseDownY=e.pageY;
e.preventDefault();
dojo.event.connect(document,"onmousemove",this,"onMouseMove");
},onMouseUp:function(e,_60d){
if(this.selectedSources.length==0){
return;
}
this.mouseDownX=null;
this.mouseDownY=null;
this._dragTriggered=false;
e.dragSource=this.dragSource;
if((!e.shiftKey)&&(!e.ctrlKey)){
if(this.currentDropTarget){
this.currentDropTarget.onDropStart();
}
dojo.lang.forEach(this.dragObjects,function(_60e){
var ret=null;
if(!_60e){
return;
}
if(this.currentDropTarget){
e.dragObject=_60e;
var ce=this.currentDropTarget.domNode.childNodes;
if(ce.length>0){
e.dropTarget=ce[0];
while(e.dropTarget==_60e.domNode){
e.dropTarget=e.dropTarget.nextSibling;
}
}else{
e.dropTarget=this.currentDropTarget.domNode;
}
if(this.dropAcceptable){
ret=this.currentDropTarget.onDrop(e);
}else{
this.currentDropTarget.onDragOut(e);
}
}
e.dragStatus=this.dropAcceptable&&ret?"dropSuccess":"dropFailure";
dojo.lang.delayThese([function(){
try{
_60e.dragSource.onDragEnd(e);
}
catch(err){
var _611={};
for(var i in e){
if(i=="type"){
_611.type="mouseup";
continue;
}
_611[i]=e[i];
}
_60e.dragSource.onDragEnd(_611);
}
},function(){
_60e.onDragEnd(e);
}]);
},this);
this.selectedSources=[];
this.dragObjects=[];
this.dragSource=null;
if(this.currentDropTarget){
this.currentDropTarget.onDropEnd();
}
}else{
}
dojo.event.disconnect(document,"onmousemove",this,"onMouseMove");
this.currentDropTarget=null;
},onScroll:function(){
for(var i=0;i<this.dragObjects.length;i++){
if(this.dragObjects[i].updateDragOffset){
this.dragObjects[i].updateDragOffset();
}
}
if(this.dragObjects.length){
this.cacheTargetLocations();
}
},_dragStartDistance:function(x,y){
if((!this.mouseDownX)||(!this.mouseDownX)){
return;
}
var dx=Math.abs(x-this.mouseDownX);
var dx2=dx*dx;
var dy=Math.abs(y-this.mouseDownY);
var dy2=dy*dy;
return parseInt(Math.sqrt(dx2+dy2),10);
},cacheTargetLocations:function(){
dojo.profile.start("cacheTargetLocations");
this.dropTargetDimensions=[];
dojo.lang.forEach(this.dropTargets,function(_61a){
var tn=_61a.domNode;
if(!tn||!_61a.accepts([this.dragSource])){
return;
}
var abs=dojo.html.getAbsolutePosition(tn,true);
var bb=dojo.html.getBorderBox(tn);
this.dropTargetDimensions.push([[abs.x,abs.y],[abs.x+bb.width,abs.y+bb.height],_61a]);
},this);
dojo.profile.end("cacheTargetLocations");
},onMouseMove:function(e){
if((dojo.render.html.ie)&&(e.button!=1)){
this.currentDropTarget=null;
this.onMouseUp(e,true);
return;
}
if((this.selectedSources.length)&&(!this.dragObjects.length)){
var dx;
var dy;
if(!this._dragTriggered){
this._dragTriggered=(this._dragStartDistance(e.pageX,e.pageY)>this.threshold);
if(!this._dragTriggered){
return;
}
dx=e.pageX-this.mouseDownX;
dy=e.pageY-this.mouseDownY;
}
this.dragSource=this.selectedSources[0];
dojo.lang.forEach(this.selectedSources,function(_621){
if(!_621){
return;
}
var tdo=_621.onDragStart(e);
if(tdo){
tdo.onDragStart(e);
tdo.dragOffset.y+=dy;
tdo.dragOffset.x+=dx;
tdo.dragSource=_621;
this.dragObjects.push(tdo);
}
},this);
this.previousDropTarget=null;
this.cacheTargetLocations();
}
dojo.lang.forEach(this.dragObjects,function(_623){
if(_623){
_623.onDragMove(e);
}
});
if(this.currentDropTarget){
var c=dojo.html.toCoordinateObject(this.currentDropTarget.domNode,true);
var dtp=[[c.x,c.y],[c.x+c.width,c.y+c.height]];
}
if((!this.nestedTargets)&&(dtp)&&(this.isInsideBox(e,dtp))){
if(this.dropAcceptable){
this.currentDropTarget.onDragMove(e,this.dragObjects);
}
}else{
var _626=this.findBestTarget(e);
if(_626.target===null){
if(this.currentDropTarget){
this.currentDropTarget.onDragOut(e);
this.previousDropTarget=this.currentDropTarget;
this.currentDropTarget=null;
}
this.dropAcceptable=false;
return;
}
if(this.currentDropTarget!==_626.target){
if(this.currentDropTarget){
this.previousDropTarget=this.currentDropTarget;
this.currentDropTarget.onDragOut(e);
}
this.currentDropTarget=_626.target;
e.dragObjects=this.dragObjects;
this.dropAcceptable=this.currentDropTarget.onDragOver(e);
}else{
if(this.dropAcceptable){
this.currentDropTarget.onDragMove(e,this.dragObjects);
}
}
}
},findBestTarget:function(e){
var _628=this;
var _629=new Object();
_629.target=null;
_629.points=null;
dojo.lang.every(this.dropTargetDimensions,function(_62a){
if(!_628.isInsideBox(e,_62a)){
return true;
}
_629.target=_62a[2];
_629.points=_62a;
return Boolean(_628.nestedTargets);
});
return _629;
},isInsideBox:function(e,_62c){
if((e.pageX>_62c[0][0])&&(e.pageX<_62c[1][0])&&(e.pageY>_62c[0][1])&&(e.pageY<_62c[1][1])){
return true;
}
return false;
},onMouseOver:function(e){
},onMouseOut:function(e){
}});
dojo.dnd.dragManager=new dojo.dnd.HtmlDragManager();
(function(){
var d=document;
var dm=dojo.dnd.dragManager;
dojo.event.connect(d,"onkeydown",dm,"onKeyDown");
dojo.event.connect(d,"onmouseover",dm,"onMouseOver");
dojo.event.connect(d,"onmouseout",dm,"onMouseOut");
dojo.event.connect(d,"onmousedown",dm,"onMouseDown");
dojo.event.connect(d,"onmouseup",dm,"onMouseUp");
dojo.event.connect(window,"onscroll",dm,"onScroll");
})();
dojo.provide("dojo.html.selection");
dojo.html.selectionType={NONE:0,TEXT:1,CONTROL:2};
dojo.html.clearSelection=function(){
var _631=dojo.global();
var _632=dojo.doc();
try{
if(_631["getSelection"]){
if(dojo.render.html.safari){
_631.getSelection().collapse();
}else{
_631.getSelection().removeAllRanges();
}
}else{
if(_632.selection){
if(_632.selection.empty){
_632.selection.empty();
}else{
if(_632.selection.clear){
_632.selection.clear();
}
}
}
}
return true;
}
catch(e){
dojo.debug(e);
return false;
}
};
dojo.html.disableSelection=function(_633){
_633=dojo.byId(_633)||dojo.body();
var h=dojo.render.html;
if(h.mozilla){
_633.style.MozUserSelect="none";
}else{
if(h.safari){
_633.style.KhtmlUserSelect="none";
}else{
if(h.ie){
_633.unselectable="on";
}else{
return false;
}
}
}
return true;
};
dojo.html.enableSelection=function(_635){
_635=dojo.byId(_635)||dojo.body();
var h=dojo.render.html;
if(h.mozilla){
_635.style.MozUserSelect="";
}else{
if(h.safari){
_635.style.KhtmlUserSelect="";
}else{
if(h.ie){
_635.unselectable="off";
}else{
return false;
}
}
}
return true;
};
dojo.html.selectElement=function(_637){
dojo.deprecated("dojo.html.selectElement","replaced by dojo.html.selection.selectElementChildren",0.5);
};
dojo.html.selectInputText=function(_638){
var _639=dojo.global();
var _63a=dojo.doc();
_638=dojo.byId(_638);
if(_63a["selection"]&&dojo.body()["createTextRange"]){
var _63b=_638.createTextRange();
_63b.moveStart("character",0);
_63b.moveEnd("character",_638.value.length);
_63b.select();
}else{
if(_639["getSelection"]){
var _63c=_639.getSelection();
_638.setSelectionRange(0,_638.value.length);
}
}
_638.focus();
};
dojo.html.isSelectionCollapsed=function(){
dojo.deprecated("dojo.html.isSelectionCollapsed","replaced by dojo.html.selection.isCollapsed",0.5);
return dojo.html.selection.isCollapsed();
};
dojo.lang.mixin(dojo.html.selection,{getType:function(){
if(dojo.doc()["selection"]){
return dojo.html.selectionType[dojo.doc().selection.type.toUpperCase()];
}else{
var _63d=dojo.html.selectionType.TEXT;
var oSel;
try{
oSel=dojo.global().getSelection();
}
catch(e){
}
if(oSel&&oSel.rangeCount==1){
var _63f=oSel.getRangeAt(0);
if(_63f.startContainer==_63f.endContainer&&(_63f.endOffset-_63f.startOffset)==1&&_63f.startContainer.nodeType!=dojo.dom.TEXT_NODE){
_63d=dojo.html.selectionType.CONTROL;
}
}
return _63d;
}
},isCollapsed:function(){
var _640=dojo.global();
var _641=dojo.doc();
if(_641["selection"]){
return _641.selection.createRange().text=="";
}else{
if(_640["getSelection"]){
var _642=_640.getSelection();
if(dojo.lang.isString(_642)){
return _642=="";
}else{
return _642.isCollapsed||_642.toString()=="";
}
}
}
},getSelectedElement:function(){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
if(dojo.doc()["selection"]){
var _643=dojo.doc().selection.createRange();
if(_643&&_643.item){
return dojo.doc().selection.createRange().item(0);
}
}else{
var _644=dojo.global().getSelection();
return _644.anchorNode.childNodes[_644.anchorOffset];
}
}
},getParentElement:function(){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
var p=dojo.html.selection.getSelectedElement();
if(p){
return p.parentNode;
}
}else{
if(dojo.doc()["selection"]){
return dojo.doc().selection.createRange().parentElement();
}else{
var _646=dojo.global().getSelection();
if(_646){
var node=_646.anchorNode;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.parentNode;
}
return node;
}
}
}
},getSelectedText:function(){
if(dojo.doc()["selection"]){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
return null;
}
return dojo.doc().selection.createRange().text;
}else{
var _648=dojo.global().getSelection();
if(_648){
return _648.toString();
}
}
},getSelectedHtml:function(){
if(dojo.doc()["selection"]){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
return null;
}
return dojo.doc().selection.createRange().htmlText;
}else{
var _649=dojo.global().getSelection();
if(_649&&_649.rangeCount){
var frag=_649.getRangeAt(0).cloneContents();
var div=document.createElement("div");
div.appendChild(frag);
return div.innerHTML;
}
return null;
}
},hasAncestorElement:function(_64c){
return (dojo.html.selection.getAncestorElement.apply(this,arguments)!=null);
},getAncestorElement:function(_64d){
var node=dojo.html.selection.getSelectedElement()||dojo.html.selection.getParentElement();
while(node){
if(dojo.html.selection.isTag(node,arguments).length>0){
return node;
}
node=node.parentNode;
}
return null;
},isTag:function(node,tags){
if(node&&node.tagName){
for(var i=0;i<tags.length;i++){
if(node.tagName.toLowerCase()==String(tags[i]).toLowerCase()){
return String(tags[i]).toLowerCase();
}
}
}
return "";
},selectElement:function(_652){
var _653=dojo.global();
var _654=dojo.doc();
_652=dojo.byId(_652);
if(_654.selection&&dojo.body().createTextRange){
try{
var _655=dojo.body().createControlRange();
_655.addElement(_652);
_655.select();
}
catch(e){
dojo.html.selection.selectElementChildren(_652);
}
}else{
if(_653["getSelection"]){
var _656=_653.getSelection();
if(_656["removeAllRanges"]){
var _655=_654.createRange();
_655.selectNode(_652);
_656.removeAllRanges();
_656.addRange(_655);
}
}
}
},selectElementChildren:function(_657){
var _658=dojo.global();
var _659=dojo.doc();
_657=dojo.byId(_657);
if(_659.selection&&dojo.body().createTextRange){
var _65a=dojo.body().createTextRange();
_65a.moveToElementText(_657);
_65a.select();
}else{
if(_658["getSelection"]){
var _65b=_658.getSelection();
if(_65b["setBaseAndExtent"]){
_65b.setBaseAndExtent(_657,0,_657,_657.innerText.length-1);
}else{
if(_65b["selectAllChildren"]){
_65b.selectAllChildren(_657);
}
}
}
}
},getBookmark:function(){
var _65c;
var _65d=dojo.doc();
if(_65d["selection"]){
var _65e=_65d.selection.createRange();
_65c=_65e.getBookmark();
}else{
var _65f;
try{
_65f=dojo.global().getSelection();
}
catch(e){
}
if(_65f){
var _65e=_65f.getRangeAt(0);
_65c=_65e.cloneRange();
}else{
dojo.debug("No idea how to store the current selection for this browser!");
}
}
return _65c;
},moveToBookmark:function(_660){
var _661=dojo.doc();
if(_661["selection"]){
var _662=_661.selection.createRange();
_662.moveToBookmark(_660);
_662.select();
}else{
var _663;
try{
_663=dojo.global().getSelection();
}
catch(e){
}
if(_663&&_663["removeAllRanges"]){
_663.removeAllRanges();
_663.addRange(_660);
}else{
dojo.debug("No idea how to restore selection for this browser!");
}
}
},collapse:function(_664){
if(dojo.global()["getSelection"]){
var _665=dojo.global().getSelection();
if(_665.removeAllRanges){
if(_664){
_665.collapseToStart();
}else{
_665.collapseToEnd();
}
}else{
dojo.global().getSelection().collapse(_664);
}
}else{
if(dojo.doc().selection){
var _666=dojo.doc().selection.createRange();
_666.collapse(_664);
_666.select();
}
}
},remove:function(){
if(dojo.doc().selection){
var _667=dojo.doc().selection;
if(_667.type.toUpperCase()!="NONE"){
_667.clear();
}
return _667;
}else{
var _667=dojo.global().getSelection();
for(var i=0;i<_667.rangeCount;i++){
_667.getRangeAt(i).deleteContents();
}
return _667;
}
}});
dojo.provide("dojo.html.iframe");
dojo.html.iframeContentWindow=function(_669){
var win=dojo.html.getDocumentWindow(dojo.html.iframeContentDocument(_669))||dojo.html.iframeContentDocument(_669).__parent__||(_669.name&&document.frames[_669.name])||null;
return win;
};
dojo.html.iframeContentDocument=function(_66b){
var doc=_66b.contentDocument||((_66b.contentWindow)&&(_66b.contentWindow.document))||((_66b.name)&&(document.frames[_66b.name])&&(document.frames[_66b.name].document))||null;
return doc;
};
dojo.html.BackgroundIframe=function(node){
if(dojo.render.html.ie55||dojo.render.html.ie60){
var html="<iframe src='javascript:false'"+" style='position: absolute; left: 0px; top: 0px; width: 100%; height: 100%;"+"z-index: -1; filter:Alpha(Opacity=\"0\");' "+">";
this.iframe=dojo.doc().createElement(html);
this.iframe.tabIndex=-1;
if(node){
node.appendChild(this.iframe);
this.domNode=node;
}else{
dojo.body().appendChild(this.iframe);
this.iframe.style.display="none";
}
}
};
dojo.lang.extend(dojo.html.BackgroundIframe,{iframe:null,onResized:function(){
if(this.iframe&&this.domNode&&this.domNode.parentNode){
var _66f=dojo.html.getMarginBox(this.domNode);
if(_66f.width==0||_66f.height==0){
dojo.lang.setTimeout(this,this.onResized,100);
return;
}
this.iframe.style.width=_66f.width+"px";
this.iframe.style.height=_66f.height+"px";
}
},size:function(node){
if(!this.iframe){
return;
}
var _671=dojo.html.toCoordinateObject(node,true,dojo.html.boxSizing.BORDER_BOX);
with(this.iframe.style){
width=_671.width+"px";
height=_671.height+"px";
left=_671.left+"px";
top=_671.top+"px";
}
},setZIndex:function(node){
if(!this.iframe){
return;
}
if(dojo.dom.isNode(node)){
this.iframe.style.zIndex=dojo.html.getStyle(node,"z-index")-1;
}else{
if(!isNaN(node)){
this.iframe.style.zIndex=node;
}
}
},show:function(){
if(this.iframe){
this.iframe.style.display="block";
}
},hide:function(){
if(this.iframe){
this.iframe.style.display="none";
}
},remove:function(){
if(this.iframe){
dojo.html.removeNode(this.iframe,true);
delete this.iframe;
this.iframe=null;
}
}});
dojo.provide("dojo.gfx.color");
dojo.gfx.color.Color=function(r,g,b,a){
if(dojo.lang.isArray(r)){
this.r=r[0];
this.g=r[1];
this.b=r[2];
this.a=r[3]||1;
}else{
if(dojo.lang.isString(r)){
var rgb=dojo.gfx.color.extractRGB(r);
this.r=rgb[0];
this.g=rgb[1];
this.b=rgb[2];
this.a=g||1;
}else{
if(r instanceof dojo.gfx.color.Color){
this.r=r.r;
this.b=r.b;
this.g=r.g;
this.a=r.a;
}else{
this.r=r;
this.g=g;
this.b=b;
this.a=a;
}
}
}
};
dojo.gfx.color.Color.fromArray=function(arr){
return new dojo.gfx.color.Color(arr[0],arr[1],arr[2],arr[3]);
};
dojo.extend(dojo.gfx.color.Color,{toRgb:function(_679){
if(_679){
return this.toRgba();
}else{
return [this.r,this.g,this.b];
}
},toRgba:function(){
return [this.r,this.g,this.b,this.a];
},toHex:function(){
return dojo.gfx.color.rgb2hex(this.toRgb());
},toCss:function(){
return "rgb("+this.toRgb().join()+")";
},toString:function(){
return this.toHex();
},blend:function(_67a,_67b){
var rgb=null;
if(dojo.lang.isArray(_67a)){
rgb=_67a;
}else{
if(_67a instanceof dojo.gfx.color.Color){
rgb=_67a.toRgb();
}else{
rgb=new dojo.gfx.color.Color(_67a).toRgb();
}
}
return dojo.gfx.color.blend(this.toRgb(),rgb,_67b);
}});
dojo.gfx.color.named={white:[255,255,255],black:[0,0,0],red:[255,0,0],green:[0,255,0],lime:[0,255,0],blue:[0,0,255],navy:[0,0,128],gray:[128,128,128],silver:[192,192,192]};
dojo.gfx.color.blend=function(a,b,_67f){
if(typeof a=="string"){
return dojo.gfx.color.blendHex(a,b,_67f);
}
if(!_67f){
_67f=0;
}
_67f=Math.min(Math.max(-1,_67f),1);
_67f=((_67f+1)/2);
var c=[];
for(var x=0;x<3;x++){
c[x]=parseInt(b[x]+((a[x]-b[x])*_67f));
}
return c;
};
dojo.gfx.color.blendHex=function(a,b,_684){
return dojo.gfx.color.rgb2hex(dojo.gfx.color.blend(dojo.gfx.color.hex2rgb(a),dojo.gfx.color.hex2rgb(b),_684));
};
dojo.gfx.color.extractRGB=function(_685){
var hex="0123456789abcdef";
_685=_685.toLowerCase();
if(_685.indexOf("rgb")==0){
var _687=_685.match(/rgba*\((\d+), *(\d+), *(\d+)/i);
var ret=_687.splice(1,3);
return ret;
}else{
var _689=dojo.gfx.color.hex2rgb(_685);
if(_689){
return _689;
}else{
return dojo.gfx.color.named[_685]||[255,255,255];
}
}
};
dojo.gfx.color.hex2rgb=function(hex){
var _68b="0123456789ABCDEF";
var rgb=new Array(3);
if(hex.indexOf("#")==0){
hex=hex.substring(1);
}
hex=hex.toUpperCase();
if(hex.replace(new RegExp("["+_68b+"]","g"),"")!=""){
return null;
}
if(hex.length==3){
rgb[0]=hex.charAt(0)+hex.charAt(0);
rgb[1]=hex.charAt(1)+hex.charAt(1);
rgb[2]=hex.charAt(2)+hex.charAt(2);
}else{
rgb[0]=hex.substring(0,2);
rgb[1]=hex.substring(2,4);
rgb[2]=hex.substring(4);
}
for(var i=0;i<rgb.length;i++){
rgb[i]=_68b.indexOf(rgb[i].charAt(0))*16+_68b.indexOf(rgb[i].charAt(1));
}
return rgb;
};
dojo.gfx.color.rgb2hex=function(r,g,b){
if(dojo.lang.isArray(r)){
g=r[1]||0;
b=r[2]||0;
r=r[0]||0;
}
var ret=dojo.lang.map([r,g,b],function(x){
x=new Number(x);
var s=x.toString(16);
while(s.length<2){
s="0"+s;
}
return s;
});
ret.unshift("#");
return ret.join("");
};
dojo.provide("dojo.lfx.Animation");
dojo.lfx.Line=function(_694,end){
this.start=_694;
this.end=end;
if(dojo.lang.isArray(_694)){
var diff=[];
dojo.lang.forEach(this.start,function(s,i){
diff[i]=this.end[i]-s;
},this);
this.getValue=function(n){
var res=[];
dojo.lang.forEach(this.start,function(s,i){
res[i]=(diff[i]*n)+s;
},this);
return res;
};
}else{
var diff=end-_694;
this.getValue=function(n){
return (diff*n)+this.start;
};
}
};
if((dojo.render.html.khtml)&&(!dojo.render.html.safari)){
dojo.lfx.easeDefault=function(n){
return (parseFloat("0.5")+((Math.sin((n+parseFloat("1.5"))*Math.PI))/2));
};
}else{
dojo.lfx.easeDefault=function(n){
return (0.5+((Math.sin((n+1.5)*Math.PI))/2));
};
}
dojo.lfx.easeIn=function(n){
return Math.pow(n,3);
};
dojo.lfx.easeOut=function(n){
return (1-Math.pow(1-n,3));
};
dojo.lfx.easeInOut=function(n){
return ((3*Math.pow(n,2))-(2*Math.pow(n,3)));
};
dojo.lfx.IAnimation=function(){
};
dojo.lang.extend(dojo.lfx.IAnimation,{curve:null,duration:1000,easing:null,repeatCount:0,rate:10,handler:null,beforeBegin:null,onBegin:null,onAnimate:null,onEnd:null,onPlay:null,onPause:null,onStop:null,play:null,pause:null,stop:null,connect:function(evt,_6a4,_6a5){
if(!_6a5){
_6a5=_6a4;
_6a4=this;
}
_6a5=dojo.lang.hitch(_6a4,_6a5);
var _6a6=this[evt]||function(){
};
this[evt]=function(){
var ret=_6a6.apply(this,arguments);
_6a5.apply(this,arguments);
return ret;
};
return this;
},fire:function(evt,args){
if(this[evt]){
this[evt].apply(this,(args||[]));
}
return this;
},repeat:function(_6aa){
this.repeatCount=_6aa;
return this;
},_active:false,_paused:false});
dojo.lfx.Animation=function(_6ab,_6ac,_6ad,_6ae,_6af,rate){
dojo.lfx.IAnimation.call(this);
if(dojo.lang.isNumber(_6ab)||(!_6ab&&_6ac.getValue)){
rate=_6af;
_6af=_6ae;
_6ae=_6ad;
_6ad=_6ac;
_6ac=_6ab;
_6ab=null;
}else{
if(_6ab.getValue||dojo.lang.isArray(_6ab)){
rate=_6ae;
_6af=_6ad;
_6ae=_6ac;
_6ad=_6ab;
_6ac=null;
_6ab=null;
}
}
if(dojo.lang.isArray(_6ad)){
this.curve=new dojo.lfx.Line(_6ad[0],_6ad[1]);
}else{
this.curve=_6ad;
}
if(_6ac!=null&&_6ac>0){
this.duration=_6ac;
}
if(_6af){
this.repeatCount=_6af;
}
if(rate){
this.rate=rate;
}
if(_6ab){
dojo.lang.forEach(["handler","beforeBegin","onBegin","onEnd","onPlay","onStop","onAnimate"],function(item){
if(_6ab[item]){
this.connect(item,_6ab[item]);
}
},this);
}
if(_6ae&&dojo.lang.isFunction(_6ae)){
this.easing=_6ae;
}
};
dojo.inherits(dojo.lfx.Animation,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Animation,{_startTime:null,_endTime:null,_timer:null,_percent:0,_startRepeatCount:0,play:function(_6b2,_6b3){
if(_6b3){
clearTimeout(this._timer);
this._active=false;
this._paused=false;
this._percent=0;
}else{
if(this._active&&!this._paused){
return this;
}
}
this.fire("handler",["beforeBegin"]);
this.fire("beforeBegin");
if(_6b2>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_6b3);
}),_6b2);
return this;
}
this._startTime=new Date().valueOf();
if(this._paused){
this._startTime-=(this.duration*this._percent/100);
}
this._endTime=this._startTime+this.duration;
this._active=true;
this._paused=false;
var step=this._percent/100;
var _6b5=this.curve.getValue(step);
if(this._percent==0){
if(!this._startRepeatCount){
this._startRepeatCount=this.repeatCount;
}
this.fire("handler",["begin",_6b5]);
this.fire("onBegin",[_6b5]);
}
this.fire("handler",["play",_6b5]);
this.fire("onPlay",[_6b5]);
this._cycle();
return this;
},pause:function(){
clearTimeout(this._timer);
if(!this._active){
return this;
}
this._paused=true;
var _6b6=this.curve.getValue(this._percent/100);
this.fire("handler",["pause",_6b6]);
this.fire("onPause",[_6b6]);
return this;
},gotoPercent:function(pct,_6b8){
clearTimeout(this._timer);
this._active=true;
this._paused=true;
this._percent=pct;
if(_6b8){
this.play();
}
return this;
},stop:function(_6b9){
clearTimeout(this._timer);
var step=this._percent/100;
if(_6b9){
step=1;
}
var _6bb=this.curve.getValue(step);
this.fire("handler",["stop",_6bb]);
this.fire("onStop",[_6bb]);
this._active=false;
this._paused=false;
return this;
},status:function(){
if(this._active){
return this._paused?"paused":"playing";
}else{
return "stopped";
}
return this;
},_cycle:function(){
clearTimeout(this._timer);
if(this._active){
var curr=new Date().valueOf();
var step=(curr-this._startTime)/(this._endTime-this._startTime);
if(step>=1){
step=1;
this._percent=100;
}else{
this._percent=step*100;
}
if((this.easing)&&(dojo.lang.isFunction(this.easing))){
step=this.easing(step);
}
var _6be=this.curve.getValue(step);
this.fire("handler",["animate",_6be]);
this.fire("onAnimate",[_6be]);
if(step<1){
this._timer=setTimeout(dojo.lang.hitch(this,"_cycle"),this.rate);
}else{
this._active=false;
this.fire("handler",["end"]);
this.fire("onEnd");
if(this.repeatCount>0){
this.repeatCount--;
this.play(null,true);
}else{
if(this.repeatCount==-1){
this.play(null,true);
}else{
if(this._startRepeatCount){
this.repeatCount=this._startRepeatCount;
this._startRepeatCount=0;
}
}
}
}
}
return this;
}});
dojo.lfx.Combine=function(_6bf){
dojo.lfx.IAnimation.call(this);
this._anims=[];
this._animsEnded=0;
var _6c0=arguments;
if(_6c0.length==1&&(dojo.lang.isArray(_6c0[0])||dojo.lang.isArrayLike(_6c0[0]))){
_6c0=_6c0[0];
}
dojo.lang.forEach(_6c0,function(anim){
this._anims.push(anim);
anim.connect("onEnd",dojo.lang.hitch(this,"_onAnimsEnded"));
},this);
};
dojo.inherits(dojo.lfx.Combine,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Combine,{_animsEnded:0,play:function(_6c2,_6c3){
if(!this._anims.length){
return this;
}
this.fire("beforeBegin");
if(_6c2>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_6c3);
}),_6c2);
return this;
}
if(_6c3||this._anims[0].percent==0){
this.fire("onBegin");
}
this.fire("onPlay");
this._animsCall("play",null,_6c3);
return this;
},pause:function(){
this.fire("onPause");
this._animsCall("pause");
return this;
},stop:function(_6c4){
this.fire("onStop");
this._animsCall("stop",_6c4);
return this;
},_onAnimsEnded:function(){
this._animsEnded++;
if(this._animsEnded>=this._anims.length){
this.fire("onEnd");
}
return this;
},_animsCall:function(_6c5){
var args=[];
if(arguments.length>1){
for(var i=1;i<arguments.length;i++){
args.push(arguments[i]);
}
}
var _6c8=this;
dojo.lang.forEach(this._anims,function(anim){
anim[_6c5](args);
},_6c8);
return this;
}});
dojo.lfx.Chain=function(_6ca){
dojo.lfx.IAnimation.call(this);
this._anims=[];
this._currAnim=-1;
var _6cb=arguments;
if(_6cb.length==1&&(dojo.lang.isArray(_6cb[0])||dojo.lang.isArrayLike(_6cb[0]))){
_6cb=_6cb[0];
}
var _6cc=this;
dojo.lang.forEach(_6cb,function(anim,i,_6cf){
this._anims.push(anim);
if(i<_6cf.length-1){
anim.connect("onEnd",dojo.lang.hitch(this,"_playNext"));
}else{
anim.connect("onEnd",dojo.lang.hitch(this,function(){
this.fire("onEnd");
}));
}
},this);
};
dojo.inherits(dojo.lfx.Chain,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Chain,{_currAnim:-1,play:function(_6d0,_6d1){
if(!this._anims.length){
return this;
}
if(_6d1||!this._anims[this._currAnim]){
this._currAnim=0;
}
var _6d2=this._anims[this._currAnim];
this.fire("beforeBegin");
if(_6d0>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_6d1);
}),_6d0);
return this;
}
if(_6d2){
if(this._currAnim==0){
this.fire("handler",["begin",this._currAnim]);
this.fire("onBegin",[this._currAnim]);
}
this.fire("onPlay",[this._currAnim]);
_6d2.play(null,_6d1);
}
return this;
},pause:function(){
if(this._anims[this._currAnim]){
this._anims[this._currAnim].pause();
this.fire("onPause",[this._currAnim]);
}
return this;
},playPause:function(){
if(this._anims.length==0){
return this;
}
if(this._currAnim==-1){
this._currAnim=0;
}
var _6d3=this._anims[this._currAnim];
if(_6d3){
if(!_6d3._active||_6d3._paused){
this.play();
}else{
this.pause();
}
}
return this;
},stop:function(){
var _6d4=this._anims[this._currAnim];
if(_6d4){
_6d4.stop();
this.fire("onStop",[this._currAnim]);
}
return _6d4;
},_playNext:function(){
if(this._currAnim==-1||this._anims.length==0){
return this;
}
this._currAnim++;
if(this._anims[this._currAnim]){
this._anims[this._currAnim].play(null,true);
}
return this;
}});
dojo.lfx.combine=function(_6d5){
var _6d6=arguments;
if(dojo.lang.isArray(arguments[0])){
_6d6=arguments[0];
}
if(_6d6.length==1){
return _6d6[0];
}
return new dojo.lfx.Combine(_6d6);
};
dojo.lfx.chain=function(_6d7){
var _6d8=arguments;
if(dojo.lang.isArray(arguments[0])){
_6d8=arguments[0];
}
if(_6d8.length==1){
return _6d8[0];
}
return new dojo.lfx.Chain(_6d8);
};
dojo.provide("dojo.html.color");
dojo.html.getBackgroundColor=function(node){
node=dojo.byId(node);
var _6da;
do{
_6da=dojo.html.getStyle(node,"background-color");
if(_6da.toLowerCase()=="rgba(0, 0, 0, 0)"){
_6da="transparent";
}
if(node==document.getElementsByTagName("body")[0]){
node=null;
break;
}
node=node.parentNode;
}while(node&&dojo.lang.inArray(["transparent",""],_6da));
if(_6da=="transparent"){
_6da=[255,255,255,0];
}else{
_6da=dojo.gfx.color.extractRGB(_6da);
}
return _6da;
};
dojo.provide("dojo.lfx.html");
dojo.lfx.html._byId=function(_6db){
if(!_6db){
return [];
}
if(dojo.lang.isArrayLike(_6db)){
if(!_6db.alreadyChecked){
var n=[];
dojo.lang.forEach(_6db,function(node){
n.push(dojo.byId(node));
});
n.alreadyChecked=true;
return n;
}else{
return _6db;
}
}else{
var n=[];
n.push(dojo.byId(_6db));
n.alreadyChecked=true;
return n;
}
};
dojo.lfx.html.propertyAnimation=function(_6de,_6df,_6e0,_6e1,_6e2){
_6de=dojo.lfx.html._byId(_6de);
var _6e3={"propertyMap":_6df,"nodes":_6de,"duration":_6e0,"easing":_6e1||dojo.lfx.easeDefault};
var _6e4=function(args){
if(args.nodes.length==1){
var pm=args.propertyMap;
if(!dojo.lang.isArray(args.propertyMap)){
var parr=[];
for(var _6e8 in pm){
pm[_6e8].property=_6e8;
parr.push(pm[_6e8]);
}
pm=args.propertyMap=parr;
}
dojo.lang.forEach(pm,function(prop){
if(dj_undef("start",prop)){
if(prop.property!="opacity"){
prop.start=parseInt(dojo.html.getComputedStyle(args.nodes[0],prop.property));
}else{
prop.start=dojo.html.getOpacity(args.nodes[0]);
}
}
});
}
};
var _6ea=function(_6eb){
var _6ec=[];
dojo.lang.forEach(_6eb,function(c){
_6ec.push(Math.round(c));
});
return _6ec;
};
var _6ee=function(n,_6f0){
n=dojo.byId(n);
if(!n||!n.style){
return;
}
for(var s in _6f0){
try{
if(s=="opacity"){
dojo.html.setOpacity(n,_6f0[s]);
}else{
n.style[s]=_6f0[s];
}
}
catch(e){
dojo.debug(e);
}
}
};
var _6f2=function(_6f3){
this._properties=_6f3;
this.diffs=new Array(_6f3.length);
dojo.lang.forEach(_6f3,function(prop,i){
if(dojo.lang.isFunction(prop.start)){
prop.start=prop.start(prop,i);
}
if(dojo.lang.isFunction(prop.end)){
prop.end=prop.end(prop,i);
}
if(dojo.lang.isArray(prop.start)){
this.diffs[i]=null;
}else{
if(prop.start instanceof dojo.gfx.color.Color){
prop.startRgb=prop.start.toRgb();
prop.endRgb=prop.end.toRgb();
}else{
this.diffs[i]=prop.end-prop.start;
}
}
},this);
this.getValue=function(n){
var ret={};
dojo.lang.forEach(this._properties,function(prop,i){
var _6fa=null;
if(dojo.lang.isArray(prop.start)){
}else{
if(prop.start instanceof dojo.gfx.color.Color){
_6fa=(prop.units||"rgb")+"(";
for(var j=0;j<prop.startRgb.length;j++){
_6fa+=Math.round(((prop.endRgb[j]-prop.startRgb[j])*n)+prop.startRgb[j])+(j<prop.startRgb.length-1?",":"");
}
_6fa+=")";
}else{
_6fa=((this.diffs[i])*n)+prop.start+(prop.property!="opacity"?prop.units||"px":"");
}
}
ret[dojo.html.toCamelCase(prop.property)]=_6fa;
},this);
return ret;
};
};
var anim=new dojo.lfx.Animation({beforeBegin:function(){
_6e4(_6e3);
anim.curve=new _6f2(_6e3.propertyMap);
},onAnimate:function(_6fd){
dojo.lang.forEach(_6e3.nodes,function(node){
_6ee(node,_6fd);
});
}},_6e3.duration,null,_6e3.easing);
if(_6e2){
for(var x in _6e2){
if(dojo.lang.isFunction(_6e2[x])){
anim.connect(x,anim,_6e2[x]);
}
}
}
return anim;
};
dojo.lfx.html._makeFadeable=function(_700){
var _701=function(node){
if(dojo.render.html.ie){
if((node.style.zoom.length==0)&&(dojo.html.getStyle(node,"zoom")=="normal")){
node.style.zoom="1";
}
if((node.style.width.length==0)&&(dojo.html.getStyle(node,"width")=="auto")){
node.style.width="auto";
}
}
};
if(dojo.lang.isArrayLike(_700)){
dojo.lang.forEach(_700,_701);
}else{
_701(_700);
}
};
dojo.lfx.html.fade=function(_703,_704,_705,_706,_707){
_703=dojo.lfx.html._byId(_703);
var _708={property:"opacity"};
if(!dj_undef("start",_704)){
_708.start=_704.start;
}else{
_708.start=function(){
return dojo.html.getOpacity(_703[0]);
};
}
if(!dj_undef("end",_704)){
_708.end=_704.end;
}else{
dojo.raise("dojo.lfx.html.fade needs an end value");
}
var anim=dojo.lfx.propertyAnimation(_703,[_708],_705,_706);
anim.connect("beforeBegin",function(){
dojo.lfx.html._makeFadeable(_703);
});
if(_707){
anim.connect("onEnd",function(){
_707(_703,anim);
});
}
return anim;
};
dojo.lfx.html.fadeIn=function(_70a,_70b,_70c,_70d){
return dojo.lfx.html.fade(_70a,{end:1},_70b,_70c,_70d);
};
dojo.lfx.html.fadeOut=function(_70e,_70f,_710,_711){
return dojo.lfx.html.fade(_70e,{end:0},_70f,_710,_711);
};
dojo.lfx.html.fadeShow=function(_712,_713,_714,_715){
_712=dojo.lfx.html._byId(_712);
dojo.lang.forEach(_712,function(node){
dojo.html.setOpacity(node,0);
});
var anim=dojo.lfx.html.fadeIn(_712,_713,_714,_715);
anim.connect("beforeBegin",function(){
if(dojo.lang.isArrayLike(_712)){
dojo.lang.forEach(_712,dojo.html.show);
}else{
dojo.html.show(_712);
}
});
return anim;
};
dojo.lfx.html.fadeHide=function(_718,_719,_71a,_71b){
var anim=dojo.lfx.html.fadeOut(_718,_719,_71a,function(){
if(dojo.lang.isArrayLike(_718)){
dojo.lang.forEach(_718,dojo.html.hide);
}else{
dojo.html.hide(_718);
}
if(_71b){
_71b(_718,anim);
}
});
return anim;
};
dojo.lfx.html.wipeIn=function(_71d,_71e,_71f,_720){
_71d=dojo.lfx.html._byId(_71d);
var _721=[];
dojo.lang.forEach(_71d,function(node){
var _723={};
var _724,_725,_726;
with(node.style){
_724=top;
_725=left;
_726=position;
top="-9999px";
left="-9999px";
position="absolute";
display="";
}
var _727=dojo.html.getBorderBox(node).height;
with(node.style){
top=_724;
left=_725;
position=_726;
display="none";
}
var anim=dojo.lfx.propertyAnimation(node,{"height":{start:1,end:function(){
return _727;
}}},_71e,_71f);
anim.connect("beforeBegin",function(){
_723.overflow=node.style.overflow;
_723.height=node.style.height;
with(node.style){
overflow="hidden";
height="1px";
}
dojo.html.show(node);
});
anim.connect("onEnd",function(){
with(node.style){
overflow=_723.overflow;
height=_723.height;
}
if(_720){
_720(node,anim);
}
});
_721.push(anim);
});
return dojo.lfx.combine(_721);
};
dojo.lfx.html.wipeOut=function(_729,_72a,_72b,_72c){
_729=dojo.lfx.html._byId(_729);
var _72d=[];
dojo.lang.forEach(_729,function(node){
var _72f={};
var anim=dojo.lfx.propertyAnimation(node,{"height":{start:function(){
return dojo.html.getContentBox(node).height;
},end:1}},_72a,_72b,{"beforeBegin":function(){
_72f.overflow=node.style.overflow;
_72f.height=node.style.height;
with(node.style){
overflow="hidden";
}
dojo.html.show(node);
},"onEnd":function(){
dojo.html.hide(node);
with(node.style){
overflow=_72f.overflow;
height=_72f.height;
}
if(_72c){
_72c(node,anim);
}
}});
_72d.push(anim);
});
return dojo.lfx.combine(_72d);
};
dojo.lfx.html.slideTo=function(_731,_732,_733,_734,_735){
_731=dojo.lfx.html._byId(_731);
var _736=[];
var _737=dojo.html.getComputedStyle;
if(dojo.lang.isArray(_732)){
dojo.deprecated("dojo.lfx.html.slideTo(node, array)","use dojo.lfx.html.slideTo(node, {top: value, left: value});","0.5");
_732={top:_732[0],left:_732[1]};
}
dojo.lang.forEach(_731,function(node){
var top=null;
var left=null;
var init=(function(){
var _73c=node;
return function(){
var pos=_737(_73c,"position");
top=(pos=="absolute"?node.offsetTop:parseInt(_737(node,"top"))||0);
left=(pos=="absolute"?node.offsetLeft:parseInt(_737(node,"left"))||0);
if(!dojo.lang.inArray(["absolute","relative"],pos)){
var ret=dojo.html.abs(_73c,true);
dojo.html.setStyleAttributes(_73c,"position:absolute;top:"+ret.y+"px;left:"+ret.x+"px;");
top=ret.y;
left=ret.x;
}
};
})();
init();
var anim=dojo.lfx.propertyAnimation(node,{"top":{start:top,end:(_732.top||0)},"left":{start:left,end:(_732.left||0)}},_733,_734,{"beforeBegin":init});
if(_735){
anim.connect("onEnd",function(){
_735(_731,anim);
});
}
_736.push(anim);
});
return dojo.lfx.combine(_736);
};
dojo.lfx.html.slideBy=function(_740,_741,_742,_743,_744){
_740=dojo.lfx.html._byId(_740);
var _745=[];
var _746=dojo.html.getComputedStyle;
if(dojo.lang.isArray(_741)){
dojo.deprecated("dojo.lfx.html.slideBy(node, array)","use dojo.lfx.html.slideBy(node, {top: value, left: value});","0.5");
_741={top:_741[0],left:_741[1]};
}
dojo.lang.forEach(_740,function(node){
var top=null;
var left=null;
var init=(function(){
var _74b=node;
return function(){
var pos=_746(_74b,"position");
top=(pos=="absolute"?node.offsetTop:parseInt(_746(node,"top"))||0);
left=(pos=="absolute"?node.offsetLeft:parseInt(_746(node,"left"))||0);
if(!dojo.lang.inArray(["absolute","relative"],pos)){
var ret=dojo.html.abs(_74b,true);
dojo.html.setStyleAttributes(_74b,"position:absolute;top:"+ret.y+"px;left:"+ret.x+"px;");
top=ret.y;
left=ret.x;
}
};
})();
init();
var anim=dojo.lfx.propertyAnimation(node,{"top":{start:top,end:top+(_741.top||0)},"left":{start:left,end:left+(_741.left||0)}},_742,_743).connect("beforeBegin",init);
if(_744){
anim.connect("onEnd",function(){
_744(_740,anim);
});
}
_745.push(anim);
});
return dojo.lfx.combine(_745);
};
dojo.lfx.html.explode=function(_74f,_750,_751,_752,_753){
var h=dojo.html;
_74f=dojo.byId(_74f);
_750=dojo.byId(_750);
var _755=h.toCoordinateObject(_74f,true);
var _756=document.createElement("div");
h.copyStyle(_756,_750);
if(_750.explodeClassName){
_756.className=_750.explodeClassName;
}
with(_756.style){
position="absolute";
display="none";
var _757=h.getStyle(_74f,"background-color");
backgroundColor=_757?_757.toLowerCase():"transparent";
backgroundColor=(backgroundColor=="transparent")?"rgb(221, 221, 221)":backgroundColor;
}
dojo.body().appendChild(_756);
with(_750.style){
visibility="hidden";
display="block";
}
var _758=h.toCoordinateObject(_750,true);
with(_750.style){
display="none";
visibility="visible";
}
var _759={opacity:{start:0.5,end:1}};
dojo.lang.forEach(["height","width","top","left"],function(type){
_759[type]={start:_755[type],end:_758[type]};
});
var anim=new dojo.lfx.propertyAnimation(_756,_759,_751,_752,{"beforeBegin":function(){
h.setDisplay(_756,"block");
},"onEnd":function(){
h.setDisplay(_750,"block");
_756.parentNode.removeChild(_756);
}});
if(_753){
anim.connect("onEnd",function(){
_753(_750,anim);
});
}
return anim;
};
dojo.lfx.html.implode=function(_75c,end,_75e,_75f,_760){
var h=dojo.html;
_75c=dojo.byId(_75c);
end=dojo.byId(end);
var _762=dojo.html.toCoordinateObject(_75c,true);
var _763=dojo.html.toCoordinateObject(end,true);
var _764=document.createElement("div");
dojo.html.copyStyle(_764,_75c);
if(_75c.explodeClassName){
_764.className=_75c.explodeClassName;
}
dojo.html.setOpacity(_764,0.3);
with(_764.style){
position="absolute";
display="none";
backgroundColor=h.getStyle(_75c,"background-color").toLowerCase();
}
dojo.body().appendChild(_764);
var _765={opacity:{start:1,end:0.5}};
dojo.lang.forEach(["height","width","top","left"],function(type){
_765[type]={start:_762[type],end:_763[type]};
});
var anim=new dojo.lfx.propertyAnimation(_764,_765,_75e,_75f,{"beforeBegin":function(){
dojo.html.hide(_75c);
dojo.html.show(_764);
},"onEnd":function(){
_764.parentNode.removeChild(_764);
}});
if(_760){
anim.connect("onEnd",function(){
_760(_75c,anim);
});
}
return anim;
};
dojo.lfx.html.highlight=function(_768,_769,_76a,_76b,_76c){
_768=dojo.lfx.html._byId(_768);
var _76d=[];
dojo.lang.forEach(_768,function(node){
var _76f=dojo.html.getBackgroundColor(node);
var bg=dojo.html.getStyle(node,"background-color").toLowerCase();
var _771=dojo.html.getStyle(node,"background-image");
var _772=(bg=="transparent"||bg=="rgba(0, 0, 0, 0)");
while(_76f.length>3){
_76f.pop();
}
var rgb=new dojo.gfx.color.Color(_769);
var _774=new dojo.gfx.color.Color(_76f);
var anim=dojo.lfx.propertyAnimation(node,{"background-color":{start:rgb,end:_774}},_76a,_76b,{"beforeBegin":function(){
if(_771){
node.style.backgroundImage="none";
}
node.style.backgroundColor="rgb("+rgb.toRgb().join(",")+")";
},"onEnd":function(){
if(_771){
node.style.backgroundImage=_771;
}
if(_772){
node.style.backgroundColor="transparent";
}
if(_76c){
_76c(node,anim);
}
}});
_76d.push(anim);
});
return dojo.lfx.combine(_76d);
};
dojo.lfx.html.unhighlight=function(_776,_777,_778,_779,_77a){
_776=dojo.lfx.html._byId(_776);
var _77b=[];
dojo.lang.forEach(_776,function(node){
var _77d=new dojo.gfx.color.Color(dojo.html.getBackgroundColor(node));
var rgb=new dojo.gfx.color.Color(_777);
var _77f=dojo.html.getStyle(node,"background-image");
var anim=dojo.lfx.propertyAnimation(node,{"background-color":{start:_77d,end:rgb}},_778,_779,{"beforeBegin":function(){
if(_77f){
node.style.backgroundImage="none";
}
node.style.backgroundColor="rgb("+_77d.toRgb().join(",")+")";
},"onEnd":function(){
if(_77a){
_77a(node,anim);
}
}});
_77b.push(anim);
});
return dojo.lfx.combine(_77b);
};
dojo.lang.mixin(dojo.lfx,dojo.lfx.html);
dojo.kwCompoundRequire({browser:["dojo.lfx.html"],dashboard:["dojo.lfx.html"]});
dojo.provide("dojo.lfx.*");
dojo.provide("dojo.dnd.HtmlDragAndDrop");
dojo.declare("dojo.dnd.HtmlDragSource",dojo.dnd.DragSource,{dragClass:"",onDragStart:function(){
var _781=new dojo.dnd.HtmlDragObject(this.dragObject,this.type);
if(this.dragClass){
_781.dragClass=this.dragClass;
}
if(this.constrainToContainer){
_781.constrainTo(this.constrainingContainer||this.domNode.parentNode);
}
return _781;
},setDragHandle:function(node){
node=dojo.byId(node);
dojo.dnd.dragManager.unregisterDragSource(this);
this.domNode=node;
dojo.dnd.dragManager.registerDragSource(this);
},setDragTarget:function(node){
this.dragObject=node;
},constrainTo:function(_784){
this.constrainToContainer=true;
if(_784){
this.constrainingContainer=_784;
}
},onSelected:function(){
for(var i=0;i<this.dragObjects.length;i++){
dojo.dnd.dragManager.selectedSources.push(new dojo.dnd.HtmlDragSource(this.dragObjects[i]));
}
},addDragObjects:function(el){
for(var i=0;i<arguments.length;i++){
this.dragObjects.push(dojo.byId(arguments[i]));
}
}},function(node,type){
node=dojo.byId(node);
this.dragObjects=[];
this.constrainToContainer=false;
if(node){
this.domNode=node;
this.dragObject=node;
this.type=(type)||(this.domNode.nodeName.toLowerCase());
dojo.dnd.DragSource.prototype.reregister.call(this);
}
});
dojo.declare("dojo.dnd.HtmlDragObject",dojo.dnd.DragObject,{dragClass:"",opacity:0.5,createIframe:true,disableX:false,disableY:false,createDragNode:function(){
var node=this.domNode.cloneNode(true);
if(this.dragClass){
dojo.html.addClass(node,this.dragClass);
}
if(this.opacity<1){
dojo.html.setOpacity(node,this.opacity);
}
var ltn=node.tagName.toLowerCase();
var isTr=(ltn=="tr");
if((isTr)||(ltn=="tbody")){
var doc=this.domNode.ownerDocument;
var _78e=doc.createElement("table");
if(isTr){
var _78f=doc.createElement("tbody");
_78e.appendChild(_78f);
_78f.appendChild(node);
}else{
_78e.appendChild(node);
}
var _790=((isTr)?this.domNode:this.domNode.firstChild);
var _791=((isTr)?node:node.firstChild);
var _792=_790.childNodes;
var _793=_791.childNodes;
for(var i=0;i<_792.length;i++){
if((_793[i])&&(_793[i].style)){
_793[i].style.width=dojo.html.getContentBox(_792[i]).width+"px";
}
}
node=_78e;
}
if((dojo.render.html.ie55||dojo.render.html.ie60)&&this.createIframe){
with(node.style){
top="0px";
left="0px";
}
var _795=document.createElement("div");
_795.appendChild(node);
this.bgIframe=new dojo.html.BackgroundIframe(_795);
_795.appendChild(this.bgIframe.iframe);
node=_795;
}
node.style.zIndex=999;
return node;
},onDragStart:function(e){
dojo.html.clearSelection();
this.scrollOffset=dojo.html.getScroll().offset;
this.dragStartPosition=dojo.html.getAbsolutePosition(this.domNode,true);
this.dragOffset={y:this.dragStartPosition.y-e.pageY,x:this.dragStartPosition.x-e.pageX};
this.dragClone=this.createDragNode();
this.containingBlockPosition=this.domNode.offsetParent?dojo.html.getAbsolutePosition(this.domNode.offsetParent,true):{x:0,y:0};
if(this.constrainToContainer){
this.constraints=this.getConstraints();
}
with(this.dragClone.style){
position="absolute";
top=this.dragOffset.y+e.pageY+"px";
left=this.dragOffset.x+e.pageX+"px";
}
dojo.body().appendChild(this.dragClone);
dojo.event.topic.publish("dragStart",{source:this});
},getConstraints:function(){
if(this.constrainingContainer.nodeName.toLowerCase()=="body"){
var _797=dojo.html.getViewport();
var _798=_797.width;
var _799=_797.height;
var _79a=dojo.html.getScroll().offset;
var x=_79a.x;
var y=_79a.y;
}else{
var _79d=dojo.html.getContentBox(this.constrainingContainer);
_798=_79d.width;
_799=_79d.height;
x=this.containingBlockPosition.x+dojo.html.getPixelValue(this.constrainingContainer,"padding-left",true)+dojo.html.getBorderExtent(this.constrainingContainer,"left");
y=this.containingBlockPosition.y+dojo.html.getPixelValue(this.constrainingContainer,"padding-top",true)+dojo.html.getBorderExtent(this.constrainingContainer,"top");
}
var mb=dojo.html.getMarginBox(this.domNode);
return {minX:x,minY:y,maxX:x+_798-mb.width,maxY:y+_799-mb.height};
},updateDragOffset:function(){
var _79f=dojo.html.getScroll().offset;
if(_79f.y!=this.scrollOffset.y){
var diff=_79f.y-this.scrollOffset.y;
this.dragOffset.y+=diff;
this.scrollOffset.y=_79f.y;
}
if(_79f.x!=this.scrollOffset.x){
var diff=_79f.x-this.scrollOffset.x;
this.dragOffset.x+=diff;
this.scrollOffset.x=_79f.x;
}
},onDragMove:function(e){
this.updateDragOffset();
var x=this.dragOffset.x+e.pageX;
var y=this.dragOffset.y+e.pageY;
if(this.constrainToContainer){
if(x<this.constraints.minX){
x=this.constraints.minX;
}
if(y<this.constraints.minY){
y=this.constraints.minY;
}
if(x>this.constraints.maxX){
x=this.constraints.maxX;
}
if(y>this.constraints.maxY){
y=this.constraints.maxY;
}
}
this.setAbsolutePosition(x,y);
dojo.event.topic.publish("dragMove",{source:this});
},setAbsolutePosition:function(x,y){
if(!this.disableY){
this.dragClone.style.top=y+"px";
}
if(!this.disableX){
this.dragClone.style.left=x+"px";
}
},onDragEnd:function(e){
switch(e.dragStatus){
case "dropSuccess":
dojo.html.removeNode(this.dragClone);
this.dragClone=null;
break;
case "dropFailure":
var _7a7=dojo.html.getAbsolutePosition(this.dragClone,true);
var _7a8={left:this.dragStartPosition.x+1,top:this.dragStartPosition.y+1};
var anim=dojo.lfx.slideTo(this.dragClone,_7a8,300);
var _7aa=this;
dojo.event.connect(anim,"onEnd",function(e){
dojo.html.removeNode(_7aa.dragClone);
_7aa.dragClone=null;
});
anim.play();
break;
}
dojo.event.topic.publish("dragEnd",{source:this});
},constrainTo:function(_7ac){
this.constrainToContainer=true;
if(_7ac){
this.constrainingContainer=_7ac;
}else{
this.constrainingContainer=this.domNode.parentNode;
}
}},function(node,type){
this.domNode=dojo.byId(node);
this.type=type;
this.constrainToContainer=false;
this.dragSource=null;
dojo.dnd.DragObject.prototype.register.call(this);
});
dojo.declare("dojo.dnd.HtmlDropTarget",dojo.dnd.DropTarget,{vertical:false,onDragOver:function(e){
if(!this.accepts(e.dragObjects)){
return false;
}
this.childBoxes=[];
for(var i=0,_7b1;i<this.domNode.childNodes.length;i++){
_7b1=this.domNode.childNodes[i];
if(_7b1.nodeType!=dojo.html.ELEMENT_NODE){
continue;
}
var pos=dojo.html.getAbsolutePosition(_7b1,true);
var _7b3=dojo.html.getBorderBox(_7b1);
this.childBoxes.push({top:pos.y,bottom:pos.y+_7b3.height,left:pos.x,right:pos.x+_7b3.width,height:_7b3.height,width:_7b3.width,node:_7b1});
}
return true;
},_getNodeUnderMouse:function(e){
for(var i=0,_7b6;i<this.childBoxes.length;i++){
with(this.childBoxes[i]){
if(e.pageX>=left&&e.pageX<=right&&e.pageY>=top&&e.pageY<=bottom){
return i;
}
}
}
return -1;
},createDropIndicator:function(){
this.dropIndicator=document.createElement("div");
with(this.dropIndicator.style){
position="absolute";
zIndex=999;
if(this.vertical){
borderLeftWidth="1px";
borderLeftColor="black";
borderLeftStyle="solid";
height=dojo.html.getBorderBox(this.domNode).height+"px";
top=dojo.html.getAbsolutePosition(this.domNode,true).y+"px";
}else{
borderTopWidth="1px";
borderTopColor="black";
borderTopStyle="solid";
width=dojo.html.getBorderBox(this.domNode).width+"px";
left=dojo.html.getAbsolutePosition(this.domNode,true).x+"px";
}
}
},onDragMove:function(e,_7b8){
var i=this._getNodeUnderMouse(e);
if(!this.dropIndicator){
this.createDropIndicator();
}
var _7ba=this.vertical?dojo.html.gravity.WEST:dojo.html.gravity.NORTH;
var hide=false;
if(i<0){
if(this.childBoxes.length){
var _7bc=(dojo.html.gravity(this.childBoxes[0].node,e)&_7ba);
if(_7bc){
hide=true;
}
}else{
var _7bc=true;
}
}else{
var _7bd=this.childBoxes[i];
var _7bc=(dojo.html.gravity(_7bd.node,e)&_7ba);
if(_7bd.node===_7b8[0].dragSource.domNode){
hide=true;
}else{
var _7be=_7bc?(i>0?this.childBoxes[i-1]:_7bd):(i<this.childBoxes.length-1?this.childBoxes[i+1]:_7bd);
if(_7be.node===_7b8[0].dragSource.domNode){
hide=true;
}
}
}
if(hide){
this.dropIndicator.style.display="none";
return;
}else{
this.dropIndicator.style.display="";
}
this.placeIndicator(e,_7b8,i,_7bc);
if(!dojo.html.hasParent(this.dropIndicator)){
dojo.body().appendChild(this.dropIndicator);
}
},placeIndicator:function(e,_7c0,_7c1,_7c2){
var _7c3=this.vertical?"left":"top";
var _7c4;
if(_7c1<0){
if(this.childBoxes.length){
_7c4=_7c2?this.childBoxes[0]:this.childBoxes[this.childBoxes.length-1];
}else{
this.dropIndicator.style[_7c3]=dojo.html.getAbsolutePosition(this.domNode,true)[this.vertical?"x":"y"]+"px";
}
}else{
_7c4=this.childBoxes[_7c1];
}
if(_7c4){
this.dropIndicator.style[_7c3]=(_7c2?_7c4[_7c3]:_7c4[this.vertical?"right":"bottom"])+"px";
if(this.vertical){
this.dropIndicator.style.height=_7c4.height+"px";
this.dropIndicator.style.top=_7c4.top+"px";
}else{
this.dropIndicator.style.width=_7c4.width+"px";
this.dropIndicator.style.left=_7c4.left+"px";
}
}
},onDragOut:function(e){
if(this.dropIndicator){
dojo.html.removeNode(this.dropIndicator);
delete this.dropIndicator;
}
},onDrop:function(e){
this.onDragOut(e);
var i=this._getNodeUnderMouse(e);
var _7c8=this.vertical?dojo.html.gravity.WEST:dojo.html.gravity.NORTH;
if(i<0){
if(this.childBoxes.length){
if(dojo.html.gravity(this.childBoxes[0].node,e)&_7c8){
return this.insert(e,this.childBoxes[0].node,"before");
}else{
return this.insert(e,this.childBoxes[this.childBoxes.length-1].node,"after");
}
}
return this.insert(e,this.domNode,"append");
}
var _7c9=this.childBoxes[i];
if(dojo.html.gravity(_7c9.node,e)&_7c8){
return this.insert(e,_7c9.node,"before");
}else{
return this.insert(e,_7c9.node,"after");
}
},insert:function(e,_7cb,_7cc){
var node=e.dragObject.domNode;
if(_7cc=="before"){
return dojo.html.insertBefore(node,_7cb);
}else{
if(_7cc=="after"){
return dojo.html.insertAfter(node,_7cb);
}else{
if(_7cc=="append"){
_7cb.appendChild(node);
return true;
}
}
}
return false;
}},function(node,_7cf){
if(arguments.length==0){
return;
}
this.domNode=dojo.byId(node);
dojo.dnd.DropTarget.call(this);
if(_7cf&&dojo.lang.isString(_7cf)){
_7cf=[_7cf];
}
this.acceptedTypes=_7cf||[];
dojo.dnd.dragManager.registerDropTarget(this);
});
dojo.kwCompoundRequire({common:["dojo.dnd.DragAndDrop"],browser:["dojo.dnd.HtmlDragAndDrop"],dashboard:["dojo.dnd.HtmlDragAndDrop"]});
dojo.provide("dojo.dnd.*");
dojo.provide("dojo.dnd.HtmlDragMove");
dojo.declare("dojo.dnd.HtmlDragMoveSource",dojo.dnd.HtmlDragSource,{onDragStart:function(){
var _7d0=new dojo.dnd.HtmlDragMoveObject(this.dragObject,this.type);
if(this.constrainToContainer){
_7d0.constrainTo(this.constrainingContainer);
}
return _7d0;
},onSelected:function(){
for(var i=0;i<this.dragObjects.length;i++){
dojo.dnd.dragManager.selectedSources.push(new dojo.dnd.HtmlDragMoveSource(this.dragObjects[i]));
}
}});
dojo.declare("dojo.dnd.HtmlDragMoveObject",dojo.dnd.HtmlDragObject,{onDragStart:function(e){
dojo.html.clearSelection();
this.dragClone=this.domNode;
if(dojo.html.getComputedStyle(this.domNode,"position")!="absolute"){
this.domNode.style.position="relative";
}
var left=parseInt(dojo.html.getComputedStyle(this.domNode,"left"));
var top=parseInt(dojo.html.getComputedStyle(this.domNode,"top"));
this.dragStartPosition={x:isNaN(left)?0:left,y:isNaN(top)?0:top};
this.scrollOffset=dojo.html.getScroll().offset;
this.dragOffset={y:this.dragStartPosition.y-e.pageY,x:this.dragStartPosition.x-e.pageX};
this.containingBlockPosition={x:0,y:0};
if(this.constrainToContainer){
this.constraints=this.getConstraints();
}
dojo.event.connect(this.domNode,"onclick",this,"_squelchOnClick");
},onDragEnd:function(e){
},setAbsolutePosition:function(x,y){
if(!this.disableY){
this.domNode.style.top=y+"px";
}
if(!this.disableX){
this.domNode.style.left=x+"px";
}
},_squelchOnClick:function(e){
dojo.event.browser.stopEvent(e);
dojo.event.disconnect(this.domNode,"onclick",this,"_squelchOnClick");
}});
dojo.provide("dojo.xml.Parse");
dojo.xml.Parse=function(){
var isIE=((dojo.render.html.capable)&&(dojo.render.html.ie));
function getTagName(node){
try{
return node.tagName.toLowerCase();
}
catch(e){
return "";
}
}
function getDojoTagName(node){
var _7dc=getTagName(node);
if(!_7dc){
return "";
}
if((dojo.widget)&&(dojo.widget.tags[_7dc])){
return _7dc;
}
var p=_7dc.indexOf(":");
if(p>=0){
return _7dc;
}
if(_7dc.substr(0,5)=="dojo:"){
return _7dc;
}
if(dojo.render.html.capable&&dojo.render.html.ie&&node.scopeName!="HTML"){
return node.scopeName.toLowerCase()+":"+_7dc;
}
if(_7dc.substr(0,4)=="dojo"){
return "dojo:"+_7dc.substring(4);
}
var djt=node.getAttribute("dojoType")||node.getAttribute("dojotype");
if(djt){
if(djt.indexOf(":")<0){
djt="dojo:"+djt;
}
return djt.toLowerCase();
}
djt=node.getAttributeNS&&node.getAttributeNS(dojo.dom.dojoml,"type");
if(djt){
return "dojo:"+djt.toLowerCase();
}
try{
djt=node.getAttribute("dojo:type");
}
catch(e){
}
if(djt){
return "dojo:"+djt.toLowerCase();
}
if((dj_global["djConfig"])&&(!djConfig["ignoreClassNames"])){
var _7df=node.className||node.getAttribute("class");
if((_7df)&&(_7df.indexOf)&&(_7df.indexOf("dojo-")!=-1)){
var _7e0=_7df.split(" ");
for(var x=0,c=_7e0.length;x<c;x++){
if(_7e0[x].slice(0,5)=="dojo-"){
return "dojo:"+_7e0[x].substr(5).toLowerCase();
}
}
}
}
return "";
}
this.parseElement=function(node,_7e4,_7e5,_7e6){
var _7e7=getTagName(node);
if(isIE&&_7e7.indexOf("/")==0){
return null;
}
try{
var attr=node.getAttribute("parseWidgets");
if(attr&&attr.toLowerCase()=="false"){
return {};
}
}
catch(e){
}
var _7e9=true;
if(_7e5){
var _7ea=getDojoTagName(node);
_7e7=_7ea||_7e7;
_7e9=Boolean(_7ea);
}
var _7eb={};
_7eb[_7e7]=[];
var pos=_7e7.indexOf(":");
if(pos>0){
var ns=_7e7.substring(0,pos);
_7eb["ns"]=ns;
if((dojo.ns)&&(!dojo.ns.allow(ns))){
_7e9=false;
}
}
if(_7e9){
var _7ee=this.parseAttributes(node);
for(var attr in _7ee){
if((!_7eb[_7e7][attr])||(typeof _7eb[_7e7][attr]!="array")){
_7eb[_7e7][attr]=[];
}
_7eb[_7e7][attr].push(_7ee[attr]);
}
_7eb[_7e7].nodeRef=node;
_7eb.tagName=_7e7;
_7eb.index=_7e6||0;
}
var _7ef=0;
for(var i=0;i<node.childNodes.length;i++){
var tcn=node.childNodes.item(i);
switch(tcn.nodeType){
case dojo.dom.ELEMENT_NODE:
var ctn=getDojoTagName(tcn)||getTagName(tcn);
if(!_7eb[ctn]){
_7eb[ctn]=[];
}
_7eb[ctn].push(this.parseElement(tcn,true,_7e5,_7ef));
if((tcn.childNodes.length==1)&&(tcn.childNodes.item(0).nodeType==dojo.dom.TEXT_NODE)){
_7eb[ctn][_7eb[ctn].length-1].value=tcn.childNodes.item(0).nodeValue;
}
_7ef++;
break;
case dojo.dom.TEXT_NODE:
if(node.childNodes.length==1){
_7eb[_7e7].push({value:node.childNodes.item(0).nodeValue});
}
break;
default:
break;
}
}
return _7eb;
};
this.parseAttributes=function(node){
var _7f4={};
var atts=node.attributes;
var _7f6,i=0;
while((_7f6=atts[i++])){
if(isIE){
if(!_7f6){
continue;
}
if((typeof _7f6=="object")&&(typeof _7f6.nodeValue=="undefined")||(_7f6.nodeValue==null)||(_7f6.nodeValue=="")){
continue;
}
}
var nn=_7f6.nodeName.split(":");
nn=(nn.length==2)?nn[1]:_7f6.nodeName;
_7f4[nn]={value:_7f6.nodeValue};
}
return _7f4;
};
};
dojo.provide("dojo.ns");
dojo.ns={namespaces:{},failed:{},loading:{},loaded:{},register:function(name,_7fa,_7fb,_7fc){
if(!_7fc||!this.namespaces[name]){
this.namespaces[name]=new dojo.ns.Ns(name,_7fa,_7fb);
}
},allow:function(name){
if(this.failed[name]){
return false;
}
if((djConfig.excludeNamespace)&&(dojo.lang.inArray(djConfig.excludeNamespace,name))){
return false;
}
return ((name==this.dojo)||(!djConfig.includeNamespace)||(dojo.lang.inArray(djConfig.includeNamespace,name)));
},get:function(name){
return this.namespaces[name];
},require:function(name){
var ns=this.namespaces[name];
if((ns)&&(this.loaded[name])){
return ns;
}
if(!this.allow(name)){
return false;
}
if(this.loading[name]){
dojo.debug("dojo.namespace.require: re-entrant request to load namespace \""+name+"\" must fail.");
return false;
}
var req=dojo.require;
this.loading[name]=true;
try{
if(name=="dojo"){
req("dojo.namespaces.dojo");
}else{
if(!dojo.hostenv.moduleHasPrefix(name)){
dojo.registerModulePath(name,"../"+name);
}
req([name,"manifest"].join("."),false,true);
}
if(!this.namespaces[name]){
this.failed[name]=true;
}
}
finally{
this.loading[name]=false;
}
return this.namespaces[name];
}};
dojo.ns.Ns=function(name,_803,_804){
this.name=name;
this.module=_803;
this.resolver=_804;
this._loaded=[];
this._failed=[];
};
dojo.ns.Ns.prototype.resolve=function(name,_806,_807){
if(!this.resolver||djConfig["skipAutoRequire"]){
return false;
}
var _808=this.resolver(name,_806);
if((_808)&&(!this._loaded[_808])&&(!this._failed[_808])){
var req=dojo.require;
req(_808,false,true);
if(dojo.hostenv.findModule(_808,false)){
this._loaded[_808]=true;
}else{
if(!_807){
dojo.raise("dojo.ns.Ns.resolve: module '"+_808+"' not found after loading via namespace '"+this.name+"'");
}
this._failed[_808]=true;
}
}
return Boolean(this._loaded[_808]);
};
dojo.registerNamespace=function(name,_80b,_80c){
dojo.ns.register.apply(dojo.ns,arguments);
};
dojo.registerNamespaceResolver=function(name,_80e){
var n=dojo.ns.namespaces[name];
if(n){
n.resolver=_80e;
}
};
dojo.registerNamespaceManifest=function(_810,path,name,_813,_814){
dojo.registerModulePath(name,path);
dojo.registerNamespace(name,_813,_814);
};
dojo.registerNamespace("dojo","dojo.widget");
dojo.provide("dojo.widget.Manager");
dojo.widget.manager=new function(){
this.widgets=[];
this.widgetIds=[];
this.topWidgets={};
var _815={};
var _816=[];
this.getUniqueId=function(_817){
var _818;
do{
_818=_817+"_"+(_815[_817]!=undefined?++_815[_817]:_815[_817]=0);
}while(this.getWidgetById(_818));
return _818;
};
this.add=function(_819){
this.widgets.push(_819);
if(!_819.extraArgs["id"]){
_819.extraArgs["id"]=_819.extraArgs["ID"];
}
if(_819.widgetId==""){
if(_819["id"]){
_819.widgetId=_819["id"];
}else{
if(_819.extraArgs["id"]){
_819.widgetId=_819.extraArgs["id"];
}else{
_819.widgetId=this.getUniqueId(_819.ns+"_"+_819.widgetType);
}
}
}
if(this.widgetIds[_819.widgetId]){
dojo.debug("widget ID collision on ID: "+_819.widgetId);
}
this.widgetIds[_819.widgetId]=_819;
};
this.destroyAll=function(){
for(var x=this.widgets.length-1;x>=0;x--){
try{
this.widgets[x].destroy(true);
delete this.widgets[x];
}
catch(e){
}
}
};
this.remove=function(_81b){
if(dojo.lang.isNumber(_81b)){
var tw=this.widgets[_81b].widgetId;
delete this.topWidgets[tw];
delete this.widgetIds[tw];
this.widgets.splice(_81b,1);
}else{
this.removeById(_81b);
}
};
this.removeById=function(id){
if(!dojo.lang.isString(id)){
id=id["widgetId"];
if(!id){
dojo.debug("invalid widget or id passed to removeById");
return;
}
}
for(var i=0;i<this.widgets.length;i++){
if(this.widgets[i].widgetId==id){
this.remove(i);
break;
}
}
};
this.getWidgetById=function(id){
if(dojo.lang.isString(id)){
return this.widgetIds[id];
}
return id;
};
this.getWidgetsByType=function(type){
var lt=type.toLowerCase();
var _822=(type.indexOf(":")<0?function(x){
return x.widgetType.toLowerCase();
}:function(x){
return x.getNamespacedType();
});
var ret=[];
dojo.lang.forEach(this.widgets,function(x){
if(_822(x)==lt){
ret.push(x);
}
});
return ret;
};
this.getWidgetsByFilter=function(_827,_828){
var ret=[];
dojo.lang.every(this.widgets,function(x){
if(_827(x)){
ret.push(x);
if(_828){
return false;
}
}
return true;
});
return (_828?ret[0]:ret);
};
this.getAllWidgets=function(){
return this.widgets.concat();
};
this.getWidgetByNode=function(node){
var w=this.getAllWidgets();
node=dojo.byId(node);
for(var i=0;i<w.length;i++){
if(w[i].domNode==node){
return w[i];
}
}
return null;
};
this.byId=this.getWidgetById;
this.byType=this.getWidgetsByType;
this.byFilter=this.getWidgetsByFilter;
this.byNode=this.getWidgetByNode;
var _82e={};
var _82f=["dojo.widget"];
for(var i=0;i<_82f.length;i++){
_82f[_82f[i]]=true;
}
this.registerWidgetPackage=function(_831){
if(!_82f[_831]){
_82f[_831]=true;
_82f.push(_831);
}
};
this.getWidgetPackageList=function(){
return dojo.lang.map(_82f,function(elt){
return (elt!==true?elt:undefined);
});
};
this.getImplementation=function(_833,_834,_835,ns){
var impl=this.getImplementationName(_833,ns);
if(impl){
var ret=_834?new impl(_834):new impl();
return ret;
}
};
function buildPrefixCache(){
for(var _839 in dojo.render){
if(dojo.render[_839]["capable"]===true){
var _83a=dojo.render[_839].prefixes;
for(var i=0;i<_83a.length;i++){
_816.push(_83a[i].toLowerCase());
}
}
}
}
var _83c=function(_83d,_83e){
if(!_83e){
return null;
}
for(var i=0,l=_816.length,_841;i<=l;i++){
_841=(i<l?_83e[_816[i]]:_83e);
if(!_841){
continue;
}
for(var name in _841){
if(name.toLowerCase()==_83d){
return _841[name];
}
}
}
return null;
};
var _843=function(_844,_845){
var _846=dojo.evalObjPath(_845,false);
return (_846?_83c(_844,_846):null);
};
this.getImplementationName=function(_847,ns){
var _849=_847.toLowerCase();
ns=ns||"dojo";
var imps=_82e[ns]||(_82e[ns]={});
var impl=imps[_849];
if(impl){
return impl;
}
if(!_816.length){
buildPrefixCache();
}
var _84c=dojo.ns.get(ns);
if(!_84c){
dojo.ns.register(ns,ns+".widget");
_84c=dojo.ns.get(ns);
}
if(_84c){
_84c.resolve(_847);
}
impl=_843(_849,_84c.module);
if(impl){
return (imps[_849]=impl);
}
_84c=dojo.ns.require(ns);
if((_84c)&&(_84c.resolver)){
_84c.resolve(_847);
impl=_843(_849,_84c.module);
if(impl){
return (imps[_849]=impl);
}
}
dojo.deprecated("dojo.widget.Manager.getImplementationName","Could not locate widget implementation for \""+_847+"\" in \""+_84c.module+"\" registered to namespace \""+_84c.name+"\". "+"Developers must specify correct namespaces for all non-Dojo widgets","0.5");
for(var i=0;i<_82f.length;i++){
impl=_843(_849,_82f[i]);
if(impl){
return (imps[_849]=impl);
}
}
throw new Error("Could not locate widget implementation for \""+_847+"\" in \""+_84c.module+"\" registered to namespace \""+_84c.name+"\"");
};
this.resizing=false;
this.onWindowResized=function(){
if(this.resizing){
return;
}
try{
this.resizing=true;
for(var id in this.topWidgets){
var _84f=this.topWidgets[id];
if(_84f.checkSize){
_84f.checkSize();
}
}
}
catch(e){
}
finally{
this.resizing=false;
}
};
if(typeof window!="undefined"){
dojo.addOnLoad(this,"onWindowResized");
dojo.event.connect(window,"onresize",this,"onWindowResized");
}
};
(function(){
var dw=dojo.widget;
var dwm=dw.manager;
var h=dojo.lang.curry(dojo.lang,"hitch",dwm);
var g=function(_854,_855){
dw[(_855||_854)]=h(_854);
};
g("add","addWidget");
g("destroyAll","destroyAllWidgets");
g("remove","removeWidget");
g("removeById","removeWidgetById");
g("getWidgetById");
g("getWidgetById","byId");
g("getWidgetsByType");
g("getWidgetsByFilter");
g("getWidgetsByType","byType");
g("getWidgetsByFilter","byFilter");
g("getWidgetByNode","byNode");
dw.all=function(n){
var _857=dwm.getAllWidgets.apply(dwm,arguments);
if(arguments.length>0){
return _857[n];
}
return _857;
};
g("registerWidgetPackage");
g("getImplementation","getWidgetImplementation");
g("getImplementationName","getWidgetImplementationName");
dw.widgets=dwm.widgets;
dw.widgetIds=dwm.widgetIds;
dw.root=dwm.root;
})();
dojo.kwCompoundRequire({common:[["dojo.uri.Uri",false,false]]});
dojo.provide("dojo.uri.*");
dojo.provide("dojo.a11y");
dojo.a11y={imgPath:dojo.uri.moduleUri("dojo.widget","templates/images"),doAccessibleCheck:true,accessible:null,checkAccessible:function(){
if(this.accessible===null){
this.accessible=false;
if(this.doAccessibleCheck==true){
this.accessible=this.testAccessible();
}
}
return this.accessible;
},testAccessible:function(){
this.accessible=false;
if(dojo.render.html.ie||dojo.render.html.mozilla){
var div=document.createElement("div");
div.style.backgroundImage="url(\""+this.imgPath+"/tab_close.gif\")";
dojo.body().appendChild(div);
var _859=null;
if(window.getComputedStyle){
var _85a=getComputedStyle(div,"");
_859=_85a.getPropertyValue("background-image");
}else{
_859=div.currentStyle.backgroundImage;
}
var _85b=false;
if(_859!=null&&(_859=="none"||_859=="url(invalid-url:)")){
this.accessible=true;
}
dojo.body().removeChild(div);
}
return this.accessible;
},setCheckAccessible:function(_85c){
this.doAccessibleCheck=_85c;
},setAccessibleMode:function(){
if(this.accessible===null){
if(this.checkAccessible()){
dojo.render.html.prefixes.unshift("a11y");
}
}
return this.accessible;
}};
dojo.provide("dojo.widget.Widget");
dojo.declare("dojo.widget.Widget",null,function(){
this.children=[];
this.extraArgs={};
},{parent:null,isTopLevel:false,disabled:false,isContainer:false,widgetId:"",widgetType:"Widget",ns:"dojo",getNamespacedType:function(){
return (this.ns?this.ns+":"+this.widgetType:this.widgetType).toLowerCase();
},toString:function(){
return "[Widget "+this.getNamespacedType()+", "+(this.widgetId||"NO ID")+"]";
},repr:function(){
return this.toString();
},enable:function(){
this.disabled=false;
},disable:function(){
this.disabled=true;
},onResized:function(){
this.notifyChildrenOfResize();
},notifyChildrenOfResize:function(){
for(var i=0;i<this.children.length;i++){
var _85e=this.children[i];
if(_85e.onResized){
_85e.onResized();
}
}
},create:function(args,_860,_861,ns){
if(ns){
this.ns=ns;
}
this.satisfyPropertySets(args,_860,_861);
this.mixInProperties(args,_860,_861);
this.postMixInProperties(args,_860,_861);
dojo.widget.manager.add(this);
this.buildRendering(args,_860,_861);
this.initialize(args,_860,_861);
this.postInitialize(args,_860,_861);
this.postCreate(args,_860,_861);
return this;
},destroy:function(_863){
if(this.parent){
this.parent.removeChild(this);
}
this.destroyChildren();
this.uninitialize();
this.destroyRendering(_863);
dojo.widget.manager.removeById(this.widgetId);
},destroyChildren:function(){
var _864;
var i=0;
while(this.children.length>i){
_864=this.children[i];
if(_864 instanceof dojo.widget.Widget){
this.removeChild(_864);
_864.destroy();
continue;
}
i++;
}
},getChildrenOfType:function(type,_867){
var ret=[];
var _869=dojo.lang.isFunction(type);
if(!_869){
type=type.toLowerCase();
}
for(var x=0;x<this.children.length;x++){
if(_869){
if(this.children[x] instanceof type){
ret.push(this.children[x]);
}
}else{
if(this.children[x].widgetType.toLowerCase()==type){
ret.push(this.children[x]);
}
}
if(_867){
ret=ret.concat(this.children[x].getChildrenOfType(type,_867));
}
}
return ret;
},getDescendants:function(){
var _86b=[];
var _86c=[this];
var elem;
while((elem=_86c.pop())){
_86b.push(elem);
if(elem.children){
dojo.lang.forEach(elem.children,function(elem){
_86c.push(elem);
});
}
}
return _86b;
},isFirstChild:function(){
return this===this.parent.children[0];
},isLastChild:function(){
return this===this.parent.children[this.parent.children.length-1];
},satisfyPropertySets:function(args){
return args;
},mixInProperties:function(args,frag){
if((args["fastMixIn"])||(frag["fastMixIn"])){
for(var x in args){
this[x]=args[x];
}
return;
}
var _873;
var _874=dojo.widget.lcArgsCache[this.widgetType];
if(_874==null){
_874={};
for(var y in this){
_874[((new String(y)).toLowerCase())]=y;
}
dojo.widget.lcArgsCache[this.widgetType]=_874;
}
var _876={};
for(var x in args){
if(!this[x]){
var y=_874[(new String(x)).toLowerCase()];
if(y){
args[y]=args[x];
x=y;
}
}
if(_876[x]){
continue;
}
_876[x]=true;
if((typeof this[x])!=(typeof _873)){
if(typeof args[x]!="string"){
this[x]=args[x];
}else{
if(dojo.lang.isString(this[x])){
this[x]=args[x];
}else{
if(dojo.lang.isNumber(this[x])){
this[x]=new Number(args[x]);
}else{
if(dojo.lang.isBoolean(this[x])){
this[x]=(args[x].toLowerCase()=="false")?false:true;
}else{
if(dojo.lang.isFunction(this[x])){
if(args[x].search(/[^\w\.]+/i)==-1){
this[x]=dojo.evalObjPath(args[x],false);
}else{
var tn=dojo.lang.nameAnonFunc(new Function(args[x]),this);
dojo.event.kwConnect({srcObj:this,srcFunc:x,adviceObj:this,adviceFunc:tn});
}
}else{
if(dojo.lang.isArray(this[x])){
this[x]=args[x].split(";");
}else{
if(this[x] instanceof Date){
this[x]=new Date(Number(args[x]));
}else{
if(typeof this[x]=="object"){
if(this[x] instanceof dojo.uri.Uri){
this[x]=dojo.uri.dojoUri(args[x]);
}else{
var _878=args[x].split(";");
for(var y=0;y<_878.length;y++){
var si=_878[y].indexOf(":");
if((si!=-1)&&(_878[y].length>si)){
this[x][_878[y].substr(0,si).replace(/^\s+|\s+$/g,"")]=_878[y].substr(si+1);
}
}
}
}else{
this[x]=args[x];
}
}
}
}
}
}
}
}
}else{
this.extraArgs[x.toLowerCase()]=args[x];
}
}
},postMixInProperties:function(args,frag,_87c){
},initialize:function(args,frag,_87f){
return false;
},postInitialize:function(args,frag,_882){
return false;
},postCreate:function(args,frag,_885){
return false;
},uninitialize:function(){
return false;
},buildRendering:function(args,frag,_888){
dojo.unimplemented("dojo.widget.Widget.buildRendering, on "+this.toString()+", ");
return false;
},destroyRendering:function(){
dojo.unimplemented("dojo.widget.Widget.destroyRendering");
return false;
},addedTo:function(_889){
},addChild:function(_88a){
dojo.unimplemented("dojo.widget.Widget.addChild");
return false;
},removeChild:function(_88b){
for(var x=0;x<this.children.length;x++){
if(this.children[x]===_88b){
this.children.splice(x,1);
_88b.parent=null;
break;
}
}
return _88b;
},getPreviousSibling:function(){
var idx=this.getParentIndex();
if(idx<=0){
return null;
}
return this.parent.children[idx-1];
},getSiblings:function(){
return this.parent.children;
},getParentIndex:function(){
return dojo.lang.indexOf(this.parent.children,this,true);
},getNextSibling:function(){
var idx=this.getParentIndex();
if(idx==this.parent.children.length-1){
return null;
}
if(idx<0){
return null;
}
return this.parent.children[idx+1];
}});
dojo.widget.lcArgsCache={};
dojo.widget.tags={};
dojo.widget.tags.addParseTreeHandler=function(type){
dojo.deprecated("addParseTreeHandler",". ParseTreeHandlers are now reserved for components. Any unfiltered DojoML tag without a ParseTreeHandler is assumed to be a widget","0.5");
};
dojo.widget.tags["dojo:propertyset"]=function(_890,_891,_892){
var _893=_891.parseProperties(_890["dojo:propertyset"]);
};
dojo.widget.tags["dojo:connect"]=function(_894,_895,_896){
var _897=_895.parseProperties(_894["dojo:connect"]);
};
dojo.widget.buildWidgetFromParseTree=function(type,frag,_89a,_89b,_89c,_89d){
dojo.a11y.setAccessibleMode();
var _89e=type.split(":");
_89e=(_89e.length==2)?_89e[1]:type;
var _89f=_89d||_89a.parseProperties(frag[frag["ns"]+":"+_89e]);
var _8a0=dojo.widget.manager.getImplementation(_89e,null,null,frag["ns"]);
if(!_8a0){
throw new Error("cannot find \""+type+"\" widget");
}else{
if(!_8a0.create){
throw new Error("\""+type+"\" widget object has no \"create\" method and does not appear to implement *Widget");
}
}
_89f["dojoinsertionindex"]=_89c;
var ret=_8a0.create(_89f,frag,_89b,frag["ns"]);
return ret;
};
dojo.widget.defineWidget=function(_8a2,_8a3,_8a4,init,_8a6){
if(dojo.lang.isString(arguments[3])){
dojo.widget._defineWidget(arguments[0],arguments[3],arguments[1],arguments[4],arguments[2]);
}else{
var args=[arguments[0]],p=3;
if(dojo.lang.isString(arguments[1])){
args.push(arguments[1],arguments[2]);
}else{
args.push("",arguments[1]);
p=2;
}
if(dojo.lang.isFunction(arguments[p])){
args.push(arguments[p],arguments[p+1]);
}else{
args.push(null,arguments[p]);
}
dojo.widget._defineWidget.apply(this,args);
}
};
dojo.widget.defineWidget.renderers="html|svg|vml";
dojo.widget._defineWidget=function(_8a9,_8aa,_8ab,init,_8ad){
var _8ae=_8a9.split(".");
var type=_8ae.pop();
var regx="\\.("+(_8aa?_8aa+"|":"")+dojo.widget.defineWidget.renderers+")\\.";
var r=_8a9.search(new RegExp(regx));
_8ae=(r<0?_8ae.join("."):_8a9.substr(0,r));
dojo.widget.manager.registerWidgetPackage(_8ae);
var pos=_8ae.indexOf(".");
var _8b3=(pos>-1)?_8ae.substring(0,pos):_8ae;
_8ad=(_8ad)||{};
_8ad.widgetType=type;
if((!init)&&(_8ad["classConstructor"])){
init=_8ad.classConstructor;
delete _8ad.classConstructor;
}
dojo.declare(_8a9,_8ab,init,_8ad);
};
dojo.provide("dojo.widget.Parse");
dojo.widget.Parse=function(_8b4){
this.propertySetsList=[];
this.fragment=_8b4;
this.createComponents=function(frag,_8b6){
var _8b7=[];
var _8b8=false;
try{
if(frag&&frag.tagName&&(frag!=frag.nodeRef)){
var _8b9=dojo.widget.tags;
var tna=String(frag.tagName).split(";");
for(var x=0;x<tna.length;x++){
var ltn=tna[x].replace(/^\s+|\s+$/g,"").toLowerCase();
frag.tagName=ltn;
var ret;
if(_8b9[ltn]){
_8b8=true;
ret=_8b9[ltn](frag,this,_8b6,frag.index);
_8b7.push(ret);
}else{
if(ltn.indexOf(":")==-1){
ltn="dojo:"+ltn;
}
ret=dojo.widget.buildWidgetFromParseTree(ltn,frag,this,_8b6,frag.index);
if(ret){
_8b8=true;
_8b7.push(ret);
}
}
}
}
}
catch(e){
dojo.debug("dojo.widget.Parse: error:",e);
}
if(!_8b8){
_8b7=_8b7.concat(this.createSubComponents(frag,_8b6));
}
return _8b7;
};
this.createSubComponents=function(_8be,_8bf){
var frag,_8c1=[];
for(var item in _8be){
frag=_8be[item];
if(frag&&typeof frag=="object"&&(frag!=_8be.nodeRef)&&(frag!=_8be.tagName)&&(!dojo.dom.isNode(frag))){
_8c1=_8c1.concat(this.createComponents(frag,_8bf));
}
}
return _8c1;
};
this.parsePropertySets=function(_8c3){
return [];
};
this.parseProperties=function(_8c4){
var _8c5={};
for(var item in _8c4){
if((_8c4[item]==_8c4.tagName)||(_8c4[item]==_8c4.nodeRef)){
}else{
var frag=_8c4[item];
if(frag.tagName&&dojo.widget.tags[frag.tagName.toLowerCase()]){
}else{
if(frag[0]&&frag[0].value!=""&&frag[0].value!=null){
try{
if(item.toLowerCase()=="dataprovider"){
var _8c8=this;
this.getDataProvider(_8c8,frag[0].value);
_8c5.dataProvider=this.dataProvider;
}
_8c5[item]=frag[0].value;
var _8c9=this.parseProperties(frag);
for(var _8ca in _8c9){
_8c5[_8ca]=_8c9[_8ca];
}
}
catch(e){
dojo.debug(e);
}
}
}
switch(item.toLowerCase()){
case "checked":
case "disabled":
if(typeof _8c5[item]!="boolean"){
_8c5[item]=true;
}
break;
}
}
}
return _8c5;
};
this.getDataProvider=function(_8cb,_8cc){
dojo.io.bind({url:_8cc,load:function(type,_8ce){
if(type=="load"){
_8cb.dataProvider=_8ce;
}
},mimetype:"text/javascript",sync:true});
};
this.getPropertySetById=function(_8cf){
for(var x=0;x<this.propertySetsList.length;x++){
if(_8cf==this.propertySetsList[x]["id"][0].value){
return this.propertySetsList[x];
}
}
return "";
};
this.getPropertySetsByType=function(_8d1){
var _8d2=[];
for(var x=0;x<this.propertySetsList.length;x++){
var cpl=this.propertySetsList[x];
var cpcc=cpl.componentClass||cpl.componentType||null;
var _8d6=this.propertySetsList[x]["id"][0].value;
if(cpcc&&(_8d6==cpcc[0].value)){
_8d2.push(cpl);
}
}
return _8d2;
};
this.getPropertySets=function(_8d7){
var ppl="dojo:propertyproviderlist";
var _8d9=[];
var _8da=_8d7.tagName;
if(_8d7[ppl]){
var _8db=_8d7[ppl].value.split(" ");
for(var _8dc in _8db){
if((_8dc.indexOf("..")==-1)&&(_8dc.indexOf("://")==-1)){
var _8dd=this.getPropertySetById(_8dc);
if(_8dd!=""){
_8d9.push(_8dd);
}
}else{
}
}
}
return this.getPropertySetsByType(_8da).concat(_8d9);
};
this.createComponentFromScript=function(_8de,_8df,_8e0,ns){
_8e0.fastMixIn=true;
var ltn=(ns||"dojo")+":"+_8df.toLowerCase();
if(dojo.widget.tags[ltn]){
return [dojo.widget.tags[ltn](_8e0,this,null,null,_8e0)];
}
return [dojo.widget.buildWidgetFromParseTree(ltn,_8e0,this,null,null,_8e0)];
};
};
dojo.widget._parser_collection={"dojo":new dojo.widget.Parse()};
dojo.widget.getParser=function(name){
if(!name){
name="dojo";
}
if(!this._parser_collection[name]){
this._parser_collection[name]=new dojo.widget.Parse();
}
return this._parser_collection[name];
};
dojo.widget.createWidget=function(name,_8e5,_8e6,_8e7){
var _8e8=false;
var _8e9=(typeof name=="string");
if(_8e9){
var pos=name.indexOf(":");
var ns=(pos>-1)?name.substring(0,pos):"dojo";
if(pos>-1){
name=name.substring(pos+1);
}
var _8ec=name.toLowerCase();
var _8ed=ns+":"+_8ec;
_8e8=(dojo.byId(name)&&!dojo.widget.tags[_8ed]);
}
if((arguments.length==1)&&(_8e8||!_8e9)){
var xp=new dojo.xml.Parse();
var tn=_8e8?dojo.byId(name):name;
return dojo.widget.getParser().createComponents(xp.parseElement(tn,null,true))[0];
}
function fromScript(_8f0,name,_8f2,ns){
_8f2[_8ed]={dojotype:[{value:_8ec}],nodeRef:_8f0,fastMixIn:true};
_8f2.ns=ns;
return dojo.widget.getParser().createComponentFromScript(_8f0,name,_8f2,ns);
}
_8e5=_8e5||{};
var _8f4=false;
var tn=null;
var h=dojo.render.html.capable;
if(h){
tn=document.createElement("span");
}
if(!_8e6){
_8f4=true;
_8e6=tn;
if(h){
dojo.body().appendChild(_8e6);
}
}else{
if(_8e7){
dojo.dom.insertAtPosition(tn,_8e6,_8e7);
}else{
tn=_8e6;
}
}
var _8f6=fromScript(tn,name.toLowerCase(),_8e5,ns);
if((!_8f6)||(!_8f6[0])||(typeof _8f6[0].widgetType=="undefined")){
throw new Error("createWidget: Creation of \""+name+"\" widget failed.");
}
try{
if(_8f4&&_8f6[0].domNode.parentNode){
_8f6[0].domNode.parentNode.removeChild(_8f6[0].domNode);
}
}
catch(e){
dojo.debug(e);
}
return _8f6[0];
};
dojo.provide("dojo.widget.DomWidget");
dojo.widget._cssFiles={};
dojo.widget._cssStrings={};
dojo.widget._templateCache={};
dojo.widget.defaultStrings={dojoRoot:dojo.hostenv.getBaseScriptUri(),dojoWidgetModuleUri:dojo.uri.moduleUri("dojo.widget"),baseScriptUri:dojo.hostenv.getBaseScriptUri()};
dojo.widget.fillFromTemplateCache=function(obj,_8f8,_8f9,_8fa){
var _8fb=_8f8||obj.templatePath;
var _8fc=dojo.widget._templateCache;
if(!_8fb&&!obj["widgetType"]){
do{
var _8fd="__dummyTemplate__"+dojo.widget._templateCache.dummyCount++;
}while(_8fc[_8fd]);
obj.widgetType=_8fd;
}
var wt=_8fb?_8fb.toString():obj.widgetType;
var ts=_8fc[wt];
if(!ts){
_8fc[wt]={"string":null,"node":null};
if(_8fa){
ts={};
}else{
ts=_8fc[wt];
}
}
if((!obj.templateString)&&(!_8fa)){
obj.templateString=_8f9||ts["string"];
}
if(obj.templateString){
obj.templateString=this._sanitizeTemplateString(obj.templateString);
}
if((!obj.templateNode)&&(!_8fa)){
obj.templateNode=ts["node"];
}
if((!obj.templateNode)&&(!obj.templateString)&&(_8fb)){
var _900=this._sanitizeTemplateString(dojo.hostenv.getText(_8fb));
obj.templateString=_900;
if(!_8fa){
_8fc[wt]["string"]=_900;
}
}
if((!ts["string"])&&(!_8fa)){
ts.string=obj.templateString;
}
};
dojo.widget._sanitizeTemplateString=function(_901){
if(_901){
_901=_901.replace(/^\s*<\?xml(\s)+version=[\'\"](\d)*.(\d)*[\'\"](\s)*\?>/im,"");
var _902=_901.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
if(_902){
_901=_902[1];
}
}else{
_901="";
}
return _901;
};
dojo.widget._templateCache.dummyCount=0;
dojo.widget.attachProperties=["dojoAttachPoint","id"];
dojo.widget.eventAttachProperty="dojoAttachEvent";
dojo.widget.onBuildProperty="dojoOnBuild";
dojo.widget.waiNames=["waiRole","waiState"];
dojo.widget.wai={waiRole:{name:"waiRole","namespace":"http://www.w3.org/TR/xhtml2",alias:"x2",prefix:"wairole:"},waiState:{name:"waiState","namespace":"http://www.w3.org/2005/07/aaa",alias:"aaa",prefix:""},setAttr:function(node,ns,attr,_906){
if(dojo.render.html.ie){
node.setAttribute(this[ns].alias+":"+attr,this[ns].prefix+_906);
}else{
node.setAttributeNS(this[ns]["namespace"],attr,this[ns].prefix+_906);
}
},getAttr:function(node,ns,attr){
if(dojo.render.html.ie){
return node.getAttribute(this[ns].alias+":"+attr);
}else{
return node.getAttributeNS(this[ns]["namespace"],attr);
}
},removeAttr:function(node,ns,attr){
var _90d=true;
if(dojo.render.html.ie){
_90d=node.removeAttribute(this[ns].alias+":"+attr);
}else{
node.removeAttributeNS(this[ns]["namespace"],attr);
}
return _90d;
}};
dojo.widget.attachTemplateNodes=function(_90e,_90f,_910){
var _911=dojo.dom.ELEMENT_NODE;
function trim(str){
return str.replace(/^\s+|\s+$/g,"");
}
if(!_90e){
_90e=_90f.domNode;
}
if(_90e.nodeType!=_911){
return;
}
var _913=_90e.all||_90e.getElementsByTagName("*");
var _914=_90f;
for(var x=-1;x<_913.length;x++){
var _916=(x==-1)?_90e:_913[x];
var _917=[];
if(!_90f.widgetsInTemplate||!_916.getAttribute("dojoType")){
for(var y=0;y<this.attachProperties.length;y++){
var _919=_916.getAttribute(this.attachProperties[y]);
if(_919){
_917=_919.split(";");
for(var z=0;z<_917.length;z++){
if(dojo.lang.isArray(_90f[_917[z]])){
_90f[_917[z]].push(_916);
}else{
_90f[_917[z]]=_916;
}
}
break;
}
}
var _91b=_916.getAttribute(this.eventAttachProperty);
if(_91b){
var evts=_91b.split(";");
for(var y=0;y<evts.length;y++){
if((!evts[y])||(!evts[y].length)){
continue;
}
var _91d=null;
var tevt=trim(evts[y]);
if(evts[y].indexOf(":")>=0){
var _91f=tevt.split(":");
tevt=trim(_91f[0]);
_91d=trim(_91f[1]);
}
if(!_91d){
_91d=tevt;
}
var tf=function(){
var ntf=new String(_91d);
return function(evt){
if(_914[ntf]){
_914[ntf](dojo.event.browser.fixEvent(evt,this));
}
};
}();
dojo.event.browser.addListener(_916,tevt,tf,false,true);
}
}
for(var y=0;y<_910.length;y++){
var _923=_916.getAttribute(_910[y]);
if((_923)&&(_923.length)){
var _91d=null;
var _924=_910[y].substr(4);
_91d=trim(_923);
var _925=[_91d];
if(_91d.indexOf(";")>=0){
_925=dojo.lang.map(_91d.split(";"),trim);
}
for(var z=0;z<_925.length;z++){
if(!_925[z].length){
continue;
}
var tf=function(){
var ntf=new String(_925[z]);
return function(evt){
if(_914[ntf]){
_914[ntf](dojo.event.browser.fixEvent(evt,this));
}
};
}();
dojo.event.browser.addListener(_916,_924,tf,false,true);
}
}
}
}
var _928=_916.getAttribute(this.templateProperty);
if(_928){
_90f[_928]=_916;
}
dojo.lang.forEach(dojo.widget.waiNames,function(name){
var wai=dojo.widget.wai[name];
var val=_916.getAttribute(wai.name);
if(val){
if(val.indexOf("-")==-1){
dojo.widget.wai.setAttr(_916,wai.name,"role",val);
}else{
var _92c=val.split("-");
dojo.widget.wai.setAttr(_916,wai.name,_92c[0],_92c[1]);
}
}
},this);
var _92d=_916.getAttribute(this.onBuildProperty);
if(_92d){
eval("var node = baseNode; var widget = targetObj; "+_92d);
}
}
};
dojo.widget.getDojoEventsFromStr=function(str){
var re=/(dojoOn([a-z]+)(\s?))=/gi;
var evts=str?str.match(re)||[]:[];
var ret=[];
var lem={};
for(var x=0;x<evts.length;x++){
if(evts[x].length<1){
continue;
}
var cm=evts[x].replace(/\s/,"");
cm=(cm.slice(0,cm.length-1));
if(!lem[cm]){
lem[cm]=true;
ret.push(cm);
}
}
return ret;
};
dojo.declare("dojo.widget.DomWidget",dojo.widget.Widget,function(){
if((arguments.length>0)&&(typeof arguments[0]=="object")){
this.create(arguments[0]);
}
},{templateNode:null,templateString:null,templateCssString:null,preventClobber:false,domNode:null,containerNode:null,widgetsInTemplate:false,addChild:function(_935,_936,pos,ref,_939){
if(!this.isContainer){
dojo.debug("dojo.widget.DomWidget.addChild() attempted on non-container widget");
return null;
}else{
if(_939==undefined){
_939=this.children.length;
}
this.addWidgetAsDirectChild(_935,_936,pos,ref,_939);
this.registerChild(_935,_939);
}
return _935;
},addWidgetAsDirectChild:function(_93a,_93b,pos,ref,_93e){
if((!this.containerNode)&&(!_93b)){
this.containerNode=this.domNode;
}
var cn=(_93b)?_93b:this.containerNode;
if(!pos){
pos="after";
}
if(!ref){
if(!cn){
cn=dojo.body();
}
ref=cn.lastChild;
}
if(!_93e){
_93e=0;
}
_93a.domNode.setAttribute("dojoinsertionindex",_93e);
if(!ref){
cn.appendChild(_93a.domNode);
}else{
if(pos=="insertAtIndex"){
dojo.dom.insertAtIndex(_93a.domNode,ref.parentNode,_93e);
}else{
if((pos=="after")&&(ref===cn.lastChild)){
cn.appendChild(_93a.domNode);
}else{
dojo.dom.insertAtPosition(_93a.domNode,cn,pos);
}
}
}
},registerChild:function(_940,_941){
_940.dojoInsertionIndex=_941;
var idx=-1;
for(var i=0;i<this.children.length;i++){
if(this.children[i].dojoInsertionIndex<=_941){
idx=i;
}
}
this.children.splice(idx+1,0,_940);
_940.parent=this;
_940.addedTo(this,idx+1);
delete dojo.widget.manager.topWidgets[_940.widgetId];
},removeChild:function(_944){
dojo.dom.removeNode(_944.domNode);
return dojo.widget.DomWidget.superclass.removeChild.call(this,_944);
},getFragNodeRef:function(frag){
if(!frag){
return null;
}
if(!frag[this.getNamespacedType()]){
dojo.raise("Error: no frag for widget type "+this.getNamespacedType()+", id "+this.widgetId+" (maybe a widget has set it's type incorrectly)");
}
return frag[this.getNamespacedType()]["nodeRef"];
},postInitialize:function(args,frag,_948){
var _949=this.getFragNodeRef(frag);
if(_948&&(_948.snarfChildDomOutput||!_949)){
_948.addWidgetAsDirectChild(this,"","insertAtIndex","",args["dojoinsertionindex"],_949);
}else{
if(_949){
if(this.domNode&&(this.domNode!==_949)){
this._sourceNodeRef=dojo.dom.replaceNode(_949,this.domNode);
}
}
}
if(_948){
_948.registerChild(this,args.dojoinsertionindex);
}else{
dojo.widget.manager.topWidgets[this.widgetId]=this;
}
if(this.widgetsInTemplate){
var _94a=new dojo.xml.Parse();
var _94b;
var _94c=this.domNode.getElementsByTagName("*");
for(var i=0;i<_94c.length;i++){
if(_94c[i].getAttribute("dojoAttachPoint")=="subContainerWidget"){
_94b=_94c[i];
}
if(_94c[i].getAttribute("dojoType")){
_94c[i].setAttribute("isSubWidget",true);
}
}
if(this.isContainer&&!this.containerNode){
if(_94b){
var src=this.getFragNodeRef(frag);
if(src){
dojo.dom.moveChildren(src,_94b);
frag["dojoDontFollow"]=true;
}
}else{
dojo.debug("No subContainerWidget node can be found in template file for widget "+this);
}
}
var _94f=_94a.parseElement(this.domNode,null,true);
dojo.widget.getParser().createSubComponents(_94f,this);
var _950=[];
var _951=[this];
var w;
while((w=_951.pop())){
for(var i=0;i<w.children.length;i++){
var _953=w.children[i];
if(_953._processedSubWidgets||!_953.extraArgs["issubwidget"]){
continue;
}
_950.push(_953);
if(_953.isContainer){
_951.push(_953);
}
}
}
for(var i=0;i<_950.length;i++){
var _954=_950[i];
if(_954._processedSubWidgets){
dojo.debug("This should not happen: widget._processedSubWidgets is already true!");
return;
}
_954._processedSubWidgets=true;
if(_954.extraArgs["dojoattachevent"]){
var evts=_954.extraArgs["dojoattachevent"].split(";");
for(var j=0;j<evts.length;j++){
var _957=null;
var tevt=dojo.string.trim(evts[j]);
if(tevt.indexOf(":")>=0){
var _959=tevt.split(":");
tevt=dojo.string.trim(_959[0]);
_957=dojo.string.trim(_959[1]);
}
if(!_957){
_957=tevt;
}
if(dojo.lang.isFunction(_954[tevt])){
dojo.event.kwConnect({srcObj:_954,srcFunc:tevt,targetObj:this,targetFunc:_957});
}else{
alert(tevt+" is not a function in widget "+_954);
}
}
}
if(_954.extraArgs["dojoattachpoint"]){
this[_954.extraArgs["dojoattachpoint"]]=_954;
}
}
}
if(this.isContainer&&!frag["dojoDontFollow"]){
dojo.widget.getParser().createSubComponents(frag,this);
}
},buildRendering:function(args,frag){
var ts=dojo.widget._templateCache[this.widgetType];
if(args["templatecsspath"]){
args["templateCssPath"]=args["templatecsspath"];
}
var _95d=args["templateCssPath"]||this.templateCssPath;
if(_95d&&!dojo.widget._cssFiles[_95d.toString()]){
if((!this.templateCssString)&&(_95d)){
this.templateCssString=dojo.hostenv.getText(_95d);
this.templateCssPath=null;
}
dojo.widget._cssFiles[_95d.toString()]=true;
}
if((this["templateCssString"])&&(!dojo.widget._cssStrings[this.templateCssString])){
dojo.html.insertCssText(this.templateCssString,null,_95d);
dojo.widget._cssStrings[this.templateCssString]=true;
}
if((!this.preventClobber)&&((this.templatePath)||(this.templateNode)||((this["templateString"])&&(this.templateString.length))||((typeof ts!="undefined")&&((ts["string"])||(ts["node"]))))){
this.buildFromTemplate(args,frag);
}else{
this.domNode=this.getFragNodeRef(frag);
}
this.fillInTemplate(args,frag);
},buildFromTemplate:function(args,frag){
var _960=false;
if(args["templatepath"]){
args["templatePath"]=args["templatepath"];
}
dojo.widget.fillFromTemplateCache(this,args["templatePath"],null,_960);
var ts=dojo.widget._templateCache[this.templatePath?this.templatePath.toString():this.widgetType];
if((ts)&&(!_960)){
if(!this.templateString.length){
this.templateString=ts["string"];
}
if(!this.templateNode){
this.templateNode=ts["node"];
}
}
var _962=false;
var node=null;
var tstr=this.templateString;
if((!this.templateNode)&&(this.templateString)){
_962=this.templateString.match(/\$\{([^\}]+)\}/g);
if(_962){
var hash=this.strings||{};
for(var key in dojo.widget.defaultStrings){
if(dojo.lang.isUndefined(hash[key])){
hash[key]=dojo.widget.defaultStrings[key];
}
}
for(var i=0;i<_962.length;i++){
var key=_962[i];
key=key.substring(2,key.length-1);
var kval=(key.substring(0,5)=="this.")?dojo.lang.getObjPathValue(key.substring(5),this):hash[key];
var _969;
if((kval)||(dojo.lang.isString(kval))){
_969=new String((dojo.lang.isFunction(kval))?kval.call(this,key,this.templateString):kval);
while(_969.indexOf("\"")>-1){
_969=_969.replace("\"","&quot;");
}
tstr=tstr.replace(_962[i],_969);
}
}
}else{
this.templateNode=this.createNodesFromText(this.templateString,true)[0];
if(!_960){
ts.node=this.templateNode;
}
}
}
if((!this.templateNode)&&(!_962)){
dojo.debug("DomWidget.buildFromTemplate: could not create template");
return false;
}else{
if(!_962){
node=this.templateNode.cloneNode(true);
if(!node){
return false;
}
}else{
node=this.createNodesFromText(tstr,true)[0];
}
}
this.domNode=node;
this.attachTemplateNodes();
if(this.isContainer&&this.containerNode){
var src=this.getFragNodeRef(frag);
if(src){
dojo.dom.moveChildren(src,this.containerNode);
}
}
},attachTemplateNodes:function(_96b,_96c){
if(!_96b){
_96b=this.domNode;
}
if(!_96c){
_96c=this;
}
return dojo.widget.attachTemplateNodes(_96b,_96c,dojo.widget.getDojoEventsFromStr(this.templateString));
},fillInTemplate:function(){
},destroyRendering:function(){
try{
dojo.dom.destroyNode(this.domNode);
delete this.domNode;
}
catch(e){
}
if(this._sourceNodeRef){
try{
dojo.dom.destroyNode(this._sourceNodeRef);
}
catch(e){
}
}
},createNodesFromText:function(){
dojo.unimplemented("dojo.widget.DomWidget.createNodesFromText");
}});
dojo.provide("dojo.lfx.toggle");
dojo.lfx.toggle.plain={show:function(node,_96e,_96f,_970){
dojo.html.show(node);
if(dojo.lang.isFunction(_970)){
_970();
}
},hide:function(node,_972,_973,_974){
dojo.html.hide(node);
if(dojo.lang.isFunction(_974)){
_974();
}
}};
dojo.lfx.toggle.fade={show:function(node,_976,_977,_978){
dojo.lfx.fadeShow(node,_976,_977,_978).play();
},hide:function(node,_97a,_97b,_97c){
dojo.lfx.fadeHide(node,_97a,_97b,_97c).play();
}};
dojo.lfx.toggle.wipe={show:function(node,_97e,_97f,_980){
dojo.lfx.wipeIn(node,_97e,_97f,_980).play();
},hide:function(node,_982,_983,_984){
dojo.lfx.wipeOut(node,_982,_983,_984).play();
}};
dojo.lfx.toggle.explode={show:function(node,_986,_987,_988,_989){
dojo.lfx.explode(_989||{x:0,y:0,width:0,height:0},node,_986,_987,_988).play();
},hide:function(node,_98b,_98c,_98d,_98e){
dojo.lfx.implode(node,_98e||{x:0,y:0,width:0,height:0},_98b,_98c,_98d).play();
}};
dojo.provide("dojo.widget.HtmlWidget");
dojo.declare("dojo.widget.HtmlWidget",dojo.widget.DomWidget,{templateCssPath:null,templatePath:null,lang:"",toggle:"plain",toggleDuration:150,initialize:function(args,frag){
},postMixInProperties:function(args,frag){
if(this.lang===""){
this.lang=null;
}
this.toggleObj=dojo.lfx.toggle[this.toggle.toLowerCase()]||dojo.lfx.toggle.plain;
},createNodesFromText:function(txt,wrap){
return dojo.html.createNodesFromText(txt,wrap);
},destroyRendering:function(_995){
try{
if(this.bgIframe){
this.bgIframe.remove();
delete this.bgIframe;
}
if(!_995&&this.domNode){
dojo.event.browser.clean(this.domNode);
}
dojo.widget.HtmlWidget.superclass.destroyRendering.call(this);
}
catch(e){
}
},isShowing:function(){
return dojo.html.isShowing(this.domNode);
},toggleShowing:function(){
if(this.isShowing()){
this.hide();
}else{
this.show();
}
},show:function(){
if(this.isShowing()){
return;
}
this.animationInProgress=true;
this.toggleObj.show(this.domNode,this.toggleDuration,null,dojo.lang.hitch(this,this.onShow),this.explodeSrc);
},onShow:function(){
this.animationInProgress=false;
this.checkSize();
},hide:function(){
if(!this.isShowing()){
return;
}
this.animationInProgress=true;
this.toggleObj.hide(this.domNode,this.toggleDuration,null,dojo.lang.hitch(this,this.onHide),this.explodeSrc);
},onHide:function(){
this.animationInProgress=false;
},_isResized:function(w,h){
if(!this.isShowing()){
return false;
}
var wh=dojo.html.getMarginBox(this.domNode);
var _999=w||wh.width;
var _99a=h||wh.height;
if(this.width==_999&&this.height==_99a){
return false;
}
this.width=_999;
this.height=_99a;
return true;
},checkSize:function(){
if(!this._isResized()){
return;
}
this.onResized();
},resizeTo:function(w,h){
dojo.html.setMarginBox(this.domNode,{width:w,height:h});
if(this.isShowing()){
this.onResized();
}
},resizeSoon:function(){
if(this.isShowing()){
dojo.lang.setTimeout(this,this.onResized,0);
}
},onResized:function(){
dojo.lang.forEach(this.children,function(_99d){
if(_99d.checkSize){
_99d.checkSize();
}
});
}});
dojo.kwCompoundRequire({common:["dojo.xml.Parse","dojo.widget.Widget","dojo.widget.Parse","dojo.widget.Manager"],browser:["dojo.widget.DomWidget","dojo.widget.HtmlWidget"],dashboard:["dojo.widget.DomWidget","dojo.widget.HtmlWidget"],svg:["dojo.widget.SvgWidget"],rhino:["dojo.widget.SwtWidget"]});
dojo.provide("dojo.widget.*");
dojo.provide("dojo.widget.ContentPane");
dojo.widget.defineWidget("dojo.widget.ContentPane",dojo.widget.HtmlWidget,function(){
this._styleNodes=[];
this._onLoadStack=[];
this._onUnloadStack=[];
this._callOnUnload=false;
this._ioBindObj;
this.scriptScope;
this.bindArgs={};
},{isContainer:true,adjustPaths:true,href:"",extractContent:true,parseContent:true,cacheContent:true,preload:false,refreshOnShow:false,handler:"",executeScripts:false,scriptSeparation:true,loadingMessage:"Loading...",isLoaded:false,postCreate:function(args,frag,_9a0){
if(this.handler!==""){
this.setHandler(this.handler);
}
if(this.isShowing()||this.preload){
this.loadContents();
}
},show:function(){
if(this.refreshOnShow){
this.refresh();
}else{
this.loadContents();
}
dojo.widget.ContentPane.superclass.show.call(this);
},refresh:function(){
this.isLoaded=false;
this.loadContents();
},loadContents:function(){
if(this.isLoaded){
return;
}
if(dojo.lang.isFunction(this.handler)){
this._runHandler();
}else{
if(this.href!=""){
this._downloadExternalContent(this.href,this.cacheContent&&!this.refreshOnShow);
}
}
},setUrl:function(url){
this.href=url;
this.isLoaded=false;
if(this.preload||this.isShowing()){
this.loadContents();
}
},abort:function(){
var bind=this._ioBindObj;
if(!bind||!bind.abort){
return;
}
bind.abort();
delete this._ioBindObj;
},_downloadExternalContent:function(url,_9a4){
this.abort();
this._handleDefaults(this.loadingMessage,"onDownloadStart");
var self=this;
this._ioBindObj=dojo.io.bind(this._cacheSetting({url:url,mimetype:"text/html",handler:function(type,data,xhr){
delete self._ioBindObj;
if(type=="load"){
self.onDownloadEnd.call(self,url,data);
}else{
var e={responseText:xhr.responseText,status:xhr.status,statusText:xhr.statusText,responseHeaders:xhr.getAllResponseHeaders(),text:"Error loading '"+url+"' ("+xhr.status+" "+xhr.statusText+")"};
self._handleDefaults.call(self,e,"onDownloadError");
self.onLoad();
}
}},_9a4));
},_cacheSetting:function(_9aa,_9ab){
for(var x in this.bindArgs){
if(dojo.lang.isUndefined(_9aa[x])){
_9aa[x]=this.bindArgs[x];
}
}
if(dojo.lang.isUndefined(_9aa.useCache)){
_9aa.useCache=_9ab;
}
if(dojo.lang.isUndefined(_9aa.preventCache)){
_9aa.preventCache=!_9ab;
}
if(dojo.lang.isUndefined(_9aa.mimetype)){
_9aa.mimetype="text/html";
}
return _9aa;
},onLoad:function(e){
this._runStack("_onLoadStack");
this.isLoaded=true;
},onUnLoad:function(e){
dojo.deprecated(this.widgetType+".onUnLoad, use .onUnload (lowercased load)",0.5);
},onUnload:function(e){
this._runStack("_onUnloadStack");
delete this.scriptScope;
if(this.onUnLoad!==dojo.widget.ContentPane.prototype.onUnLoad){
this.onUnLoad.apply(this,arguments);
}
},_runStack:function(_9b0){
var st=this[_9b0];
var err="";
var _9b3=this.scriptScope||window;
for(var i=0;i<st.length;i++){
try{
st[i].call(_9b3);
}
catch(e){
err+="\n"+st[i]+" failed: "+e.description;
}
}
this[_9b0]=[];
if(err.length){
var name=(_9b0=="_onLoadStack")?"addOnLoad":"addOnUnLoad";
this._handleDefaults(name+" failure\n "+err,"onExecError","debug");
}
},addOnLoad:function(obj,func){
this._pushOnStack(this._onLoadStack,obj,func);
},addOnUnload:function(obj,func){
this._pushOnStack(this._onUnloadStack,obj,func);
},addOnUnLoad:function(){
dojo.deprecated(this.widgetType+".addOnUnLoad, use addOnUnload instead. (lowercased Load)",0.5);
this.addOnUnload.apply(this,arguments);
},_pushOnStack:function(_9ba,obj,func){
if(typeof func=="undefined"){
_9ba.push(obj);
}else{
_9ba.push(function(){
obj[func]();
});
}
},destroy:function(){
this.onUnload();
dojo.widget.ContentPane.superclass.destroy.call(this);
},onExecError:function(e){
},onContentError:function(e){
},onDownloadError:function(e){
},onDownloadStart:function(e){
},onDownloadEnd:function(url,data){
data=this.splitAndFixPaths(data,url);
this.setContent(data);
},_handleDefaults:function(e,_9c4,_9c5){
if(!_9c4){
_9c4="onContentError";
}
if(dojo.lang.isString(e)){
e={text:e};
}
if(!e.text){
e.text=e.toString();
}
e.toString=function(){
return this.text;
};
if(typeof e.returnValue!="boolean"){
e.returnValue=true;
}
if(typeof e.preventDefault!="function"){
e.preventDefault=function(){
this.returnValue=false;
};
}
this[_9c4](e);
if(e.returnValue){
switch(_9c5){
case true:
case "alert":
alert(e.toString());
break;
case "debug":
dojo.debug(e.toString());
break;
default:
if(this._callOnUnload){
this.onUnload();
}
this._callOnUnload=false;
if(arguments.callee._loopStop){
dojo.debug(e.toString());
}else{
arguments.callee._loopStop=true;
this._setContent(e.toString());
}
}
}
arguments.callee._loopStop=false;
},splitAndFixPaths:function(s,url){
var _9c8=[],_9c9=[],tmp=[];
var _9cb=[],_9cc=[],attr=[],_9ce=[];
var str="",path="",fix="",_9d2="",tag="",_9d4="";
if(!url){
url="./";
}
if(s){
var _9d5=/<title[^>]*>([\s\S]*?)<\/title>/i;
while(_9cb=_9d5.exec(s)){
_9c8.push(_9cb[1]);
s=s.substring(0,_9cb.index)+s.substr(_9cb.index+_9cb[0].length);
}
if(this.adjustPaths){
var _9d6=/<[a-z][a-z0-9]*[^>]*\s(?:(?:src|href|style)=[^>])+[^>]*>/i;
var _9d7=/\s(src|href|style)=(['"]?)([\w()\[\]\/.,\\'"-:;#=&?\s@]+?)\2/i;
var _9d8=/^(?:[#]|(?:(?:https?|ftps?|file|javascript|mailto|news):))/;
while(tag=_9d6.exec(s)){
str+=s.substring(0,tag.index);
s=s.substring((tag.index+tag[0].length),s.length);
tag=tag[0];
_9d2="";
while(attr=_9d7.exec(tag)){
path="";
_9d4=attr[3];
switch(attr[1].toLowerCase()){
case "src":
case "href":
if(_9d8.exec(_9d4)){
path=_9d4;
}else{
path=(new dojo.uri.Uri(url,_9d4).toString());
}
break;
case "style":
path=dojo.html.fixPathsInCssText(_9d4,url);
break;
default:
path=_9d4;
}
fix=" "+attr[1]+"="+attr[2]+path+attr[2];
_9d2+=tag.substring(0,attr.index)+fix;
tag=tag.substring((attr.index+attr[0].length),tag.length);
}
str+=_9d2+tag;
}
s=str+s;
}
_9d5=/(?:<(style)[^>]*>([\s\S]*?)<\/style>|<link ([^>]*rel=['"]?stylesheet['"]?[^>]*)>)/i;
while(_9cb=_9d5.exec(s)){
if(_9cb[1]&&_9cb[1].toLowerCase()=="style"){
_9ce.push(dojo.html.fixPathsInCssText(_9cb[2],url));
}else{
if(attr=_9cb[3].match(/href=(['"]?)([^'">]*)\1/i)){
_9ce.push({path:attr[2]});
}
}
s=s.substring(0,_9cb.index)+s.substr(_9cb.index+_9cb[0].length);
}
var _9d5=/<script([^>]*)>([\s\S]*?)<\/script>/i;
var _9d9=/src=(['"]?)([^"']*)\1/i;
var _9da=/.*(\bdojo\b\.js(?:\.uncompressed\.js)?)$/;
var _9db=/(?:var )?\bdjConfig\b(?:[\s]*=[\s]*\{[^}]+\}|\.[\w]*[\s]*=[\s]*[^;\n]*)?;?|dojo\.hostenv\.writeIncludes\(\s*\);?/g;
var _9dc=/dojo\.(?:(?:require(?:After)?(?:If)?)|(?:widget\.(?:manager\.)?registerWidgetPackage)|(?:(?:hostenv\.)?setModulePrefix|registerModulePath)|defineNamespace)\((['"]).*?\1\)\s*;?/;
while(_9cb=_9d5.exec(s)){
if(this.executeScripts&&_9cb[1]){
if(attr=_9d9.exec(_9cb[1])){
if(_9da.exec(attr[2])){
dojo.debug("Security note! inhibit:"+attr[2]+" from  being loaded again.");
}else{
_9c9.push({path:attr[2]});
}
}
}
if(_9cb[2]){
var sc=_9cb[2].replace(_9db,"");
if(!sc){
continue;
}
while(tmp=_9dc.exec(sc)){
_9cc.push(tmp[0]);
sc=sc.substring(0,tmp.index)+sc.substr(tmp.index+tmp[0].length);
}
if(this.executeScripts){
_9c9.push(sc);
}
}
s=s.substr(0,_9cb.index)+s.substr(_9cb.index+_9cb[0].length);
}
if(this.extractContent){
_9cb=s.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
if(_9cb){
s=_9cb[1];
}
}
if(this.executeScripts&&this.scriptSeparation){
var _9d5=/(<[a-zA-Z][a-zA-Z0-9]*\s[^>]*?\S=)((['"])[^>]*scriptScope[^>]*>)/;
var _9de=/([\s'";:\(])scriptScope(.*)/;
str="";
while(tag=_9d5.exec(s)){
tmp=((tag[3]=="'")?"\"":"'");
fix="";
str+=s.substring(0,tag.index)+tag[1];
while(attr=_9de.exec(tag[2])){
tag[2]=tag[2].substring(0,attr.index)+attr[1]+"dojo.widget.byId("+tmp+this.widgetId+tmp+").scriptScope"+attr[2];
}
str+=tag[2];
s=s.substr(tag.index+tag[0].length);
}
s=str+s;
}
}
return {"xml":s,"styles":_9ce,"titles":_9c8,"requires":_9cc,"scripts":_9c9,"url":url};
},_setContent:function(cont){
this.destroyChildren();
for(var i=0;i<this._styleNodes.length;i++){
if(this._styleNodes[i]&&this._styleNodes[i].parentNode){
this._styleNodes[i].parentNode.removeChild(this._styleNodes[i]);
}
}
this._styleNodes=[];
try{
var node=this.containerNode||this.domNode;
while(node.firstChild){
dojo.html.destroyNode(node.firstChild);
}
if(typeof cont!="string"){
node.appendChild(cont);
}else{
node.innerHTML=cont;
}
}
catch(e){
e.text="Couldn't load content:"+e.description;
this._handleDefaults(e,"onContentError");
}
},setContent:function(data){
this.abort();
if(this._callOnUnload){
this.onUnload();
}
this._callOnUnload=true;
if(!data||dojo.html.isNode(data)){
this._setContent(data);
this.onResized();
this.onLoad();
}else{
if(typeof data.xml!="string"){
this.href="";
data=this.splitAndFixPaths(data);
}
this._setContent(data.xml);
for(var i=0;i<data.styles.length;i++){
if(data.styles[i].path){
this._styleNodes.push(dojo.html.insertCssFile(data.styles[i].path,dojo.doc(),false,true));
}else{
this._styleNodes.push(dojo.html.insertCssText(data.styles[i]));
}
}
if(this.parseContent){
for(var i=0;i<data.requires.length;i++){
try{
eval(data.requires[i]);
}
catch(e){
e.text="ContentPane: error in package loading calls, "+(e.description||e);
this._handleDefaults(e,"onContentError","debug");
}
}
}
var _9e4=this;
function asyncParse(){
if(_9e4.executeScripts){
_9e4._executeScripts(data.scripts);
}
if(_9e4.parseContent){
var node=_9e4.containerNode||_9e4.domNode;
var _9e6=new dojo.xml.Parse();
var frag=_9e6.parseElement(node,null,true);
dojo.widget.getParser().createSubComponents(frag,_9e4);
}
_9e4.onResized();
_9e4.onLoad();
}
if(dojo.hostenv.isXDomain&&data.requires.length){
dojo.addOnLoad(asyncParse);
}else{
asyncParse();
}
}
},setHandler:function(_9e8){
var fcn=dojo.lang.isFunction(_9e8)?_9e8:window[_9e8];
if(!dojo.lang.isFunction(fcn)){
this._handleDefaults("Unable to set handler, '"+_9e8+"' not a function.","onExecError",true);
return;
}
this.handler=function(){
return fcn.apply(this,arguments);
};
},_runHandler:function(){
var ret=true;
if(dojo.lang.isFunction(this.handler)){
this.handler(this,this.domNode);
ret=false;
}
this.onLoad();
return ret;
},_executeScripts:function(_9eb){
var self=this;
var tmp="",code="";
for(var i=0;i<_9eb.length;i++){
if(_9eb[i].path){
dojo.io.bind(this._cacheSetting({"url":_9eb[i].path,"load":function(type,_9f1){
dojo.lang.hitch(self,tmp=";"+_9f1);
},"error":function(type,_9f3){
_9f3.text=type+" downloading remote script";
self._handleDefaults.call(self,_9f3,"onExecError","debug");
},"mimetype":"text/plain","sync":true},this.cacheContent));
code+=tmp;
}else{
code+=_9eb[i];
}
}
try{
if(this.scriptSeparation){
delete this.scriptScope;
this.scriptScope=new (new Function("_container_",code+"; return this;"))(self);
}else{
var djg=dojo.global();
if(djg.execScript){
djg.execScript(code);
}else{
var djd=dojo.doc();
var sc=djd.createElement("script");
sc.appendChild(djd.createTextNode(code));
(this.containerNode||this.domNode).appendChild(sc);
}
}
}
catch(e){
e.text="Error running scripts from content:\n"+e.description;
this._handleDefaults(e,"onExecError","debug");
}
}});
dojo.provide("dojo.namespaces.dojo");
(function(){
var map={html:{"accordioncontainer":"dojo.widget.AccordionContainer","animatedpng":"dojo.widget.AnimatedPng","button":"dojo.widget.Button","chart":"dojo.widget.Chart","checkbox":"dojo.widget.Checkbox","clock":"dojo.widget.Clock","colorpalette":"dojo.widget.ColorPalette","combobox":"dojo.widget.ComboBox","combobutton":"dojo.widget.Button","contentpane":"dojo.widget.ContentPane","currencytextbox":"dojo.widget.CurrencyTextbox","datepicker":"dojo.widget.DatePicker","datetextbox":"dojo.widget.DateTextbox","debugconsole":"dojo.widget.DebugConsole","dialog":"dojo.widget.Dialog","dropdownbutton":"dojo.widget.Button","dropdowndatepicker":"dojo.widget.DropdownDatePicker","dropdowntimepicker":"dojo.widget.DropdownTimePicker","emaillisttextbox":"dojo.widget.InternetTextbox","emailtextbox":"dojo.widget.InternetTextbox","editor":"dojo.widget.Editor","editor2":"dojo.widget.Editor2","filteringtable":"dojo.widget.FilteringTable","fisheyelist":"dojo.widget.FisheyeList","fisheyelistitem":"dojo.widget.FisheyeList","floatingpane":"dojo.widget.FloatingPane","modalfloatingpane":"dojo.widget.FloatingPane","form":"dojo.widget.Form","googlemap":"dojo.widget.GoogleMap","inlineeditbox":"dojo.widget.InlineEditBox","integerspinner":"dojo.widget.Spinner","integertextbox":"dojo.widget.IntegerTextbox","ipaddresstextbox":"dojo.widget.InternetTextbox","layoutcontainer":"dojo.widget.LayoutContainer","linkpane":"dojo.widget.LinkPane","popupmenu2":"dojo.widget.Menu2","menuitem2":"dojo.widget.Menu2","menuseparator2":"dojo.widget.Menu2","menubar2":"dojo.widget.Menu2","menubaritem2":"dojo.widget.Menu2","pagecontainer":"dojo.widget.PageContainer","pagecontroller":"dojo.widget.PageContainer","popupcontainer":"dojo.widget.PopupContainer","progressbar":"dojo.widget.ProgressBar","radiogroup":"dojo.widget.RadioGroup","realnumbertextbox":"dojo.widget.RealNumberTextbox","regexptextbox":"dojo.widget.RegexpTextbox","repeater":"dojo.widget.Repeater","resizabletextarea":"dojo.widget.ResizableTextarea","richtext":"dojo.widget.RichText","select":"dojo.widget.Select","show":"dojo.widget.Show","showaction":"dojo.widget.ShowAction","showslide":"dojo.widget.ShowSlide","slidervertical":"dojo.widget.Slider","sliderhorizontal":"dojo.widget.Slider","slider":"dojo.widget.Slider","slideshow":"dojo.widget.SlideShow","sortabletable":"dojo.widget.SortableTable","splitcontainer":"dojo.widget.SplitContainer","tabcontainer":"dojo.widget.TabContainer","tabcontroller":"dojo.widget.TabContainer","taskbar":"dojo.widget.TaskBar","textbox":"dojo.widget.Textbox","timepicker":"dojo.widget.TimePicker","timetextbox":"dojo.widget.DateTextbox","titlepane":"dojo.widget.TitlePane","toaster":"dojo.widget.Toaster","toggler":"dojo.widget.Toggler","toolbar":"dojo.widget.Toolbar","toolbarcontainer":"dojo.widget.Toolbar","toolbaritem":"dojo.widget.Toolbar","toolbarbuttongroup":"dojo.widget.Toolbar","toolbarbutton":"dojo.widget.Toolbar","toolbardialog":"dojo.widget.Toolbar","toolbarmenu":"dojo.widget.Toolbar","toolbarseparator":"dojo.widget.Toolbar","toolbarspace":"dojo.widget.Toolbar","toolbarselect":"dojo.widget.Toolbar","toolbarcolordialog":"dojo.widget.Toolbar","tooltip":"dojo.widget.Tooltip","tree":"dojo.widget.Tree","treebasiccontroller":"dojo.widget.TreeBasicController","treecontextmenu":"dojo.widget.TreeContextMenu","treedisablewrapextension":"dojo.widget.TreeDisableWrapExtension","treedociconextension":"dojo.widget.TreeDocIconExtension","treeeditor":"dojo.widget.TreeEditor","treeemphasizeonselect":"dojo.widget.TreeEmphasizeOnSelect","treeexpandtonodeonselect":"dojo.widget.TreeExpandToNodeOnSelect","treelinkextension":"dojo.widget.TreeLinkExtension","treeloadingcontroller":"dojo.widget.TreeLoadingController","treemenuitem":"dojo.widget.TreeContextMenu","treenode":"dojo.widget.TreeNode","treerpccontroller":"dojo.widget.TreeRPCController","treeselector":"dojo.widget.TreeSelector","treetoggleonselect":"dojo.widget.TreeToggleOnSelect","treev3":"dojo.widget.TreeV3","treebasiccontrollerv3":"dojo.widget.TreeBasicControllerV3","treecontextmenuv3":"dojo.widget.TreeContextMenuV3","treedndcontrollerv3":"dojo.widget.TreeDndControllerV3","treeloadingcontrollerv3":"dojo.widget.TreeLoadingControllerV3","treemenuitemv3":"dojo.widget.TreeContextMenuV3","treerpccontrollerv3":"dojo.widget.TreeRpcControllerV3","treeselectorv3":"dojo.widget.TreeSelectorV3","urltextbox":"dojo.widget.InternetTextbox","usphonenumbertextbox":"dojo.widget.UsTextbox","ussocialsecuritynumbertextbox":"dojo.widget.UsTextbox","usstatetextbox":"dojo.widget.UsTextbox","usziptextbox":"dojo.widget.UsTextbox","validationtextbox":"dojo.widget.ValidationTextbox","treeloadingcontroller":"dojo.widget.TreeLoadingController","wizardcontainer":"dojo.widget.Wizard","wizardpane":"dojo.widget.Wizard","yahoomap":"dojo.widget.YahooMap"},svg:{"chart":"dojo.widget.svg.Chart"},vml:{"chart":"dojo.widget.vml.Chart"}};
dojo.addDojoNamespaceMapping=function(_9f8,_9f9){
map[_9f8]=_9f9;
};
function dojoNamespaceResolver(name,_9fb){
if(!_9fb){
_9fb="html";
}
if(!map[_9fb]){
return null;
}
return map[_9fb][name];
}
dojo.registerNamespaceResolver("dojo",dojoNamespaceResolver);
})();
dojo.provide("dojo.widget.html.layout");
dojo.widget.html.layout=function(_9fc,_9fd,_9fe){
dojo.html.addClass(_9fc,"dojoLayoutContainer");
_9fd=dojo.lang.filter(_9fd,function(_9ff,idx){
_9ff.idx=idx;
return dojo.lang.inArray(["top","bottom","left","right","client","flood"],_9ff.layoutAlign);
});
if(_9fe&&_9fe!="none"){
var rank=function(_a02){
switch(_a02.layoutAlign){
case "flood":
return 1;
case "left":
case "right":
return (_9fe=="left-right")?2:3;
case "top":
case "bottom":
return (_9fe=="left-right")?3:2;
default:
return 4;
}
};
_9fd.sort(function(a,b){
return (rank(a)-rank(b))||(a.idx-b.idx);
});
}
var f={top:dojo.html.getPixelValue(_9fc,"padding-top",true),left:dojo.html.getPixelValue(_9fc,"padding-left",true)};
dojo.lang.mixin(f,dojo.html.getContentBox(_9fc));
dojo.lang.forEach(_9fd,function(_a06){
var elm=_a06.domNode;
var pos=_a06.layoutAlign;
with(elm.style){
left=f.left+"px";
top=f.top+"px";
bottom="auto";
right="auto";
}
dojo.html.addClass(elm,"dojoAlign"+dojo.string.capitalize(pos));
if((pos=="top")||(pos=="bottom")){
dojo.html.setMarginBox(elm,{width:f.width});
var h=dojo.html.getMarginBox(elm).height;
f.height-=h;
if(pos=="top"){
f.top+=h;
}else{
elm.style.top=f.top+f.height+"px";
}
if(_a06.onResized){
_a06.onResized();
}
}else{
if(pos=="left"||pos=="right"){
var w=dojo.html.getMarginBox(elm).width;
if(_a06.resizeTo){
_a06.resizeTo(w,f.height);
}else{
dojo.html.setMarginBox(elm,{width:w,height:f.height});
}
f.width-=w;
if(pos=="left"){
f.left+=w;
}else{
elm.style.left=f.left+f.width+"px";
}
}else{
if(pos=="flood"||pos=="client"){
if(_a06.resizeTo){
_a06.resizeTo(f.width,f.height);
}else{
dojo.html.setMarginBox(elm,{width:f.width,height:f.height});
}
}
}
}
});
};
dojo.html.insertCssText(".dojoLayoutContainer{ position: relative; display: block; overflow: hidden; }\n"+"body .dojoAlignTop, body .dojoAlignBottom, body .dojoAlignLeft, body .dojoAlignRight { position: absolute; overflow: hidden; }\n"+"body .dojoAlignClient { position: absolute }\n"+".dojoAlignClient { overflow: auto; }\n");
dojo.provide("dojo.widget.Dialog");
dojo.declare("dojo.widget.ModalDialogBase",null,{isContainer:true,focusElement:"",bgColor:"black",bgOpacity:0.4,followScroll:true,closeOnBackgroundClick:false,trapTabs:function(e){
if(e.target==this.tabStartOuter){
if(this._fromTrap){
this.tabStart.focus();
this._fromTrap=false;
}else{
this._fromTrap=true;
this.tabEnd.focus();
}
}else{
if(e.target==this.tabStart){
if(this._fromTrap){
this._fromTrap=false;
}else{
this._fromTrap=true;
this.tabEnd.focus();
}
}else{
if(e.target==this.tabEndOuter){
if(this._fromTrap){
this.tabEnd.focus();
this._fromTrap=false;
}else{
this._fromTrap=true;
this.tabStart.focus();
}
}else{
if(e.target==this.tabEnd){
if(this._fromTrap){
this._fromTrap=false;
}else{
this._fromTrap=true;
this.tabStart.focus();
}
}
}
}
}
},clearTrap:function(e){
var _a0d=this;
setTimeout(function(){
_a0d._fromTrap=false;
},100);
},postCreate:function(){
with(this.domNode.style){
position="absolute";
zIndex=999;
display="none";
overflow="visible";
}
var b=dojo.body();
b.appendChild(this.domNode);
this.bg=document.createElement("div");
this.bg.className="dialogUnderlay";
with(this.bg.style){
position="absolute";
left=top="0px";
zIndex=998;
display="none";
}
b.appendChild(this.bg);
this.setBackgroundColor(this.bgColor);
this.bgIframe=new dojo.html.BackgroundIframe();
if(this.bgIframe.iframe){
with(this.bgIframe.iframe.style){
position="absolute";
left=top="0px";
zIndex=90;
display="none";
}
}
if(this.closeOnBackgroundClick){
dojo.event.kwConnect({srcObj:this.bg,srcFunc:"onclick",adviceObj:this,adviceFunc:"onBackgroundClick",once:true});
}
},uninitialize:function(){
this.bgIframe.remove();
dojo.html.removeNode(this.bg,true);
},setBackgroundColor:function(_a0f){
if(arguments.length>=3){
_a0f=new dojo.gfx.color.Color(arguments[0],arguments[1],arguments[2]);
}else{
_a0f=new dojo.gfx.color.Color(_a0f);
}
this.bg.style.backgroundColor=_a0f.toString();
return this.bgColor=_a0f;
},setBackgroundOpacity:function(op){
if(arguments.length==0){
op=this.bgOpacity;
}
dojo.html.setOpacity(this.bg,op);
try{
this.bgOpacity=dojo.html.getOpacity(this.bg);
}
catch(e){
this.bgOpacity=op;
}
return this.bgOpacity;
},_sizeBackground:function(){
if(this.bgOpacity>0){
var _a11=dojo.html.getViewport();
var h=_a11.height;
var w=_a11.width;
with(this.bg.style){
width=w+"px";
height=h+"px";
}
var _a14=dojo.html.getScroll().offset;
this.bg.style.top=_a14.y+"px";
this.bg.style.left=_a14.x+"px";
var _a11=dojo.html.getViewport();
if(_a11.width!=w){
this.bg.style.width=_a11.width+"px";
}
if(_a11.height!=h){
this.bg.style.height=_a11.height+"px";
}
}
this.bgIframe.size(this.bg);
},_showBackground:function(){
if(this.bgOpacity>0){
this.bg.style.display="block";
}
if(this.bgIframe.iframe){
this.bgIframe.iframe.style.display="block";
}
},placeModalDialog:function(){
var _a15=dojo.html.getScroll().offset;
var _a16=dojo.html.getViewport();
var mb;
if(this.isShowing()){
mb=dojo.html.getMarginBox(this.domNode);
}else{
dojo.html.setVisibility(this.domNode,false);
dojo.html.show(this.domNode);
mb=dojo.html.getMarginBox(this.domNode);
dojo.html.hide(this.domNode);
dojo.html.setVisibility(this.domNode,true);
}
var x=_a15.x+(_a16.width-mb.width)/2;
var y=_a15.y+(_a16.height-mb.height)/2;
with(this.domNode.style){
left=x+"px";
top=y+"px";
}
},_onKey:function(evt){
if(evt.key){
var node=evt.target;
while(node!=null){
if(node==this.domNode){
return;
}
node=node.parentNode;
}
if(evt.key!=evt.KEY_TAB){
dojo.event.browser.stopEvent(evt);
}else{
if(!dojo.render.html.opera){
try{
this.tabStart.focus();
}
catch(e){
}
}
}
}
},showModalDialog:function(){
if(this.followScroll&&!this._scrollConnected){
this._scrollConnected=true;
dojo.event.connect(window,"onscroll",this,"_onScroll");
}
dojo.event.connect(document.documentElement,"onkey",this,"_onKey");
this.placeModalDialog();
this.setBackgroundOpacity();
this._sizeBackground();
this._showBackground();
this._fromTrap=true;
setTimeout(dojo.lang.hitch(this,function(){
try{
this.tabStart.focus();
}
catch(e){
}
}),50);
},hideModalDialog:function(){
if(this.focusElement){
dojo.byId(this.focusElement).focus();
dojo.byId(this.focusElement).blur();
}
this.bg.style.display="none";
this.bg.style.width=this.bg.style.height="1px";
if(this.bgIframe.iframe){
this.bgIframe.iframe.style.display="none";
}
dojo.event.disconnect(document.documentElement,"onkey",this,"_onKey");
if(this._scrollConnected){
this._scrollConnected=false;
dojo.event.disconnect(window,"onscroll",this,"_onScroll");
}
},_onScroll:function(){
var _a1c=dojo.html.getScroll().offset;
this.bg.style.top=_a1c.y+"px";
this.bg.style.left=_a1c.x+"px";
this.placeModalDialog();
},checkSize:function(){
if(this.isShowing()){
this._sizeBackground();
this.placeModalDialog();
this.onResized();
}
},onBackgroundClick:function(){
if(this.lifetime-this.timeRemaining>=this.blockDuration){
return;
}
this.hide();
}});
dojo.widget.defineWidget("dojo.widget.Dialog",[dojo.widget.ContentPane,dojo.widget.ModalDialogBase],{templateString:"<div id=\"${this.widgetId}\" class=\"dojoDialog\" dojoattachpoint=\"wrapper\">\r\n\t<span dojoattachpoint=\"tabStartOuter\" dojoonfocus=\"trapTabs\" dojoonblur=\"clearTrap\"\ttabindex=\"0\"></span>\r\n\t<span dojoattachpoint=\"tabStart\" dojoonfocus=\"trapTabs\" dojoonblur=\"clearTrap\" tabindex=\"0\"></span>\r\n\t<div dojoattachpoint=\"containerNode\" style=\"position: relative; z-index: 2;\"></div>\r\n\t<span dojoattachpoint=\"tabEnd\" dojoonfocus=\"trapTabs\" dojoonblur=\"clearTrap\" tabindex=\"0\"></span>\r\n\t<span dojoattachpoint=\"tabEndOuter\" dojoonfocus=\"trapTabs\" dojoonblur=\"clearTrap\" tabindex=\"0\"></span>\r\n</div>\r\n",blockDuration:0,lifetime:0,closeNode:"",postMixInProperties:function(){
dojo.widget.Dialog.superclass.postMixInProperties.apply(this,arguments);
if(this.closeNode){
this.setCloseControl(this.closeNode);
}
},postCreate:function(){
dojo.widget.Dialog.superclass.postCreate.apply(this,arguments);
dojo.widget.ModalDialogBase.prototype.postCreate.apply(this,arguments);
},show:function(){
if(this.lifetime){
this.timeRemaining=this.lifetime;
if(this.timerNode){
this.timerNode.innerHTML=Math.ceil(this.timeRemaining/1000);
}
if(this.blockDuration&&this.closeNode){
if(this.lifetime>this.blockDuration){
this.closeNode.style.visibility="hidden";
}else{
this.closeNode.style.display="none";
}
}
if(this.timer){
clearInterval(this.timer);
}
this.timer=setInterval(dojo.lang.hitch(this,"_onTick"),100);
}
this.showModalDialog();
dojo.widget.Dialog.superclass.show.call(this);
},onLoad:function(){
this.placeModalDialog();
dojo.widget.Dialog.superclass.onLoad.call(this);
},fillInTemplate:function(){
},hide:function(){
this.hideModalDialog();
dojo.widget.Dialog.superclass.hide.call(this);
if(this.timer){
clearInterval(this.timer);
}
},setTimerNode:function(node){
this.timerNode=node;
},setCloseControl:function(node){
this.closeNode=dojo.byId(node);
dojo.event.connect(this.closeNode,"onclick",this,"hide");
},setShowControl:function(node){
node=dojo.byId(node);
dojo.event.connect(node,"onclick",this,"show");
},_onTick:function(){
if(this.timer){
this.timeRemaining-=100;
if(this.lifetime-this.timeRemaining>=this.blockDuration){
if(this.closeNode){
this.closeNode.style.visibility="visible";
}
}
if(!this.timeRemaining){
clearInterval(this.timer);
this.hide();
}else{
if(this.timerNode){
this.timerNode.innerHTML=Math.ceil(this.timeRemaining/1000);
}
}
}
}});
dojo.provide("dojo.widget.ResizeHandle");
dojo.widget.defineWidget("dojo.widget.ResizeHandle",dojo.widget.HtmlWidget,{targetElmId:"",templateCssString:".dojoHtmlResizeHandle {\r\n\tfloat: right;\r\n\tposition: absolute;\r\n\tright: 2px;\r\n\tbottom: 2px;\r\n\twidth: 13px;\r\n\theight: 13px;\r\n\tz-index: 20;\r\n\tcursor: nw-resize;\r\n\tbackground-image: url(grabCorner.gif);\r\n\tline-height: 0px;\r\n}\r\n",templateCssPath:dojo.uri.moduleUri("dojo.widget","templates/ResizeHandle.css"),templateString:"<div class=\"dojoHtmlResizeHandle\"><div></div></div>",postCreate:function(){
dojo.event.connect(this.domNode,"onmousedown",this,"_beginSizing");
},_beginSizing:function(e){
if(this._isSizing){
return false;
}
this.targetWidget=dojo.widget.byId(this.targetElmId);
this.targetDomNode=this.targetWidget?this.targetWidget.domNode:dojo.byId(this.targetElmId);
if(!this.targetDomNode){
return;
}
this._isSizing=true;
this.startPoint={"x":e.clientX,"y":e.clientY};
var mb=dojo.html.getMarginBox(this.targetDomNode);
this.startSize={"w":mb.width,"h":mb.height};
dojo.event.kwConnect({srcObj:dojo.body(),srcFunc:"onmousemove",targetObj:this,targetFunc:"_changeSizing",rate:25});
dojo.event.connect(dojo.body(),"onmouseup",this,"_endSizing");
e.preventDefault();
},_changeSizing:function(e){
try{
if(!e.clientX||!e.clientY){
return;
}
}
catch(e){
return;
}
var dx=this.startPoint.x-e.clientX;
var dy=this.startPoint.y-e.clientY;
var newW=this.startSize.w-dx;
var newH=this.startSize.h-dy;
if(this.minSize){
var mb=dojo.html.getMarginBox(this.targetDomNode);
if(newW<this.minSize.w){
newW=mb.width;
}
if(newH<this.minSize.h){
newH=mb.height;
}
}
if(this.targetWidget){
this.targetWidget.resizeTo(newW,newH);
}else{
dojo.html.setMarginBox(this.targetDomNode,{width:newW,height:newH});
}
e.preventDefault();
},_endSizing:function(e){
dojo.event.disconnect(dojo.body(),"onmousemove",this,"_changeSizing");
dojo.event.disconnect(dojo.body(),"onmouseup",this,"_endSizing");
this._isSizing=false;
}});
dojo.provide("dojo.widget.FloatingPane");
dojo.declare("dojo.widget.FloatingPaneBase",null,{title:"",iconSrc:"",hasShadow:false,constrainToContainer:false,taskBarId:"",resizable:true,titleBarDisplay:true,windowState:"normal",displayCloseAction:false,displayMinimizeAction:false,displayMaximizeAction:false,_max_taskBarConnectAttempts:5,_taskBarConnectAttempts:0,templateString:"<div id=\"${this.widgetId}\" dojoAttachEvent=\"onMouseDown\" class=\"dojoFloatingPane\">\r\n\t<div dojoAttachPoint=\"titleBar\" class=\"dojoFloatingPaneTitleBar\"  style=\"display:none\">\r\n\t  \t<img dojoAttachPoint=\"titleBarIcon\"  class=\"dojoFloatingPaneTitleBarIcon\">\r\n\t\t<div dojoAttachPoint=\"closeAction\" dojoAttachEvent=\"onClick:closeWindow\"\r\n   \t  \t\tclass=\"dojoFloatingPaneCloseIcon\"></div>\r\n\t\t<div dojoAttachPoint=\"restoreAction\" dojoAttachEvent=\"onClick:restoreWindow\"\r\n   \t  \t\tclass=\"dojoFloatingPaneRestoreIcon\"></div>\r\n\t\t<div dojoAttachPoint=\"maximizeAction\" dojoAttachEvent=\"onClick:maximizeWindow\"\r\n   \t  \t\tclass=\"dojoFloatingPaneMaximizeIcon\"></div>\r\n\t\t<div dojoAttachPoint=\"minimizeAction\" dojoAttachEvent=\"onClick:minimizeWindow\"\r\n   \t  \t\tclass=\"dojoFloatingPaneMinimizeIcon\"></div>\r\n\t  \t<div dojoAttachPoint=\"titleBarText\" class=\"dojoFloatingPaneTitleText\">${this.title}</div>\r\n\t</div>\r\n\r\n\t<div id=\"${this.widgetId}_container\" dojoAttachPoint=\"containerNode\" class=\"dojoFloatingPaneClient\"></div>\r\n\r\n\t<div dojoAttachPoint=\"resizeBar\" class=\"dojoFloatingPaneResizebar\" style=\"display:none\"></div>\r\n</div>\r\n",templateCssString:"\r\n/********** Outer Window ***************/\r\n\r\n.dojoFloatingPane {\r\n\t/* essential css */\r\n\tposition: absolute;\r\n\toverflow: visible;\t\t/* so drop shadow is displayed */\r\n\tz-index: 10;\r\n\r\n\t/* styling css */\r\n\tborder: 1px solid;\r\n\tborder-color: ThreeDHighlight ThreeDShadow ThreeDShadow ThreeDHighlight;\r\n\tbackground-color: ThreeDFace;\r\n}\r\n\r\n\r\n/********** Title Bar ****************/\r\n\r\n.dojoFloatingPaneTitleBar {\r\n\tvertical-align: top;\r\n\tmargin: 2px 2px 2px 2px;\r\n\tz-index: 10;\r\n\tbackground-color: #7596c6;\r\n\tcursor: default;\r\n\toverflow: hidden;\r\n\tborder-color: ThreeDHighlight ThreeDShadow ThreeDShadow ThreeDHighlight;\r\n\tvertical-align: middle;\r\n}\r\n\r\n.dojoFloatingPaneTitleText {\r\n\tfloat: left;\r\n\tpadding: 2px 4px 2px 2px;\r\n\twhite-space: nowrap;\r\n\tcolor: CaptionText;\r\n\tfont: small-caption;\r\n}\r\n\r\n.dojoTitleBarIcon {\r\n\tfloat: left;\r\n\theight: 22px;\r\n\twidth: 22px;\r\n\tvertical-align: middle;\r\n\tmargin-right: 5px;\r\n\tmargin-left: 5px;\r\n}\r\n\r\n.dojoFloatingPaneActions{\r\n\tfloat: right;\r\n\tposition: absolute;\r\n\tright: 2px;\r\n\ttop: 2px;\r\n\tvertical-align: middle;\r\n}\r\n\r\n\r\n.dojoFloatingPaneActionItem {\r\n\tvertical-align: middle;\r\n\tmargin-right: 1px;\r\n\theight: 22px;\r\n\twidth: 22px;\r\n}\r\n\r\n\r\n.dojoFloatingPaneTitleBarIcon {\r\n\t/* essential css */\r\n\tfloat: left;\r\n\r\n\t/* styling css */\r\n\tmargin-left: 2px;\r\n\tmargin-right: 4px;\r\n\theight: 22px;\r\n}\r\n\r\n/* minimize/maximize icons are specified by CSS only */\r\n.dojoFloatingPaneMinimizeIcon,\r\n.dojoFloatingPaneMaximizeIcon,\r\n.dojoFloatingPaneRestoreIcon,\r\n.dojoFloatingPaneCloseIcon {\r\n\tvertical-align: middle;\r\n\theight: 22px;\r\n\twidth: 22px;\r\n\tfloat: right;\r\n}\r\n.dojoFloatingPaneMinimizeIcon {\r\n\tbackground-image: url(images/floatingPaneMinimize.gif);\r\n}\r\n.dojoFloatingPaneMaximizeIcon {\r\n\tbackground-image: url(images/floatingPaneMaximize.gif);\r\n}\r\n.dojoFloatingPaneRestoreIcon {\r\n\tbackground-image: url(images/floatingPaneRestore.gif);\r\n}\r\n.dojoFloatingPaneCloseIcon {\r\n\tbackground-image: url(images/floatingPaneClose.gif);\r\n}\r\n\r\n/* bar at bottom of window that holds resize handle */\r\n.dojoFloatingPaneResizebar {\r\n\tz-index: 10;\r\n\theight: 13px;\r\n\tbackground-color: ThreeDFace;\r\n}\r\n\r\n/************* Client Area ***************/\r\n\r\n.dojoFloatingPaneClient {\r\n\tposition: relative;\r\n\tz-index: 10;\r\n\tborder: 1px solid;\r\n\tborder-color: ThreeDShadow ThreeDHighlight ThreeDHighlight ThreeDShadow;\r\n\tmargin: 2px;\r\n\tbackground-color: ThreeDFace;\r\n\tpadding: 8px;\r\n\tfont-family: Verdana, Helvetica, Garamond, sans-serif;\r\n\tfont-size: 12px;\r\n\toverflow: auto;\r\n}\r\n\r\n",templateCssPath:dojo.uri.moduleUri("dojo.widget","templates/FloatingPane.css"),fillInFloatingPaneTemplate:function(args,frag){
var _a2b=this.getFragNodeRef(frag);
dojo.html.copyStyle(this.domNode,_a2b);
dojo.body().appendChild(this.domNode);
if(!this.isShowing()){
this.windowState="minimized";
}
if(this.iconSrc==""){
dojo.html.removeNode(this.titleBarIcon);
}else{
this.titleBarIcon.src=this.iconSrc.toString();
}
if(this.titleBarDisplay){
this.titleBar.style.display="";
dojo.html.disableSelection(this.titleBar);
this.titleBarIcon.style.display=(this.iconSrc==""?"none":"");
this.minimizeAction.style.display=(this.displayMinimizeAction?"":"none");
this.maximizeAction.style.display=(this.displayMaximizeAction&&this.windowState!="maximized"?"":"none");
this.restoreAction.style.display=(this.displayMaximizeAction&&this.windowState=="maximized"?"":"none");
this.closeAction.style.display=(this.displayCloseAction?"":"none");
this.drag=new dojo.dnd.HtmlDragMoveSource(this.domNode);
if(this.constrainToContainer){
this.drag.constrainTo();
}
this.drag.setDragHandle(this.titleBar);
var self=this;
dojo.event.topic.subscribe("dragMove",function(info){
if(info.source.domNode==self.domNode){
dojo.event.topic.publish("floatingPaneMove",{source:self});
}
});
}
if(this.resizable){
this.resizeBar.style.display="";
this.resizeHandle=dojo.widget.createWidget("ResizeHandle",{targetElmId:this.widgetId,id:this.widgetId+"_resize"});
this.resizeBar.appendChild(this.resizeHandle.domNode);
}
if(this.hasShadow){
this.shadow=new dojo.lfx.shadow(this.domNode);
}
this.bgIframe=new dojo.html.BackgroundIframe(this.domNode);
if(this.taskBarId){
this._taskBarSetup();
}
dojo.body().removeChild(this.domNode);
},postCreate:function(){
if(dojo.hostenv.post_load_){
this._setInitialWindowState();
}else{
dojo.addOnLoad(this,"_setInitialWindowState");
}
},maximizeWindow:function(evt){
var mb=dojo.html.getMarginBox(this.domNode);
this.previous={width:mb.width||this.width,height:mb.height||this.height,left:this.domNode.style.left,top:this.domNode.style.top,bottom:this.domNode.style.bottom,right:this.domNode.style.right};
if(this.domNode.parentNode.style.overflow.toLowerCase()!="hidden"){
this.parentPrevious={overflow:this.domNode.parentNode.style.overflow};
dojo.debug(this.domNode.parentNode.style.overflow);
this.domNode.parentNode.style.overflow="hidden";
}
this.domNode.style.left=dojo.html.getPixelValue(this.domNode.parentNode,"padding-left",true)+"px";
this.domNode.style.top=dojo.html.getPixelValue(this.domNode.parentNode,"padding-top",true)+"px";
if((this.domNode.parentNode.nodeName.toLowerCase()=="body")){
var _a30=dojo.html.getViewport();
var _a31=dojo.html.getPadding(dojo.body());
this.resizeTo(_a30.width-_a31.width,_a30.height-_a31.height);
}else{
var _a32=dojo.html.getContentBox(this.domNode.parentNode);
this.resizeTo(_a32.width,_a32.height);
}
this.maximizeAction.style.display="none";
this.restoreAction.style.display="";
if(this.resizeHandle){
this.resizeHandle.domNode.style.display="none";
}
this.drag.setDragHandle(null);
this.windowState="maximized";
},minimizeWindow:function(evt){
this.hide();
for(var attr in this.parentPrevious){
this.domNode.parentNode.style[attr]=this.parentPrevious[attr];
}
this.lastWindowState=this.windowState;
this.windowState="minimized";
},restoreWindow:function(evt){
if(this.windowState=="minimized"){
this.show();
if(this.lastWindowState=="maximized"){
this.domNode.parentNode.style.overflow="hidden";
this.windowState="maximized";
}else{
this.windowState="normal";
}
}else{
if(this.windowState=="maximized"){
for(var attr in this.previous){
this.domNode.style[attr]=this.previous[attr];
}
for(var attr in this.parentPrevious){
this.domNode.parentNode.style[attr]=this.parentPrevious[attr];
}
this.resizeTo(this.previous.width,this.previous.height);
this.previous=null;
this.parentPrevious=null;
this.restoreAction.style.display="none";
this.maximizeAction.style.display=this.displayMaximizeAction?"":"none";
if(this.resizeHandle){
this.resizeHandle.domNode.style.display="";
}
this.drag.setDragHandle(this.titleBar);
this.windowState="normal";
}else{
}
}
},toggleDisplay:function(){
if(this.windowState=="minimized"){
this.restoreWindow();
}else{
this.minimizeWindow();
}
},closeWindow:function(evt){
dojo.html.removeNode(this.domNode);
this.destroy();
},onMouseDown:function(evt){
this.bringToTop();
},bringToTop:function(){
var _a39=dojo.widget.manager.getWidgetsByType(this.widgetType);
var _a3a=[];
for(var x=0;x<_a39.length;x++){
if(this.widgetId!=_a39[x].widgetId){
_a3a.push(_a39[x]);
}
}
_a3a.sort(function(a,b){
return a.domNode.style.zIndex-b.domNode.style.zIndex;
});
_a3a.push(this);
var _a3e=100;
for(x=0;x<_a3a.length;x++){
_a3a[x].domNode.style.zIndex=_a3e+x*2;
}
},_setInitialWindowState:function(){
if(this.isShowing()){
this.width=-1;
var mb=dojo.html.getMarginBox(this.domNode);
this.resizeTo(mb.width,mb.height);
}
if(this.windowState=="maximized"){
this.maximizeWindow();
this.show();
return;
}
if(this.windowState=="normal"){
this.show();
return;
}
if(this.windowState=="minimized"){
this.hide();
return;
}
this.windowState="minimized";
},_taskBarSetup:function(){
var _a40=dojo.widget.getWidgetById(this.taskBarId);
if(!_a40){
if(this._taskBarConnectAttempts<this._max_taskBarConnectAttempts){
dojo.lang.setTimeout(this,this._taskBarSetup,50);
this._taskBarConnectAttempts++;
}else{
dojo.debug("Unable to connect to the taskBar");
}
return;
}
_a40.addChild(this);
},showFloatingPane:function(){
this.bringToTop();
},onFloatingPaneShow:function(){
var mb=dojo.html.getMarginBox(this.domNode);
this.resizeTo(mb.width,mb.height);
},resizeTo:function(_a42,_a43){
dojo.html.setMarginBox(this.domNode,{width:_a42,height:_a43});
dojo.widget.html.layout(this.domNode,[{domNode:this.titleBar,layoutAlign:"top"},{domNode:this.resizeBar,layoutAlign:"bottom"},{domNode:this.containerNode,layoutAlign:"client"}]);
dojo.widget.html.layout(this.containerNode,this.children,"top-bottom");
this.bgIframe.onResized();
if(this.shadow){
this.shadow.size(_a42,_a43);
}
this.onResized();
},checkSize:function(){
},destroyFloatingPane:function(){
if(this.resizeHandle){
this.resizeHandle.destroy();
this.resizeHandle=null;
}
}});
dojo.widget.defineWidget("dojo.widget.FloatingPane",[dojo.widget.ContentPane,dojo.widget.FloatingPaneBase],{fillInTemplate:function(args,frag){
this.fillInFloatingPaneTemplate(args,frag);
dojo.widget.FloatingPane.superclass.fillInTemplate.call(this,args,frag);
},postCreate:function(){
dojo.widget.FloatingPaneBase.prototype.postCreate.apply(this,arguments);
dojo.widget.FloatingPane.superclass.postCreate.apply(this,arguments);
},show:function(){
dojo.widget.FloatingPane.superclass.show.apply(this,arguments);
this.showFloatingPane();
},onShow:function(){
dojo.widget.FloatingPane.superclass.onShow.call(this);
this.onFloatingPaneShow();
},destroy:function(){
this.destroyFloatingPane();
dojo.widget.FloatingPane.superclass.destroy.apply(this,arguments);
}});
dojo.widget.defineWidget("dojo.widget.ModalFloatingPane",[dojo.widget.FloatingPane,dojo.widget.ModalDialogBase],{windowState:"minimized",displayCloseAction:true,postCreate:function(){
dojo.widget.ModalDialogBase.prototype.postCreate.call(this);
dojo.widget.ModalFloatingPane.superclass.postCreate.call(this);
},show:function(){
this.showModalDialog();
dojo.widget.ModalFloatingPane.superclass.show.apply(this,arguments);
this.bg.style.zIndex=this.domNode.style.zIndex-1;
},hide:function(){
this.hideModalDialog();
dojo.widget.ModalFloatingPane.superclass.hide.apply(this,arguments);
},closeWindow:function(){
this.hide();
dojo.widget.ModalFloatingPane.superclass.closeWindow.apply(this,arguments);
}});
dojo.provide("dojo.widget.SplitContainer");
dojo.widget.defineWidget("dojo.widget.SplitContainer",dojo.widget.HtmlWidget,function(){
this.sizers=[];
},{isContainer:true,templateCssString:".dojoSplitContainer{\r\n\tposition: relative;\r\n\toverflow: hidden;\r\n\tdisplay: block;\r\n}\r\n\r\n.dojoSplitPane{\r\n\tposition: absolute;\r\n}\r\n\r\n.dojoSplitContainerSizerH,\r\n.dojoSplitContainerSizerV {\r\n\tfont-size: 1px;\r\n\tcursor: move;\r\n\tcursor: w-resize;\r\n\tbackground-color: ThreeDFace;\r\n\tborder: 1px solid;\r\n\tborder-color: ThreeDHighlight ThreeDShadow ThreeDShadow ThreeDHighlight;\r\n\tmargin: 0;\r\n}\r\n\r\n.dojoSplitContainerSizerV {\r\n\tcursor: n-resize;\r\n}\r\n\r\n.dojoSplitContainerVirtualSizerH,\r\n.dojoSplitContainerVirtualSizerV {\r\n\tfont-size: 1px;\r\n\tcursor: move;\r\n\tcursor: w-resize;\r\n\tbackground-color: ThreeDShadow;\r\n\t-moz-opacity: 0.5;\r\n\topacity: 0.5;\r\n\tfilter: Alpha(Opacity=50);\r\n\tmargin: 0;\r\n}\r\n\r\n.dojoSplitContainerVirtualSizerV {\r\n\tcursor: n-resize;\r\n}\r\n",templateCssPath:dojo.uri.moduleUri("dojo.widget","templates/SplitContainer.css"),activeSizing:false,sizerWidth:15,orientation:"horizontal",persist:true,postMixInProperties:function(){
dojo.widget.SplitContainer.superclass.postMixInProperties.apply(this,arguments);
this.isHorizontal=(this.orientation=="horizontal");
},fillInTemplate:function(){
dojo.widget.SplitContainer.superclass.fillInTemplate.apply(this,arguments);
dojo.html.addClass(this.domNode,"dojoSplitContainer");
if(dojo.render.html.moz){
this.domNode.style.overflow="-moz-scrollbars-none";
}
var _a46=dojo.html.getContentBox(this.domNode);
this.paneWidth=_a46.width;
this.paneHeight=_a46.height;
},onResized:function(e){
var _a48=dojo.html.getContentBox(this.domNode);
this.paneWidth=_a48.width;
this.paneHeight=_a48.height;
this._layoutPanels();
},postCreate:function(args,_a4a,_a4b){
dojo.widget.SplitContainer.superclass.postCreate.apply(this,arguments);
for(var i=0;i<this.children.length;i++){
with(this.children[i].domNode.style){
position="absolute";
}
dojo.html.addClass(this.children[i].domNode,"dojoSplitPane");
if(i==this.children.length-1){
break;
}
this._addSizer();
}
if(typeof this.sizerWidth=="object"){
try{
this.sizerWidth=parseInt(this.sizerWidth.toString());
}
catch(e){
this.sizerWidth=15;
}
}
this.virtualSizer=document.createElement("div");
this.virtualSizer.style.position="absolute";
this.virtualSizer.style.display="none";
this.virtualSizer.style.zIndex=10;
this.virtualSizer.className=this.isHorizontal?"dojoSplitContainerVirtualSizerH":"dojoSplitContainerVirtualSizerV";
this.domNode.appendChild(this.virtualSizer);
dojo.html.disableSelection(this.virtualSizer);
if(this.persist){
this._restoreState();
}
this.resizeSoon();
},_injectChild:function(_a4d){
with(_a4d.domNode.style){
position="absolute";
}
dojo.html.addClass(_a4d.domNode,"dojoSplitPane");
},_addSizer:function(){
var i=this.sizers.length;
this.sizers[i]=document.createElement("div");
this.sizers[i].style.position="absolute";
this.sizers[i].className=this.isHorizontal?"dojoSplitContainerSizerH":"dojoSplitContainerSizerV";
var self=this;
var _a50=(function(){
var _a51=i;
return function(e){
self.beginSizing(e,_a51);
};
})();
dojo.event.connect(this.sizers[i],"onmousedown",_a50);
this.domNode.appendChild(this.sizers[i]);
dojo.html.disableSelection(this.sizers[i]);
},removeChild:function(_a53){
if(this.sizers.length>0){
for(var x=0;x<this.children.length;x++){
if(this.children[x]===_a53){
var i=this.sizers.length-1;
this.domNode.removeChild(this.sizers[i]);
this.sizers.length=i;
break;
}
}
}
dojo.widget.SplitContainer.superclass.removeChild.call(this,_a53,arguments);
this.onResized();
},addChild:function(_a56){
dojo.widget.SplitContainer.superclass.addChild.apply(this,arguments);
this._injectChild(_a56);
if(this.children.length>1){
this._addSizer();
}
this._layoutPanels();
},_layoutPanels:function(){
if(this.children.length==0){
return;
}
var _a57=this.isHorizontal?this.paneWidth:this.paneHeight;
if(this.children.length>1){
_a57-=this.sizerWidth*(this.children.length-1);
}
var _a58=0;
for(var i=0;i<this.children.length;i++){
_a58+=this.children[i].sizeShare;
}
var _a5a=_a57/_a58;
var _a5b=0;
for(var i=0;i<this.children.length-1;i++){
var size=Math.round(_a5a*this.children[i].sizeShare);
this.children[i].sizeActual=size;
_a5b+=size;
}
this.children[this.children.length-1].sizeActual=_a57-_a5b;
this._checkSizes();
var pos=0;
var size=this.children[0].sizeActual;
this._movePanel(this.children[0],pos,size);
this.children[0].position=pos;
pos+=size;
for(var i=1;i<this.children.length;i++){
this._moveSlider(this.sizers[i-1],pos,this.sizerWidth);
this.sizers[i-1].position=pos;
pos+=this.sizerWidth;
size=this.children[i].sizeActual;
this._movePanel(this.children[i],pos,size);
this.children[i].position=pos;
pos+=size;
}
},_movePanel:function(_a5e,pos,size){
if(this.isHorizontal){
_a5e.domNode.style.left=pos+"px";
_a5e.domNode.style.top=0;
_a5e.resizeTo(size,this.paneHeight);
}else{
_a5e.domNode.style.left=0;
_a5e.domNode.style.top=pos+"px";
_a5e.resizeTo(this.paneWidth,size);
}
},_moveSlider:function(_a61,pos,size){
if(this.isHorizontal){
_a61.style.left=pos+"px";
_a61.style.top=0;
dojo.html.setMarginBox(_a61,{width:size,height:this.paneHeight});
}else{
_a61.style.left=0;
_a61.style.top=pos+"px";
dojo.html.setMarginBox(_a61,{width:this.paneWidth,height:size});
}
},_growPane:function(_a64,pane){
if(_a64>0){
if(pane.sizeActual>pane.sizeMin){
if((pane.sizeActual-pane.sizeMin)>_a64){
pane.sizeActual=pane.sizeActual-_a64;
_a64=0;
}else{
_a64-=pane.sizeActual-pane.sizeMin;
pane.sizeActual=pane.sizeMin;
}
}
}
return _a64;
},_checkSizes:function(){
var _a66=0;
var _a67=0;
for(var i=0;i<this.children.length;i++){
_a67+=this.children[i].sizeActual;
_a66+=this.children[i].sizeMin;
}
if(_a66<=_a67){
var _a69=0;
for(var i=0;i<this.children.length;i++){
if(this.children[i].sizeActual<this.children[i].sizeMin){
_a69+=this.children[i].sizeMin-this.children[i].sizeActual;
this.children[i].sizeActual=this.children[i].sizeMin;
}
}
if(_a69>0){
if(this.isDraggingLeft){
for(var i=this.children.length-1;i>=0;i--){
_a69=this._growPane(_a69,this.children[i]);
}
}else{
for(var i=0;i<this.children.length;i++){
_a69=this._growPane(_a69,this.children[i]);
}
}
}
}else{
for(var i=0;i<this.children.length;i++){
this.children[i].sizeActual=Math.round(_a67*(this.children[i].sizeMin/_a66));
}
}
},beginSizing:function(e,i){
this.paneBefore=this.children[i];
this.paneAfter=this.children[i+1];
this.isSizing=true;
this.sizingSplitter=this.sizers[i];
this.originPos=dojo.html.getAbsolutePosition(this.children[0].domNode,true,dojo.html.boxSizing.MARGIN_BOX);
if(this.isHorizontal){
var _a6c=(e.layerX?e.layerX:e.offsetX);
var _a6d=e.pageX;
this.originPos=this.originPos.x;
}else{
var _a6c=(e.layerY?e.layerY:e.offsetY);
var _a6d=e.pageY;
this.originPos=this.originPos.y;
}
this.startPoint=this.lastPoint=_a6d;
this.screenToClientOffset=_a6d-_a6c;
this.dragOffset=this.lastPoint-this.paneBefore.sizeActual-this.originPos-this.paneBefore.position;
if(!this.activeSizing){
this._showSizingLine();
}
dojo.event.connect(document.documentElement,"onmousemove",this,"changeSizing");
dojo.event.connect(document.documentElement,"onmouseup",this,"endSizing");
dojo.event.browser.stopEvent(e);
},changeSizing:function(e){
this.lastPoint=this.isHorizontal?e.pageX:e.pageY;
if(this.activeSizing){
this.movePoint();
this._updateSize();
}else{
this.movePoint();
this._moveSizingLine();
}
dojo.event.browser.stopEvent(e);
},endSizing:function(e){
if(!this.activeSizing){
this._hideSizingLine();
}
this._updateSize();
this.isSizing=false;
dojo.event.disconnect(document.documentElement,"onmousemove",this,"changeSizing");
dojo.event.disconnect(document.documentElement,"onmouseup",this,"endSizing");
if(this.persist){
this._saveState(this);
}
},movePoint:function(){
var p=this.lastPoint-this.screenToClientOffset;
var a=p-this.dragOffset;
a=this.legaliseSplitPoint(a);
p=a+this.dragOffset;
this.lastPoint=p+this.screenToClientOffset;
},legaliseSplitPoint:function(a){
a+=this.sizingSplitter.position;
this.isDraggingLeft=(a>0)?true:false;
if(!this.activeSizing){
if(a<this.paneBefore.position+this.paneBefore.sizeMin){
a=this.paneBefore.position+this.paneBefore.sizeMin;
}
if(a>this.paneAfter.position+(this.paneAfter.sizeActual-(this.sizerWidth+this.paneAfter.sizeMin))){
a=this.paneAfter.position+(this.paneAfter.sizeActual-(this.sizerWidth+this.paneAfter.sizeMin));
}
}
a-=this.sizingSplitter.position;
this._checkSizes();
return a;
},_updateSize:function(){
var pos=this.lastPoint-this.dragOffset-this.originPos;
var _a74=this.paneBefore.position;
var _a75=this.paneAfter.position+this.paneAfter.sizeActual;
this.paneBefore.sizeActual=pos-_a74;
this.paneAfter.position=pos+this.sizerWidth;
this.paneAfter.sizeActual=_a75-this.paneAfter.position;
for(var i=0;i<this.children.length;i++){
this.children[i].sizeShare=this.children[i].sizeActual;
}
this._layoutPanels();
},_showSizingLine:function(){
this._moveSizingLine();
if(this.isHorizontal){
dojo.html.setMarginBox(this.virtualSizer,{width:this.sizerWidth,height:this.paneHeight});
}else{
dojo.html.setMarginBox(this.virtualSizer,{width:this.paneWidth,height:this.sizerWidth});
}
this.virtualSizer.style.display="block";
},_hideSizingLine:function(){
this.virtualSizer.style.display="none";
},_moveSizingLine:function(){
var pos=this.lastPoint-this.startPoint+this.sizingSplitter.position;
if(this.isHorizontal){
this.virtualSizer.style.left=pos+"px";
}else{
var pos=(this.lastPoint-this.startPoint)+this.sizingSplitter.position;
this.virtualSizer.style.top=pos+"px";
}
},_getCookieName:function(i){
return this.widgetId+"_"+i;
},_restoreState:function(){
for(var i=0;i<this.children.length;i++){
var _a7a=this._getCookieName(i);
var _a7b=dojo.io.cookie.getCookie(_a7a);
if(_a7b!=null){
var pos=parseInt(_a7b);
if(typeof pos=="number"){
this.children[i].sizeShare=pos;
}
}
}
},_saveState:function(){
for(var i=0;i<this.children.length;i++){
var _a7e=this._getCookieName(i);
dojo.io.cookie.setCookie(_a7e,this.children[i].sizeShare,null,null,null,null);
}
}});
dojo.lang.extend(dojo.widget.Widget,{sizeMin:10,sizeShare:10});
dojo.widget.defineWidget("dojo.widget.SplitContainerPanel",dojo.widget.ContentPane,{});
dojo.provide("dojo.widget.TreeNode");
dojo.widget.defineWidget("dojo.widget.TreeNode",dojo.widget.HtmlWidget,function(){
this.actionsDisabled=[];
},{widgetType:"TreeNode",loadStates:{UNCHECKED:"UNCHECKED",LOADING:"LOADING",LOADED:"LOADED"},actions:{MOVE:"MOVE",REMOVE:"REMOVE",EDIT:"EDIT",ADDCHILD:"ADDCHILD"},isContainer:true,lockLevel:0,templateString:("<div class=\"dojoTreeNode\"> "+"<span treeNode=\"${this.widgetId}\" class=\"dojoTreeNodeLabel\" dojoAttachPoint=\"labelNode\"> "+"\t\t<span dojoAttachPoint=\"titleNode\" dojoAttachEvent=\"onClick: onTitleClick\" class=\"dojoTreeNodeLabelTitle\">${this.title}</span> "+"</span> "+"<span class=\"dojoTreeNodeAfterLabel\" dojoAttachPoint=\"afterLabelNode\">${this.afterLabel}</span> "+"<div dojoAttachPoint=\"containerNode\" style=\"display:none\"></div> "+"</div>").replace(/(>|<)\s+/g,"$1"),childIconSrc:"",childIconFolderSrc:dojo.uri.moduleUri("dojo.widget","templates/images/Tree/closed.gif"),childIconDocumentSrc:dojo.uri.moduleUri("dojo.widget","templates/images/Tree/document.gif"),childIcon:null,isTreeNode:true,objectId:"",afterLabel:"",afterLabelNode:null,expandIcon:null,title:"",object:"",isFolder:false,labelNode:null,titleNode:null,imgs:null,expandLevel:"",tree:null,depth:0,isExpanded:false,state:null,domNodeInitialized:false,isFirstChild:function(){
return this.getParentIndex()==0?true:false;
},isLastChild:function(){
return this.getParentIndex()==this.parent.children.length-1?true:false;
},lock:function(){
return this.tree.lock.apply(this,arguments);
},unlock:function(){
return this.tree.unlock.apply(this,arguments);
},isLocked:function(){
return this.tree.isLocked.apply(this,arguments);
},cleanLock:function(){
return this.tree.cleanLock.apply(this,arguments);
},actionIsDisabled:function(_a7f){
var _a80=this;
var _a81=false;
if(this.tree.strictFolders&&_a7f==this.actions.ADDCHILD&&!this.isFolder){
_a81=true;
}
if(dojo.lang.inArray(_a80.actionsDisabled,_a7f)){
_a81=true;
}
if(this.isLocked()){
_a81=true;
}
return _a81;
},getInfo:function(){
var info={widgetId:this.widgetId,objectId:this.objectId,index:this.getParentIndex(),isFolder:this.isFolder};
return info;
},initialize:function(args,frag){
this.state=this.loadStates.UNCHECKED;
for(var i=0;i<this.actionsDisabled.length;i++){
this.actionsDisabled[i]=this.actionsDisabled[i].toUpperCase();
}
this.expandLevel=parseInt(this.expandLevel);
},adjustDepth:function(_a86){
for(var i=0;i<this.children.length;i++){
this.children[i].adjustDepth(_a86);
}
this.depth+=_a86;
if(_a86>0){
for(var i=0;i<_a86;i++){
var img=this.tree.makeBlankImg();
this.imgs.unshift(img);
dojo.html.insertBefore(this.imgs[0],this.domNode.firstChild);
}
}
if(_a86<0){
for(var i=0;i<-_a86;i++){
this.imgs.shift();
dojo.html.removeNode(this.domNode.firstChild);
}
}
},markLoading:function(){
this._markLoadingSavedIcon=this.expandIcon.src;
this.expandIcon.src=this.tree.expandIconSrcLoading;
},unMarkLoading:function(){
if(!this._markLoadingSavedIcon){
return;
}
var im=new Image();
im.src=this.tree.expandIconSrcLoading;
if(this.expandIcon.src==im.src){
this.expandIcon.src=this._markLoadingSavedIcon;
}
this._markLoadingSavedIcon=null;
},setFolder:function(){
dojo.event.connect(this.expandIcon,"onclick",this,"onTreeClick");
this.expandIcon.src=this.isExpanded?this.tree.expandIconSrcMinus:this.tree.expandIconSrcPlus;
this.isFolder=true;
},createDOMNode:function(tree,_a8b){
this.tree=tree;
this.depth=_a8b;
this.imgs=[];
for(var i=0;i<this.depth+1;i++){
var img=this.tree.makeBlankImg();
this.domNode.insertBefore(img,this.labelNode);
this.imgs.push(img);
}
this.expandIcon=this.imgs[this.imgs.length-1];
this.childIcon=this.tree.makeBlankImg();
this.imgs.push(this.childIcon);
dojo.html.insertBefore(this.childIcon,this.titleNode);
if(this.children.length||this.isFolder){
this.setFolder();
}else{
this.state=this.loadStates.LOADED;
}
dojo.event.connect(this.childIcon,"onclick",this,"onIconClick");
for(var i=0;i<this.children.length;i++){
this.children[i].parent=this;
var node=this.children[i].createDOMNode(this.tree,this.depth+1);
this.containerNode.appendChild(node);
}
if(this.children.length){
this.state=this.loadStates.LOADED;
}
this.updateIcons();
this.domNodeInitialized=true;
dojo.event.topic.publish(this.tree.eventNames.createDOMNode,{source:this});
return this.domNode;
},onTreeClick:function(e){
dojo.event.topic.publish(this.tree.eventNames.treeClick,{source:this,event:e});
},onIconClick:function(e){
dojo.event.topic.publish(this.tree.eventNames.iconClick,{source:this,event:e});
},onTitleClick:function(e){
dojo.event.topic.publish(this.tree.eventNames.titleClick,{source:this,event:e});
},markSelected:function(){
dojo.html.addClass(this.titleNode,"dojoTreeNodeLabelSelected");
},unMarkSelected:function(){
dojo.html.removeClass(this.titleNode,"dojoTreeNodeLabelSelected");
},updateExpandIcon:function(){
if(this.isFolder){
this.expandIcon.src=this.isExpanded?this.tree.expandIconSrcMinus:this.tree.expandIconSrcPlus;
}else{
this.expandIcon.src=this.tree.blankIconSrc;
}
},updateExpandGrid:function(){
if(this.tree.showGrid){
if(this.depth){
this.setGridImage(-2,this.isLastChild()?this.tree.gridIconSrcL:this.tree.gridIconSrcT);
}else{
if(this.isFirstChild()){
this.setGridImage(-2,this.isLastChild()?this.tree.gridIconSrcX:this.tree.gridIconSrcY);
}else{
this.setGridImage(-2,this.isLastChild()?this.tree.gridIconSrcL:this.tree.gridIconSrcT);
}
}
}else{
this.setGridImage(-2,this.tree.blankIconSrc);
}
},updateChildGrid:function(){
if((this.depth||this.tree.showRootGrid)&&this.tree.showGrid){
this.setGridImage(-1,(this.children.length&&this.isExpanded)?this.tree.gridIconSrcP:this.tree.gridIconSrcC);
}else{
if(this.tree.showGrid&&!this.tree.showRootGrid){
this.setGridImage(-1,(this.children.length&&this.isExpanded)?this.tree.gridIconSrcZ:this.tree.blankIconSrc);
}else{
this.setGridImage(-1,this.tree.blankIconSrc);
}
}
},updateParentGrid:function(){
var _a92=this.parent;
for(var i=0;i<this.depth;i++){
var idx=this.imgs.length-(3+i);
var img=(this.tree.showGrid&&!_a92.isLastChild())?this.tree.gridIconSrcV:this.tree.blankIconSrc;
this.setGridImage(idx,img);
_a92=_a92.parent;
}
},updateExpandGridColumn:function(){
if(!this.tree.showGrid){
return;
}
var _a96=this;
var icon=this.isLastChild()?this.tree.blankIconSrc:this.tree.gridIconSrcV;
dojo.lang.forEach(_a96.getDescendants(),function(node){
node.setGridImage(_a96.depth,icon);
});
this.updateExpandGrid();
},updateIcons:function(){
this.imgs[0].style.display=this.tree.showRootGrid?"inline":"none";
this.buildChildIcon();
this.updateExpandGrid();
this.updateChildGrid();
this.updateParentGrid();
dojo.profile.stop("updateIcons");
},buildChildIcon:function(){
if(this.childIconSrc){
this.childIcon.src=this.childIconSrc;
}
this.childIcon.style.display=this.childIconSrc?"inline":"none";
},setGridImage:function(idx,src){
if(idx<0){
idx=this.imgs.length+idx;
}
this.imgs[idx].style.backgroundImage="url("+src+")";
},updateIconTree:function(){
this.tree.updateIconTree.call(this);
},expand:function(){
if(this.isExpanded){
return;
}
if(this.children.length){
this.showChildren();
}
this.isExpanded=true;
this.updateExpandIcon();
dojo.event.topic.publish(this.tree.eventNames.expand,{source:this});
},collapse:function(){
if(!this.isExpanded){
return;
}
this.hideChildren();
this.isExpanded=false;
this.updateExpandIcon();
dojo.event.topic.publish(this.tree.eventNames.collapse,{source:this});
},hideChildren:function(){
this.tree.toggleObj.hide(this.containerNode,this.toggleDuration,this.explodeSrc,dojo.lang.hitch(this,"onHide"));
if(dojo.exists(dojo,"dnd.dragManager.dragObjects")&&dojo.dnd.dragManager.dragObjects.length){
dojo.dnd.dragManager.cacheTargetLocations();
}
},showChildren:function(){
this.tree.toggleObj.show(this.containerNode,this.toggleDuration,this.explodeSrc,dojo.lang.hitch(this,"onShow"));
if(dojo.exists(dojo,"dnd.dragManager.dragObjects")&&dojo.dnd.dragManager.dragObjects.length){
dojo.dnd.dragManager.cacheTargetLocations();
}
},addChild:function(){
return this.tree.addChild.apply(this,arguments);
},doAddChild:function(){
return this.tree.doAddChild.apply(this,arguments);
},edit:function(_a9b){
dojo.lang.mixin(this,_a9b);
if(_a9b.title){
this.titleNode.innerHTML=this.title;
}
if(_a9b.afterLabel){
this.afterLabelNode.innerHTML=this.afterLabel;
}
if(_a9b.childIconSrc){
this.buildChildIcon();
}
},removeNode:function(){
return this.tree.removeNode.apply(this,arguments);
},doRemoveNode:function(){
return this.tree.doRemoveNode.apply(this,arguments);
},toString:function(){
return "["+this.widgetType+" Tree:"+this.tree+" ID:"+this.widgetId+" Title:"+this.title+"]";
}});
dojo.provide("dojo.dnd.TreeDragAndDrop");
dojo.dnd.TreeDragSource=function(node,_a9d,type,_a9f){
this.controller=_a9d;
this.treeNode=_a9f;
dojo.dnd.HtmlDragSource.call(this,node,type);
};
dojo.inherits(dojo.dnd.TreeDragSource,dojo.dnd.HtmlDragSource);
dojo.lang.extend(dojo.dnd.TreeDragSource,{onDragStart:function(){
var _aa0=dojo.dnd.HtmlDragSource.prototype.onDragStart.call(this);
_aa0.treeNode=this.treeNode;
_aa0.onDragStart=dojo.lang.hitch(_aa0,function(e){
this.savedSelectedNode=this.treeNode.tree.selector.selectedNode;
if(this.savedSelectedNode){
this.savedSelectedNode.unMarkSelected();
}
var _aa2=dojo.dnd.HtmlDragObject.prototype.onDragStart.apply(this,arguments);
var _aa3=this.dragClone.getElementsByTagName("img");
for(var i=0;i<_aa3.length;i++){
_aa3.item(i).style.backgroundImage="url()";
}
return _aa2;
});
_aa0.onDragEnd=function(e){
if(this.savedSelectedNode){
this.savedSelectedNode.markSelected();
}
return dojo.dnd.HtmlDragObject.prototype.onDragEnd.apply(this,arguments);
};
return _aa0;
},onDragEnd:function(e){
var res=dojo.dnd.HtmlDragSource.prototype.onDragEnd.call(this,e);
return res;
}});
dojo.dnd.TreeDropTarget=function(_aa8,_aa9,type,_aab){
this.treeNode=_aab;
this.controller=_aa9;
dojo.dnd.HtmlDropTarget.apply(this,[_aa8,type]);
};
dojo.inherits(dojo.dnd.TreeDropTarget,dojo.dnd.HtmlDropTarget);
dojo.lang.extend(dojo.dnd.TreeDropTarget,{autoExpandDelay:1500,autoExpandTimer:null,position:null,indicatorStyle:"2px black solid",showIndicator:function(_aac){
if(this.position==_aac){
return;
}
this.hideIndicator();
this.position=_aac;
if(_aac=="before"){
this.treeNode.labelNode.style.borderTop=this.indicatorStyle;
}else{
if(_aac=="after"){
this.treeNode.labelNode.style.borderBottom=this.indicatorStyle;
}else{
if(_aac=="onto"){
this.treeNode.markSelected();
}
}
}
},hideIndicator:function(){
this.treeNode.labelNode.style.borderBottom="";
this.treeNode.labelNode.style.borderTop="";
this.treeNode.unMarkSelected();
this.position=null;
},onDragOver:function(e){
var _aae=dojo.dnd.HtmlDropTarget.prototype.onDragOver.apply(this,arguments);
if(_aae&&this.treeNode.isFolder&&!this.treeNode.isExpanded){
this.setAutoExpandTimer();
}
return _aae;
},accepts:function(_aaf){
var _ab0=dojo.dnd.HtmlDropTarget.prototype.accepts.apply(this,arguments);
if(!_ab0){
return false;
}
var _ab1=_aaf[0].treeNode;
if(dojo.lang.isUndefined(_ab1)||!_ab1||!_ab1.isTreeNode){
dojo.raise("Source is not TreeNode or not found");
}
if(_ab1===this.treeNode){
return false;
}
return true;
},setAutoExpandTimer:function(){
var _ab2=this;
var _ab3=function(){
if(dojo.dnd.dragManager.currentDropTarget===_ab2){
_ab2.controller.expand(_ab2.treeNode);
}
};
this.autoExpandTimer=dojo.lang.setTimeout(_ab3,_ab2.autoExpandDelay);
},getDNDMode:function(){
return this.treeNode.tree.DNDMode;
},getAcceptPosition:function(e,_ab5){
var _ab6=this.getDNDMode();
if(_ab6&dojo.widget.Tree.prototype.DNDModes.ONTO&&!(!this.treeNode.actionIsDisabled(dojo.widget.TreeNode.prototype.actions.ADDCHILD)&&_ab5.parent!==this.treeNode&&this.controller.canMove(_ab5,this.treeNode))){
_ab6&=~dojo.widget.Tree.prototype.DNDModes.ONTO;
}
var _ab7=this.getPosition(e,_ab6);
if(_ab7=="onto"||(!this.isAdjacentNode(_ab5,_ab7)&&this.controller.canMove(_ab5,this.treeNode.parent))){
return _ab7;
}else{
return false;
}
},onDragOut:function(e){
this.clearAutoExpandTimer();
this.hideIndicator();
},clearAutoExpandTimer:function(){
if(this.autoExpandTimer){
clearTimeout(this.autoExpandTimer);
this.autoExpandTimer=null;
}
},onDragMove:function(e,_aba){
var _abb=_aba[0].treeNode;
var _abc=this.getAcceptPosition(e,_abb);
if(_abc){
this.showIndicator(_abc);
}
},isAdjacentNode:function(_abd,_abe){
if(_abd===this.treeNode){
return true;
}
if(_abd.getNextSibling()===this.treeNode&&_abe=="before"){
return true;
}
if(_abd.getPreviousSibling()===this.treeNode&&_abe=="after"){
return true;
}
return false;
},getPosition:function(e,_ac0){
var node=dojo.byId(this.treeNode.labelNode);
var _ac2=e.pageY||e.clientY+dojo.body().scrollTop;
var _ac3=dojo.html.getAbsolutePosition(node).y;
var _ac4=dojo.html.getBorderBox(node).height;
var relY=_ac2-_ac3;
var p=relY/_ac4;
var _ac7="";
if(_ac0&dojo.widget.Tree.prototype.DNDModes.ONTO&&_ac0&dojo.widget.Tree.prototype.DNDModes.BETWEEN){
if(p<=0.3){
_ac7="before";
}else{
if(p<=0.7){
_ac7="onto";
}else{
_ac7="after";
}
}
}else{
if(_ac0&dojo.widget.Tree.prototype.DNDModes.BETWEEN){
if(p<=0.5){
_ac7="before";
}else{
_ac7="after";
}
}else{
if(_ac0&dojo.widget.Tree.prototype.DNDModes.ONTO){
_ac7="onto";
}
}
}
return _ac7;
},getTargetParentIndex:function(_ac8,_ac9){
var _aca=_ac9=="before"?this.treeNode.getParentIndex():this.treeNode.getParentIndex()+1;
if(this.treeNode.parent===_ac8.parent&&this.treeNode.getParentIndex()>_ac8.getParentIndex()){
_aca--;
}
return _aca;
},onDrop:function(e){
var _acc=this.position;
this.onDragOut(e);
var _acd=e.dragObject.treeNode;
if(!dojo.lang.isObject(_acd)){
dojo.raise("TreeNode not found in dragObject");
}
if(_acc=="onto"){
return this.controller.move(_acd,this.treeNode,0);
}else{
var _ace=this.getTargetParentIndex(_acd,_acc);
return this.controller.move(_acd,this.treeNode.parent,_ace);
}
}});
dojo.dnd.TreeDNDController=function(_acf){
this.treeController=_acf;
this.dragSources={};
this.dropTargets={};
};
dojo.lang.extend(dojo.dnd.TreeDNDController,{listenTree:function(tree){
dojo.event.topic.subscribe(tree.eventNames.createDOMNode,this,"onCreateDOMNode");
dojo.event.topic.subscribe(tree.eventNames.moveFrom,this,"onMoveFrom");
dojo.event.topic.subscribe(tree.eventNames.moveTo,this,"onMoveTo");
dojo.event.topic.subscribe(tree.eventNames.addChild,this,"onAddChild");
dojo.event.topic.subscribe(tree.eventNames.removeNode,this,"onRemoveNode");
dojo.event.topic.subscribe(tree.eventNames.treeDestroy,this,"onTreeDestroy");
},unlistenTree:function(tree){
dojo.event.topic.unsubscribe(tree.eventNames.createDOMNode,this,"onCreateDOMNode");
dojo.event.topic.unsubscribe(tree.eventNames.moveFrom,this,"onMoveFrom");
dojo.event.topic.unsubscribe(tree.eventNames.moveTo,this,"onMoveTo");
dojo.event.topic.unsubscribe(tree.eventNames.addChild,this,"onAddChild");
dojo.event.topic.unsubscribe(tree.eventNames.removeNode,this,"onRemoveNode");
dojo.event.topic.unsubscribe(tree.eventNames.treeDestroy,this,"onTreeDestroy");
},onTreeDestroy:function(_ad2){
this.unlistenTree(_ad2.source);
},onCreateDOMNode:function(_ad3){
this.registerDNDNode(_ad3.source);
},onAddChild:function(_ad4){
this.registerDNDNode(_ad4.child);
},onMoveFrom:function(_ad5){
var _ad6=this;
dojo.lang.forEach(_ad5.child.getDescendants(),function(node){
_ad6.unregisterDNDNode(node);
});
},onMoveTo:function(_ad8){
var _ad9=this;
dojo.lang.forEach(_ad8.child.getDescendants(),function(node){
_ad9.registerDNDNode(node);
});
},registerDNDNode:function(node){
if(!node.tree.DNDMode){
return;
}
var _adc=null;
var _add=null;
if(!node.actionIsDisabled(node.actions.MOVE)){
var _adc=new dojo.dnd.TreeDragSource(node.labelNode,this,node.tree.widgetId,node);
this.dragSources[node.widgetId]=_adc;
}
var _add=new dojo.dnd.TreeDropTarget(node.labelNode,this.treeController,node.tree.DNDAcceptTypes,node);
this.dropTargets[node.widgetId]=_add;
},unregisterDNDNode:function(node){
if(this.dragSources[node.widgetId]){
dojo.dnd.dragManager.unregisterDragSource(this.dragSources[node.widgetId]);
delete this.dragSources[node.widgetId];
}
if(this.dropTargets[node.widgetId]){
dojo.dnd.dragManager.unregisterDropTarget(this.dropTargets[node.widgetId]);
delete this.dropTargets[node.widgetId];
}
}});
dojo.provide("dojo.widget.TreeBasicController");
dojo.widget.defineWidget("dojo.widget.TreeBasicController",dojo.widget.HtmlWidget,{widgetType:"TreeBasicController",DNDController:"",dieWithTree:false,initialize:function(args,frag){
if(this.DNDController=="create"){
this.DNDController=new dojo.dnd.TreeDNDController(this);
}
},listenTree:function(tree){
dojo.event.topic.subscribe(tree.eventNames.createDOMNode,this,"onCreateDOMNode");
dojo.event.topic.subscribe(tree.eventNames.treeClick,this,"onTreeClick");
dojo.event.topic.subscribe(tree.eventNames.treeCreate,this,"onTreeCreate");
dojo.event.topic.subscribe(tree.eventNames.treeDestroy,this,"onTreeDestroy");
if(this.DNDController){
this.DNDController.listenTree(tree);
}
},unlistenTree:function(tree){
dojo.event.topic.unsubscribe(tree.eventNames.createDOMNode,this,"onCreateDOMNode");
dojo.event.topic.unsubscribe(tree.eventNames.treeClick,this,"onTreeClick");
dojo.event.topic.unsubscribe(tree.eventNames.treeCreate,this,"onTreeCreate");
dojo.event.topic.unsubscribe(tree.eventNames.treeDestroy,this,"onTreeDestroy");
},onTreeDestroy:function(_ae3){
var tree=_ae3.source;
this.unlistenTree(tree);
if(this.dieWithTree){
this.destroy();
}
},onCreateDOMNode:function(_ae5){
var node=_ae5.source;
if(node.expandLevel>0){
this.expandToLevel(node,node.expandLevel);
}
},onTreeCreate:function(_ae7){
var tree=_ae7.source;
var _ae9=this;
if(tree.expandLevel){
dojo.lang.forEach(tree.children,function(_aea){
_ae9.expandToLevel(_aea,tree.expandLevel-1);
});
}
},expandToLevel:function(node,_aec){
if(_aec==0){
return;
}
var _aed=node.children;
var _aee=this;
var _aef=function(node,_af1){
this.node=node;
this.expandLevel=_af1;
this.process=function(){
for(var i=0;i<this.node.children.length;i++){
var _af3=node.children[i];
_aee.expandToLevel(_af3,this.expandLevel);
}
};
};
var h=new _aef(node,_aec-1);
this.expand(node,false,h,h.process);
},onTreeClick:function(_af5){
var node=_af5.source;
if(node.isLocked()){
return false;
}
if(node.isExpanded){
this.collapse(node);
}else{
this.expand(node);
}
},expand:function(node,sync,_af9,_afa){
node.expand();
if(_afa){
_afa.apply(_af9,[node]);
}
},collapse:function(node){
node.collapse();
},canMove:function(_afc,_afd){
if(_afc.actionIsDisabled(_afc.actions.MOVE)){
return false;
}
if(_afc.parent!==_afd&&_afd.actionIsDisabled(_afd.actions.ADDCHILD)){
return false;
}
var node=_afd;
while(node.isTreeNode){
if(node===_afc){
return false;
}
node=node.parent;
}
return true;
},move:function(_aff,_b00,_b01){
if(!this.canMove(_aff,_b00)){
return false;
}
var _b02=this.doMove(_aff,_b00,_b01);
if(!_b02){
return _b02;
}
if(_b00.isTreeNode){
this.expand(_b00);
}
return _b02;
},doMove:function(_b03,_b04,_b05){
_b03.tree.move(_b03,_b04,_b05);
return true;
},canRemoveNode:function(_b06){
if(_b06.actionIsDisabled(_b06.actions.REMOVE)){
return false;
}
return true;
},removeNode:function(node,_b08,_b09){
if(!this.canRemoveNode(node)){
return false;
}
return this.doRemoveNode(node,_b08,_b09);
},doRemoveNode:function(node,_b0b,_b0c){
node.tree.removeNode(node);
if(_b0c){
_b0c.apply(dojo.lang.isUndefined(_b0b)?this:_b0b,[node]);
}
},canCreateChild:function(_b0d,_b0e,data){
if(_b0d.actionIsDisabled(_b0d.actions.ADDCHILD)){
return false;
}
return true;
},createChild:function(_b10,_b11,data,_b13,_b14){
if(!this.canCreateChild(_b10,_b11,data)){
return false;
}
return this.doCreateChild.apply(this,arguments);
},doCreateChild:function(_b15,_b16,data,_b18,_b19){
var _b1a=data.widgetType?data.widgetType:"TreeNode";
var _b1b=dojo.widget.createWidget(_b1a,data);
_b15.addChild(_b1b,_b16);
this.expand(_b15);
if(_b19){
_b19.apply(_b18,[_b1b]);
}
return _b1b;
}});
dojo.provide("dojo.widget.TreeSelector");
dojo.widget.defineWidget("dojo.widget.TreeSelector",dojo.widget.HtmlWidget,function(){
this.eventNames={};
this.listenedTrees=[];
},{widgetType:"TreeSelector",selectedNode:null,dieWithTree:false,eventNamesDefault:{select:"select",destroy:"destroy",deselect:"deselect",dblselect:"dblselect"},initialize:function(){
for(var name in this.eventNamesDefault){
if(dojo.lang.isUndefined(this.eventNames[name])){
this.eventNames[name]=this.widgetId+"/"+this.eventNamesDefault[name];
}
}
},destroy:function(){
dojo.event.topic.publish(this.eventNames.destroy,{source:this});
return dojo.widget.HtmlWidget.prototype.destroy.apply(this,arguments);
},listenTree:function(tree){
dojo.event.topic.subscribe(tree.eventNames.titleClick,this,"select");
dojo.event.topic.subscribe(tree.eventNames.iconClick,this,"select");
dojo.event.topic.subscribe(tree.eventNames.collapse,this,"onCollapse");
dojo.event.topic.subscribe(tree.eventNames.moveFrom,this,"onMoveFrom");
dojo.event.topic.subscribe(tree.eventNames.removeNode,this,"onRemoveNode");
dojo.event.topic.subscribe(tree.eventNames.treeDestroy,this,"onTreeDestroy");
this.listenedTrees.push(tree);
},unlistenTree:function(tree){
dojo.event.topic.unsubscribe(tree.eventNames.titleClick,this,"select");
dojo.event.topic.unsubscribe(tree.eventNames.iconClick,this,"select");
dojo.event.topic.unsubscribe(tree.eventNames.collapse,this,"onCollapse");
dojo.event.topic.unsubscribe(tree.eventNames.moveFrom,this,"onMoveFrom");
dojo.event.topic.unsubscribe(tree.eventNames.removeNode,this,"onRemoveNode");
dojo.event.topic.unsubscribe(tree.eventNames.treeDestroy,this,"onTreeDestroy");
for(var i=0;i<this.listenedTrees.length;i++){
if(this.listenedTrees[i]===tree){
this.listenedTrees.splice(i,1);
break;
}
}
},onTreeDestroy:function(_b20){
this.unlistenTree(_b20.source);
if(this.dieWithTree){
this.destroy();
}
},onCollapse:function(_b21){
if(!this.selectedNode){
return;
}
var node=_b21.source;
var _b23=this.selectedNode.parent;
while(_b23!==node&&_b23.isTreeNode){
_b23=_b23.parent;
}
if(_b23.isTreeNode){
this.deselect();
}
},select:function(_b24){
var node=_b24.source;
var e=_b24.event;
if(this.selectedNode===node){
if(e.ctrlKey||e.shiftKey||e.metaKey){
this.deselect();
return;
}
dojo.event.topic.publish(this.eventNames.dblselect,{node:node});
return;
}
if(this.selectedNode){
this.deselect();
}
this.doSelect(node);
dojo.event.topic.publish(this.eventNames.select,{node:node});
},onMoveFrom:function(_b27){
if(_b27.child!==this.selectedNode){
return;
}
if(!dojo.lang.inArray(this.listenedTrees,_b27.newTree)){
this.deselect();
}
},onRemoveNode:function(_b28){
if(_b28.child!==this.selectedNode){
return;
}
this.deselect();
},doSelect:function(node){
node.markSelected();
this.selectedNode=node;
},deselect:function(){
var node=this.selectedNode;
this.selectedNode=null;
node.unMarkSelected();
dojo.event.topic.publish(this.eventNames.deselect,{node:node});
}});
dojo.provide("dojo.widget.Tree");
dojo.widget.defineWidget("dojo.widget.Tree",dojo.widget.HtmlWidget,function(){
this.eventNames={};
this.tree=this;
this.DNDAcceptTypes=[];
this.actionsDisabled=[];
},{widgetType:"Tree",eventNamesDefault:{createDOMNode:"createDOMNode",treeCreate:"treeCreate",treeDestroy:"treeDestroy",treeClick:"treeClick",iconClick:"iconClick",titleClick:"titleClick",moveFrom:"moveFrom",moveTo:"moveTo",addChild:"addChild",removeNode:"removeNode",expand:"expand",collapse:"collapse"},isContainer:true,DNDMode:"off",lockLevel:0,strictFolders:true,DNDModes:{BETWEEN:1,ONTO:2},DNDAcceptTypes:"",templateCssString:"\r\n.dojoTree {\r\n\tfont: caption;\r\n\tfont-size: 11px;\r\n\tfont-weight: normal;\r\n\toverflow: auto;\r\n}\r\n\r\n\r\n.dojoTreeNodeLabelTitle {\r\n\tpadding-left: 2px;\r\n\tcolor: WindowText;\r\n}\r\n\r\n.dojoTreeNodeLabel {\r\n\tcursor:hand;\r\n\tcursor:pointer;\r\n}\r\n\r\n.dojoTreeNodeLabelTitle:hover {\r\n\ttext-decoration: underline;\r\n}\r\n\r\n.dojoTreeNodeLabelSelected {\r\n\tbackground-color: Highlight;\r\n\tcolor: HighlightText;\r\n}\r\n\r\n.dojoTree div {\r\n\twhite-space: nowrap;\r\n}\r\n\r\n.dojoTree img, .dojoTreeNodeLabel img {\r\n\tvertical-align: middle;\r\n}\r\n\r\n",templateCssPath:dojo.uri.moduleUri("dojo.widget","templates/images/Tree/Tree.css"),templateString:"<div class=\"dojoTree\"></div>",isExpanded:true,isTree:true,objectId:"",controller:"",selector:"",menu:"",expandLevel:"",blankIconSrc:dojo.uri.moduleUri("dojo.widget","templates/images/Tree/treenode_blank.gif"),gridIconSrcT:dojo.uri.moduleUri("dojo.widget","templates/images/Tree/treenode_grid_t.gif"),gridIconSrcL:dojo.uri.moduleUri("dojo.widget","templates/images/Tree/treenode_grid_l.gif"),gridIconSrcV:dojo.uri.moduleUri("dojo.widget","templates/images/Tree/treenode_grid_v.gif"),gridIconSrcP:dojo.uri.moduleUri("dojo.widget","templates/images/Tree/treenode_grid_p.gif"),gridIconSrcC:dojo.uri.moduleUri("dojo.widget","templates/images/Tree/treenode_grid_c.gif"),gridIconSrcX:dojo.uri.moduleUri("dojo.widget","templates/images/Tree/treenode_grid_x.gif"),gridIconSrcY:dojo.uri.moduleUri("dojo.widget","templates/images/Tree/treenode_grid_y.gif"),gridIconSrcZ:dojo.uri.moduleUri("dojo.widget","templates/images/Tree/treenode_grid_z.gif"),expandIconSrcPlus:dojo.uri.moduleUri("dojo.widget","templates/images/Tree/treenode_expand_plus.gif"),expandIconSrcMinus:dojo.uri.moduleUri("dojo.widget","templates/images/Tree/treenode_expand_minus.gif"),expandIconSrcLoading:dojo.uri.moduleUri("dojo.widget","templates/images/Tree/treenode_loading.gif"),iconWidth:18,iconHeight:18,showGrid:true,showRootGrid:true,actionIsDisabled:function(_b2b){
var _b2c=this;
return dojo.lang.inArray(_b2c.actionsDisabled,_b2b);
},actions:{ADDCHILD:"ADDCHILD"},getInfo:function(){
var info={widgetId:this.widgetId,objectId:this.objectId};
return info;
},initializeController:function(){
if(this.controller!="off"){
if(this.controller){
this.controller=dojo.widget.byId(this.controller);
}else{
this.controller=dojo.widget.createWidget("TreeBasicController",{DNDController:(this.DNDMode?"create":""),dieWithTree:true});
}
this.controller.listenTree(this);
}else{
this.controller=null;
}
},initializeSelector:function(){
if(this.selector!="off"){
if(this.selector){
this.selector=dojo.widget.byId(this.selector);
}else{
this.selector=dojo.widget.createWidget("TreeSelector",{dieWithTree:true});
}
this.selector.listenTree(this);
}else{
this.selector=null;
}
},initialize:function(args,frag){
var _b30=this;
for(name in this.eventNamesDefault){
if(dojo.lang.isUndefined(this.eventNames[name])){
this.eventNames[name]=this.widgetId+"/"+this.eventNamesDefault[name];
}
}
for(var i=0;i<this.actionsDisabled.length;i++){
this.actionsDisabled[i]=this.actionsDisabled[i].toUpperCase();
}
if(this.DNDMode=="off"){
this.DNDMode=0;
}else{
if(this.DNDMode=="between"){
this.DNDMode=this.DNDModes.ONTO|this.DNDModes.BETWEEN;
}else{
if(this.DNDMode=="onto"){
this.DNDMode=this.DNDModes.ONTO;
}
}
}
this.expandLevel=parseInt(this.expandLevel);
this.initializeSelector();
this.initializeController();
if(this.menu){
this.menu=dojo.widget.byId(this.menu);
this.menu.listenTree(this);
}
this.containerNode=this.domNode;
},postCreate:function(){
this.createDOMNode();
},createDOMNode:function(){
dojo.html.disableSelection(this.domNode);
for(var i=0;i<this.children.length;i++){
this.children[i].parent=this;
var node=this.children[i].createDOMNode(this,0);
this.domNode.appendChild(node);
}
if(!this.showRootGrid){
for(var i=0;i<this.children.length;i++){
this.children[i].expand();
}
}
dojo.event.topic.publish(this.eventNames.treeCreate,{source:this});
},destroy:function(){
dojo.event.topic.publish(this.tree.eventNames.treeDestroy,{source:this});
return dojo.widget.HtmlWidget.prototype.destroy.apply(this,arguments);
},addChild:function(_b34,_b35){
var _b36={child:_b34,index:_b35,parent:this,domNodeInitialized:_b34.domNodeInitialized};
this.doAddChild.apply(this,arguments);
dojo.event.topic.publish(this.tree.eventNames.addChild,_b36);
},doAddChild:function(_b37,_b38){
if(dojo.lang.isUndefined(_b38)){
_b38=this.children.length;
}
if(!_b37.isTreeNode){
dojo.raise("You can only add TreeNode widgets to a "+this.widgetType+" widget!");
return;
}
if(this.isTreeNode){
if(!this.isFolder){
this.setFolder();
}
}
var _b39=this;
dojo.lang.forEach(_b37.getDescendants(),function(elem){
elem.tree=_b39.tree;
});
_b37.parent=this;
if(this.isTreeNode){
this.state=this.loadStates.LOADED;
}
if(_b38<this.children.length){
dojo.html.insertBefore(_b37.domNode,this.children[_b38].domNode);
}else{
this.containerNode.appendChild(_b37.domNode);
if(this.isExpanded&&this.isTreeNode){
this.showChildren();
}
}
this.children.splice(_b38,0,_b37);
if(_b37.domNodeInitialized){
var d=this.isTreeNode?this.depth:-1;
_b37.adjustDepth(d-_b37.depth+1);
_b37.updateIconTree();
}else{
_b37.depth=this.isTreeNode?this.depth+1:0;
_b37.createDOMNode(_b37.tree,_b37.depth);
}
var _b3c=_b37.getPreviousSibling();
if(_b37.isLastChild()&&_b3c){
_b3c.updateExpandGridColumn();
}
},makeBlankImg:function(){
var img=document.createElement("img");
img.style.width=this.iconWidth+"px";
img.style.height=this.iconHeight+"px";
img.src=this.blankIconSrc;
img.style.verticalAlign="middle";
return img;
},updateIconTree:function(){
if(!this.isTree){
this.updateIcons();
}
for(var i=0;i<this.children.length;i++){
this.children[i].updateIconTree();
}
},toString:function(){
return "["+this.widgetType+" ID:"+this.widgetId+"]";
},move:function(_b3f,_b40,_b41){
var _b42=_b3f.parent;
var _b43=_b3f.tree;
this.doMove.apply(this,arguments);
var _b40=_b3f.parent;
var _b44=_b3f.tree;
var _b45={oldParent:_b42,oldTree:_b43,newParent:_b40,newTree:_b44,child:_b3f};
dojo.event.topic.publish(_b43.eventNames.moveFrom,_b45);
dojo.event.topic.publish(_b44.eventNames.moveTo,_b45);
},doMove:function(_b46,_b47,_b48){
_b46.parent.doRemoveNode(_b46);
_b47.doAddChild(_b46,_b48);
},removeNode:function(_b49){
if(!_b49.parent){
return;
}
var _b4a=_b49.tree;
var _b4b=_b49.parent;
var _b4c=this.doRemoveNode.apply(this,arguments);
dojo.event.topic.publish(this.tree.eventNames.removeNode,{child:_b4c,tree:_b4a,parent:_b4b});
return _b4c;
},doRemoveNode:function(_b4d){
if(!_b4d.parent){
return;
}
var _b4e=_b4d.parent;
var _b4f=_b4e.children;
var _b50=_b4d.getParentIndex();
if(_b50<0){
dojo.raise("Couldn't find node "+_b4d+" for removal");
}
_b4f.splice(_b50,1);
dojo.html.removeNode(_b4d.domNode);
if(_b4e.children.length==0&&!_b4e.isTree){
_b4e.containerNode.style.display="none";
}
if(_b50==_b4f.length&&_b50>0){
_b4f[_b50-1].updateExpandGridColumn();
}
if(_b4e instanceof dojo.widget.Tree&&_b50==0&&_b4f.length>0){
_b4f[0].updateExpandGrid();
}
_b4d.parent=_b4d.tree=null;
return _b4d;
},markLoading:function(){
},unMarkLoading:function(){
},lock:function(){
!this.lockLevel&&this.markLoading();
this.lockLevel++;
},unlock:function(){
if(!this.lockLevel){
dojo.raise("unlock: not locked");
}
this.lockLevel--;
!this.lockLevel&&this.unMarkLoading();
},isLocked:function(){
var node=this;
while(true){
if(node.lockLevel){
return true;
}
if(node instanceof dojo.widget.Tree){
break;
}
node=node.parent;
}
return false;
},flushLock:function(){
this.lockLevel=0;
this.unMarkLoading();
}});
dojo.provide("dojo.date.common");
dojo.date.setDayOfYear=function(_b52,_b53){
_b52.setMonth(0);
_b52.setDate(_b53);
return _b52;
};
dojo.date.getDayOfYear=function(_b54){
var _b55=_b54.getFullYear();
var _b56=new Date(_b55-1,11,31);
return Math.floor((_b54.getTime()-_b56.getTime())/86400000);
};
dojo.date.setWeekOfYear=function(_b57,week,_b59){
if(arguments.length==1){
_b59=0;
}
dojo.unimplemented("dojo.date.setWeekOfYear");
};
dojo.date.getWeekOfYear=function(_b5a,_b5b){
if(arguments.length==1){
_b5b=0;
}
var _b5c=new Date(_b5a.getFullYear(),0,1);
var day=_b5c.getDay();
_b5c.setDate(_b5c.getDate()-day+_b5b-(day>_b5b?7:0));
return Math.floor((_b5a.getTime()-_b5c.getTime())/604800000);
};
dojo.date.setIsoWeekOfYear=function(_b5e,week,_b60){
if(arguments.length==1){
_b60=1;
}
dojo.unimplemented("dojo.date.setIsoWeekOfYear");
};
dojo.date.getIsoWeekOfYear=function(_b61,_b62){
if(arguments.length==1){
_b62=1;
}
dojo.unimplemented("dojo.date.getIsoWeekOfYear");
};
dojo.date.shortTimezones=["IDLW","BET","HST","MART","AKST","PST","MST","CST","EST","AST","NFT","BST","FST","AT","GMT","CET","EET","MSK","IRT","GST","AFT","AGTT","IST","NPT","ALMT","MMT","JT","AWST","JST","ACST","AEST","LHST","VUT","NFT","NZT","CHAST","PHOT","LINT"];
dojo.date.timezoneOffsets=[-720,-660,-600,-570,-540,-480,-420,-360,-300,-240,-210,-180,-120,-60,0,60,120,180,210,240,270,300,330,345,360,390,420,480,540,570,600,630,660,690,720,765,780,840];
dojo.date.getDaysInMonth=function(_b63){
var _b64=_b63.getMonth();
var days=[31,28,31,30,31,30,31,31,30,31,30,31];
if(_b64==1&&dojo.date.isLeapYear(_b63)){
return 29;
}else{
return days[_b64];
}
};
dojo.date.isLeapYear=function(_b66){
var year=_b66.getFullYear();
return (year%400==0)?true:(year%100==0)?false:(year%4==0)?true:false;
};
dojo.date.getTimezoneName=function(_b68){
var str=_b68.toString();
var tz="";
var _b6b;
var pos=str.indexOf("(");
if(pos>-1){
pos++;
tz=str.substring(pos,str.indexOf(")"));
}else{
var pat=/([A-Z\/]+) \d{4}$/;
if((_b6b=str.match(pat))){
tz=_b6b[1];
}else{
str=_b68.toLocaleString();
pat=/ ([A-Z\/]+)$/;
if((_b6b=str.match(pat))){
tz=_b6b[1];
}
}
}
return tz=="AM"||tz=="PM"?"":tz;
};
dojo.date.getOrdinal=function(_b6e){
var date=_b6e.getDate();
if(date%100!=11&&date%10==1){
return "st";
}else{
if(date%100!=12&&date%10==2){
return "nd";
}else{
if(date%100!=13&&date%10==3){
return "rd";
}else{
return "th";
}
}
}
};
dojo.date.compareTypes={DATE:1,TIME:2};
dojo.date.compare=function(_b70,_b71,_b72){
var dA=_b70;
var dB=_b71||new Date();
var now=new Date();
with(dojo.date.compareTypes){
var opt=_b72||(DATE|TIME);
var d1=new Date((opt&DATE)?dA.getFullYear():now.getFullYear(),(opt&DATE)?dA.getMonth():now.getMonth(),(opt&DATE)?dA.getDate():now.getDate(),(opt&TIME)?dA.getHours():0,(opt&TIME)?dA.getMinutes():0,(opt&TIME)?dA.getSeconds():0);
var d2=new Date((opt&DATE)?dB.getFullYear():now.getFullYear(),(opt&DATE)?dB.getMonth():now.getMonth(),(opt&DATE)?dB.getDate():now.getDate(),(opt&TIME)?dB.getHours():0,(opt&TIME)?dB.getMinutes():0,(opt&TIME)?dB.getSeconds():0);
}
if(d1.valueOf()>d2.valueOf()){
return 1;
}
if(d1.valueOf()<d2.valueOf()){
return -1;
}
return 0;
};
dojo.date.dateParts={YEAR:0,MONTH:1,DAY:2,HOUR:3,MINUTE:4,SECOND:5,MILLISECOND:6,QUARTER:7,WEEK:8,WEEKDAY:9};
dojo.date.add=function(dt,_b7a,incr){
if(typeof dt=="number"){
dt=new Date(dt);
}
function fixOvershoot(){
if(sum.getDate()<dt.getDate()){
sum.setDate(0);
}
}
var sum=new Date(dt);
with(dojo.date.dateParts){
switch(_b7a){
case YEAR:
sum.setFullYear(dt.getFullYear()+incr);
fixOvershoot();
break;
case QUARTER:
incr*=3;
case MONTH:
sum.setMonth(dt.getMonth()+incr);
fixOvershoot();
break;
case WEEK:
incr*=7;
case DAY:
sum.setDate(dt.getDate()+incr);
break;
case WEEKDAY:
var dat=dt.getDate();
var _b7e=0;
var days=0;
var strt=0;
var trgt=0;
var adj=0;
var mod=incr%5;
if(mod==0){
days=(incr>0)?5:-5;
_b7e=(incr>0)?((incr-5)/5):((incr+5)/5);
}else{
days=mod;
_b7e=parseInt(incr/5);
}
strt=dt.getDay();
if(strt==6&&incr>0){
adj=1;
}else{
if(strt==0&&incr<0){
adj=-1;
}
}
trgt=(strt+days);
if(trgt==0||trgt==6){
adj=(incr>0)?2:-2;
}
sum.setDate(dat+(7*_b7e)+days+adj);
break;
case HOUR:
sum.setHours(sum.getHours()+incr);
break;
case MINUTE:
sum.setMinutes(sum.getMinutes()+incr);
break;
case SECOND:
sum.setSeconds(sum.getSeconds()+incr);
break;
case MILLISECOND:
sum.setMilliseconds(sum.getMilliseconds()+incr);
break;
default:
break;
}
}
return sum;
};
dojo.date.diff=function(dtA,dtB,_b86){
if(typeof dtA=="number"){
dtA=new Date(dtA);
}
if(typeof dtB=="number"){
dtB=new Date(dtB);
}
var _b87=dtB.getFullYear()-dtA.getFullYear();
var _b88=(dtB.getMonth()-dtA.getMonth())+(_b87*12);
var _b89=dtB.getTime()-dtA.getTime();
var _b8a=_b89/1000;
var _b8b=_b8a/60;
var _b8c=_b8b/60;
var _b8d=_b8c/24;
var _b8e=_b8d/7;
var _b8f=0;
with(dojo.date.dateParts){
switch(_b86){
case YEAR:
_b8f=_b87;
break;
case QUARTER:
var mA=dtA.getMonth();
var mB=dtB.getMonth();
var qA=Math.floor(mA/3)+1;
var qB=Math.floor(mB/3)+1;
qB+=(_b87*4);
_b8f=qB-qA;
break;
case MONTH:
_b8f=_b88;
break;
case WEEK:
_b8f=parseInt(_b8e);
break;
case DAY:
_b8f=_b8d;
break;
case WEEKDAY:
var days=Math.round(_b8d);
var _b95=parseInt(days/7);
var mod=days%7;
if(mod==0){
days=_b95*5;
}else{
var adj=0;
var aDay=dtA.getDay();
var bDay=dtB.getDay();
_b95=parseInt(days/7);
mod=days%7;
var _b9a=new Date(dtA);
_b9a.setDate(_b9a.getDate()+(_b95*7));
var _b9b=_b9a.getDay();
if(_b8d>0){
switch(true){
case aDay==6:
adj=-1;
break;
case aDay==0:
adj=0;
break;
case bDay==6:
adj=-1;
break;
case bDay==0:
adj=-2;
break;
case (_b9b+mod)>5:
adj=-2;
break;
default:
break;
}
}else{
if(_b8d<0){
switch(true){
case aDay==6:
adj=0;
break;
case aDay==0:
adj=1;
break;
case bDay==6:
adj=2;
break;
case bDay==0:
adj=1;
break;
case (_b9b+mod)<0:
adj=2;
break;
default:
break;
}
}
}
days+=adj;
days-=(_b95*2);
}
_b8f=days;
break;
case HOUR:
_b8f=_b8c;
break;
case MINUTE:
_b8f=_b8b;
break;
case SECOND:
_b8f=_b8a;
break;
case MILLISECOND:
_b8f=_b89;
break;
default:
break;
}
}
return Math.round(_b8f);
};
dojo.provide("dojo.date.supplemental");
dojo.date.getFirstDayOfWeek=function(_b9c){
var _b9d={mv:5,ae:6,af:6,bh:6,dj:6,dz:6,eg:6,er:6,et:6,iq:6,ir:6,jo:6,ke:6,kw:6,lb:6,ly:6,ma:6,om:6,qa:6,sa:6,sd:6,so:6,tn:6,ye:6,as:0,au:0,az:0,bw:0,ca:0,cn:0,fo:0,ge:0,gl:0,gu:0,hk:0,ie:0,il:0,is:0,jm:0,jp:0,kg:0,kr:0,la:0,mh:0,mo:0,mp:0,mt:0,nz:0,ph:0,pk:0,sg:0,th:0,tt:0,tw:0,um:0,us:0,uz:0,vi:0,za:0,zw:0,et:0,mw:0,ng:0,tj:0,gb:0,sy:4};
_b9c=dojo.hostenv.normalizeLocale(_b9c);
var _b9e=_b9c.split("-")[1];
var dow=_b9d[_b9e];
return (typeof dow=="undefined")?1:dow;
};
dojo.date.getWeekend=function(_ba0){
var _ba1={eg:5,il:5,sy:5,"in":0,ae:4,bh:4,dz:4,iq:4,jo:4,kw:4,lb:4,ly:4,ma:4,om:4,qa:4,sa:4,sd:4,tn:4,ye:4};
var _ba2={ae:5,bh:5,dz:5,iq:5,jo:5,kw:5,lb:5,ly:5,ma:5,om:5,qa:5,sa:5,sd:5,tn:5,ye:5,af:5,ir:5,eg:6,il:6,sy:6};
_ba0=dojo.hostenv.normalizeLocale(_ba0);
var _ba3=_ba0.split("-")[1];
var _ba4=_ba1[_ba3];
var end=_ba2[_ba3];
if(typeof _ba4=="undefined"){
_ba4=6;
}
if(typeof end=="undefined"){
end=0;
}
return {start:_ba4,end:end};
};
dojo.date.isWeekend=function(_ba6,_ba7){
var _ba8=dojo.date.getWeekend(_ba7);
var day=(_ba6||new Date()).getDay();
if(_ba8.end<_ba8.start){
_ba8.end+=7;
if(day<_ba8.start){
day+=7;
}
}
return day>=_ba8.start&&day<=_ba8.end;
};
dojo.provide("dojo.i18n.common");
dojo.i18n.getLocalization=function(_baa,_bab,_bac){
dojo.hostenv.preloadLocalizations();
_bac=dojo.hostenv.normalizeLocale(_bac);
var _bad=_bac.split("-");
var _bae=[_baa,"nls",_bab].join(".");
var _baf=dojo.hostenv.findModule(_bae,true);
var _bb0;
for(var i=_bad.length;i>0;i--){
var loc=_bad.slice(0,i).join("_");
if(_baf[loc]){
_bb0=_baf[loc];
break;
}
}
if(!_bb0){
_bb0=_baf.ROOT;
}
if(_bb0){
var _bb3=function(){
};
_bb3.prototype=_bb0;
return new _bb3();
}
dojo.raise("Bundle not found: "+_bab+" in "+_baa+" , locale="+_bac);
};
dojo.i18n.isLTR=function(_bb4){
var lang=dojo.hostenv.normalizeLocale(_bb4).split("-")[0];
var RTL={ar:true,fa:true,he:true,ur:true,yi:true};
return !RTL[lang];
};
dojo.provide("dojo.date.format");
(function(){
dojo.date.format=function(_bb7,_bb8){
if(typeof _bb8=="string"){
dojo.deprecated("dojo.date.format","To format dates with POSIX-style strings, please use dojo.date.strftime instead","0.5");
return dojo.date.strftime(_bb7,_bb8);
}
function formatPattern(_bb9,_bba){
return _bba.replace(/([a-z])\1*/ig,function(_bbb){
var s;
var c=_bbb.charAt(0);
var l=_bbb.length;
var pad;
var _bc0=["abbr","wide","narrow"];
switch(c){
case "G":
if(l>3){
dojo.unimplemented("Era format not implemented");
}
s=info.eras[_bb9.getFullYear()<0?1:0];
break;
case "y":
s=_bb9.getFullYear();
switch(l){
case 1:
break;
case 2:
s=String(s).substr(-2);
break;
default:
pad=true;
}
break;
case "Q":
case "q":
s=Math.ceil((_bb9.getMonth()+1)/3);
switch(l){
case 1:
case 2:
pad=true;
break;
case 3:
case 4:
dojo.unimplemented("Quarter format not implemented");
}
break;
case "M":
case "L":
var m=_bb9.getMonth();
var _bc3;
switch(l){
case 1:
case 2:
s=m+1;
pad=true;
break;
case 3:
case 4:
case 5:
_bc3=_bc0[l-3];
break;
}
if(_bc3){
var type=(c=="L")?"standalone":"format";
var prop=["months",type,_bc3].join("-");
s=info[prop][m];
}
break;
case "w":
var _bc6=0;
s=dojo.date.getWeekOfYear(_bb9,_bc6);
pad=true;
break;
case "d":
s=_bb9.getDate();
pad=true;
break;
case "D":
s=dojo.date.getDayOfYear(_bb9);
pad=true;
break;
case "E":
case "e":
case "c":
var d=_bb9.getDay();
var _bc3;
switch(l){
case 1:
case 2:
if(c=="e"){
var _bc8=dojo.date.getFirstDayOfWeek(_bb8.locale);
d=(d-_bc8+7)%7;
}
if(c!="c"){
s=d+1;
pad=true;
break;
}
case 3:
case 4:
case 5:
_bc3=_bc0[l-3];
break;
}
if(_bc3){
var type=(c=="c")?"standalone":"format";
var prop=["days",type,_bc3].join("-");
s=info[prop][d];
}
break;
case "a":
var _bc9=(_bb9.getHours()<12)?"am":"pm";
s=info[_bc9];
break;
case "h":
case "H":
case "K":
case "k":
var h=_bb9.getHours();
switch(c){
case "h":
s=(h%12)||12;
break;
case "H":
s=h;
break;
case "K":
s=(h%12);
break;
case "k":
s=h||24;
break;
}
pad=true;
break;
case "m":
s=_bb9.getMinutes();
pad=true;
break;
case "s":
s=_bb9.getSeconds();
pad=true;
break;
case "S":
s=Math.round(_bb9.getMilliseconds()*Math.pow(10,l-3));
break;
case "v":
case "z":
s=dojo.date.getTimezoneName(_bb9);
if(s){
break;
}
l=4;
case "Z":
var _bcb=_bb9.getTimezoneOffset();
var tz=[(_bcb<=0?"+":"-"),dojo.string.pad(Math.floor(Math.abs(_bcb)/60),2),dojo.string.pad(Math.abs(_bcb)%60,2)];
if(l==4){
tz.splice(0,0,"GMT");
tz.splice(3,0,":");
}
s=tz.join("");
break;
case "Y":
case "u":
case "W":
case "F":
case "g":
case "A":
dojo.debug(_bbb+" modifier not yet implemented");
s="?";
break;
default:
dojo.raise("dojo.date.format: invalid pattern char: "+_bba);
}
if(pad){
s=dojo.string.pad(s,l);
}
return s;
});
}
_bb8=_bb8||{};
var _bcd=dojo.hostenv.normalizeLocale(_bb8.locale);
var _bce=_bb8.formatLength||"full";
var info=dojo.date._getGregorianBundle(_bcd);
var str=[];
var _bd0=dojo.lang.curry(this,formatPattern,_bb7);
if(_bb8.selector!="timeOnly"){
var _bd1=_bb8.datePattern||info["dateFormat-"+_bce];
if(_bd1){
str.push(_processPattern(_bd1,_bd0));
}
}
if(_bb8.selector!="dateOnly"){
var _bd2=_bb8.timePattern||info["timeFormat-"+_bce];
if(_bd2){
str.push(_processPattern(_bd2,_bd0));
}
}
var _bd3=str.join(" ");
return _bd3;
};
dojo.date.parse=function(_bd4,_bd5){
_bd5=_bd5||{};
var _bd6=dojo.hostenv.normalizeLocale(_bd5.locale);
var info=dojo.date._getGregorianBundle(_bd6);
var _bd8=_bd5.formatLength||"full";
if(!_bd5.selector){
_bd5.selector="dateOnly";
}
var _bd9=_bd5.datePattern||info["dateFormat-"+_bd8];
var _bda=_bd5.timePattern||info["timeFormat-"+_bd8];
var _bdb;
if(_bd5.selector=="dateOnly"){
_bdb=_bd9;
}else{
if(_bd5.selector=="timeOnly"){
_bdb=_bda;
}else{
if(_bd5.selector=="dateTime"){
_bdb=_bd9+" "+_bda;
}else{
var msg="dojo.date.parse: Unknown selector param passed: '"+_bd5.selector+"'.";
msg+=" Defaulting to date pattern.";
dojo.debug(msg);
_bdb=_bd9;
}
}
}
var _bdd=[];
var _bde=_processPattern(_bdb,dojo.lang.curry(this,_buildDateTimeRE,_bdd,info,_bd5));
var _bdf=new RegExp("^"+_bde+"$");
var _be0=_bdf.exec(_bd4);
if(!_be0){
return null;
}
var _be1=["abbr","wide","narrow"];
var _be2=new Date(1972,0);
var _be3={};
for(var i=1;i<_be0.length;i++){
var grp=_bdd[i-1];
var l=grp.length;
var v=_be0[i];
switch(grp.charAt(0)){
case "y":
if(l!=2){
_be2.setFullYear(v);
_be3.year=v;
}else{
if(v<100){
v=Number(v);
var year=""+new Date().getFullYear();
var _be9=year.substring(0,2)*100;
var _bea=Number(year.substring(2,4));
var _beb=Math.min(_bea+20,99);
var num=(v<_beb)?_be9+v:_be9-100+v;
_be2.setFullYear(num);
_be3.year=num;
}else{
if(_bd5.strict){
return null;
}
_be2.setFullYear(v);
_be3.year=v;
}
}
break;
case "M":
if(l>2){
if(!_bd5.strict){
v=v.replace(/\./g,"");
v=v.toLowerCase();
}
var _bed=info["months-format-"+_be1[l-3]].concat();
for(var j=0;j<_bed.length;j++){
if(!_bd5.strict){
_bed[j]=_bed[j].toLowerCase();
}
if(v==_bed[j]){
_be2.setMonth(j);
_be3.month=j;
break;
}
}
if(j==_bed.length){
dojo.debug("dojo.date.parse: Could not parse month name: '"+v+"'.");
return null;
}
}else{
_be2.setMonth(v-1);
_be3.month=v-1;
}
break;
case "E":
case "e":
if(!_bd5.strict){
v=v.toLowerCase();
}
var days=info["days-format-"+_be1[l-3]].concat();
for(var j=0;j<days.length;j++){
if(!_bd5.strict){
days[j]=days[j].toLowerCase();
}
if(v==days[j]){
break;
}
}
if(j==days.length){
dojo.debug("dojo.date.parse: Could not parse weekday name: '"+v+"'.");
return null;
}
break;
case "d":
_be2.setDate(v);
_be3.date=v;
break;
case "a":
var am=_bd5.am||info.am;
var pm=_bd5.pm||info.pm;
if(!_bd5.strict){
v=v.replace(/\./g,"").toLowerCase();
am=am.replace(/\./g,"").toLowerCase();
pm=pm.replace(/\./g,"").toLowerCase();
}
if(_bd5.strict&&v!=am&&v!=pm){
dojo.debug("dojo.date.parse: Could not parse am/pm part.");
return null;
}
var _bf2=_be2.getHours();
if(v==pm&&_bf2<12){
_be2.setHours(_bf2+12);
}else{
if(v==am&&_bf2==12){
_be2.setHours(0);
}
}
break;
case "K":
if(v==24){
v=0;
}
case "h":
case "H":
case "k":
if(v>23){
dojo.debug("dojo.date.parse: Illegal hours value");
return null;
}
_be2.setHours(v);
break;
case "m":
_be2.setMinutes(v);
break;
case "s":
_be2.setSeconds(v);
break;
case "S":
_be2.setMilliseconds(v);
break;
default:
dojo.unimplemented("dojo.date.parse: unsupported pattern char="+grp.charAt(0));
}
}
if(_be3.year&&_be2.getFullYear()!=_be3.year){
dojo.debug("Parsed year: '"+_be2.getFullYear()+"' did not match input year: '"+_be3.year+"'.");
return null;
}
if(_be3.month&&_be2.getMonth()!=_be3.month){
dojo.debug("Parsed month: '"+_be2.getMonth()+"' did not match input month: '"+_be3.month+"'.");
return null;
}
if(_be3.date&&_be2.getDate()!=_be3.date){
dojo.debug("Parsed day of month: '"+_be2.getDate()+"' did not match input day of month: '"+_be3.date+"'.");
return null;
}
return _be2;
};
function _processPattern(_bf3,_bf4,_bf5,_bf6){
var _bf7=function(x){
return x;
};
_bf4=_bf4||_bf7;
_bf5=_bf5||_bf7;
_bf6=_bf6||_bf7;
var _bf9=_bf3.match(/(''|[^'])+/g);
var _bfa=false;
for(var i=0;i<_bf9.length;i++){
if(!_bf9[i]){
_bf9[i]="";
}else{
_bf9[i]=(_bfa?_bf5:_bf4)(_bf9[i]);
_bfa=!_bfa;
}
}
return _bf6(_bf9.join(""));
}
function _buildDateTimeRE(_bfc,info,_bfe,_bff){
return _bff.replace(/([a-z])\1*/ig,function(_c00){
var s;
var c=_c00.charAt(0);
var l=_c00.length;
switch(c){
case "y":
s="\\d"+((l==2)?"{2,4}":"+");
break;
case "M":
s=(l>2)?"\\S+":"\\d{1,2}";
break;
case "d":
s="\\d{1,2}";
break;
case "E":
s="\\S+";
break;
case "h":
case "H":
case "K":
case "k":
s="\\d{1,2}";
break;
case "m":
case "s":
s="[0-5]\\d";
break;
case "S":
s="\\d{1,3}";
break;
case "a":
var am=_bfe.am||info.am||"AM";
var pm=_bfe.pm||info.pm||"PM";
if(_bfe.strict){
s=am+"|"+pm;
}else{
s=am;
s+=(am!=am.toLowerCase())?"|"+am.toLowerCase():"";
s+="|";
s+=(pm!=pm.toLowerCase())?pm+"|"+pm.toLowerCase():pm;
}
break;
default:
dojo.unimplemented("parse of date format, pattern="+_bff);
}
if(_bfc){
_bfc.push(_c00);
}
return "\\s*("+s+")\\s*";
});
}
})();
dojo.date.strftime=function(_c06,_c07,_c08){
var _c09=null;
function _(s,n){
return dojo.string.pad(s,n||2,_c09||"0");
}
var info=dojo.date._getGregorianBundle(_c08);
function $(_c0d){
switch(_c0d){
case "a":
return dojo.date.getDayShortName(_c06,_c08);
case "A":
return dojo.date.getDayName(_c06,_c08);
case "b":
case "h":
return dojo.date.getMonthShortName(_c06,_c08);
case "B":
return dojo.date.getMonthName(_c06,_c08);
case "c":
return dojo.date.format(_c06,{locale:_c08});
case "C":
return _(Math.floor(_c06.getFullYear()/100));
case "d":
return _(_c06.getDate());
case "D":
return $("m")+"/"+$("d")+"/"+$("y");
case "e":
if(_c09==null){
_c09=" ";
}
return _(_c06.getDate());
case "f":
if(_c09==null){
_c09=" ";
}
return _(_c06.getMonth()+1);
case "g":
break;
case "G":
dojo.unimplemented("unimplemented modifier 'G'");
break;
case "F":
return $("Y")+"-"+$("m")+"-"+$("d");
case "H":
return _(_c06.getHours());
case "I":
return _(_c06.getHours()%12||12);
case "j":
return _(dojo.date.getDayOfYear(_c06),3);
case "k":
if(_c09==null){
_c09=" ";
}
return _(_c06.getHours());
case "l":
if(_c09==null){
_c09=" ";
}
return _(_c06.getHours()%12||12);
case "m":
return _(_c06.getMonth()+1);
case "M":
return _(_c06.getMinutes());
case "n":
return "\n";
case "p":
return info[_c06.getHours()<12?"am":"pm"];
case "r":
return $("I")+":"+$("M")+":"+$("S")+" "+$("p");
case "R":
return $("H")+":"+$("M");
case "S":
return _(_c06.getSeconds());
case "t":
return "\t";
case "T":
return $("H")+":"+$("M")+":"+$("S");
case "u":
return String(_c06.getDay()||7);
case "U":
return _(dojo.date.getWeekOfYear(_c06));
case "V":
return _(dojo.date.getIsoWeekOfYear(_c06));
case "W":
return _(dojo.date.getWeekOfYear(_c06,1));
case "w":
return String(_c06.getDay());
case "x":
return dojo.date.format(_c06,{selector:"dateOnly",locale:_c08});
case "X":
return dojo.date.format(_c06,{selector:"timeOnly",locale:_c08});
case "y":
return _(_c06.getFullYear()%100);
case "Y":
return String(_c06.getFullYear());
case "z":
var _c0e=_c06.getTimezoneOffset();
return (_c0e>0?"-":"+")+_(Math.floor(Math.abs(_c0e)/60))+":"+_(Math.abs(_c0e)%60);
case "Z":
return dojo.date.getTimezoneName(_c06);
case "%":
return "%";
}
}
var _c0f="";
var i=0;
var _c11=0;
var _c12=null;
while((_c11=_c07.indexOf("%",i))!=-1){
_c0f+=_c07.substring(i,_c11++);
switch(_c07.charAt(_c11++)){
case "_":
_c09=" ";
break;
case "-":
_c09="";
break;
case "0":
_c09="0";
break;
case "^":
_c12="upper";
break;
case "*":
_c12="lower";
break;
case "#":
_c12="swap";
break;
default:
_c09=null;
_c11--;
break;
}
var _c13=$(_c07.charAt(_c11++));
switch(_c12){
case "upper":
_c13=_c13.toUpperCase();
break;
case "lower":
_c13=_c13.toLowerCase();
break;
case "swap":
var _c14=_c13.toLowerCase();
var _c15="";
var j=0;
var ch="";
while(j<_c13.length){
ch=_c13.charAt(j);
_c15+=(ch==_c14.charAt(j))?ch.toUpperCase():ch.toLowerCase();
j++;
}
_c13=_c15;
break;
default:
break;
}
_c12=null;
_c0f+=_c13;
i=_c11;
}
_c0f+=_c07.substring(i);
return _c0f;
};
(function(){
var _c18=[];
dojo.date.addCustomFormats=function(_c19,_c1a){
_c18.push({pkg:_c19,name:_c1a});
};
dojo.date._getGregorianBundle=function(_c1b){
var _c1c={};
dojo.lang.forEach(_c18,function(desc){
var _c1e=dojo.i18n.getLocalization(desc.pkg,desc.name,_c1b);
_c1c=dojo.lang.mixin(_c1c,_c1e);
},this);
return _c1c;
};
})();
dojo.date.addCustomFormats("dojo.i18n.calendar","gregorian");
dojo.date.addCustomFormats("dojo.i18n.calendar","gregorianExtras");
dojo.date.getNames=function(item,type,use,_c22){
var _c23;
var _c24=dojo.date._getGregorianBundle(_c22);
var _c25=[item,use,type];
if(use=="standAlone"){
_c23=_c24[_c25.join("-")];
}
_c25[1]="format";
return (_c23||_c24[_c25.join("-")]).concat();
};
dojo.date.getDayName=function(_c26,_c27){
return dojo.date.getNames("days","wide","format",_c27)[_c26.getDay()];
};
dojo.date.getDayShortName=function(_c28,_c29){
return dojo.date.getNames("days","abbr","format",_c29)[_c28.getDay()];
};
dojo.date.getMonthName=function(_c2a,_c2b){
return dojo.date.getNames("months","wide","format",_c2b)[_c2a.getMonth()];
};
dojo.date.getMonthShortName=function(_c2c,_c2d){
return dojo.date.getNames("months","abbr","format",_c2d)[_c2c.getMonth()];
};
dojo.date.toRelativeString=function(_c2e){
var now=new Date();
var diff=(now-_c2e)/1000;
var end=" ago";
var _c32=false;
if(diff<0){
_c32=true;
end=" from now";
diff=-diff;
}
if(diff<60){
diff=Math.round(diff);
return diff+" second"+(diff==1?"":"s")+end;
}
if(diff<60*60){
diff=Math.round(diff/60);
return diff+" minute"+(diff==1?"":"s")+end;
}
if(diff<60*60*24){
diff=Math.round(diff/3600);
return diff+" hour"+(diff==1?"":"s")+end;
}
if(diff<60*60*24*7){
diff=Math.round(diff/(3600*24));
if(diff==1){
return _c32?"Tomorrow":"Yesterday";
}else{
return diff+" days"+end;
}
}
return dojo.date.format(_c2e);
};
dojo.date.toSql=function(_c33,_c34){
return dojo.date.strftime(_c33,"%F"+!_c34?" %T":"");
};
dojo.date.fromSql=function(_c35){
var _c36=_c35.split(/[\- :]/g);
while(_c36.length<6){
_c36.push(0);
}
return new Date(_c36[0],(parseInt(_c36[1],10)-1),_c36[2],_c36[3],_c36[4],_c36[5]);
};
dojo.provide("dojo.date.serialize");
dojo.date.setIso8601=function(_c37,_c38){
var _c39=(_c38.indexOf("T")==-1)?_c38.split(" "):_c38.split("T");
_c37=dojo.date.setIso8601Date(_c37,_c39[0]);
if(_c39.length==2){
_c37=dojo.date.setIso8601Time(_c37,_c39[1]);
}
return _c37;
};
dojo.date.fromIso8601=function(_c3a){
return dojo.date.setIso8601(new Date(0,0),_c3a);
};
dojo.date.setIso8601Date=function(_c3b,_c3c){
var _c3d="^([0-9]{4})((-?([0-9]{2})(-?([0-9]{2}))?)|"+"(-?([0-9]{3}))|(-?W([0-9]{2})(-?([1-7]))?))?$";
var d=_c3c.match(new RegExp(_c3d));
if(!d){
dojo.debug("invalid date string: "+_c3c);
return null;
}
var year=d[1];
var _c40=d[4];
var date=d[6];
var _c42=d[8];
var week=d[10];
var _c44=d[12]?d[12]:1;
_c3b.setFullYear(year);
if(_c42){
_c3b.setMonth(0);
_c3b.setDate(Number(_c42));
}else{
if(week){
_c3b.setMonth(0);
_c3b.setDate(1);
var gd=_c3b.getDay();
var day=gd?gd:7;
var _c47=Number(_c44)+(7*Number(week));
if(day<=4){
_c3b.setDate(_c47+1-day);
}else{
_c3b.setDate(_c47+8-day);
}
}else{
if(_c40){
_c3b.setDate(1);
_c3b.setMonth(_c40-1);
}
if(date){
_c3b.setDate(date);
}
}
}
return _c3b;
};
dojo.date.fromIso8601Date=function(_c48){
return dojo.date.setIso8601Date(new Date(0,0),_c48);
};
dojo.date.setIso8601Time=function(_c49,_c4a){
var _c4b="Z|(([-+])([0-9]{2})(:?([0-9]{2}))?)$";
var d=_c4a.match(new RegExp(_c4b));
var _c4d=0;
if(d){
if(d[0]!="Z"){
_c4d=(Number(d[3])*60)+Number(d[5]);
_c4d*=((d[2]=="-")?1:-1);
}
_c4d-=_c49.getTimezoneOffset();
_c4a=_c4a.substr(0,_c4a.length-d[0].length);
}
var _c4e="^([0-9]{2})(:?([0-9]{2})(:?([0-9]{2})(.([0-9]+))?)?)?$";
d=_c4a.match(new RegExp(_c4e));
if(!d){
dojo.debug("invalid time string: "+_c4a);
return null;
}
var _c4f=d[1];
var mins=Number((d[3])?d[3]:0);
var secs=(d[5])?d[5]:0;
var ms=d[7]?(Number("0."+d[7])*1000):0;
_c49.setHours(_c4f);
_c49.setMinutes(mins);
_c49.setSeconds(secs);
_c49.setMilliseconds(ms);
if(_c4d!==0){
_c49.setTime(_c49.getTime()+_c4d*60000);
}
return _c49;
};
dojo.date.fromIso8601Time=function(_c53){
return dojo.date.setIso8601Time(new Date(0,0),_c53);
};
dojo.date.toRfc3339=function(_c54,_c55){
if(!_c54){
_c54=new Date();
}
var _=dojo.string.pad;
var _c57=[];
if(_c55!="timeOnly"){
var date=[_(_c54.getFullYear(),4),_(_c54.getMonth()+1,2),_(_c54.getDate(),2)].join("-");
_c57.push(date);
}
if(_c55!="dateOnly"){
var time=[_(_c54.getHours(),2),_(_c54.getMinutes(),2),_(_c54.getSeconds(),2)].join(":");
var _c5a=_c54.getTimezoneOffset();
time+=(_c5a>0?"-":"+")+_(Math.floor(Math.abs(_c5a)/60),2)+":"+_(Math.abs(_c5a)%60,2);
_c57.push(time);
}
return _c57.join("T");
};
dojo.date.fromRfc3339=function(_c5b){
if(_c5b.indexOf("Tany")!=-1){
_c5b=_c5b.replace("Tany","");
}
var _c5c=new Date();
return dojo.date.setIso8601(_c5c,_c5b);
};
dojo.provide("dojo.widget.DatePicker");
dojo.widget.defineWidget("dojo.widget.DatePicker",dojo.widget.HtmlWidget,{value:"",name:"",displayWeeks:6,adjustWeeks:false,startDate:"1492-10-12",endDate:"2941-10-12",weekStartsOn:"",staticDisplay:false,dayWidth:"narrow",classNames:{previous:"previousMonth",disabledPrevious:"previousMonthDisabled",current:"currentMonth",disabledCurrent:"currentMonthDisabled",next:"nextMonth",disabledNext:"nextMonthDisabled",currentDate:"currentDate",selectedDate:"selectedDate"},templateString:"<div class=\"datePickerContainer\" dojoAttachPoint=\"datePickerContainerNode\">\r\n\t<table cellspacing=\"0\" cellpadding=\"0\" class=\"calendarContainer\">\r\n\t\t<thead>\r\n\t\t\t<tr>\r\n\t\t\t\t<td class=\"monthWrapper\" valign=\"top\">\r\n\t\t\t\t\t<table class=\"monthContainer\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\r\n\t\t\t\t\t\t<tr>\r\n\t\t\t\t\t\t\t<td class=\"monthCurve monthCurveTL\" valign=\"top\"></td>\r\n\t\t\t\t\t\t\t<td class=\"monthLabelContainer\" valign=\"top\">\r\n\t\t\t\t\t\t\t\t<span dojoAttachPoint=\"increaseWeekNode\" \r\n\t\t\t\t\t\t\t\t\tdojoAttachEvent=\"onClick: onIncrementWeek;\" \r\n\t\t\t\t\t\t\t\t\tclass=\"incrementControl increase\">\r\n\t\t\t\t\t\t\t\t\t<img src=\"${dojoWidgetModuleUri}templates/images/incrementMonth.png\" \r\n\t\t\t\t\t\t\t\t\talt=\"&darr;\" style=\"width:7px;height:5px;\" />\r\n\t\t\t\t\t\t\t\t</span>\r\n\t\t\t\t\t\t\t\t<span \r\n\t\t\t\t\t\t\t\t\tdojoAttachPoint=\"increaseMonthNode\" \r\n\t\t\t\t\t\t\t\t\tdojoAttachEvent=\"onClick: onIncrementMonth;\" class=\"incrementControl increase\">\r\n\t\t\t\t\t\t\t\t\t<img src=\"${dojoWidgetModuleUri}templates/images/incrementMonth.png\" \r\n\t\t\t\t\t\t\t\t\t\talt=\"&darr;\"  dojoAttachPoint=\"incrementMonthImageNode\">\r\n\t\t\t\t\t\t\t\t</span>\r\n\t\t\t\t\t\t\t\t<span \r\n\t\t\t\t\t\t\t\t\tdojoAttachPoint=\"decreaseWeekNode\" \r\n\t\t\t\t\t\t\t\t\tdojoAttachEvent=\"onClick: onIncrementWeek;\" \r\n\t\t\t\t\t\t\t\t\tclass=\"incrementControl decrease\">\r\n\t\t\t\t\t\t\t\t\t<img src=\"${dojoWidgetModuleUri}templates/images/decrementMonth.png\" alt=\"&uarr;\" style=\"width:7px;height:5px;\" />\r\n\t\t\t\t\t\t\t\t</span>\r\n\t\t\t\t\t\t\t\t<span \r\n\t\t\t\t\t\t\t\t\tdojoAttachPoint=\"decreaseMonthNode\" \r\n\t\t\t\t\t\t\t\t\tdojoAttachEvent=\"onClick: onIncrementMonth;\" class=\"incrementControl decrease\">\r\n\t\t\t\t\t\t\t\t\t<img src=\"${dojoWidgetModuleUri}templates/images/decrementMonth.png\" \r\n\t\t\t\t\t\t\t\t\t\talt=\"&uarr;\" dojoAttachPoint=\"decrementMonthImageNode\">\r\n\t\t\t\t\t\t\t\t</span>\r\n\t\t\t\t\t\t\t\t<span dojoAttachPoint=\"monthLabelNode\" class=\"month\"></span>\r\n\t\t\t\t\t\t\t</td>\r\n\t\t\t\t\t\t\t<td class=\"monthCurve monthCurveTR\" valign=\"top\"></td>\r\n\t\t\t\t\t\t</tr>\r\n\t\t\t\t\t</table>\r\n\t\t\t\t</td>\r\n\t\t\t</tr>\r\n\t\t</thead>\r\n\t\t<tbody>\r\n\t\t\t<tr>\r\n\t\t\t\t<td colspan=\"3\">\r\n\t\t\t\t\t<table class=\"calendarBodyContainer\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\r\n\t\t\t\t\t\t<thead>\r\n\t\t\t\t\t\t\t<tr dojoAttachPoint=\"dayLabelsRow\">\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t</tr>\r\n\t\t\t\t\t\t</thead>\r\n\t\t\t\t\t\t<tbody dojoAttachPoint=\"calendarDatesContainerNode\" \r\n\t\t\t\t\t\t\tdojoAttachEvent=\"onClick: _handleUiClick;\">\r\n\t\t\t\t\t\t\t<tr dojoAttachPoint=\"calendarWeekTemplate\">\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t\t<td></td>\r\n\t\t\t\t\t\t\t</tr>\r\n\t\t\t\t\t\t</tbody>\r\n\t\t\t\t\t</table>\r\n\t\t\t\t</td>\r\n\t\t\t</tr>\r\n\t\t</tbody>\r\n\t\t<tfoot>\r\n\t\t\t<tr>\r\n\t\t\t\t<td colspan=\"3\" class=\"yearWrapper\">\r\n\t\t\t\t\t<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"yearContainer\">\r\n\t\t\t\t\t\t<tr>\r\n\t\t\t\t\t\t\t<td class=\"curveBL\" valign=\"top\"></td>\r\n\t\t\t\t\t\t\t<td valign=\"top\">\r\n\t\t\t\t\t\t\t\t<h3 class=\"yearLabel\">\r\n\t\t\t\t\t\t\t\t\t<span dojoAttachPoint=\"previousYearLabelNode\"\r\n\t\t\t\t\t\t\t\t\t\tdojoAttachEvent=\"onClick: onIncrementYear;\" class=\"previousYear\"></span>\r\n\t\t\t\t\t\t\t\t\t<span class=\"selectedYear\" dojoAttachPoint=\"currentYearLabelNode\"></span>\r\n\t\t\t\t\t\t\t\t\t<span dojoAttachPoint=\"nextYearLabelNode\" \r\n\t\t\t\t\t\t\t\t\t\tdojoAttachEvent=\"onClick: onIncrementYear;\" class=\"nextYear\"></span>\r\n\t\t\t\t\t\t\t\t</h3>\r\n\t\t\t\t\t\t\t</td>\r\n\t\t\t\t\t\t\t<td class=\"curveBR\" valign=\"top\"></td>\r\n\t\t\t\t\t\t</tr>\r\n\t\t\t\t\t</table>\r\n\t\t\t\t</td>\r\n\t\t\t</tr>\r\n\t\t</tfoot>\r\n\t</table>\r\n</div>\r\n",templateCssString:".datePickerContainer {\r\n\twidth:164px; /* needed for proper user styling */\r\n}\r\n\r\n.calendarContainer {\r\n/*\tborder:1px solid #566f8f;*/\r\n}\r\n\r\n.calendarBodyContainer {\r\n\twidth:100%; /* needed for the explode effect (explain?) */\r\n\tbackground: #7591bc url(\"images/dpBg.gif\") top left repeat-x;\r\n}\r\n\r\n.calendarBodyContainer thead tr td {\r\n\tcolor:#293a4b;\r\n\tfont:bold 0.75em Helvetica, Arial, Verdana, sans-serif;\r\n\ttext-align:center;\r\n\tpadding:0.25em;\r\n\tbackground: url(\"images/dpHorizLine.gif\") bottom left repeat-x;\r\n}\r\n\r\n.calendarBodyContainer tbody tr td {\r\n\tcolor:#fff;\r\n\tfont:bold 0.7em Helvetica, Arial, Verdana, sans-serif;\r\n\ttext-align:center;\r\n\tpadding:0.4em;\r\n\tbackground: url(\"images/dpVertLine.gif\") top right repeat-y;\r\n\tcursor:pointer;\r\n\tcursor:hand;\r\n}\r\n\r\n\r\n.monthWrapper {\r\n\tpadding-bottom:2px;\r\n\tbackground: url(\"images/dpHorizLine.gif\") bottom left repeat-x;\r\n}\r\n\r\n.monthContainer {\r\n\twidth:100%;\r\n}\r\n\r\n.monthLabelContainer {\r\n\ttext-align:center;\r\n\tfont:bold 0.75em Helvetica, Arial, Verdana, sans-serif;\r\n\tbackground: url(\"images/dpMonthBg.png\") repeat-x top left !important;\r\n\tcolor:#293a4b;\r\n\tpadding:0.25em;\r\n}\r\n\r\n.monthCurve {\r\n\twidth:12px;\r\n}\r\n\r\n.monthCurveTL {\r\n\tbackground: url(\"images/dpCurveTL.png\") no-repeat top left !important;\r\n}\r\n\r\n.monthCurveTR {\r\n\t\tbackground: url(\"images/dpCurveTR.png\") no-repeat top right !important;\r\n}\r\n\r\n\r\n.yearWrapper {\r\n\tbackground: url(\"images/dpHorizLineFoot.gif\") top left repeat-x;\r\n\tpadding-top:2px;\r\n}\r\n\r\n.yearContainer {\r\n\twidth:100%;\r\n}\r\n\r\n.yearContainer td {\r\n\tbackground:url(\"images/dpYearBg.png\") top left repeat-x;\r\n}\r\n\r\n.yearContainer .yearLabel {\r\n\tmargin:0;\r\n\tpadding:0.45em 0 0.45em 0;\r\n\tcolor:#fff;\r\n\tfont:bold 0.75em Helvetica, Arial, Verdana, sans-serif;\r\n\ttext-align:center;\r\n}\r\n\r\n.curveBL {\r\n\tbackground: url(\"images/dpCurveBL.png\") bottom left no-repeat !important;\r\n\twidth:9px !important;\r\n\tpadding:0;\r\n\tmargin:0;\r\n}\r\n\r\n.curveBR {\r\n\tbackground: url(\"images/dpCurveBR.png\") bottom right no-repeat !important;\r\n\twidth:9px !important;\r\n\tpadding:0;\r\n\tmargin:0;\r\n}\r\n\r\n\r\n.previousMonth {\r\n\tbackground-color:#6782a8 !important;\r\n}\r\n\r\n.previousMonthDisabled {\r\n\tbackground-color:#a4a5a6 !important;\r\n\tcursor:default !important\r\n}\r\n.currentMonth {\r\n}\r\n\r\n.currentMonthDisabled {\r\n\tbackground-color:#bbbbbc !important;\r\n\tcursor:default !important\r\n}\r\n.nextMonth {\r\n\tbackground-color:#6782a8 !important;\r\n}\r\n.nextMonthDisabled {\r\n\tbackground-color:#a4a5a6 !important;\r\n\tcursor:default !important;\r\n}\r\n\r\n.currentDate {\r\n\ttext-decoration:underline;\r\n\tfont-style:italic;\r\n}\r\n\r\n.selectedDate {\r\n\tbackground-color:#fff !important;\r\n\tcolor:#6782a8 !important;\r\n}\r\n\r\n.yearLabel .selectedYear {\r\n\tpadding:0.2em;\r\n\tbackground-color:#9ec3fb !important;\r\n}\r\n\r\n.nextYear, .previousYear {\r\n\tcursor:pointer;cursor:hand;\r\n\tpadding:0;\r\n}\r\n\r\n.nextYear {\r\n\tmargin:0 0 0 0.55em;\r\n}\r\n\r\n.previousYear {\r\n\tmargin:0 0.55em 0 0;\r\n}\r\n\r\n.incrementControl {\r\n\tcursor:pointer;cursor:hand;\r\n\twidth:1em;\r\n}\r\n\r\n.increase {\r\n\tfloat:right;\r\n}\r\n\r\n.decrease {\r\n\tfloat:left;\r\n}\r\n\r\n.lastColumn {\r\n\tbackground-image:none !important;\r\n}\r\n\r\n\r\n",templateCssPath:dojo.uri.moduleUri("dojo.widget","templates/DatePicker.css"),postMixInProperties:function(){
dojo.widget.DatePicker.superclass.postMixInProperties.apply(this,arguments);
if(!this.weekStartsOn){
this.weekStartsOn=dojo.date.getFirstDayOfWeek(this.lang);
}
this.today=new Date();
this.today.setHours(0,0,0,0);
if(typeof (this.value)=="string"&&this.value.toLowerCase()=="today"){
this.value=new Date();
}else{
if(this.value&&(typeof this.value=="string")&&(this.value.split("-").length>2)){
this.value=dojo.date.fromRfc3339(this.value);
this.value.setHours(0,0,0,0);
}
}
},fillInTemplate:function(args,frag){
dojo.widget.DatePicker.superclass.fillInTemplate.apply(this,arguments);
var _c5f=this.getFragNodeRef(frag);
dojo.html.copyStyle(this.domNode,_c5f);
this.weekTemplate=dojo.dom.removeNode(this.calendarWeekTemplate);
this._preInitUI(this.value?this.value:this.today,false,true);
var _c60=dojo.lang.unnest(dojo.date.getNames("days",this.dayWidth,"standAlone",this.lang));
if(this.weekStartsOn>0){
for(var i=0;i<this.weekStartsOn;i++){
_c60.push(_c60.shift());
}
}
var _c62=this.dayLabelsRow.getElementsByTagName("td");
for(i=0;i<7;i++){
_c62.item(i).innerHTML=_c60[i];
}
if(this.value){
this.setValue(this.value);
}
},getValue:function(){
return dojo.date.toRfc3339(new Date(this.value),"dateOnly");
},getDate:function(){
return this.value;
},setValue:function(_c63){
this.setDate(_c63);
},setDate:function(_c64){
if(_c64==""){
this.value="";
this._preInitUI(this.curMonth,false,true);
}else{
if(typeof _c64=="string"){
this.value=dojo.date.fromRfc3339(_c64);
this.value.setHours(0,0,0,0);
}else{
this.value=new Date(_c64);
this.value.setHours(0,0,0,0);
}
}
if(this.selectedNode!=null){
dojo.html.removeClass(this.selectedNode,this.classNames.selectedDate);
}
if(this.clickedNode!=null){
dojo.debug("adding selectedDate");
dojo.html.addClass(this.clickedNode,this.classNames.selectedDate);
this.selectedNode=this.clickedNode;
}else{
this._preInitUI(this.value,false,true);
}
this.clickedNode=null;
this.onValueChanged(this.value);
},_preInitUI:function(_c65,_c66,_c67){
if(typeof (this.startDate)=="string"){
this.startDate=dojo.date.fromRfc3339(this.startDate);
}
if(typeof (this.endDate)=="string"){
this.endDate=dojo.date.fromRfc3339(this.endDate);
}
this.startDate.setHours(0,0,0,0);
this.endDate.setHours(24,0,0,-1);
if(_c65<this.startDate||_c65>this.endDate){
_c65=new Date((_c65<this.startDate)?this.startDate:this.endDate);
}
this.firstDay=this._initFirstDay(_c65,_c66);
this.selectedIsUsed=false;
this.currentIsUsed=false;
var _c68=new Date(this.firstDay);
var _c69=_c68.getMonth();
this.curMonth=new Date(_c68);
this.curMonth.setDate(_c68.getDate()+6);
this.curMonth.setDate(1);
if(this.displayWeeks==""||this.adjustWeeks){
this.adjustWeeks=true;
this.displayWeeks=Math.ceil((dojo.date.getDaysInMonth(this.curMonth)+this._getAdjustedDay(this.curMonth))/7);
}
var days=this.displayWeeks*7;
if(dojo.date.diff(this.startDate,this.endDate,dojo.date.dateParts.DAY)<days){
this.staticDisplay=true;
if(dojo.date.diff(_c68,this.endDate,dojo.date.dateParts.DAY)>days){
this._preInitUI(this.startDate,true,false);
_c68=new Date(this.firstDay);
}
this.curMonth=new Date(_c68);
this.curMonth.setDate(_c68.getDate()+6);
this.curMonth.setDate(1);
var _c6b=(_c68.getMonth()==this.curMonth.getMonth())?"current":"previous";
}
if(_c67){
this._initUI(days);
}
},_initUI:function(days){
dojo.dom.removeChildren(this.calendarDatesContainerNode);
for(var i=0;i<this.displayWeeks;i++){
this.calendarDatesContainerNode.appendChild(this.weekTemplate.cloneNode(true));
}
var _c6e=new Date(this.firstDay);
this._setMonthLabel(this.curMonth.getMonth());
this._setYearLabels(this.curMonth.getFullYear());
var _c6f=this.calendarDatesContainerNode.getElementsByTagName("td");
var _c70=this.calendarDatesContainerNode.getElementsByTagName("tr");
var _c71;
for(i=0;i<days;i++){
_c71=_c6f.item(i);
_c71.innerHTML=_c6e.getDate();
_c71.setAttribute("djDateValue",_c6e.valueOf());
var _c72=(_c6e.getMonth()!=this.curMonth.getMonth()&&Number(_c6e)<Number(this.curMonth))?"previous":(_c6e.getMonth()==this.curMonth.getMonth())?"current":"next";
var _c73=_c72;
if(this._isDisabledDate(_c6e)){
var _c74={previous:"disabledPrevious",current:"disabledCurrent",next:"disabledNext"};
_c73=_c74[_c72];
}
dojo.html.setClass(_c71,this._getDateClassName(_c6e,_c73));
if(dojo.html.hasClass(_c71,this.classNames.selectedDate)){
this.selectedNode=_c71;
}
_c6e=dojo.date.add(_c6e,dojo.date.dateParts.DAY,1);
}
this.lastDay=dojo.date.add(_c6e,dojo.date.dateParts.DAY,-1);
this._initControls();
},_initControls:function(){
var d=this.firstDay;
var d2=this.lastDay;
var _c77,_c78,_c79,_c7a,_c7b,_c7c;
_c77=_c78=_c79=_c7a=_c7b=_c7c=!this.staticDisplay;
with(dojo.date.dateParts){
var add=dojo.date.add;
if(_c77&&add(d,DAY,(-1*(this._getAdjustedDay(d)+1)))<this.startDate){
_c77=_c79=_c7b=false;
}
if(_c78&&d2>this.endDate){
_c78=_c7a=_c7c=false;
}
if(_c79&&add(d,DAY,-1)<this.startDate){
_c79=_c7b=false;
}
if(_c7a&&add(d2,DAY,1)>this.endDate){
_c7a=_c7c=false;
}
if(_c7b&&add(d2,YEAR,-1)<this.startDate){
_c7b=false;
}
if(_c7c&&add(d,YEAR,1)>this.endDate){
_c7c=false;
}
}
function enableControl(node,_c7f){
dojo.html.setVisibility(node,_c7f?"":"hidden");
}
enableControl(this.decreaseWeekNode,_c77);
enableControl(this.increaseWeekNode,_c78);
enableControl(this.decreaseMonthNode,_c79);
enableControl(this.increaseMonthNode,_c7a);
enableControl(this.previousYearLabelNode,_c7b);
enableControl(this.nextYearLabelNode,_c7c);
},_incrementWeek:function(evt){
var d=new Date(this.firstDay);
switch(evt.target){
case this.increaseWeekNode.getElementsByTagName("img").item(0):
case this.increaseWeekNode:
var _c82=dojo.date.add(d,dojo.date.dateParts.WEEK,1);
if(_c82<this.endDate){
d=dojo.date.add(d,dojo.date.dateParts.WEEK,1);
}
break;
case this.decreaseWeekNode.getElementsByTagName("img").item(0):
case this.decreaseWeekNode:
if(d>=this.startDate){
d=dojo.date.add(d,dojo.date.dateParts.WEEK,-1);
}
break;
}
this._preInitUI(d,true,true);
},_incrementMonth:function(evt){
var d=new Date(this.curMonth);
var _c85=new Date(this.firstDay);
switch(evt.currentTarget){
case this.increaseMonthNode.getElementsByTagName("img").item(0):
case this.increaseMonthNode:
_c85=dojo.date.add(_c85,dojo.date.dateParts.DAY,this.displayWeeks*7);
if(_c85<this.endDate){
d=dojo.date.add(d,dojo.date.dateParts.MONTH,1);
}else{
var _c86=true;
}
break;
case this.decreaseMonthNode.getElementsByTagName("img").item(0):
case this.decreaseMonthNode:
if(_c85>this.startDate){
d=dojo.date.add(d,dojo.date.dateParts.MONTH,-1);
}else{
var _c87=true;
}
break;
}
if(_c87){
d=new Date(this.startDate);
}else{
if(_c86){
d=new Date(this.endDate);
}
}
this._preInitUI(d,false,true);
},_incrementYear:function(evt){
var year=this.curMonth.getFullYear();
var _c8a=new Date(this.firstDay);
switch(evt.target){
case this.nextYearLabelNode:
_c8a=dojo.date.add(_c8a,dojo.date.dateParts.YEAR,1);
if(_c8a<this.endDate){
year++;
}else{
var _c8b=true;
}
break;
case this.previousYearLabelNode:
_c8a=dojo.date.add(_c8a,dojo.date.dateParts.YEAR,-1);
if(_c8a>this.startDate){
year--;
}else{
var _c8c=true;
}
break;
}
var d;
if(_c8c){
d=new Date(this.startDate);
}else{
if(_c8b){
d=new Date(this.endDate);
}else{
d=new Date(year,this.curMonth.getMonth(),1);
}
}
this._preInitUI(d,false,true);
},onIncrementWeek:function(evt){
evt.stopPropagation();
if(!this.staticDisplay){
this._incrementWeek(evt);
}
},onIncrementMonth:function(evt){
evt.stopPropagation();
if(!this.staticDisplay){
this._incrementMonth(evt);
}
},onIncrementYear:function(evt){
evt.stopPropagation();
if(!this.staticDisplay){
this._incrementYear(evt);
}
},_setMonthLabel:function(_c91){
this.monthLabelNode.innerHTML=dojo.date.getNames("months","wide","standAlone",this.lang)[_c91];
},_setYearLabels:function(year){
var y=year-1;
var that=this;
function f(n){
that[n+"YearLabelNode"].innerHTML=dojo.date.format(new Date(y++,0),{formatLength:"yearOnly",locale:that.lang});
}
f("previous");
f("current");
f("next");
},_getDateClassName:function(date,_c97){
var _c98=this.classNames[_c97];
if((!this.selectedIsUsed&&this.value)&&(Number(date)==Number(this.value))){
_c98=this.classNames.selectedDate+" "+_c98;
this.selectedIsUsed=true;
}
if((!this.currentIsUsed)&&(Number(date)==Number(this.today))){
_c98=_c98+" "+this.classNames.currentDate;
this.currentIsUsed=true;
}
return _c98;
},onClick:function(evt){
dojo.event.browser.stopEvent(evt);
},_handleUiClick:function(evt){
var _c9b=evt.target;
if(_c9b.nodeType!=dojo.dom.ELEMENT_NODE){
_c9b=_c9b.parentNode;
}
dojo.event.browser.stopEvent(evt);
this.selectedIsUsed=this.todayIsUsed=false;
if(dojo.html.hasClass(_c9b,this.classNames["disabledPrevious"])||dojo.html.hasClass(_c9b,this.classNames["disabledCurrent"])||dojo.html.hasClass(_c9b,this.classNames["disabledNext"])){
return;
}
this.clickedNode=_c9b;
this.setDate(new Date(Number(dojo.html.getAttribute(_c9b,"djDateValue"))));
},onValueChanged:function(date){
},_isDisabledDate:function(_c9d){
if(_c9d<this.startDate||_c9d>this.endDate){
return true;
}
return this.isDisabledDate(_c9d,this.lang);
},isDisabledDate:function(_c9e,_c9f){
return false;
},_initFirstDay:function(_ca0,adj){
var d=new Date(_ca0);
if(!adj){
d.setDate(1);
}
d.setDate(d.getDate()-this._getAdjustedDay(d,this.weekStartsOn));
d.setHours(0,0,0,0);
return d;
},_getAdjustedDay:function(_ca3){
var days=[0,1,2,3,4,5,6];
if(this.weekStartsOn>0){
for(var i=0;i<this.weekStartsOn;i++){
days.unshift(days.pop());
}
}
return days[_ca3.getDay()];
},destroy:function(){
dojo.widget.DatePicker.superclass.destroy.apply(this,arguments);
dojo.html.destroyNode(this.weekTemplate);
}});
if(!this["dojo"]){
alert("\"dojo/__package__.js\" is now located at \"dojo/dojo.js\". Please update your includes accordingly");
}

