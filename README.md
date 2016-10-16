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

