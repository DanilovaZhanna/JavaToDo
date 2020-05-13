package toDo;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
public class TaskController {

    // Хранилище задач
    private final Repository repository;

    public TaskController(Repository repository) {
        this.repository = repository;
    }

    //    GET /tasks
    //    Ответ (в формате JSON):
    //    HTTP 200
    //    Возвращает все таски
    @GetMapping("/tasks")
    public List<Task> getTasks() {
        return (List<Task>) repository.findAll();
    }

    // GET /tasks/{id} (например, /tasks/3)
    // Ответ (в формате JSON):
    // HTTP 200
    // Получить задание по id
    @GetMapping("/tasks/{id}")
    public Task getTaskId(@PathVariable Long id) throws InternalServerExeption {
        return repository.findById(id)
                .orElseThrow(ThereIsNoSuchTaskException::new);
    }

    // POST /tasks
    // Запрос (в формате JSON):
    // {
    //    "name": "Реализовать веб-сервис",
    //        "status": "PENDING"
    // }
    // Ответ (в формате JSON):
    // HTTP 201 и вернуть созданный json объект
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

    private Task putToRepository(Task el, Task newTask) {
        el.setName(
                newTask.getName());
        el.setStatus(
                newTask.getStatus());
        return repository.save(el);
    }

    //  PUT /tasks/{id}
    //  Запрос (в формате JSON):
    // {
    // "name": "Реализовать веб-сервис",
    // "status": "IN_PROGRESS"
    // }
    //  Ответ (в формате JSON):
    //  HTTP 204
    // Изменить существующую запись
    @PutMapping("/tasks/{id}")
    @ResponseStatus( HttpStatus.NO_CONTENT)
    public Task putTask(@RequestBody Task newTask, @PathVariable Long id) throws InternalServerExeption {
        return repository.findById(id)
                .map(el -> putToRepository(el,newTask))
                .orElseThrow(ThereIsNoSuchTaskException::new);
    }

    // Удалить запись
    //DELETE /tasks/{id}
    //Ответ (в формате JSON):
    //HTTP 204
    @DeleteMapping("/tasks/{id}")
    @ResponseStatus( HttpStatus.NO_CONTENT)
    void deleteTask(@PathVariable Long id) throws InternalServerExeption {
        repository.deleteById(id);
    }

    // 400 BAD REQUEST - означает ошибку, связанную с содержимым
    //запроса, например, валидация данных (отсутствие обязательных
    //полей)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Check params")
    public class BadRequestExeption  extends RuntimeException {}
    // Внутренняя ошибка сервера 500
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public class InternalServerExeption extends RuntimeException {}
    // Не найдено 404
    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "There is no such task")
    public class ThereIsNoSuchTaskException extends RuntimeException {}

}
