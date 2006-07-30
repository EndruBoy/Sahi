package net.sf.sahi.request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * User: nraman
 * Date: May 18, 2005
 * Time: 9:18:19 PM
 */
public class MultiPartRequest {
    private final HttpRequest httpRequest;
    Map subRequests;
    public String delimiter;

    public MultiPartRequest(HttpRequest httpRequest) throws IOException {
        this.httpRequest = httpRequest;
        populateSubParts();
    }

    private void populateSubParts() throws IOException {
        String dataStr = new String(httpRequest.data()).trim();
        delimiter = getDelimiter(dataStr);
        int nextIx;
        int prevIx = delimiter.length();
        subRequests = new HashMap();
        while (prevIx + 1 < dataStr.length() && (nextIx = dataStr.indexOf(delimiter, prevIx + 1)) != -1) {
            String subReqStr = dataStr.substring(prevIx, nextIx).trim();
            MultiPartSubRequest multiPartSubRequest = new MultiPartSubRequest(new ByteArrayInputStream(subReqStr.getBytes()));
            subRequests.put(multiPartSubRequest.name(), multiPartSubRequest);
            prevIx = nextIx + delimiter.length();
        }
    }


    private String getDelimiter(String dataStr) {
        return dataStr.substring(0, dataStr.indexOf("\n")).trim();
    }

    public HttpRequest getSimpleHttpRequest() {
        return httpRequest;
    }

    public Map getMultiPartSubRequests() {
        return subRequests;
    }

    public final int contentLength() {
        return httpRequest.contentLength();
    }

    public final Map headers() {
        return httpRequest.headers();
    }

    public final byte[] rawHeaders() {
        return httpRequest.rawHeaders();
    }

    public byte[] rawHeaders(byte[] bytes) {
        return httpRequest.setRawHeaders(bytes);
    }

    public String host() {
        return httpRequest.host();
    }

    public int port() {
        return httpRequest.port();
    }

    public boolean isPost() {
        return httpRequest.isPost();
    }

    public boolean isSSL() {
        return httpRequest.isSSL();
    }

    public String method() {
        return httpRequest.method();
    }

    public String uri() {
        return httpRequest.uri();
    }

    public String protocol() {
        return httpRequest.protocol();
    }


    public HttpRequest getRebuiltRequest() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] delimBytes = (delimiter + "\r\n").getBytes();
        try {
            for (Iterator iterator = subRequests.values().iterator(); iterator.hasNext();) {
                out.write(delimBytes);
                MultiPartSubRequest part = (MultiPartSubRequest) iterator.next();
                part.resetRawHeaders();
                out.write(part.rawHeaders());
                out.write(part.data());
                out.write("\r\n".getBytes());
            }
            out.write(delimBytes);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        httpRequest.setData(out.toByteArray());
        return httpRequest;
    }
}
