# Extended-Multi-Server-Chat-System   Banana


//demo time : 11:00am 20-Oct
//due time  : 11:59pm 19-Oct

/need to extend : Security | Failure | Scalability

//must to do: !!important!!

1 encrypted communicaiton (TSL/SSL TCP connections)

2 authentication mechanism (username and password), need a central authentication server.
    need to add the authentication feature to the given chat client to support above authentication mechanism.
    [p.s. the username is not necessarily the same as client identity]
    
3 when a chat server crashes or stops responding, should detect this situation. delete all chatrooms of that server. client cannot redirected to that server anymore.
   can use heartbeat signals

4 the system can add new servers to the system. (add by admin using command "java - jar myServer.jar <any arguments>" ) 

5 set the Cloud Nectar (if port 4444 cannot use, can try port 80)

// need to do:

1 authentication using OAuth2 with Facebook,Google or Twitter accounts. (5 extra marks)

2 GUI (10 extra marks)


————Lang Lin's idea

1.信息交互之间进行加密（TSL／SSL TCP connection），这个可以用java内部自带的方法来实现

2.登陆的时候加入账号密码验证系统（要新加入一个总的server，内部存入了所有的已经设定好了的用户账号密码，记得加个锁，同一个账号密码只能同时登陆一个）

3.＊（＋5）加入API让程序能和twiter/facebook的账号登陆互相连接，使用的协议是OAuth2

4.服务器崩溃后（心跳检测失败的情况下），删除所有关于崩溃服务器的房间的信息，使用户无法再movein那个服务器的房间

5.系统管理员可以增加新的server（这个我觉得是通过和main server的连接来接受所有新server的信息同时开启一个连接）（加入新的JSON协议来控制创建server的信息发送到旧的服务器上）

6.＊（＋10）GUI
