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
var filterList = ['L全球发明创新的秒拍视频','L秒拍视频'];
var __scrollRecord = 400;
var proxyUrl = "http://127.0.0.1:8989/";

var taskRecrod={};
var itemRetryLimit = 8; //每个item忽略重试次数上限
var allRetryLimit = 15; //reloadItem 重试上限
var allRetryRecord = 0; //reloadItem 触发重试记录

var timeInterval = 2800;//queue Interval
var timeIntervalhWnd =null;

function doWork(){
	register_aop(false,{'key':'weiboDelayLoad','cb':getVideoUrl,'ex':/mbloglist/});
	reloadItem();
}

function reloadItem(){
	allRetryRecord=0;
	taskRecrod={};
	getVideoUrl();
	$$(document).scrollTop(__scrollRecord);
	pushQueue(0);
	if(timeIntervalhWnd!=null){
		clearInterval(timeIntervalhWnd);
	}
	timeIntervalhWnd = setInterval(queueWork,timeInterval);
}
function queueWork(){
	if(allRetryRecord>=allRetryLimit){
		allRetryRecord = 0;
		getVideoUrl();
	}
	
	var index = getQueue();
	//next
	if(index>=$$('video').length){
		var next = $('.next')[0];
		if(next){
			console.log("next page");
			next.click();
		}
		setTimeout(reloadItem,8000);
		return;
	}
	//console.log(taskRecrod,index);
	if(index <0){
		return;
	}
	
	var $this = $$($$('video')[index]);
	var  y = $this.offset().top - $this.height()/2;
	$(document).scrollTop(y);
	if(taskRecrod[index].retry>1){
		allRetryRecord++;
	}
	if(taskRecrod[index].retry>=itemRetryLimit){
		extractData($this);
	}
	taskRecrod[index].retry++;
}
function getQueue(){
	for(var index in taskRecrod){
 		if(taskRecrod[index].statu>0 || taskRecrod[index].retry>itemRetryLimit){
			continue;
		}
		return index;
	}
 	return -1; 
}
function pushQueue(index){
	if(taskRecrod[index]==null){
		taskRecrod[index]={
			retry:0,
			statu:0
		};
	}
	taskRecrod[index].statu=-1;
}

function getVideoUrl(){
	console.log("reload getVideoUrl");
	$$('video').each(function(i,d){
		$(d).attr('_index',i);
	}).unbind('play').bind('play',function(d){
		var $this = $(d.target);
		extractData($this);
	})
}
function extractData($this){
	var parent = $this.closest("div.WB_detail"),
		titleDom = parent.find('div.WB_text'),
		title = titleDom.text(),
		url = $this.attr('src'),
		index = parseInt($this.attr('_index'));
	//过滤重复提交
	if(taskRecrod[index]==null){
		pushQueue(index);
	}else if( taskRecrod[index]!=null && taskRecrod[index].statu>=0){
		$this[0].pause();
		return;
	}
	taskRecrod[index].statu = 0;
	for(var i in filterList){
		var key = filterList[i];
		if(typeof(key)=='string'){
			title = title.replace(key,"");
		}else if(typeof(key)=='object'){
			title = title.replace(key,"");
		}
	}
	title = title.replace(/(^\s*)|(\s*$)|(\t*)/g,"");

	//currentSrc
	console.log(index,title,url);
	$this[0].pause();
	
	//post add
	var x = $this.offset().left + $this.width()/2,
		y = $this.offset().top-$(document).scrollTop() + $this.height()/2 + 70;
	addTask(index,{x:x,y:y,url:url,title:title,index:index,suffix:".mp4"});
	
	//next Item
	//__scrollFlag = setTimeout(function(){ __autoScroll({x:x,y:y}); },3500);
/*
	setTimeout(function(){
		var nextItem = __ItemList[parseInt($this.attr('_index'))+1];
		if(nextItem) nextItem.click();
		console.log("next",nextItem.src)
	},250);
*/
}
function addTask(index,data){
	console.log(data);
	$$.ajax({  
        url:proxyUrl+"addTask",  
        dataType:'jsonp',  
        data:data,
        jsonp:'cb',
		async:false,
        success:function(result) {
			taskRecrod[index].statu = 1;
        },error:function(){
			pushQueue(index);
		},  
        timeout:3000  
    });
	pushQueue(index+1);
}

function __autoScroll(data){
	var data = data  || {};
	__scrollRecord+=438;
	$$(document).scrollTop(__scrollRecord);
	$$.ajax({  
        url:proxyUrl+"click",  
        dataType:'jsonp',  
        jsonp:'cb',
		data : data,
        success:function(result) {  
        },  
        timeout:3000  
    });
}
/*
function doTask(){
	// 配置观察选项:  
	var __config = { attributes: true, characterData: true};
	__observer.disconnect();  
	$$("video").each(function(e,d){
		__observer.observe(d, __config);  
	});

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
} 
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
