package com.twisthenry8gmail.weeklyphoenix.view.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.twisthenry8gmail.weeklyphoenix.R

class LayoutPopupAdapter : BaseAdapter() {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view =
            LayoutInflater.from(parent!!.context).inflate(R.layout.layout_popup_view, parent, false)

        val mainLayout = MainLayout.values()[position]


        return view
    }

    override fun getItem(position: Int): Any {

        return Unit
    }

    override fun getItemId(position: Int): Long {

        return 0
    }

    override fun getCount(): Int {

        return MainLayout.values().size
    }
}