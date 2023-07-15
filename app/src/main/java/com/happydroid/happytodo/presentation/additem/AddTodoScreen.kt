package com.happydroid.happytodo.presentation.additem

import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.happydroid.happytodo.R

@Composable
fun AddTodoScreen() {
    val (dateSwitchState, setDateSwitchState) = remember { mutableStateOf(false) }
    Surface {
        CloseButton()
        SaveButton()
        EditText()
        SeparatorView()

        PriorityTextView()
        PrioritySpinner()
        SeparatorView()

        DueDateTextView()
        DateTextView(isVisible = true)
        DateSwitch(
            isChecked = dateSwitchState,
            onCheckedChange = { isChecked -> setDateSwitchState(isChecked) }
        )
        SeparatorView()
        DeleteButton()

    }
}

@Composable
fun CloseButton() {
    val imageSize = 16.dp
    Image(
        painter = painterResource(id = R.drawable.ic_close),
        contentDescription = null,
        modifier = Modifier
            .size(imageSize)
            .padding(start = 20.dp, top = 21.dp),
        colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun SaveButton() {
    Text(
        text = stringResource(id = R.string.button_save),
        modifier = Modifier
            .padding(vertical = 10.dp)
            .offset(x = (-20).dp, y = 5.dp),
        color = Color.Blue,
        fontSize = 19.sp
    )
}

@Composable
fun EditText() {
    val hint = stringResource(id = R.string.hint_what_todo)
    TextField(
        value = "", // TODO:
        onValueChange = { /* TODO:  */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, top = 20.dp, end = 15.dp)
            .padding(20.dp),
        textStyle = LocalTextStyle.current.copy(fontSize = 19.sp),
        maxLines = 10,
        minLines = 3,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text,
            autoCorrect = true

        ),
        singleLine = false,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        placeholder = { Text(text = hint) }
    )
}

@Composable
fun SeparatorView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(start = 15.dp, top = 20.dp, end = 15.dp)
            .background(color = Color.Gray) // Update with desired color
    )
}

@Composable
fun PriorityTextView() {
    Text(
        text = stringResource(id = R.string.text_priority),
        modifier = Modifier
            .padding(start = 15.dp, top = 15.dp),
        color = Color.Black,
        fontSize = 19.sp
    )
}

@Composable
fun PrioritySpinner() {
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
        modifier = Modifier.padding(start = 15.dp)
    )
}

@Composable
fun DueDateTextView() {
    Text(
        text = stringResource(id = R.string.due_date),
        modifier = Modifier
            .padding(start = 15.dp, top = 15.dp),
        color = MaterialTheme.colors.primary,
        fontSize = 19.sp
    )
}

@Composable
fun DateSwitch(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        modifier = Modifier
            .padding(top = 15.dp, end = 15.dp)
    )
}

@Composable
fun DateTextView(isVisible: Boolean) {
    val chooseDateText = stringResource(id = R.string.text_choose_date)

    Text(
        text = chooseDateText,
        modifier = Modifier
            .padding(start = 15.dp)
            .clickable { /* TODO:  */ }
            .focusable(true),
        color = Color.Blue,
        fontSize = 14.sp,
    )
}

@Composable
fun DeleteButton() {
    val deleteButtonText = stringResource(id = R.string.text_delete)

    Button(
        onClick = { /* TODO: Handle button click */ },
        modifier = Modifier
            .padding(horizontal = 30.dp, vertical = 30.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = null, // Provide a content description if needed
                modifier = Modifier.size(24.dp),
                tint = Color.Red
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = deleteButtonText,
                color = Color.Red,
                fontSize = 19.sp
            )
        }
    }


}
