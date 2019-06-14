package com.zhhl.entry_exit.tour;

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class TokenHeadInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {

        var response: Response? = null
        var updateRequest: Request? = null
        try {
            val request = chain.request()//拿到请求对象 用于创建新的请求对象
            updateRequest = request.newBuilder().//没有发现服务器的 request
                method(request.method(), request.body()).build()
            response = chain.proceed(updateRequest)//设置新的服务器request 获得返回的response

            val responseBody = response!!.body()//服务器返回的response
            val content = responseBody!!.string()
            return response.newBuilder().body(ResponseBody.create(response.body()!!.contentType(), content))
                .build()//设置新的服务器返回response
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return response
    }
}

private var okHttpClient: OkHttpClient? = null;

private fun okHttpClient(): OkHttpClient {
    if (okHttpClient == null) {
        okHttpClient =
            OkHttpClient.Builder()/*.retryOnConnectionFailure(true).addInterceptor(TokenHeadInterceptor())*/.build();
    }
    return okHttpClient!!;
}

class HttpDSL {

    companion object {
        val gson: Gson = Gson();
    }

    var usingOkHttp = true;

    private val okHttpClient = okHttpClient()
    private var httpConnection: HttpURLConnection? = null
    private var httpsConnection: HttpsURLConnection? = null
    val requestDescription = RequestDescription()
    lateinit var request: Request
    operator fun invoke(block: HttpDSL.() -> Unit) {
        block();
    }

    private fun useOkhttpParsed() {
        val mediaType: MediaType? = MediaType.parse(requestDescription.mimeType.values())!!
        val body: RequestBody = RequestBody.create(mediaType, requestDescription.body)
        val builder: Request.Builder = Request.Builder()
        when (requestDescription.method) {
            Method.GET ->
                builder.get().url(requestDescription.uri + "?${requestDescription.body}")

            Method.POST ->
                builder.post(body).url(requestDescription.uri)
        }

        builder.addHeader("content-type", requestDescription.mimeType.values())
        for (i in requestDescription.headers)
            builder.addHeader(i.key, i.value)
        request = builder.build()
    }


    private fun parsed() {
        if (usingOkHttp) {
            useOkhttpParsed()
        }
    }


    private fun okhttpInvokeString(function: (String) -> Unit, exception: (Throwable) -> Unit) {
        okhttpInvokeBytes({
            function(String(it))
        }, { exception(it) })
    }

    private fun invokeAccessString(function: (String) -> Unit, exception: (Throwable) -> Unit) {
        if (usingOkHttp) {
            okhttpInvokeString(function, exception)
        } else {
            urlConnectionInvokeString(function, exception)
        }
    }

    private fun invokeAccessBytes(function: (ByteArray) -> Unit, exception: (Throwable) -> Unit) {
        if (usingOkHttp) {
            okhttpInvokeBytes(function, exception)
        } else {

            if (requestDescription.uri.startsWith("https")) {
                urlConnectionsInvokeBytes(function, exception)
            } else
                urlConnectionInvokeBytes(function, exception)
        }
    }

