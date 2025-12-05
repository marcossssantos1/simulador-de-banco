package com.banco.ms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banco.ms.dto.ContaPfReponseDto;
import com.banco.ms.dto.ContaPfRequestDto;
import com.banco.ms.dto.ContaPfUpdateRequestDto;
import com.banco.ms.model.AccountPf;
import com.banco.ms.service.ContaPfService;


@RestController
@RequestMapping("/pf")
public class ContaPfController {
	
	@Autowired
	private ContaPfService service;
	
	@PostMapping
	public ResponseEntity<ContaPfReponseDto> create(@RequestBody ContaPfRequestDto pf){
		AccountPf accPf = service.create(service.fromDto(pf));
		return ResponseEntity.status(HttpStatus.CREATED).body(service.toDto(accPf));
	}
	
	@GetMapping
	public ResponseEntity<List<ContaPfReponseDto>> findAll(){
		List<ContaPfReponseDto> listAcc = service.findAll().stream().map(service::toDto).toList();
		return ResponseEntity.ok(listAcc);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ContaPfReponseDto> findById(@PathVariable Long id){
		 AccountPf acc = service.findById(id);
		 return ResponseEntity.ok(service.toDto(acc));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ContaPfReponseDto> update(@PathVariable Long id, @RequestBody ContaPfUpdateRequestDto pf){
		AccountPf acc = service.update(id, pf);
		return ResponseEntity.ok(service.toDto(acc));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> inativeAccount(@PathVariable Long id){
		service.inative(id);
		return ResponseEntity.noContent().build();
	}

}
