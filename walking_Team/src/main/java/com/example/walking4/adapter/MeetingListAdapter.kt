package com.example.walking4.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.walking4.ChatActivity
import com.example.walking4.MainActivity
import com.example.walking4.MyApplication
import com.example.walking4.R
import com.example.walking4.databinding.ItemMeetingListBinding
import com.example.walking4.fragment.MeetingFragment
import com.example.walking4.model.Meeting
import com.example.walking4.model.MeetingListModel
import com.example.walking4.model.Userinmeeting
import kotlinx.coroutines.DefaultExecutor.enqueue
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MeetingListViewHolder(val binding: ItemMeetingListBinding): RecyclerView.ViewHolder(binding.root)

class MeetingListAdapter (val context: MeetingFragment, val datas:List<Meeting>?, val nickname: String, val username: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var meetingList: ArrayList<Meeting>? = null
    private var num=datas?.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = MeetingListViewHolder(ItemMeetingListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    //text 가져올 때,
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MeetingListViewHolder).binding
        //여행 정보 받아와야함

        var meeting = datas?.get(position)

        binding.meetingListTitle.text = meeting?.meeting_title
//        binding.meetingListMem.text = meeting?.member
//        Log.d("test", "${meeting?.member}")
        binding.meetingListStartDate.text = meeting?.start_date
        binding.meetingListEndDate.text = meeting?.end_date

        //Image 가져올 때,
        Glide.with(context)
            .load(R.drawable.apeach002)
//            .load(trip?.profileImg)
            .override(50, 50)
            .placeholder(R.drawable.apeach002)
            .error(R.drawable.error)
            .into(binding.meetingListImg)



        var dialog_listener = DialogInterface.OnClickListener { dialog, which ->

            var title = meeting!!.meeting_title
            var userinmeeting = Userinmeeting(username, title)
            val networkService = (context.context?.applicationContext as MyApplication).networkService
            val userinmeetingListCall = networkService.doInsertUserinmeeting(userinmeeting)
            userinmeetingListCall.enqueue(object: Callback<Userinmeeting> {
                override fun onResponse(call: Call<Userinmeeting>, response: Response<Userinmeeting>) {
                    val meetingList = response.body()
                    Log.d("dialog", "$meetingList")

                    binding.chatentrybtn.visibility=View.VISIBLE



                }
                override fun onFailure(call: Call<MeetingListModel>, t: Throwable) {
                    Log.d("dialog", "실패")
                }
            })

        }



            holder.binding.meetingListItem.setOnClickListener {

                if( binding.chatentrybtn.visibility==View.VISIBLE){
                    val intent = Intent(context.activity, ChatActivity::class.java) //fragment -> activity Intent
                    intent.putExtra("title", datas?.get(position)?.meeting_title)
                    intent.putExtra("nickname",nickname)
                    context?.startActivity(intent)
                }
                else {
                    AlertDialog.Builder(context.context).run {
                        setTitle("채팅방 참가")
                        setMessage("${meeting?.meeting_title} 모임 채팅방에 참가하시겠습니까?")
                        setPositiveButton("아니오", null)
                        setNegativeButton("네", dialog_listener)
                        show()
                    }
                }
            }





        //터치시 채팅방으로 이동
        holder.binding.chatentrybtn.setOnClickListener {
            val intent = Intent(context.activity, ChatActivity::class.java) //fragment -> activity Intent
            intent.putExtra("title", datas?.get(position)?.meeting_title)
            intent.putExtra("nickname",nickname)
            context?.startActivity(intent)
        }










    }
    override fun getItemCount(): Int {
        return num ?:0
    }


}



