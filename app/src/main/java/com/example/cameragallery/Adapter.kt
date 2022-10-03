package com.example.cameragallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView

class Adapter(private val context: Context):RecyclerView.Adapter<Adapter.ViewHolder>() {
    private val allData = ArrayList<DatabaseData>()

    inner class ViewHolder(itemView: View) :  RecyclerView.ViewHolder(itemView)
    {
        val uploadedImage = itemView.findViewById<ImageView>(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.items,parent,false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentImage = allData[position]
//        holder.uploadedImage.setImageBitmap(currentImage.picture) : used before when we are storing bytearray of image
        holder.uploadedImage.setImageURI(currentImage.pictureUri.toUri())
    }

    override fun getItemCount(): Int {
        return allData.size
    }

    fun updateList(newList: List<DatabaseData>)
    {
        allData.clear()
        allData.addAll(newList)
        notifyDataSetChanged()
    }
}