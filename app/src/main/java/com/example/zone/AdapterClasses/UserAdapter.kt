package com.example.zone.AdapterClasses

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.zone.ModelClasses.Users
import com.example.zone.R
import com.example.zone.chatroom.ChatRoomActivity
import com.squareup.picasso.Picasso

class UserAdapter(
    private val mContext:Context,
    private val mUsers: List<Users>,
    private var isChatCheck: Boolean): RecyclerView.Adapter<UserAdapter.ViewHolder?>()
{

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val view:View = LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, viewGroup, false)
            return UserAdapter.ViewHolder(view)
        }
        override fun getItemCount(): Int {
            return mUsers.size
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val user: Users = mUsers[position]
            holder.userNameTxt.text = user.username
            //Picasso.get().load(user.profile).placeholder(R.drawable.ic_profile_placeholder_foreground).into(holder.profileImageView)

            holder.itemView.setOnClickListener {
                val options = arrayOf<CharSequence>(
                    "Send Message",
                    "Visit Profile"
                )
                val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
                builder.setTitle("What do you want?")
                builder.setItems(options, {dialog, which ->
                    if (which == 0)
                    {
                        val intent = Intent(mContext, ChatRoomActivity::class.java)
                        intent.putExtra("visit_id", user.uid)
                        mContext.startActivity(intent)
                    }
                    if (which == 1)
                    {

                    }
                })
                builder.show()
            }
        }


        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        {
            var userNameTxt: TextView = itemView.findViewById(R.id.username)
            var profileImageView: ImageView = itemView.findViewById(R.id.profile_image)
            var onlineTxt: ImageView = itemView.findViewById(R.id.image_online)
            var offlineTxt: ImageView = itemView.findViewById(R.id.image_offline)
            var lastMessageTxt: TextView = itemView.findViewById(R.id.message_last)
        }
}