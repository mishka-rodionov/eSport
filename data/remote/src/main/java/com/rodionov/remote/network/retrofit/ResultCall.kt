package com.rodionov.remote.network.retrofit

import com.rodionov.remote.base.BaseModel
import com.rodionov.remote.base.CommonModel
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultCall<T : BaseModel>(private val request: Call<T>) : Call<Result<T>> {

    final override fun enqueue(callback: Callback<Result<T>>) {
        request.enqueue(
            object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        handleSuccessResponse(callback, call, response)
                    } else {
                        handleErrorResponse(callback, call, response)
                    }
                }

                override fun onFailure(call: Call<T>, throwable: Throwable) {
                    handleError(callback, call, throwable)
                }
            }
        )
    }

    final override fun isCanceled(): Boolean {
        return request.isCanceled
    }

    final override fun isExecuted(): Boolean {
        return request.isExecuted
    }

    final override fun request(): Request {
        return request.request()
    }

    final override fun timeout(): Timeout {
        return request.timeout()
    }

    final override fun cancel() {
        request.cancel()
    }

    fun handleSuccessResponse(callback: Callback<Result<T>>, call: Call<T>, response: Response<T>) {
        try {
            val body = response.body()!!
            if (body.status != 1) {
                callback.onResponse(
                    this@ResultCall,
                    Response.success(
                        response.code(),
                        Result.failure(getError(response))
                    )
                )
            } else {
                callback.onResponse(
                    this@ResultCall,
                    Response.success(
                        response.code(),
                        Result.success(body)
                    )
                )
            }
        } catch (exception: Throwable) {
            callback.onResponse(
                this@ResultCall,
                Response.success(
                    Result.failure(exception)
                )
            )
        }
    }

    fun handleErrorResponse(callback: Callback<Result<T>>, call: Call<T>, response: Response<T>) {
        callback.onResponse(
            this@ResultCall,
            Response.success(
                Result.failure(getError(response))
            )
        )
    }

    fun handleError(callback: Callback<Result<T>>, call: Call<T>, throwable: Throwable) {
        callback.onResponse(
            this@ResultCall,
            Response.success(Result.failure(throwable))
        )
    }

    fun getError(response: Response<T>): Throwable {
        val body = response.body()

        return if (body !is BaseModel) {
            Throwable(response.errorBody()?.string())
        } else {
            Throwable(body.getFirstErrorMessage()?.message)
        }
    }

    override fun execute(): Response<Result<T>> {
        return try {
            val response = request.execute()
            val body = response.body()
            if (body == null) {
                Response.success(Result.failure(NullPointerException(request.toString())))
            } else {
                Response.success(Result.success(body))
            }
        } catch (exception: Throwable) {
            Response.success(Result.failure(exception))
        }
    }

    override fun clone(): Call<Result<T>> {
        return ResultCall(request.clone())
    }
}