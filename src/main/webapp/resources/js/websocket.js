// var MyWebSocket = function () {
//
//
// }();

var socket;
if(typeof(WebSocket) == "undefined") {
    console.log("您的浏览器不支持WebSocket");
}else {
    console.log("您的浏览器支持WebSocket");
}

function initSocket(url) {
    //初始化一个socket客户端
    var path = '<%=basePath%>';
    socket = new WebSocket(url);
    //打开事件
    socket.onopen = function() {
        console.log("Socket 已打开");
        //socket.send("这是来自客户端的消息" + location.href + new Date());
        };


    //关闭事件
        socket.onclose = function() {
            console.log("Socket已关闭");
        };
    //发生了错误事件
        socket.onerror = function() {
            alert("Socket发生了错误");
        }

}


/**
 * 当用户离开了页面，关闭连接
 * 点击某个离开页面的链接
 在地址栏中键入了新的 URL
 使用前进或后退按钮
 关闭浏览器
 重新加载页面
 *
 */
$(window).unload(function(){
    socket.close();
});