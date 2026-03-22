package com.example.myapplication.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.TodoPriority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoBottomSheet(
    onDismissRequest: () -> Unit,
    onSaveTodo: (title: String, description: String, priority: TodoPriority) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var priority by remember { mutableStateOf(TodoPriority.MEDIUM) }

    val isSaveEnabled = title.text.isNotBlank()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "새 할 일 추가",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("제목 (필수)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("설명 (선택)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "우선순위",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TodoPriority.values().forEach { p ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(p.name) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismissRequest) {
                    Text("취소")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (isSaveEnabled) {
                            onSaveTodo(title.text, description.text, priority)
                            onDismissRequest()
                        }
                    },
                    enabled = isSaveEnabled
                ) {
                    Text("저장")
                }
            }
        }
    }
}
