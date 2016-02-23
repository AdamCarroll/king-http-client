package com.king.platform.net.http.netty.requestbuilder;


import com.king.platform.net.http.*;
import com.king.platform.net.http.netty.ConfMap;
import com.king.platform.net.http.netty.NettyHttpClient;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.ByteBuffer;
import java.util.concurrent.Future;

public class HttpClientSSERequestBuilderImpl extends HttpClientRequestHeaderBuilderImpl<HttpClientSSERequestBuilder> implements HttpClientSSERequestBuilder {
	public HttpClientSSERequestBuilderImpl(NettyHttpClient nettyHttpClient, HttpVersion httpVersion, HttpMethod httpMethod, String uri, ConfMap confMap) {
		super(HttpClientSSERequestBuilder.class, nettyHttpClient, httpVersion, httpMethod, uri, confMap);
	}

	@Override
	public BuiltSSEClientRequest build() {

		withHeader("Accept", "text/event-stream");

		final BuiltNettyClientRequest builtNettyClientRequest = new BuiltNettyClientRequest(nettyHttpClient, httpVersion, httpMethod, uri, defaultUserAgent,
			idleTimeoutMillis, totalRequestTimeoutMillis, followRedirects, acceptCompressedResponse, keepAlive, null, null, null, queryParameters,
			headerParameters);


		return new BuiltSSEClientRequest() {
			@Override
			public Future<FutureResult<Void>> execute(HttpSSECallback httpSSECallback) {
				return builtNettyClientRequest.execute(new VoidHttpCallback(httpSSECallback), new VoidResponseConsumer(), new DelegatingNioHttpCallback(httpSSECallback));
			}
		};

	}

	private static class DelegatingNioHttpCallback implements NioCallback {
		private final HttpSSECallback httpSSECallback;

		public DelegatingNioHttpCallback(HttpSSECallback httpSSECallback) {
			this.httpSSECallback = httpSSECallback;
		}

		@Override
		public void onConnecting() {

		}

		@Override
		public void onConnected() {

		}

		@Override
		public void onWroteHeaders() {

		}

		@Override
		public void onWroteContentProgressed(long progress, long total) {

		}

		@Override
		public void onWroteContentCompleted() {

		}

		@Override
		public void onReceivedStatus(HttpResponseStatus httpResponseStatus) {

		}

		@Override
		public void onReceivedHeaders(HttpHeaders httpHeaders) {

		}

		@Override
		public void onReceivedContentPart(int len, ByteBuf buffer) {

		}

		@Override
		public void onReceivedCompleted(HttpResponseStatus httpResponseStatus, HttpHeaders httpHeaders) {

		}

		@Override
		public void onError(Throwable throwable) {

		}
	}


	private static class VoidHttpCallback implements HttpCallback<Void> {
		private final HttpSSECallback httpSSECallback;

		public VoidHttpCallback(HttpSSECallback httpSSECallback) {
			this.httpSSECallback = httpSSECallback;
		}

		@Override
		public void onCompleted(HttpResponse<Void> httpResponse) {
			httpSSECallback.onDisconnect();
		}

		@Override
		public void onError(Throwable throwable) {
			httpSSECallback.onError(throwable);
		}
	}

	private static class VoidResponseConsumer implements ResponseBodyConsumer<Void> {
		@Override
		public void onBodyStart(String contentType, String charset, long contentLength) throws Exception {
		}

		@Override
		public void onReceivedContentPart(ByteBuffer buffer) throws Exception {
		}

		@Override
		public void onCompletedBody() throws Exception {
		}

		@Override
		public Void getBody() {
			return null;
		}
	}
}
