package com.cerentekin.restaurantorderapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cerentekin.restaurantorderapp.databinding.ItemUserBinding
import com.cerentekin.restaurantorderapp.models.User

class UserAdapter(
    private val userList: List<User>,
    private val onDeleteClicked: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.userName.text = user.username
        holder.binding.userRole.text = user.role_name

        holder.binding.deleteUserButton.setOnClickListener {
            onDeleteClicked(user)
        }
    }

    override fun getItemCount() = userList.size
}
