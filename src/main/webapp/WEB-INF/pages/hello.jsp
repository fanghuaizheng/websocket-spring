<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html zh-CN>
<head>
     <script src="resources/js/jquery-1.10.2.js"></script>
    <script src="resources/js/websocket.js"></script>
    <meta charset="UTF-8">
    <title>Title</title>
    <script>

        $(function () {
            var contextPath = '${pageContext.request.contextPath}';
            var ip = window.location.host;
            var url ;
            if (contextPath){
                url = "ws://"+ip+"/"+contextPath+"/ws?userId="+parseInt(Math.random()*100);
            }else {
                url = "ws://"+ip+"/ws?userId="+parseInt(Math.random()*100);
            }
            //前面是协议，后面是项目名称/监听的url,加入uid是为了区分不同的页面
            console.log(url);
            initSocket(url);

            //获得消息事件
            socket.onmessage = function(msg) {
                console.log(msg.data);
                //发现消息进入    调后台获取
                // getCallingList();
            };

            $("#sendMessage").click(function () {
                socket.send("我要给服务器发送消息")
            });

        })

    </script>
</head>
<body>
<p>你好啊</p>
<button id="sendMessage">发送消息</button>
</body>
</html>