package com.example.demo.Controller;

import com.example.demo.Entity.Condition;
import com.example.demo.Entity.ConditionDTO;
import com.example.demo.Entity.Elder;
import com.example.demo.repository.ConditionRepository;
import com.example.demo.repository.Elderrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Condition")
public class ConditionController {
    private int n;
    @Autowired
    ConditionRepository conditionRepository;

    @Autowired
    Elderrepository elderrepository;

    @PostMapping("/create")
    public ResponseEntity<Condition> createcondition(@RequestBody ConditionDTO conditionDTO) {

        String state = "normal";
        if (conditionDTO.getHeartrhythm() < 60 || conditionDTO.getHeartrhythm() > 100) {
            state = "abnormal";
            n += 10;
        } else if (n != 0) {
            state = "normal but have precondition";
        }
        Long elderId = conditionDTO.getElderID();
        Optional<Elder> _elder = elderrepository.findById(elderId);
        Condition condition = conditionRepository.save(new Condition(conditionDTO.getHeartrhythm(),
                conditionDTO.getBloody_oxy(), conditionDTO.getLonggps(), conditionDTO.getLatigps(), state, _elder.get()));
        return new ResponseEntity<>(condition, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public List<Condition> get_condition(@PathVariable("id") long id) {
        Optional<Elder> elder_data = elderrepository.findById(id);
        List<Condition> condition_data;
        if (elder_data.isPresent()) {
            condition_data = conditionRepository.findByElder(elder_data.get());
            System.out.println(condition_data.toString());
        } else {
            condition_data = new ArrayList<Condition>();
        }
        return condition_data;
    }

    @PutMapping("/accept/{id}")
    public ResponseEntity<Condition> changecondition(@PathVariable("id") long id) {
        Optional<Condition> condition = conditionRepository.findById(id);
        condition.get().setNotify_accept(true);
        conditionRepository.save(condition.get());
        n = 0;
        return new ResponseEntity<>(condition.get(), HttpStatus.OK);
    }
}

