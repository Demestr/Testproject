package example.testproject.adapters


import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.view.LayoutInflater
import example.testproject.LocateData
import example.testproject.R


class TableAdapter(context: Context, val onClickListener: OnClickListener) : RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    private val locates = ArrayList<LocateData>()
    private val res = context.resources

    fun setItems(locs: Collection<LocateData>) {
        locates.addAll(locs)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return locates.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.locate_item, parent, false)
        return TableViewHolder(view)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        holder.txtLatitude.text =  res.getString(R.string.latitude, locates[position].latitude)
        holder.txtLongitude.text = res.getString(R.string.longitude,locates[position].longitude)
        holder.txtAddress.text = locates[position].address
        holder.txtDate.text = locates[position].date
        holder.itemView.setOnClickListener{
            onClickListener.onClick(locates[position])
        }
    }


    inner class TableViewHolder(item: View) : RecyclerView.ViewHolder(item){
        val txtLatitude = item.findViewById<TextView>(R.id.txtLat)!!
        val txtLongitude = item.findViewById<TextView>(R.id.txtLong)!!
        val txtAddress = item.findViewById<TextView>(R.id.txtAddress)!!
        val txtDate = item.findViewById<TextView>(R.id.txtDate)!!
    }

    class OnClickListener(val clickListener: (locateData: LocateData) -> Unit) {
        fun onClick(locateData: LocateData) = clickListener(locateData)
    }
}