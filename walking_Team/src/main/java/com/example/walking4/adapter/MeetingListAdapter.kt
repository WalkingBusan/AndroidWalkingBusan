package com.example.walking4.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.walking4.ChatActivity
import com.example.walking4.R
import com.example.walking4.databinding.ItemMeetingListBinding
import com.example.walking4.fragment.MeetingFragment
import com.example.walking4.model.Meeting
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MeetingListViewHolder(val binding: ItemMeetingListBinding): RecyclerView.ViewHolder(binding.root)

class MeetingListAdapter (val context: MeetingFragment, val datas:List<Meeting>?, val nickname: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
        binding.meetingListStartDate.text = convertLongToDate(meeting?.start_date)
        binding.meetingListEndDate.text = convertLongToDate(meeting?.end_date)

        //Image 가져올 때,
        Glide.with(context)
            .load(R.drawable.apeach002)
//            .load(trip?.profileImg)
            .override(50, 50)
            .placeholder(R.drawable.apeach002)
            .error(R.drawable.error)
            .into(binding.meetingListImg)

        //터치시 채팅방으로 이동
        holder.binding.meetingListItem.setOnClickListener {
            val intent = Intent(context.activity, ChatActivity::class.java) //fragment -> activity Intent
            intent.putExtra("title", datas?.get(position)?.meeting_title)
//            intent.putExtra("member", meeting?.member.toString())
            intent.putExtra("nickname",nickname)
//            Log.d("test", "tAdapt.....Mem...${meeting?.member}")
            context?.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return num ?:0
    }


}



    private fun convertLongToDate(time: Long?):String {

        val date = Date(time?:0)
        val format = SimpleDateFormat(
            "yyyy.MM.dd",
            Locale.getDefault()
        )

        return format.format(date)
    }