package com.example.travelrequestform.ui.addTravelActivity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation
import com.example.travelrequestform.R
import com.example.travelrequestform.data.models.Travel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.button.MaterialButton
import com.example.travelrequestform.data.models.Travel.UserLocation
import java.text.DateFormat
import java.util.*
import java.util.Calendar.DATE


const val ADDRESS_CODE = 1
const val LOCATION_CODE1 = 2
const val LOCATION_CODE2 = 3
const val LOCATION_CODE3 = 4

class AddTravelActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var travelViewModel: TravelViewModel

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etMail: EditText
    private lateinit var etAddress: EditText
    private lateinit var etDestination1: EditText
    private lateinit var numOfTravelers: Spinner
    private lateinit var etTravelDate: EditText
    private lateinit var etArrivalDate: EditText
    private lateinit var btnSend: MaterialButton
    private lateinit var etDestination2: EditText
    private lateinit var etDestination3: EditText

    private lateinit var travelDate: Date
    private lateinit var arrivalDate: Date
    private lateinit var addressPlace: Place
    private lateinit var destinationPlace1: Place
    private lateinit var destinationPlace2: Place
    private lateinit var destinationPlace3: Place
    private lateinit var awesomeValidation: AwesomeValidation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_travel)

        Places.initialize(applicationContext, "AIzaSyD10IpMmv30oFrH1iwNvsXkjFx7ZuKCSck")
        initializeViews()
        setValidation()
        setOnClickListeners()
        initializeSpinner()


        travelViewModel = ViewModelProviders.of(this).get(TravelViewModel::class.java)
        travelViewModel.getIsSuccess().observe(this, { isSuccess ->
            if (isSuccess) {
                setToast()
                clearViews()
            } else
                Toast.makeText(
                    applicationContext, "Failed to register please try again",
                    Toast.LENGTH_LONG
                ).show()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {

            when (requestCode) {
                ADDRESS_CODE -> {
                    // initialize place
                    addressPlace = Autocomplete.getPlaceFromIntent(data as Intent)
                    // set address on EditText
                    etAddress.setText(addressPlace.address)
                }
                LOCATION_CODE1 -> {
                    // initialize place
                    destinationPlace1 = Autocomplete.getPlaceFromIntent(data as Intent)
                    // set address on EditText
                    etDestination1.setText(destinationPlace1.address)
                }
                LOCATION_CODE2 -> {
                    // initialize place
                    destinationPlace2 = Autocomplete.getPlaceFromIntent(data as Intent)
                    // set address on EditText
                    etDestination2.setText(destinationPlace2.address)
                }
                LOCATION_CODE3 -> {
                    // initialize place
                    destinationPlace3 = Autocomplete.getPlaceFromIntent(data as Intent)
                    // set address on EditText
                    etDestination3.setText(destinationPlace3.address)
                }
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // initialize status
            val status = Autocomplete.getStatusFromIntent(data as Intent)
            // Display toast
            Toast.makeText(
                applicationContext, status.statusMessage,
                Toast.LENGTH_LONG
            ).show()
        }
    }


    private fun initializeViews() {
        etName = findViewById(R.id.et_name)
        etPhone = findViewById(R.id.et_phone)
        etMail = findViewById(R.id.et_mail)
        etAddress = findViewById(R.id.et_address)
        etAddress.inputType = InputType.TYPE_NULL
        etDestination1 = findViewById(R.id.et_destination1)
        etDestination1.inputType = InputType.TYPE_NULL
        etDestination2 = findViewById(R.id.et_destination2)
        etDestination2.inputType = InputType.TYPE_NULL
        etDestination3 = findViewById(R.id.et_destination3)
        etDestination3.inputType = InputType.TYPE_NULL
        numOfTravelers = findViewById(R.id.num_of_travelers)
        etTravelDate = findViewById(R.id.et_travelDate)
        etTravelDate.inputType = InputType.TYPE_NULL
        etArrivalDate = findViewById(R.id.et_returnDate)
        etArrivalDate.inputType = InputType.TYPE_NULL
        btnSend = findViewById(R.id.btn_send)
    }

    private fun onClickPlace(v: View) {

        // initialize place filed list
        val fieldList: List<Place.Field> = arrayListOf(
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG, Place.Field.NAME
        )
        // Create intent
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fieldList
        ).build(this@AddTravelActivity)
        //start activity result
        when (v) {
            etAddress -> startActivityForResult(intent, ADDRESS_CODE)
            etDestination1 -> startActivityForResult(intent, LOCATION_CODE1)
            etDestination2 -> startActivityForResult(intent, LOCATION_CODE2)
            etDestination3 -> startActivityForResult(intent, LOCATION_CODE3)
        }
    }


    private fun setOnClickListeners() {
        etAddress.setOnClickListener(this)
        etDestination1.setOnClickListener(this)
        etDestination2.setOnClickListener(this)
        etDestination3.setOnClickListener(this)
        etTravelDate.setOnClickListener(this)
        etArrivalDate.setOnClickListener(this)
        btnSend.setOnClickListener(this)
    }

    private fun initializeSpinner() {
        val items: MutableList<Int> = arrayListOf()
        for (i in 1..50) {
            items.add(i)
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        numOfTravelers.adapter = adapter
    }

    private fun onClickDate(view: View) {

        val cldr: Calendar = Calendar.getInstance()
        val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
        val month: Int = cldr.get(Calendar.MONTH)
        val year: Int = cldr.get(Calendar.YEAR)

        when (view) {
            // date picker dialog
            etTravelDate -> {
                val picker = DatePickerDialog(
                    this@AddTravelActivity, { view, year, monthOfYear, dayOfMonth ->
                        etTravelDate.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                        travelDate = GregorianCalendar(
                            year + 1900,
                            monthOfYear,
                            dayOfMonth
                        ).time
                    },
                    year,
                    month,
                    day
                )
                picker.datePicker.minDate = System.currentTimeMillis() - 1000
                picker.show()
            }
            etArrivalDate -> {
                val picker = DatePickerDialog(
                    this@AddTravelActivity, { view, year, monthOfYear, dayOfMonth ->
                        etArrivalDate.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                        arrivalDate = GregorianCalendar(
                            year + 1900,
                            monthOfYear,
                            dayOfMonth
                        ).time
                    },
                    year,
                    month,
                    day
                )
                picker.datePicker.minDate = System.currentTimeMillis() - 1000
                picker.show()
            }
        }

    }

    override fun onClick(v: View?) {
        when (v) {
            etAddress -> onClickPlace(v)
            etDestination1 -> onClickPlace(v)
            etDestination2 -> onClickPlace(v)
            etDestination3 -> onClickPlace(v)
            etTravelDate -> onClickDate(v)
            etArrivalDate -> onClickDate(v)
            btnSend -> clickSend()
        }
    }

    private fun setValidation() {
        awesomeValidation = AwesomeValidation(ValidationStyle.BASIC)
        awesomeValidation.addValidation(
            this, R.id.et_name,
            RegexTemplate.NOT_EMPTY, R.string.invalid_name
        )
        awesomeValidation.addValidation(
            this,
            R.id.et_phone,
            "^\\+?(972|0)(\\-)?0?(([23489]{1}\\d{7})|[5]{1}\\d{8})\$",
            R.string.invalid_phone
        )
        awesomeValidation.addValidation(
            this, R.id.et_mail,
            Patterns.EMAIL_ADDRESS, R.string.invalid_email
        )
        awesomeValidation.addValidation(etAddress, SimpleCustomValidation {
            return@SimpleCustomValidation it.isNotEmpty()
        }, "אנא בחר כתובת יציאה וחזרה")
        awesomeValidation.addValidation(
            etDestination1, SimpleCustomValidation {
                return@SimpleCustomValidation it.isNotEmpty()
            }, "אנא בחר כתובת יעד"
        )
        awesomeValidation.addValidation(etTravelDate, SimpleCustomValidation {
            etArrivalDate.text.isNotEmpty() && etTravelDate.text.isNotEmpty() && !travelDate.after(
                arrivalDate
            )
        }, "התאריך חזרה חייב להיות מאוחר מתאריך היציאה")

        awesomeValidation.addValidation(etArrivalDate, SimpleCustomValidation {
            etArrivalDate.text.isNotEmpty() && etTravelDate.text.isNotEmpty() && !travelDate.after(
                arrivalDate
            )
        }, "התאריך חזרה חייב להיות מאוחר מתאריך היציאה")
    }

    private fun clickSend() {
        awesomeValidation.clear()
        if (awesomeValidation.validate()) {
            val travel = Travel()
            travel.arrivalDate = arrivalDate
            travel.clientEmail = etMail.text.toString()
            travel.clientName = etName.text.toString()
            travel.clientPhone = etPhone.text.toString()
            travel.company = HashMap()
            travel.requestType = Travel.RequestType.SENT
            travel.travelDate = travelDate
            travel.numOfTravelers = numOfTravelers.selectedItem as Int
            travel.travelLocations.add(UserLocation(destinationPlace1))
            if (etDestination2.text.isNotEmpty())
                travel.travelLocations.add(UserLocation(destinationPlace2))
            if (etDestination3.text.isNotEmpty())
                travel.travelLocations.add(UserLocation(destinationPlace3))

            travel.address = UserLocation(addressPlace)

            travelViewModel.addTravel(travel)
        }
    }

    private fun setToast() {
        val layout = layoutInflater.inflate(
            R.layout.custom_toast,
            findViewById(R.id.cl_customToastContainer)
        )

        Toast(this).apply {
            setGravity(Gravity.BOTTOM, 0, 40)
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }

    private fun clearViews() {
        etName.text.clear()
        etMail.text.clear()
        etPhone.text.clear()
        etAddress.text.clear()
        etTravelDate.text.clear()
        etDestination1.text.clear()
        etDestination2.text.clear()
        etDestination3.text.clear()
        etTravelDate.text.clear()
        etArrivalDate.text.clear()
        numOfTravelers.setSelection(0)
    }
}

