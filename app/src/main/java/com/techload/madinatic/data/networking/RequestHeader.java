package com.techload.madinatic.data.networking;

import android.icu.lang.UCharacter;
import android.util.Log;

import androidx.annotation.NonNull;

import com.techload.madinatic.data.networking.networkOperations.LimitedNetworkOperation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestHeader {
    private ArrayList<Property> properties;


    public RequestHeader(Property... requestProperties) {
        properties = new ArrayList<>();
        for (Property r : requestProperties) {
            properties.add(r);
        }
    }

    public void addProperties(Property... requestProperties) {
        for (Property r : requestProperties) {
            properties.add(r);
        }
    }

    /**/

    public ArrayList<Property> getProperties() {
        return properties;
    }

    public Map<String, String> getPropertiesMap() {
        Map<String, String> propertiesA = new HashMap<>();
        for (int i = 0; i < this.properties.size(); i++) {
            Property property = properties.get(i);
            propertiesA.put(property.getField(), property.getValue());
        }
        return propertiesA;
    }

    /*every property and it's value*/
    public static class Property {

        private String field, value;

        public Property(String field, String value) {
            this.field = field;
            this.value = value;
        }

        public String getField() {
            return field;
        }

        public String getValue() {
            return value;
        }

        public void setField(String field) {
            this.field = field;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @NonNull
        @Override
        public String toString() {
            return "\nfield: " + field
                    + "\nvalue: " + value;
        }
    }

    /**/
    public interface Field {
        String ACCEPT = "Accept";
        String ACCEPT_CHARSET = "Accept-Charset";
        String ACCEPT_ENCODING = "Accept-Encoding";
        String ACCEPT_LANGUAGE = "Accept-Language";
        String AUTHORIZATION = "Authorization";
        String BOUNDARY = "boundary";
        String CACHE_CONTROL = "Cache-Control";
        String CONTENT_ENCODING = "Content-Encoding";
        String CONTENT_LANGUAGE = "Content-Language";
        String CONTENT_LENGTH = "Content-Length";
        String CONTENT_LOCATION = "Content-Location";
        String CONTENT_TYPE = "Content-Type";
        String COOKIE = "Cookie";
        String DATE = "Date";
        String ETAG = "ETag";
        String EXPIRES = "Expires";
        String HOST = "Host";
        String IF_MATCH = "If-Match";
        String IF_MODIFIED_SINCE = "If-Modified-Since";
        String IF_NONE_MATCH = "If-None-Match";
        String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
        String LAST_MODIFIED = "Last-Modified";
        String LOCATION = "Location";
        String SET_COOKIE = "Set-Cookie";
        String USER_AGENT = "User-Agent";
        String VARY = "Vary";
        String WWW_AUTHENTICATE = "WWW-Authenticate";
    }

    public interface MediaType {
        String APPLICATION_ATOM_XML = "application/atom+xml";
        String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
        String APPLICATION_JSON = "application/json";
        String APPLICATION_OCTET_STREAM = "application/octet-stream";
        String APPLICATION_SVG_XML = "application/svg+xml";
        String APPLICATION_XHTML_XML = "application/xhtml+xml";
        String APPLICATION_XML = "application/xml";
        String MEDIA_TYPE_WILDCARD = "*";
        String MULTIPART_FORM_DATA = "multipart/form-data";
        String TEXT_HTML = "text/html";
        String TEXT_PLAIN = "text/plain";
        String TEXT_XML = "text/xml";
        String WILDCARD = "*/*";
    }


}