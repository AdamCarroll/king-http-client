# king-http-client

[![Jenkins](https://img.shields.io/jenkins/s/https/kingcom.ci.cloudbees.com/king-http-client.master.build.linux.svg)](https://kingcom.ci.cloudbees.com/job/king-http-client.master.build.linux/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.king.king-http-client/king-http-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.king.king-http-client/king-http-client)

## New in Version 2.1.0
 * Upgraded to use Netty-4.1
 
## New in Version 2.0.0
 * Supports Server Side Events.
 * Support for enabling epoll (set ConfKeys.EPOLL to true)


## Api

First off the HttpClient needs to be created and started.
To make it easier, use the NettyHttpClientBuilder.
Use the different set methods on the NettyHttpClientBuilder to tweak how HttpClient works.

```java
NettyHttpClientBuilder nettyHttpClientBuilder = new NettyHttpClientBuilder();
HttpClient httpClient = nettyHttpClientBuilder.createHttpClient();
httpClient.start();
```

Before start is called, httpClient.setConf can be used to tweak internal settings of the client.

Then to use it, use the fluent method builder on the HttpClient:

```java
Future<FutureResult<String>> resultFuture = httpClient.createGet("http://some.url").build().execute();
httpClient.createPost("http://someUrl").content("someContentToBePosted".getBytes()).withQueryParameter("param1", "value1").withHeader("header1", "headerValue1").build().execute();
```

Or using callback objects:

```java
httpClient.createGet("http://some.url").build().execute(new HttpCallback<String>() {
			@Override
			public void onCompleted(HttpResponse<String> httpResponse) {
				
			}

			@Override
			public void onError(Throwable throwable) {

			}
		});
```


For more complex cases or when the result is unfit to handle as a string, a ResponseBodyConsumer can be defined as the second parameter:

```java
httpClient.createGet("http://some.url").build().execute(new HttpCallback<SomeObject>() {
			@Override
			public void onCompleted(HttpResponse<SomeObject> httpResponse) {

			}

			@Override
			public void onError(Throwable throwable) {

			}
		}, new ResponseBodyConsumer<SomeObject>() {
			@Override
			public void onBodyStart(String contentType, String charset, long contentLength) throws Exception {
				
			}

			@Override
			public void onReceivedContentPart(ByteBuffer buffer) throws Exception {
				//aggregate the content from the server
			}

			@Override
			public void onCompletedBody() throws Exception {
				//build the SomeObject from the aggregated data
			}

			@Override
			public SomeObject getBody() {
				return someObject;
			}
		});
```
This can also be used to stream the returned body to a file instead of buffer all bytes in memory.


## Server Side Events Api
A server side event connection can be made by callign the createSSE method on the httpClient.
```java
SseClient sseClient = httpClient.createSSE(url).build().execute(new SseExecutionCallback() {
			@Override
			public void onConnect() {

			}

			@Override
			public void onDisconnect() {

			}

			@Override
			public void onError(Throwable throwable) {

			}

			@Override
			public void onEvent(String lastSentId, String event, String data) {

			}
		});
		
sseClient.awaitClose();

```
The SseClient also supports reconnecting after a connection has been lost
```java
sseClient.connect();
```
It is also possible to subscribe specific events
```java
sseClient.subscribe("stocks", new SseCallback() {
			@Override
			public void onEvent(String lastSentId, String event, String data) {
				
			}
		});
```


## Thread model

The methods in HttpCallback are only invoked from the threads in HttpCallbackExecutor.
This means that you can have long running jobs without blocking IO operations for other requests. The executor will of course
need to be resized accordingly to prevent excessive queues.  

The methods in NioCallback are always invoked from the internal nio threads. They should therefore not be blocking nor long running. 
The same goes for implementations of the ResponseBodyConsumer and MetricCallback interfaces.


## Limitations
Does not support complex authentication (basic should work using the headers). Does not support proxys. Does not support http2.
