package com.happydroid.happytodo.presentation.additem.compose

import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.happydroid.happytodo.R
import com.happydroid.happytodo.presentation.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

@Preview(showSystemUi = true)
@Composable
fun AddTodoScreenPreview() {
    AddTodoScreen()
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddTodoScreen() {

    val textValue = remember { mutableStateOf("") }
    val dateSwitchState = remember { mutableStateOf(false) }
    val selectedDate = remember { mutableStateOf<Date?>(null) }
    val textPriorityNone = stringResource(id = R.string.priority_none)
    val selectedPriorityText = remember { mutableStateOf(textPriorityNone) }

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
    )
    val scope = rememberCoroutineScope()



    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = {
            BottomSheetContent(scope, sheetState, selectedPriorityText)
        })
    {
        Scaffold(content = { padding ->
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFF7F6F2)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = padding.calculateTopPadding())
                ) {
                    TopBar(textValue, selectedDate, selectedPriorityText)
                    EditText(textValue)
                    Separator()
                    PriorityTextView()
                    PriorityTextField(scope, sheetState, selectedPriorityText)
                    Separator()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Column(Modifier.weight(1f)) {
                            DueDateTextView()
                            DateTextView(dateSwitchState, selectedDate)
                        }
                        DateSwitch(
                            dateSwitchState,
                            selectedDate,

                            )
                    }
                    Separator()
                    DeleteButton()
                }
            }
        })
    }
}


@Composable
fun TopBar(
    textValue: MutableState<String>,
    selectedDate: MutableState<Date?>,
    selectedPriorityText: MutableState<String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CloseButton()
        SaveButton(textValue, selectedDate, selectedPriorityText)

    }
}

@Composable
fun CloseButton() {
    val imageSize = 24.dp
    val context = LocalContext.current
    Image(
        painter = painterResource(id = R.drawable.ic_close),
        contentDescription = null,
        modifier = Modifier
            .size(imageSize)
            .padding(vertical = 5.dp)
            .clickable {
                (context as MainActivity).supportFragmentManager.popBackStack()
            },

        contentScale = ContentScale.Fit
    )
}

@Composable
fun SaveButton(
    textValue: MutableState<String>,
    selectedDate: MutableState<Date?>,
    selectedPriorityText: MutableState<String>
) {
    val context = LocalContext.current
    val message = stringResource(id = R.string.alert_vide_text)
    Text(
        text = stringResource(id = R.string.button_save),
        modifier = Modifier
            .padding(vertical = 5.dp)
            .clickable {
                if (textValue.value.isNullOrEmpty() || textValue.value.trim() == "") {
                    Toast
                        .makeText(
                            (context as MainActivity),
                            message,
                            Toast.LENGTH_SHORT
                        )
                        .show()
                } else {

//                    addTodoViewModel.addOrUpdateTodoItem(
//                        oldId,
//                        textValue.value,
//                        TodoItem.Priority.values()[1],
//                        deadline
//                    )

                    (context as MainActivity).supportFragmentManager.popBackStack()
                }


            },
        color = Color.Blue,
        fontSize = 19.sp
    )
}

@Composable
fun EditText(textValue: MutableState<String>) {

    val hint = stringResource(id = R.string.hint_what_todo)
    OutlinedTextField(
        value = textValue.value,
        onValueChange = { textValue.value = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .wrapContentHeight()
            .background(Color.White),
        textStyle = LocalTextStyle.current.copy(fontSize = 19.sp),
        maxLines = 6,
        minLines = 3,
        singleLine = false,
        placeholder = { Text(text = hint) }
    )
}

@Composable
fun Separator() {
    Divider(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 20.dp),
        color = Color.Gray,
        thickness = 1.dp
    )
}

@Composable
fun PriorityTextView() {
    Text(
        text = stringResource(id = R.string.text_priority),
        modifier = Modifier
            .padding(start = 20.dp, top = 5.dp),
        color = Color.Black,
        fontSize = 19.sp
    )
}

