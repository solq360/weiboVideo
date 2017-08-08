(function(){
var debugFlag = false;
var urlRoot = "https:";
var url = "https://code.jquery.com/jquery-1.12.3.min.js";

var AOP_DATA = {
	after : {},
	before : {}
};
window.AOP_DATA = AOP_DATA;
window.embed_script = embed_script;
window.register_aop = register_aop;

//执行主程序
embed_script(url,Main);

function Main(){
	if(typeof(jQuery)  ==  'undefined'){
		console.log("deley");
		setTimeout(Main,500);
		return;
	}
	console.log("loaded");
	if(typeof($$)  ==  'undefined'){
		var $$ = jQuery;
		window.$$=$$;
	}
	doWork();
}
/////////////////////业务代码//////////////////////////////
var __urlCount = 0;
function getVideoUrl(){
	console.log("reload getVideoUrl");
	$$('video').unbind('play').bind('play',function(d){
		var $this = $(d.target);
		//currentSrc
		console.log(++__urlCount,$this.attr('src'));
		d.target.pause();
	})
}
function doWork(){
	register_aop(false,{'key':'weiboDelayLoad','cb':getVideoUrl,'ex':/mbloglist/});
	getVideoUrl();
	var __scrollRecord = 700;
	
	var __scrollFlag = setTimeout(__autoScroll,500);
	$$(document).scrollTop(__scrollRecord);

	function __autoScroll(){
		__scrollRecord+=438;
		$$(document).scrollTop(__scrollRecord);
		__scrollFlag = setTimeout(__autoScroll,3500);
	}
}
/*
function doTask(){
	// 配置观察选项:  
	var __config = { attributes: true, characterData: true};
		// 随后,你还可以停止观察  
		__observer.disconnect();  
		$$("video").each(function(e,d){
			// 传入目标节点和观察选项  
			__observer.observe(d, __config);  
		});
	}
	new MutationObserver(function(mutations) {  
		console.log("instert",mutations);  
		doTask();
	}).observe(document.querySelector('.WB_feed_v4'), { childList: true}); 


	// 创建观察者对象  
	var __observer = new MutationObserver(function(mutations) {  
	  mutations.forEach(function(mutation) {
		if(mutation.attributeName!="src"){
			return;
		}
		var url = $(mutation.target).attr("src");
		if(url==""){
			url = mutation.target.currentSrc;
		}
		console.log("url", url);  
	  });      
});  
*/
/**
格式化csv数据
*/
function formatCsv(ar){
	var tr = "";
	for(var i in ar){
		tr+=ar[i]+"	";
	}
	return tr+"\r\n";
}
//////////////////////////////////////////////////////////////////////////////////////////
/**
嵌入script
*/
function embed_script(url ,cb){
 	var script = document.createElement('script');
    script.setAttribute('type', 'text/javascript');
    script.setAttribute('src', url);    
	document.getElementsByTagName('head')[0].appendChild(script);
	script.onload= cb;
}



function register_aop(flag,option){
	if(flag){
		AOP_DATA.after[option.key]=option;
	}else{
		AOP_DATA.before[option.key]=option;
	}
}
/**
重写 XMLHttpRequest http 进行拦截
*/
if(typeof(XMLHttpRequest.prototype.aopXHR) == "undefined"){
	XMLHttpRequest.prototype.aopXHR = true;
	var oriXOpen = XMLHttpRequest.prototype.open; 
	XMLHttpRequest.prototype.open = function(method,url,asncFlag,user,password) { 
		var beforeCb , afterCb = null;
		for(var k in AOP_DATA.before){
			var obj = AOP_DATA.before[k];
			if(obj.str!=null && obj.str == url){
				beforeCb = obj.cb;
				break;
			}else if(obj.ex!=null &&  obj.ex.test(url)){
				beforeCb = obj.cb;
				break;
			}
		}
		for(var obj in AOP_DATA.after){
			var obj = AOP_DATA.after[k];
			if(obj.str!=null && obj.str == url){
				afterCb = obj.cb;
				break;
			}else if(obj.ex!=null &&  obj.ex.test(url)){
				afterCb = obj.cb;
				break;
			}
		}
			
		if(beforeCb!=null || afterCb != null){			
			var cb = this.onreadystatechange;
			this.onreadystatechange = function(){
				if(beforeCb!=null) beforeCb(); 
				if(cb!=null) cb(); 
				if(afterCb!=null) afterCb(); 
 			}
		}
		oriXOpen.call(this,method,url,asncFlag,user,password);
	};
	/**
	var oriXSend = XMLHttpRequest.prototype.send; 
	XMLHttpRequest.prototype.send = function(args) {	
		oriXSend.call(this,args); 	
	};
	*/
}

})();
