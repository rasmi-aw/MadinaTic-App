package com.techload.madinatic.data.networking.networkOperations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import com.techload.madinatic.data.networking.DataCachingOperation;
import com.techload.madinatic.data.networking.RequestHeader;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;


public abstract class LimitedNetworkOperation {

    // url we want to be connected to
    private String urlStr;

    // whether the thread is in foreground or in background by default it's in background
    private int taskPriority;

    /**/
    private int dataType;

    /**/
    private boolean putInCache = false;

    /*used read cache dirs*/
    private Context context;

    /**/
    private static String boundary;

    /* the possible states of a network operation*/
    public interface EvolutionFlag {

        //error flags

        int WRONG_URL = -3;
        int ERROR_MESSAGE = -2;
        int REQUEST_UNACCEPTED = -1;
        //
        int REQUEST_ACCEPTED = 0;

        //data downloaded flags
        int JSON_IS_READY = 1;
        int IMAGE_IS_READY = 2;
        int VIDEO_IS_READY = 3;
        int AUDIO_IS_READY = 4;

        //data uploaded flags
        int DATA_SENT = 5;

        //Caching flags
        int JSON_CACHED = 11;
        int IMAGE_CACHED = 22;


    }

    /**/
    public interface DataType {
        int TEXT = 0;
        int IMAGE = 1;
        int VIDEO = 2;
        int AUDIO = 3;
        int MULTI_PART_DATA_FORM = 4;
    }


    /* predefined values for the established
     * connection timeout to the server in seconds*/
    public interface ThreadPriority {
        int HIGH = Process.THREAD_PRIORITY_FOREGROUND;
        int LOW = Process.THREAD_PRIORITY_BACKGROUND;

    }


    /**/
    private interface ConnectionMethod {
        String GET = "GET";
        String POST = "POST";
        String DELETE = "DELETE";
        String PUT = "PUT";
        String PATCH = "PATCH";
    }


    /*constructor*/
    public LimitedNetworkOperation(Context context) {
        this.context = context;
        boundary = (boundary == null) ? UUID.randomUUID().toString() : boundary;
    }

    // every class wants to use this should override this method to handle its callbacks


    protected abstract void doInUiThread(int evolutionFlag, Object result);

    private void informUserWithNewFlag(final int evolutionFlag, final Object result) {
        /*posting the code to be executed in the ui thread*/
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                doInUiThread(evolutionFlag, result);
            }
        });

    }

    /*creating a url from  string*/
    private URL createUrl() {
        URL urlObj;
        try {
            urlObj = new URL(urlStr);
        } catch (MalformedURLException e) {
            return null;
        }

        return urlObj;
    }

    /*connect to the server*/
    private HttpURLConnection connectToServer(URL url, String connectionMethod, RequestHeader header) {
        HttpURLConnection connection;
        try {

            connection = (HttpURLConnection) url.openConnection();

            /*setting the way to contact the server (one of crud methods)*/
            connection.setRequestMethod(connectionMethod);

            /**/
            connection.setDoInput(true);
            if (!connectionMethod.equals(ConnectionMethod.GET)) connection.setDoOutput(true);

            /*setting the request headers*/
            if (header != null && header.getProperties() != null)
                for (RequestHeader.Property property : header.getProperties()) {
                    connection.addRequestProperty(property.getField(), property.getValue());
                }

            if (dataType == DataType.TEXT) {
                /*setting content to be sent to json when it's a json*/
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");

            } else if (dataType == DataType.MULTI_PART_DATA_FORM) {
                connection.setRequestProperty(RequestHeader.Field.CONTENT_TYPE,
                        RequestHeader.MediaType.MULTIPART_FORM_DATA + "; " + RequestHeader.Field.BOUNDARY + "= --" + boundary);
            }
            /*setting response type to json*/
            connection.addRequestProperty("Accept", "application/json");

            if (connectionMethod.equals(ConnectionMethod.DELETE)) {
                connection.setConnectTimeout(5000);
                connection.connect();
            }


        } catch (IOException e) {
            connection = null;
        }
        return connection;
    }

    //public methods

    /*download methods*/

    public void downloadText(String url, RequestHeader requestHeader, int taskPriority, boolean putInCache) {
        dataType = DataType.TEXT;
        this.urlStr = url;
        this.taskPriority = taskPriority;
        this.putInCache = putInCache;
        readFromServer(requestHeader);
    }

    public void downloadImage(String url, RequestHeader requestHeader, int taskPriority, boolean putInCache) {
        dataType = DataType.IMAGE;
        this.urlStr = url;
        this.taskPriority = taskPriority;
        this.putInCache = putInCache;
        readFromServer(requestHeader);
    }

    /*any video has to be put in the cache
     so the video view can read it*/
    public void downloadVideo(String url, RequestHeader requestHeader, int taskPriority) {
        dataType = DataType.VIDEO;
        this.urlStr = url;
        this.taskPriority = taskPriority;
        dataType = DataType.VIDEO;
        readFromServer(requestHeader);
    }

    /*Sending methods*/

    /*any video has to be put in the cache so the video view can read it*/
    public void downloadAudio(String url, RequestHeader requestHeader, int taskPriority) {
        dataType = DataType.AUDIO;
        this.urlStr = url;
        this.taskPriority = taskPriority;
        dataType = DataType.VIDEO;
        readFromServer(requestHeader);
    }