@Composable
fun PrioritySpinnerOld() {
    val context = LocalContext.current
    val priorityEntries = stringArrayResource(id = R.array.priority_entries)

    AndroidView(
        factory = { context ->
            Spinner(context).apply {
                adapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_spinner_item,
                    priorityEntries
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            }
        },
        modifier = Modifier.padding(start = 20.dp)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PriorityTextField(
    scope: CoroutineScope,
    sheetState: ModalBottomSheetState,
    selectedText: MutableState<String>
) {

    Text(
        text = selectedText.value,
        modifier = Modifier
            .padding(start = 25.dp, top = 5.dp)
            .clickable(
                onClick = {
                    scope.launch {
                        sheetState.show()
                    }
                }
            )
            .focusable(true),

        color = Color.Blue,
        fontSize = 16.sp,

        )
}


@Preview
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetContent(
    scope: CoroutineScope = rememberCoroutineScope(),
    sheetState: ModalBottomSheetState = ModalBottomSheetState(
        ModalBottomSheetValue.Expanded
    ),
    selectedText: MutableState<String> = mutableStateOf("нет")
) {
    val textPriorityNone = stringResource(R.string.priority_none)
    val textPriorityHigh = stringResource(R.string.priority_high)
    val textPriorityLow = stringResource(R.string.priority_low)
    Surface(
        modifier = Modifier.height(170.dp),
        color = Color(0xFFF7F6F2)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedText.value = textPriorityNone
                        scope.launch {
                            sheetState.hide()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = textPriorityNone,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(10.dp),
                    color = Color.Black
                )
            }
            Divider(
                modifier = Modifier.padding(5.dp),
                color = Color.Black
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedText.value = textPriorityHigh
                        scope.launch {
                            sheetState.hide()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = textPriorityHigh,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(10.dp),
                    color = Color.Black
                )
            }
            Divider(
                modifier = Modifier.padding(5.dp),
                color = Color.Black
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedText.value = textPriorityLow
                        scope.launch {
                            sheetState.hide()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = textPriorityLow,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(10.dp),
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun DueDateTextView() {
    Text(
        text = stringResource(id = R.string.due_date),
        modifier = Modifier
            .padding(start = 15.dp, top = 15.dp),
        color = Color.Black,
        fontSize = 19.sp
    )
}

@Composable
fun DateSwitch(
    dateSwitchState: MutableState<Boolean>,
    selectedDate: MutableState<Date?>
) {
    Switch(
        checked = dateSwitchState.value,
        onCheckedChange = { isChecked ->
            dateSwitchState.value = isChecked
            if (!isChecked) {
                selectedDate.value = null
            }
        },
        modifier = Modifier
            .padding(top = 15.dp, end = 15.dp),
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color(0xFF007AFF)
        )
    )
}

@Composable
fun DateTextView(
    dateSwitchState: MutableState<Boolean>,
    selectedDate: MutableState<Date?>

) {
    val chooseDateText = stringResource(id = R.string.text_choose_date)

    val datePickerDialog = remember {
        MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(CalendarConstraints.Builder().build())
            .build()
    }
    datePickerDialog.addOnPositiveButtonClickListener { selectedTimestamp ->
        val pickedDate = Date(selectedTimestamp)
        selectedDate.value = pickedDate
        dateSwitchState.value = true
    }


    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Log.i("hhh", dateSwitchState.value.toString() + selectedDate?.toString())

    if (dateSwitchState.value && selectedDate.value == null) {
        Log.i("hhh", dateSwitchState.value.toString() + " " + selectedDate?.toString())

        datePickerDialog.show(
            (context as MainActivity).supportFragmentManager, null
        )
    }

    Text(
        text = if (selectedDate.value != null) {
            DateFormat.getDateInstance(DateFormat.MEDIUM).format(selectedDate.value!!)
        } else {
            chooseDateText
        },
        modifier = Modifier
            .padding(start = 20.dp, top = 5.dp)
            .clickable {
                scope.launch {
                    datePickerDialog.show(
                        (context as MainActivity).supportFragmentManager, null
                    )
                }

            }

            .focusable(true),
        color = Color.Blue,
        fontSize = 16.sp,
    )
}


@Composable
fun DeleteButton() {
    val deleteButtonText = stringResource(id = R.string.text_delete)
    val isEnabled = false
    Button(
        onClick = { /* TODO:  */ },
        modifier = Modifier
            .padding(horizontal = 0.dp, vertical = 0.dp)
            .background(Color.Transparent),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent
        ),
        elevation = ButtonDefaults.elevation(0.dp),
        contentPadding = PaddingValues(10.dp),
        shape = RoundedCornerShape(0.dp),
        enabled = isEnabled
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(22.dp),
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = null, // Provide a content description if needed
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = deleteButtonText,
                color = Color.Gray,
                fontSize = 18.sp
            )
        }
    }


}
