package com.mislead.ikanxue.app.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpStack;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

public class MyHurlStack implements HttpStack {
  private static final String HEADER_CONTENT_TYPE = "Content-Type";
  private final MyHurlStack.UrlRewriter mUrlRewriter;
  private final SSLSocketFactory mSslSocketFactory;

  public MyHurlStack() {
    this((MyHurlStack.UrlRewriter) null);
  }

  public MyHurlStack(MyHurlStack.UrlRewriter urlRewriter) {
    this(urlRewriter, (SSLSocketFactory) null);
  }

  public MyHurlStack(MyHurlStack.UrlRewriter urlRewriter, SSLSocketFactory sslSocketFactory) {
    this.mUrlRewriter = urlRewriter;
    this.mSslSocketFactory = sslSocketFactory;
  }

  public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
      throws IOException, AuthFailureError {
    String url = request.getUrl();
    HashMap map = new HashMap();
    map.putAll(request.getHeaders());
    map.putAll(additionalHeaders);
    if (this.mUrlRewriter != null) {
      String parsedUrl = this.mUrlRewriter.rewriteUrl(url);
      if (parsedUrl == null) {
        throw new IOException("URL blocked by rewriter: " + url);
      }

      url = parsedUrl;
    }

    URL parsedUrl1 = new URL(url);
    HttpURLConnection connection = this.openConnection(parsedUrl1, request);
    Iterator responseCode = map.keySet().iterator();

    while (responseCode.hasNext()) {
      String protocolVersion = (String) responseCode.next();
      connection.addRequestProperty(protocolVersion, (String) map.get(protocolVersion));
    }

    setConnectionParametersForRequest(connection, request);
    ProtocolVersion protocolVersion1 = new ProtocolVersion("HTTP", 1, 1);
    int responseCode1 = connection.getResponseCode();
    if (responseCode1 == -1) {
      throw new IOException("Could not retrieve response code from HttpUrlConnection.");
    } else {
      BasicStatusLine responseStatus =
          new BasicStatusLine(protocolVersion1, connection.getResponseCode(),
              connection.getResponseMessage());
      BasicHttpResponse response = new BasicHttpResponse(responseStatus);
      response.setEntity(entityFromConnection(connection));

      for (Object o : connection.getHeaderFields().entrySet()) {
        Entry header = (Entry) o;
        if (header.getKey() != null) {
          if (header.getKey().equals("Set-Cookie")) {
            List headerList = (List) header.getValue();
            for (int i = 0; i < headerList.size(); i++) {
              BasicHeader h =
                  new BasicHeader((String) header.getKey() + i, (String) headerList.get(i));
              response.addHeader(h);
            }
          } else {
            List headerList = (List) header.getValue();
            for (Object aHeaderList : headerList) {
              BasicHeader h = new BasicHeader((String) header.getKey(), (String) aHeaderList);
              response.addHeader(h);
            }
          }
        }
      }

      return response;
    }
  }

  private static HttpEntity entityFromConnection(HttpURLConnection connection) {
    BasicHttpEntity entity = new BasicHttpEntity();

    InputStream inputStream;
    try {
      inputStream = connection.getInputStream();
    } catch (IOException var4) {
      inputStream = connection.getErrorStream();
    }

    entity.setContent(inputStream);
    entity.setContentLength((long) connection.getContentLength());
    entity.setContentEncoding(connection.getContentEncoding());
    entity.setContentType(connection.getContentType());
    return entity;
  }

  protected HttpURLConnection createConnection(URL url) throws IOException {
    return (HttpURLConnection) url.openConnection();
  }

  private HttpURLConnection openConnection(URL url, Request<?> request) throws IOException {
    HttpURLConnection connection = this.createConnection(url);
    int timeoutMs = request.getTimeoutMs();
    connection.setConnectTimeout(timeoutMs);
    connection.setReadTimeout(timeoutMs);
    connection.setUseCaches(false);
    connection.setDoInput(true);
    if ("https".equals(url.getProtocol()) && this.mSslSocketFactory != null) {
      ((HttpsURLConnection) connection).setSSLSocketFactory(this.mSslSocketFactory);
    }

    return connection;
  }

  static void setConnectionParametersForRequest(HttpURLConnection connection, Request<?> request)
      throws IOException, AuthFailureError {
    switch (request.getMethod()) {
      case -1:
        byte[] postBody = request.getPostBody();
        if (postBody != null) {
          connection.setDoOutput(true);
          connection.setRequestMethod("POST");
          connection.addRequestProperty("Content-Type", request.getPostBodyContentType());
          DataOutputStream out = new DataOutputStream(connection.getOutputStream());
          out.write(postBody);
          out.close();
        }
        break;
      case 0:
        connection.setRequestMethod("GET");
        break;
      case 1:
        connection.setRequestMethod("POST");
        addBodyIfExists(connection, request);
        break;
      case 2:
        connection.setRequestMethod("PUT");
        addBodyIfExists(connection, request);
        break;
      case 3:
        connection.setRequestMethod("DELETE");
        break;
      default:
        throw new IllegalStateException("Unknown method type.");
    }
  }

  private static void addBodyIfExists(HttpURLConnection connection, Request<?> request)
      throws IOException, AuthFailureError {
    byte[] body = request.getBody();
    if (body != null) {
      connection.setDoOutput(true);
      connection.addRequestProperty("Content-Type", request.getBodyContentType());
      DataOutputStream out = new DataOutputStream(connection.getOutputStream());
      out.write(body);
      out.close();
    }
  }

  public interface UrlRewriter {
    String rewriteUrl(String var1);
  }
}
