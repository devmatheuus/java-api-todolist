package localhost.todolist.task;

import jakarta.servlet.http.HttpServletRequest;
import localhost.todolist.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var userId = request.getAttribute("userId");
        taskModel.setUserId((UUID) userId);

        LocalDate currentDate = LocalDate.now();
        LocalDate taskStartDate = taskModel.getStartsAt().toLocalDate();
        LocalDate taskEndDate = taskModel.getEndsAt().toLocalDate();

        if (currentDate.isAfter(taskStartDate) || currentDate.isAfter(taskEndDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("StartsAt/EndsAt must be after current date");
        }

        if (taskStartDate.isAfter(taskEndDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("StartsAt must be before EndsAt");
        }


        TaskModel task = this.taskRepository.save(taskModel);


        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping
    public List<TaskModel> list(HttpServletRequest request) {
        var userId = (UUID) request.getAttribute("userId");

        return this.taskRepository.findAllByUserId(userId);

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        UUID userId = (UUID) request.getAttribute("userId");

        TaskModel task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }

        if (!task.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        Utils.copyNonNullProperties(taskModel, task);

        try {
            this.taskRepository.save(task);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }
}
