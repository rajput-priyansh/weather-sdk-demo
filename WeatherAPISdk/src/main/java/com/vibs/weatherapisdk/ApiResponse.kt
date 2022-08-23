package com.vibs.weatherapisdk

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.vibs.weatherapisdk.models.ErrorResponse
import com.vibs.weatherapisdk.models.Error
import retrofit2.Response
import java.util.regex.Pattern

/**
 * Common class used by API responses.
 * @param <T> the type of the response object
</T> */
@Suppress("unused") // T is used in extending classes
sealed class ApiResponse<T> {

    companion object {
        fun <T> create(errorCode: Int, errorMessage: String): ApiErrorResponse<T> {
            return ApiErrorResponse(errorCode, errorMessage)
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            return processResponse(response)
        }

        private fun <T> processResponse(response: Response<T>): ApiResponse<T> {
//            if (response == null) return ApiEmptyResponse()
            response.let {
                when (response.code()) {
                    // 2xx success series
                    200 -> {
                        val body = response.body()
                        return ApiSuccessResponse(
                            body = body!!,
                            linkHeader = response.headers().get("link")
                        )
                    }
                    // 2xx success series
                    in 201..299 -> {
                        return ApiEmptyResponse()
                    }


                    // 3xx redirection, 4xx client errors, 5xx server errors series
                    in 300..599 -> {
                        val errorResponseBody = response.errorBody()?.string()
                        errorResponseBody?.let {
                            var error: Error
                            try {
                                val errorResponse = Gson().fromJson(it, ErrorResponse::class.java)
                                error = Error(
                                    errorResponse.errorCode ?: -1,
                                    errorResponse.userErrorMessage ?: "",
                                    errorResponse.errorMessage ?: "",
                                    response.code(),
                                )
                            } catch (exception: JsonSyntaxException) {
                                error = Error(
                                    -1,
                                    exception.localizedMessage?:"",
                                    exception.message.toString(),
                                    response.code(),
                                )
                            }

                            return ApiErrorResponse(error.errorCode, error.errorMessage)
                        }
                        return ApiErrorResponse(response.code(), response.message())
                    }
                    else -> {
                        val msg = response.errorBody()?.string()
                        val errorMsg = if (msg.isNullOrEmpty()) {
                            response.message()
                        } else {
                            msg
                        }
                        return ApiErrorResponse(response.code(), errorMsg)
                    }
                }
            }
        }
    }
}

/**
 * separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 */
class ApiEmptyResponse<T> : ApiResponse<T>()


/**
 * separate class for HTTP 200 responses.
 */
data class ApiSuccessResponse<T>(
    val body: T,
    val links: Map<String, String>
) : ApiResponse<T>() {
    constructor(body: T, linkHeader: String?) : this(
        body = body,
        links = linkHeader?.extractLinks() ?: emptyMap()
    )

    val nextPage: Int? by lazy(LazyThreadSafetyMode.NONE) {
        links[NEXT_LINK]?.let { next ->
            val matcher = PAGE_PATTERN.matcher(next)
            if (!matcher.find() || matcher.groupCount() != 1) {
                null
            } else {
                try {
                    Integer.parseInt(matcher.group(1)!!)
                } catch (ex: NumberFormatException) {

                    null
                }
            }
        }
    }

    companion object {
        private val LINK_PATTERN = Pattern.compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
        private val PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)")
        private const val NEXT_LINK = "next"

        private fun String.extractLinks(): Map<String, String> {
            val links = mutableMapOf<String, String>()
            val matcher = LINK_PATTERN.matcher(this)

            while (matcher.find()) {
                val count = matcher.groupCount()
                if (count == 2) {
                    links[matcher.group(2)!!] = matcher.group(1)!!
                }
            }
            return links
        }
    }
}

/**
 * separate class for HTTP 300..599 an responses so that we can manage API error.
 */
data class ApiErrorResponse<T>(val errorCode: Int, val errorMessage: String) : ApiResponse<T>()
