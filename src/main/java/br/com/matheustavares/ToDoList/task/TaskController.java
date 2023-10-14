package br.com.matheustavares.ToDoList.task;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.matheustavares.ToDoList.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ItaskRepository taskReposiory;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        System.out.println("chegou no controller" + request.getAttribute("idUser"));
        var idUSer = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUSer);

        var currentDate = LocalDateTime.now();

        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de incio / termino deve ser maaior que a data atual");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de incio deve ser menor que a data de fim");
        }

        var task = this.taskReposiory.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request) {
        var idUSer = request.getAttribute("idUser");

        var tasks = this.taskReposiory.findByIdUser((UUID) idUSer);

        return tasks;
    }

    @PutMapping("{id}")
    public ResponseEntity updtate(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {

        var task = this.taskReposiory.findById(id).orElse( null );
        var idUser = request.getAttribute("idUser");

        if (task == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tareffa não encotrada");

        }
        if(!task.getIdUser().equals(idUser)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Usuario nao tem permissão para alterar essa tarefa");

        }
        
        Utils.copyNonNullProperties(taskModel,task);

        var taskUpdated = this.taskReposiory.save(task);


        return ResponseEntity.ok().body(taskUpdated);
    }
}
