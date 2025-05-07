package com.example.zone.AdapterClasses

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zone.ModelClasses.Chat
import com.example.zone.R
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class ChatsAdapter(
    private val mContext: Context,
    private val mChatList: List<Chat>,
): RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile_image: ImageView = itemView.findViewById(R.id.profile_image)
        var show_text_message: TextView = itemView.findViewById(R.id.show_text_message)
        //var left_image_view: ImageView = itemView.findViewById(R.id.left_image_view)
        var text_seen: TextView = itemView.findViewById(R.id.text_seen)
        //var right_image_view: ImageView = itemView.findViewById(R.id.right_image_view)

    }

    override fun getItemViewType(position: Int): Int {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser == null)
        {
            Log.d("Chat Adapter", "failed to get firebase user")
            return 1
        }
        else
        {
            return if (mChatList[position].sender.equals(firebaseUser.uid))
            {
                1
            }
            else{
                0
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 1)
        {
            Log.d("View Holder", "SENDER")
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
            ViewHolder(view)
        }
        else
        {
            Log.d("View Holder", "RECEIVER")
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat: Chat = mChatList[position]

        //Picasso.get().load(imageUrl).into(holder.profile_image)
        /*
        if (chat.message.equals("sent you an image.") && !chat.url.equals(""))
        {
            if (chat.sender.equals(firebaseUser.uid))
            {
                holder.show_text_message.visibility = View.GONE
                holder.right_image_view.visibility = View.VISIBLE
                //Picasso.get().load(chat.url).into(holder.right_image_view)
            }
            else if (!chat.sender.equals(firebaseUser.uid))
            {
                holder.show_text_message.visibility = View.GONE
                holder.left_image_view.visibility = View.VISIBLE
                //Picasso.get().load(chat.url).into(holder.left_image_view)
            }

        }


        else
        {
            holder.show_text_message.text = chat.message
        }

         */

        holder.show_text_message.text = chat.message




        if (position == mChatList.size-1)
        {
            if (chat.isseen!!)
            {
                holder.text_seen.text = "Seen"
                /*
                if (chat.message.equals("sent you an image.") && !chat.url.equals(""))
                {
                    val lp: RelativeLayout.LayoutParams? = holder.text_seen.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10,0)
                    holder.text_seen.layoutParams = lp
                }

                 */
            }
            else
            {
                holder.text_seen.text = "Sent"
                /*
                if (chat.message.equals("sent you an image.") && !chat.url.equals(""))
                {
                    val lp: RelativeLayout.LayoutParams? = holder.text_seen.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10,0)
                    holder.text_seen.layoutParams = lp
                }

                 */
            }
        }
        else
        {
            holder.text_seen.visibility = View.GONE
        }

    }
}