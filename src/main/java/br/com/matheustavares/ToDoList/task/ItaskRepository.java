package br.com.matheustavares.ToDoList.task;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ItaskRepository extends JpaRepository<TaskModel, UUID>{
    
    List<TaskModel> findByIdUser(UUID idUser);
    
}
