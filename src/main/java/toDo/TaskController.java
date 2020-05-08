package toDo;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


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

    // Создать новое задание
    @PostMapping("/tasks")
    @ResponseBody
    @ResponseStatus( HttpStatus.CREATED)
    public Task postTask(@RequestBody Task newTask) throws InternalServerExeption {
        // Если отсутствуют обязательные поля
        if (newTask.getName() == null ||
                newTask.getStatus() == null)
            throw new BadRequestExeption();

        return repository.save(newTask);
    }

    // Получить задание по id
    @GetMapping("/tasks/{id}")
    public Task getTaskId(@PathVariable Long id) throws InternalServerExeption {
        return repository.findById(id)
                .orElseThrow(ThereIsNoSuchTaskException::new);
    }



    private Task putToRepository(Task el, Task newTask) {

        el.setName(
                newTask.getName());
        el.setStatus(
                newTask.getStatus());

        return repository.save(newTask);
    }

    // Изменить существующую запись
    // Или добавить
    @PutMapping("/tasks/{id}")
    public Task putTask(@RequestBody Task newTask, @PathVariable Long id) throws InternalServerExeption {

        return repository.findById(id)
                .map(el -> putToRepository(el,newTask))
                .orElseGet(() -> {
                    newTask.setId(id);
                    return repository.save(newTask);
                });
    }

    // Удалить запись
    @DeleteMapping("/tasks/{id}")
    void deleteTask(@PathVariable Long id) throws InternalServerExeption {
        repository.deleteById(id);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Check params")
    public class BadRequestExeption  extends RuntimeException {}
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public class InternalServerExeption extends RuntimeException {}
    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "There is no such task")
    public class ThereIsNoSuchTaskException extends RuntimeException {}

}