    private fun urlConnectionsInvokeBytes(function: (ByteArray) -> Unit, exception: (Throwable) -> Unit) {
        val url =if(requestDescription.method==Method.GET){
            URL("${requestDescription.uri}?${requestDescription.body}")
        }else{
            URL(requestDescription.uri)
        }
        httpsConnection = url.openConnection() as HttpsURLConnection?

        httpsConnection?.let {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    it.allowUserInteraction = true
                    it.addRequestProperty("Content-Type", requestDescription.mimeType.values())
                    it.addRequestProperty("accept", "*/*");
                    it.addRequestProperty("connection", "Keep-Alive")
                    it.requestMethod = requestDescription.method.getValue()
                    it.doOutput = true
                    it.doInput = true

                    it.connectTimeout = 30000
                    it.readTimeout = 30000
                    it.connect()
                    if (requestDescription.body.isNotEmpty()&&requestDescription.method!=Method.GET) {
                        val outputStream = it.outputStream
                        outputStream.write(requestDescription.body.toByteArray())
                        outputStream.flush()
                        outputStream.close()
                    }

                    if (it.responseCode == 200) {
                        val inputStream = it.inputStream
                        var value = inputStream.readBytes()
                        inputStream.close()
                        withContext(Dispatchers.Main) {
                            function(value)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            exception(IOException("${it.responseCode} ${it.responseMessage}"))
                        }

                    }
                } catch (e: Throwable) {
                    withContext(Dispatchers.Main) {
                        exception(e)
                    }
                }
            }
        }
    }

    private fun urlConnectionInvokeString(function: (String) -> Unit, exception: (Throwable) -> Unit) {
        urlConnectionInvokeBytes({
            function(String(it))
        }, {
            exception(it)
        })
    }


    private fun okhttpInvokeBytes(function: (ByteArray) -> Unit, exception: (Throwable) -> Unit) {
        request.let {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = okHttpClient.newCall(it).execute()
                    response.body()?.let { body ->
                        val string = body.bytes()
                        withContext(Dispatchers.Main) {
                            Log.e("response", "url:${requestDescription.uri} content:${String(string)}")
                            function(string)
                        }
                    }

                } catch (e: Throwable) {
                    withContext(Dispatchers.Main) {
                        exception(e)
                    }
                    e.printStackTrace()
                }
            }
        }
    }

    private fun urlConnectionInvokeBytes(function: (ByteArray) -> Unit, exception: (Throwable) -> Unit) {
        val url = if(requestDescription.method==Method.GET){
            URL("${requestDescription.uri}?${requestDescription.body}")
        }else{
            URL(requestDescription.uri)
        }
        httpConnection = url.openConnection() as HttpURLConnection?

        httpConnection?.let {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    it.allowUserInteraction = true
                    it.addRequestProperty("Content-Type", requestDescription.mimeType.values())
                    it.addRequestProperty("accept", "*/*");
                    it.addRequestProperty("connection", "Keep-Alive")
                    it.requestMethod = requestDescription.method.getValue()
                    it.doOutput = true
                    it.doInput = true

                    it.connectTimeout = 30000
                    it.readTimeout = 30000
                    it.connect()
                    if (requestDescription.body.isNotEmpty()&&requestDescription.method!=Method.GET) {
                        val outputStream = it.outputStream
                        outputStream.write(requestDescription.body.toByteArray())
                        outputStream.flush()
                        outputStream.close()
                    }

                    if (it.responseCode == 200) {
                        val inputStream = it.inputStream
                        var value = inputStream.readBytes()
                        inputStream.close()
                        withContext(Dispatchers.Main) {
                            function(value)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            exception(IOException("${it.responseCode} ${it.responseMessage}"))
                        }

                    }
                } catch (e: Throwable) {
                    withContext(Dispatchers.Main) {
                        exception(e)
                    }
                }
            }
        }
    }

    fun callString(function: (String) -> Unit, exception: (Throwable) -> Unit) {
        parsed()
        invokeAccessString(function, exception)
    }

    fun <T> callType(type: Class<T>, function: (T) -> Unit, exception: (Throwable) -> Unit) {

        callString({
            Log.e("response", "---$it--")
            function(gson.fromJson<T>(it, type))
        }, {
            exception(it)
        })
    }

    fun callBytes(function: (ByteArray) -> Unit, exception: (Throwable) -> Unit) {
        parsed()
        invokeAccessBytes(function, exception)
    }
}

class RequestDescription {
    lateinit var uri: String
    var method: Method = Method.POST
    var mimeType: MimeType = MimeType.APPLICATION_JSON
    var headers = ArrayList<Header>()
    var body: String = ""
    operator fun invoke(block: RequestDescription.() -> Unit) {
        block()
    }

    fun set(uri: String, method: Method, mimeType: MimeType, body: String) {
        this.uri = uri
        this.mimeType = mimeType
        this.method = method
        this.body = body
    }
}

enum class MimeType(private val value: String) {
    APPLICATION_X_FORM_URLENCODED("application/x-www-form-urlencoded"),
    APPLICATION_JSON("application/json"),
    APPLICATION_JAVASCRIPT("application/javascript"),
    APPLICATION_XML("application/xml"),
    TEXT_XML("text/xml"),
    TEXT_HTML("text/html"),
    TEXT_PLAIN("text/plain");

    fun values(): String {
        return value
    }
}

data class Header(var key: String, var value: String)

enum class Method(private val methodValue: String) {
    GET("GET"),
    POST("POST");

    fun getValue(): String {
        return methodValue
    }
}

interface CallType<T> : CallIo {
    fun callType(result: T)
}

interface CallString : CallIo {
    fun callString(result: String)

}

interface CallByteArray : CallIo {
    fun callBytes(result: ByteArray)

}

interface CallIo {
    fun exception(throwable: Throwable)
}