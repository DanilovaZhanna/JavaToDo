package toDo;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

    // Хранилище задач
    private final Repository repository;

    public TaskController(Repository repository) {
        this.repository = repository;
    }

    // Получить все задания
    @GetMapping("/tasks")
    public List<Task> getTasks() {
        return (List<Task>) repository.findAll();
    }




}
