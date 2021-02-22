package com.example.photoplantask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.photoplantask.R
import com.example.photoplantask.model.Location

class LocationAdapter : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    private var list: List<Location> = emptyList()

    private var listener: OnItemClickListener? = null
    private var focusChangeListener: OnFocusChangeListener? = null
    private lateinit var context: Context

    fun setLocations(locations: List<Location>) {
        list = locations
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentLocation = list[position]
        holder.locationName.setText(currentLocation.name)
        holder.gridView.adapter = PictureAdapter(context, currentLocation.pictures)
    }

    override fun getItemCount(): Int {
        return if (list.isNotEmpty()) {
            list.size
        } else {
            return 0
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val locationName: EditText = itemView.findViewById(R.id.locationName)
        private val addPicture: ImageButton = itemView.findViewById(R.id.addPicture)
        val gridView: GridView = itemView.findViewById(R.id.pictureList)

        init {
            addPicture.setOnClickListener {
                listener?.let { listener ->
                    val position: Int = adapterPosition
                    if (position in 0..itemCount) {
                        listener.onItemClick(position)
                    }
                }
            }

            locationName.setOnFocusChangeListener { _, hasFocus ->
                val position: Int = adapterPosition
                if (!hasFocus) {
                    focusChangeListener?.onFocusChanged(position, locationName.text.toString())
                }
            }
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnFocusChangedListener(focusListener: OnFocusChangeListener) {
        this.focusChangeListener = focusListener
    }

    interface OnFocusChangeListener {
        fun onFocusChanged(position: Int, locationName: String)
    }
}