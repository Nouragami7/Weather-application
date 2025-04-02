package com.example.weatherapplication.ui.screen.notification.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapplication.R
import com.example.weatherapplication.domain.model.AlertData
import com.example.weatherapplication.ui.theme.SkyBlue
import com.example.weatherapplication.ui.screen.notification.viewmodel.AlertViewModel
import com.example.weatherapplication.ui.screen.notification.scheduleNotification
import java.util.Calendar

@Composable
fun BottomSheetContent(alertViewModel: AlertViewModel, context: Context, onDismiss: () -> Unit) {
    var selectedDate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.set_alert),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        DateTimePickerField(context,
            stringResource(R.string.select_date), selectedDate, R.drawable.calendar) {
            selectedDate = it
        }
        DateTimePickerField(context,
            stringResource(R.string.select_time), startTime, R.drawable.clock) {
            startTime = it
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = {
                    if (selectedDate.isNotEmpty() && startTime.isNotEmpty()) {
                        val dateParts = selectedDate.split("/").map { it.toInt() }
                        val timeParts = startTime.split(":").map { it.toInt() }

                        val notificationTime = Calendar.getInstance().apply {
                            set(Calendar.YEAR, dateParts[2])
                            set(Calendar.MONTH, dateParts[1] - 1)
                            set(Calendar.DAY_OF_MONTH, dateParts[0])
                            set(Calendar.HOUR_OF_DAY, timeParts[0])
                            set(Calendar.MINUTE, timeParts[1])
                            set(Calendar.SECOND, 0)
                        }.timeInMillis

                        val now = Calendar.getInstance().timeInMillis
                        val delayInMillis = notificationTime - now

                        if (delayInMillis > 0) {
                            val newAlert = AlertData(
                                startDate = selectedDate,
                                startTime = startTime
                            )

                            alertViewModel.insertAtAlerts(newAlert) { alertId ->
                                scheduleNotification(context, delayInMillis, alertId)
                                onDismiss()
                            }
                        } else {
                            Toast.makeText(context, "Please select a future time", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Please select date and time", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ){
                Text(stringResource(R.string.save), fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                Text(stringResource(R.string.cancel), fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun DateTimePickerField(
    context: Context,
    label: String,
    value: String,
    iconRes: Int,
    onValueSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(10.dp))
            .clickable {
                if (label.contains("Date")) showDatePicker(context, onValueSelected)
                else showTimePicker(context, onValueSelected)
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = if (value.isNotEmpty()) value else label,
            fontSize = 16.sp,
            color = if (value.isNotEmpty()) Color.Black else Color.Gray
        )
    }
}

fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected("$dayOfMonth/${month + 1}/$year")
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.datePicker.minDate = calendar.timeInMillis
    datePickerDialog.show()
}


fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _, hour, minute ->
            onTimeSelected("$hour:$minute")
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    ).show()
}
