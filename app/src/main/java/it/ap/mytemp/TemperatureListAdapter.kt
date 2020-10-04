package it.ap.mytemp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TemperatureListAdapter internal constructor(context: Context):RecyclerView.Adapter<TemperatureListAdapter.TemperatureViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var temperatures = emptyList<Temperature>()

    inner class TemperatureViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val dateItemView: TextView = itemView.findViewById(R.id.dateView)
        val tempItemView: TextView = itemView.findViewById(R.id.tempView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemperatureViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return TemperatureViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TemperatureViewHolder, position: Int) {
        val current = temperatures[position]
        holder.dateItemView.text = inflater.context.getString(R.string.date_string, current.day, current.hour)
        holder.tempItemView.text = current.temp.toString()
    }

    internal fun setWords(words: List<Temperature>) {
        this.temperatures = words
        notifyDataSetChanged()
    }

    override fun getItemCount() = temperatures.size
}