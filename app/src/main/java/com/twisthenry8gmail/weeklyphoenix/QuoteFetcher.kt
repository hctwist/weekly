package com.twisthenry8gmail.weeklyphoenix

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley

object QuoteFetcher {

    fun getQuoteOfTheDay(context: Context, callback: (String) -> Unit) {

        val queue = Volley.newRequestQueue(context)

        val request = JsonObjectRequest(Request.Method.GET, "https://quotes.rest/qod.json?category=inspire", null, Response.Listener {

            callback(it.getJSONObject("content").getJSONArray("quotes").getJSONObject(0).getString("quote"))
        }, Response.ErrorListener {


        })

        queue.add(request)
    }
}