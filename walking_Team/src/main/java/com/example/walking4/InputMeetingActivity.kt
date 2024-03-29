package com.example.walking4

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import com.example.walking4.databinding.ActivityInputMeetingBinding
import com.example.walking4.databinding.FragmentMeetingBinding
import com.example.walking4.fragment.MainFragment
import com.example.walking4.fragment.MeetingFragment
import com.example.walking4.model.Meeting
import com.example.walking4.model.User
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class InputMeetingActivity : AppCompatActivity() {

    val currentTime : Long = System.currentTimeMillis()

    lateinit var binding: ActivityInputMeetingBinding


    private var TAG: String = "InputTripActivity"
    lateinit var username: String
    lateinit var nickname: String


    var str:List<String> = convertLongToDate(currentTime).split(".")
    var startyear = str[0].toInt()
    var startmonth = str[1].toInt()-1
    var startday = str[2].toInt()
    var endyear = str[0].toInt()
    var endmonth = str[1].toInt()-1
    var endday = str[2].toInt()



    var meetingTitle:String = ""
    var meetingPlace:String = ""
    var meetingContent:String = ""



    var strDate:String =""
    var endDate:String =""
    var date:Long=0L
    val style = AlertDialog.THEME_HOLO_LIGHT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputMeetingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val database = Firebase.database
        val myRef = database.getReference("username")
        nickname = ""
        myRef.get().addOnCompleteListener {
            username = it.result.value.toString()

            Log.d(TAG, "1 InputTripActivity 파이어베이스에서 가져온 username : $username")

            val networkService = (applicationContext as MyApplication).networkService
            var oneUserCall = networkService.doGetOneUser(username)
            oneUserCall.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    nickname = response.body()?.nickname.toString()
                    Log.d(TAG, "2  response.body()?.nickname.toString() 스프링에서 가져온 이름: $nickname")
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    call.cancel()
                }

            })
        }
        Log.d(TAG, "input=======================$nickname")

//        Log.d(TAG,"input=======================$memberName")

        val intent = intent





        binding.startdate.setOnClickListener {
            if(binding.startdate.text != ""){
                var startstr:List<String> = binding.startdate.text.split(".")
                startyear = startstr[0].toInt()
                startmonth = startstr[1].toInt()-1
                startday = startstr[2].toInt()
            }

            DatePickerDialog(this, style, object: DatePickerDialog.OnDateSetListener {

                override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

                    var month = month+1
                    strDate = makeDateString(dayOfMonth, month, year).toString()
                    binding.startdate.text = strDate


                }
            }, startyear, startmonth, startday).show()
    }


        binding.enddate.setOnClickListener {
            if(binding.enddate.text != ""){
                var endstr:List<String> = binding.enddate.text.split(".")
                endyear = endstr[0].toInt()
                endmonth = endstr[1].toInt()-1
                endday = endstr[2].toInt()
            }

            DatePickerDialog(this, style, object: DatePickerDialog.OnDateSetListener {

                override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

                    var month = month+1
                    endDate = makeDateString(dayOfMonth, month, year).toString()
                    binding.enddate.text = endDate


                }
            }, endyear, endmonth, endday).show()
        }

        binding.calendarbutton.setOnClickListener {

            showDateRangePicker()

        }










        binding.submitbutton.setOnClickListener {
            intent.putExtra("startDate",strDate)
            intent.putExtra("endDate",endDate)

            meetingTitle=binding.editTitle.text.toString()
            meetingPlace=binding.editPlace.text.toString()
            meetingContent=binding.editcontent.text.toString()
            intent.putExtra("title",meetingTitle)
            intent.putExtra("place",meetingPlace)
            intent.putExtra("content",meetingContent)

            var meeting = Meeting(meetingTitle, meetingTitle, strDate, endDate, username, meetingPlace)

            val networkService = (applicationContext as MyApplication).networkService
            val meetingInsertCall = networkService.doInsertMeeting(meeting)
            Log.d(TAG,"================================${meeting}")
            meetingInsertCall.enqueue(object: Callback<Meeting> {
                override fun onResponse(call: Call<Meeting>, response: Response<Meeting>) {
                    if(response.isSuccessful) {
                        Log.d(TAG,"================================${response}")
                        val intent = Intent(this@InputMeetingActivity, MeetingFragment::class.java)
                        val sharePref = getSharedPreferences("inputPref", Context.MODE_PRIVATE)

                        sharePref.edit().run {
                            putInt("input", 1)
                            commit()
                        }

                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<Meeting>, t: Throwable) {
                    call.cancel()
                }
            })
        }

        binding.cancelbutton.setOnClickListener {
            finish()
        }















    }





    private fun makeDateString(dayOfMonth: Int, month: Int, year: Int): Any {

        return year.toString()+"."+month.toString()+"."+dayOfMonth.toString()
    }
    private fun convertLongToDate(time: Long):String {

        val date = Date(time)
        val format = SimpleDateFormat(
            "yyyy.MM.dd",
            Locale.getDefault()
        )

        return format.format(date)
    }

    private fun showDateRangePicker() {
        val dateRangePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setTitleText("Select Date")
            .build()

        dateRangePicker.show(
            supportFragmentManager,
            "date_range_picker"
        )

        dateRangePicker.addOnPositiveButtonClickListener { datePicked->

            val start = datePicked.first
            val end = datePicked.second

//            Toast.makeText(this,"$startDate $endDate", Toast.LENGTH_SHORT).show()

            strDate = convertLongToDate(start)
            endDate = convertLongToDate(end)

            binding.startdate.text = strDate
            binding.enddate.text = endDate

        }
    }







}