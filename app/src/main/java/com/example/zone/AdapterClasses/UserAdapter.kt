package com.example.zone.AdapterClasses

import android.content.Context
import android.content.DialogInterface
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
    mContext: Context,
    mUsers: List<Users>,
    isChatCheck: Boolean): RecyclerView.Adapter<UserAdapter.ViewHolder?>()
{
        private var mContext:Context
        private var mUsers: List<Users>
        private var isChatCheck: Boolean

        init {
            this.mUsers = mUsers
            this.mContext = mContext
            this.isChatCheck = isChatCheck
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val view:View = LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, viewGroup, false)
            return ViewHolder(view)
        }
        override fun getItemCount(): Int {
            return mUsers.size
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val user: Users = mUsers[position]
            holder.userNameTxt.text = user.getUserName()
            Picasso.get().load(user.getProfile()).placeholder(R.drawable.ic_profile_placeholder_foreground).into(holder.profileImageView)

            holder.itemView.setOnClickListener {
                val options = arrayOf<CharSequence>(
                    "Send Message",
                    "Visit Profile"
                )
                val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
                builder.setTitle("What do you want?")
                builder.setItems(options, DialogInterface.OnClickListener{dialog, which ->
                    if (which == 0)
                    {
                        val intent = Intent(mContext, ChatRoomActivity::class.java)
                        intent.putExtra("visit_id", user.getUID())
                        mContext.startActivity(intent)
                    }
                    if (which == 1)
                    {
                        print("fail")
                    }
                })
                builder.show()
            }
        }


        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        {
            var userNameTxt: TextView
            var profileImageView: ImageView
            var onlineTxt: ImageView
            var offlineTxt: ImageView
            var lastMessageTxt: TextView

            init {
                userNameTxt = itemView.findViewById(R.id.profileTitle)
                profileImageView = itemView.findViewById(R.id.profileImage)
                onlineTxt = itemView.findViewById(R.id.image_online)
                offlineTxt = itemView.findViewById(R.id.image_offline)
                lastMessageTxt = itemView.findViewById(R.id.message_last)
            }
        }
}