//    /*reload the same data loaded last time*/
//    public void reload() {
//        readFromServer();
//    }
//
//    /*reload the same data loaded last time*/
//    public void reloadAndCache() {
//    cb    putInCache = true;
//        readFromServer();
//    }

    /*receiving data from server*/
    private void readFromServer(final RequestHeader requestHeader) {

        //
        Thread readingTask = new Thread(new Runnable() {
            @Override
            public void run() {

                android.os.Process.setThreadPriority(taskPriority);
                // http url connection object to connect to the server and read stream from it
                URL url = createUrl();
                if (url == null) {
                    /*notify threads*/

                    informUserWithNewFlag(EvolutionFlag.WRONG_URL, null);
                    return;
                }
                HttpURLConnection connection = connectToServer(url, ConnectionMethod.GET, requestHeader);
                InputStream inputStream = null;
                if (connection == null) {

                    /*notify threads*/
                    informUserWithNewFlag(EvolutionFlag.ERROR_MESSAGE, null);

                    return;
                } else {

                    try {
                        /*case request rejected*/
                        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            /*notify threads*/
                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "UTF-8"));
                            StringBuilder stringBuilder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                stringBuilder.append(line);
                            }
                            reader.close();
                            connection.disconnect();

                            informUserWithNewFlag(EvolutionFlag.REQUEST_UNACCEPTED, stringBuilder.toString());

                            return;
                        } else {

                            /*notify threads*/
                            informUserWithNewFlag(EvolutionFlag.REQUEST_ACCEPTED, connection);

                            /*reading stream from the server & converting it
                             to the specified data type*/
                            inputStream = connection.getInputStream();

                            switch (dataType) {
                                case DataType.TEXT:
                                    StringBuilder stringBuilder = null;
                                    try {
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                                        stringBuilder = new StringBuilder();
                                        String line;
                                        while ((line = reader.readLine()) != null) {
                                            stringBuilder.append(line);
                                        }

                                        reader.close();

                                    } catch (IOException e) {
                                    } finally {
                                        /*notify threads*/
                                        if (stringBuilder != null && stringBuilder.length() != 0) {
                                            informUserWithNewFlag(EvolutionFlag.JSON_IS_READY, stringBuilder.toString());
                                        } else {

                                            informUserWithNewFlag(EvolutionFlag.ERROR_MESSAGE, null);
                                        }
                                    }
                                    break;

                                case DataType.IMAGE:
                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    informUserWithNewFlag(EvolutionFlag.IMAGE_IS_READY, bitmap);

                                    /*caching image in the background task after user is informed of the result*/
                                    if (putInCache) {
                                        String filePath = new DataCachingOperation(context).
                                                putFileInCache(DataCachingOperation.DataType.IMAGE, bitmap);
                                        // notify threads
                                        informUserWithNewFlag(EvolutionFlag.IMAGE_CACHED, filePath);

                                    }

                                    break;

                                case DataType.VIDEO:

                                    String path = new DataCachingOperation(context).putFileInCache(DataCachingOperation.DataType.VIDEO, inputStream);
                                    informUserWithNewFlag(EvolutionFlag.VIDEO_IS_READY, path);

                                    break;

                                case DataType.AUDIO:

                                    break;

                            }

                        }

                    } catch (IOException e) {

                        informUserWithNewFlag(EvolutionFlag.ERROR_MESSAGE, null);
                        return;

                    } finally {
                        connection.disconnect();

                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                }

            }
        });
        readingTask.start();
    }


    /*sending methods*/
    public void postJson(String url, RequestHeader requestHeader, String json, int taskPriority) {
        this.urlStr = url;
        this.taskPriority = taskPriority;
        writeToServer(json, ConnectionMethod.POST, requestHeader);
    }

    public void putJson(String url, RequestHeader requestHeader, String json, int taskPriority) {
        this.urlStr = url;
        this.taskPriority = taskPriority;
        writeToServer(json, ConnectionMethod.PUT, requestHeader);
    }

    public void patchJson(String url, RequestHeader requestHeader, String json, int taskPriority) {
        this.urlStr = url;
        this.taskPriority = taskPriority;
        writeToServer(json, ConnectionMethod.PATCH, requestHeader);
    }

    public void deleteJson(String url, RequestHeader requestHeader, String json, int taskPriority) {
        this.urlStr = url;
        this.taskPriority = taskPriority;
        writeToServer(json, ConnectionMethod.DELETE, requestHeader);
    }

    public void postMultiPartDataForm(String url, RequestHeader requestHeader, int taskPriority, RequestHeader.Property... values) {
        this.urlStr = url;
        this.taskPriority = taskPriority;
        this.dataType = DataType.MULTI_PART_DATA_FORM;
        writeToServer(values, ConnectionMethod.POST, requestHeader);
    }

    private void writeToServer(final Object content, final String connectionMethod, final RequestHeader requestHeader) {
        Thread writingTask = new Thread(new Runnable() {
            @Override
            public void run() {

                android.os.Process.setThreadPriority(taskPriority);
                // http url connection object to connect to the server and read stream from it
                URL url = createUrl();
                if (url == null) {
                    /*notify threads*/

                    informUserWithNewFlag(EvolutionFlag.WRONG_URL, null);
                    return;
                }
                HttpURLConnection connection = null;
                if (dataType != DataType.MULTI_PART_DATA_FORM)
                    connection = connectToServer(url, connectionMethod, requestHeader);
                OutputStream outputStream = null;


                try {


                    StringBuilder stringBuilder = null;
                    BufferedReader reader = null;

                    switch (dataType) {
                        case DataType.TEXT:
                            if (dataType == DataType.TEXT)
                                outputStream = connection.getOutputStream();
                            BufferedOutputStream writer = new BufferedOutputStream(outputStream);
                            try {
                                /*sending json to the server*/
                                writer.write(content.toString().getBytes());
                                writer.flush();


                                /*reading response from server*/

                                if (connection.getResponseCode() != 200) {
                                    //case of error
                                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "UTF-8"));
                                    stringBuilder = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        stringBuilder.append(line);
                                    }

                                    informUserWithNewFlag(EvolutionFlag.ERROR_MESSAGE, stringBuilder.toString());
                                    throw new IOException();
                                }


                                /*case request accepted*/
                                informUserWithNewFlag(EvolutionFlag.DATA_SENT, null);
                                informUserWithNewFlag(EvolutionFlag.REQUEST_ACCEPTED,
                                        Integer.valueOf(connection.getHeaderField("content-Length")));


                                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

                                stringBuilder = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    stringBuilder.append(line);
                                }

                                if (stringBuilder != null) {
                                    informUserWithNewFlag(EvolutionFlag.JSON_IS_READY, stringBuilder.toString());

                                }


                            } catch (IOException e) {
                                informUserWithNewFlag(EvolutionFlag.REQUEST_UNACCEPTED, null);

                            } finally {
                                /*notify threads*/
                                if (stringBuilder == null) {
                                    informUserWithNewFlag(EvolutionFlag.REQUEST_UNACCEPTED, null);
                                }
                                /**/
                                if (reader != null) reader.close();
                                if (outputStream != null) outputStream.close();
                                if (writer != null) writer.close();
                            }
                            break;

//                                case DataType.IMAGE:
//                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                                    informUserWithNewFlag(EvolutionFlag.IMAGE_IS_READY, bitmap);
//
//                                    /*caching image in the background task after user is informed of the result*/
//                                    if (putInCache) {
//                                        String filePath = new DataCachingOperation(context).
//                                                putFileInCache(DataCachingOperation.DataType.IMAGE, bitmap);
//                                        // notify threads
//                                        informUserWithNewFlag(EvolutionFlag.IMAGE_CACHED, filePath);
//
//                                    }
//
//                                    break;
//
//                                case DataType.VIDEO:
//
//                                    String path = new DataCachingOperation(context).putFileInCache(DataCachingOperation.DataType.VIDEO, inputStream);
//                                    informUserWithNewFlag(EvolutionFlag.VIDEO_IS_READY, path);
//
//                                    break;
//
//                                case DataType.AUDIO:
//
//                                    break;

                        case DataType.MULTI_PART_DATA_FORM:

                            PrintWriter printWriter = null;
                            try {
                                RequestHeader.Property[] values = ((RequestHeader.Property[]) content);
                                HttpPostMultiPart post = new HttpPostMultiPart(urlStr, "UTF-8", requestHeader.getPropertiesMap());

                                /*distinguish btw files and strings*/
                                for (int i = 0; i < values.length; i++) {

                                    if (values[i] != null) {

                                        File file = new File(values[i].getValue());


                                        if (file.exists()) {
                                            post.addFilePart(values[i].getField(), file);
                                        } else {
                                            post.addFormField(values[i].getField(), values[i].getValue());
                                        }
                                    }

                                }
                                post.finish();


                            } catch (IOException e) {
                                informUserWithNewFlag(EvolutionFlag.REQUEST_UNACCEPTED, null);

                            }
                            break;

                    }


                } catch (IOException e) {

                    informUserWithNewFlag(EvolutionFlag.REQUEST_UNACCEPTED, null);
                    return;

                } finally {
                    if (connection != null) connection.disconnect();

                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }

        });
        writingTask.start();
    }

    public void delete(String urlStr, final RequestHeader requestHeader) {
        this.urlStr = urlStr;

        new Thread(new Runnable() {
            @Override
            public void run() {

                android.os.Process.setThreadPriority(taskPriority);
                // http url connection object to connect to the server and read stream from it
                URL url = createUrl();
                if (url == null) {
                    /*notify threads*/
                    informUserWithNewFlag(EvolutionFlag.WRONG_URL, null);
                    return;
                }

                HttpURLConnection connection = connectToServer(url, ConnectionMethod.DELETE, requestHeader);
                BufferedReader reader = null;
                try {
                    //
                    if (connection.getResponseCode() != 200) {
                        //case of error
                        reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "UTF-8"));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        informUserWithNewFlag(EvolutionFlag.ERROR_MESSAGE, stringBuilder.toString());
                        throw new IOException();
                    } else
                        informUserWithNewFlag(EvolutionFlag.REQUEST_ACCEPTED, null);


                } catch (IOException e) {
                    informUserWithNewFlag(EvolutionFlag.REQUEST_UNACCEPTED, null);

                } finally {
                    /**/
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                        }
                        if (connection != null) connection.disconnect();
                    }

                }

            }

        }).start();
    }

    public void setUrl(String url) {
        this.urlStr = url;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public void setTaskPriority(int taskPriority) {
        this.taskPriority = taskPriority;
    }

    public void setPutInCache(boolean putInCache) {
        this.putInCache = putInCache;
    }


    private class HttpPostMultiPart {
        private final String boundary;
        private static final String LINE = "\r\n";
        private HttpURLConnection httpConn;
        private String charset;
        private OutputStream outputStream;
        private PrintWriter writer;

        /**
         * This constructor initializes a new HTTP POST request with content type
         * is set to multipart/form-data
         *
         * @param requestURL
         * @param charset
         * @param headers
         * @throws IOException
         */
        public HttpPostMultiPart(String requestURL, String charset, Map<String, String> headers) throws IOException {
            this.charset = charset;
            boundary = UUID.randomUUID().toString();
            URL url = new URL(requestURL);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true);    // indicates POST method
            httpConn.setDoInput(true);
            httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            if (headers != null && headers.size() > 0) {
                Iterator<String> it = headers.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    String value = headers.get(key);
                    httpConn.setRequestProperty(key, value);
                }
            }
            outputStream = httpConn.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
        }

        /**
         * Adds a form field to the request
         *
         * @param name  field name
         * @param value field value
         */
        public void addFormField(String name, String value) {
            writer.append("--" + boundary).append(LINE);
            writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE);
            writer.append("Content-Type: text/plain; charset=" + charset).append(LINE);
            writer.append(LINE);
            writer.append(value).append(LINE);
            writer.flush();
        }

        /**
         * Adds a upload file section to the request
         *
         * @param fieldName
         * @param uploadFile
         * @throws IOException
         */
        public void addFilePart(String fieldName, File uploadFile)
                throws IOException {
            String fileName = uploadFile.getName();
            writer.append("--" + boundary).append(LINE);
            writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE);
            writer.append("Content-Transfer-Encoding: binary").append(LINE);
            writer.append(LINE);
            writer.flush();

            FileInputStream inputStream = new FileInputStream(uploadFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();
            writer.append(LINE);
            writer.flush();
        }

        /**
         * Completes the request and receives response from the server.
         *
         * @return String as response in case the server returned
         * status OK, otherwise an exception is thrown.
         * @throws IOException
         */
        public String finish() throws IOException {
            String response = "";
            writer.flush();
            writer.append("--" + boundary + "--").append(LINE);
            writer.close();

            // checks server's status code first
            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                informUserWithNewFlag(EvolutionFlag.REQUEST_ACCEPTED, null);
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = httpConn.getInputStream().read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                response = result.toString(this.charset);
                httpConn.disconnect();
                informUserWithNewFlag(EvolutionFlag.JSON_IS_READY, response);
            } else {
                //case of error
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getErrorStream(), "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                informUserWithNewFlag(EvolutionFlag.ERROR_MESSAGE, stringBuilder.toString());
            }
            return response;
        }
    }


}