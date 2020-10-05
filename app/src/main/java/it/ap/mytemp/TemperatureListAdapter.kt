package it.ap.mytemp

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.ap.mytemp.data.models.Temperature

class TemperatureListAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<TemperatureListAdapter.TemperatureViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var temperatures = emptyList<Temperature>()

    inner class TemperatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateItemView: TextView = itemView.findViewById(R.id.dateView)
        val tempItemView: TextView = itemView.findViewById(R.id.tempView)
        val coughIcon: ImageView = itemView.findViewById(R.id.cough_icon)
        val coldIcon: ImageView = itemView.findViewById(R.id.cold_icon)
        val notesText: TextView = itemView.findViewById(R.id.notes_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemperatureViewHolder {
        val itemView = inflater.inflate(R.layout.temperature_item, parent, false)
        return TemperatureViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TemperatureViewHolder, position: Int) {
        val current = temperatures[position]
        holder.dateItemView.text =
            inflater.context.getString(R.string.date_string, current.day, current.hour)
        holder.tempItemView.text = current.temp.toString()
        if (current.temp >= 37 && current.temp < 37.5)
            holder.tempItemView.setBackgroundColor(Color.YELLOW)
        else if (current.temp >= 37.5)
            holder.tempItemView.setBackgroundColor(Color.RED)
        else
            holder.tempItemView.setBackgroundColor(Color.TRANSPARENT)
        holder.coughIcon.visibility =
            if (current.cough != null && current.cough) View.VISIBLE else View.GONE
        holder.coldIcon.visibility =
            if (current.cold != null && current.cold) View.VISIBLE else View.GONE
        if (current.notes != null && current.notes.isNotEmpty()) {
            holder.notesText.visibility = View.VISIBLE
            holder.notesText.text = current.notes.toString()
        } else {
            holder.notesText.visibility = View.GONE
        }
    }

    internal fun setTemperatures(temperatures: List<Temperature>) {
        this.temperatures = temperatures
        notifyDataSetChanged()
    }

    override fun getItemCount() = temperatures.size
}