package com.cerentekin.restaurantorderapp.adapters

import android.R
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cerentekin.restaurantorderapp.databinding.ItemTableBinding
import com.cerentekin.restaurantorderapp.models.Table
import com.cerentekin.restaurantorderapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TableAdapter(
    private val tableList: List<Table>,
    private val context: Context,
    private val onTableClicked: (Table) -> Unit
) : RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    private val statusOptions = arrayOf("Available", "Occupied", "Reserved")

    inner class TableViewHolder(val binding: ItemTableBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            val adapter = ArrayAdapter(context, R.layout.simple_spinner_item, statusOptions)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.tableStatusSpinner.adapter = adapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val binding = ItemTableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        val table = tableList[position]

        // Masa Numarasını Ayarla
        holder.binding.tableName.text = "Table: ${table.table_id}"
        holder.itemView.setOnClickListener {
            Log.d("TableAdapter", "Table clicked: ${table.table_id}")
            onTableClicked(table)
        }


        // Spinner Adapter
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.binding.tableStatusSpinner.adapter = adapter

        // Gelen `status` değerine göre Spinner'ı ayarla
        val statusIndex = statusOptions.indexOf(table.status.capitalize())
        if (statusIndex != -1) {
            holder.binding.tableStatusSpinner.setSelection(statusIndex)
        } else {
            holder.binding.tableStatusSpinner.setSelection(0)
        }

        holder.binding.tableStatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val newStatus = parent?.getItemAtPosition(position).toString()
                if (newStatus != table.status) { // Eğer durum değişmişse güncelle
                    table.table_id?.let { updateTableStatus(it, newStatus) }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun getStatusIndex(status: String): Int {
        return statusOptions.indexOf(status).takeIf { it != -1 } ?: 0
    }

    private fun updateTableStatus(tableId: Int, newStatus: String) {
        RetrofitClient.apiService.updateTableStatus(tableId, mapOf("status" to newStatus))
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Status updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun getItemCount() = tableList.size
}
