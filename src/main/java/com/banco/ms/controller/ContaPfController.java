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

import com.banco.ms.model.AccountPf;
import com.banco.ms.service.ContaPfService;


@RestController
@RequestMapping("/pf")
public class ContaPfController {
	
	@Autowired
	private ContaPfService service;
	
	@PostMapping
	public ResponseEntity<AccountPf> create(@RequestBody AccountPf pf){
		AccountPf accPf = service.create(pf);
		return ResponseEntity.status(HttpStatus.CREATED).body(accPf);
	}
	
	@GetMapping
	public ResponseEntity<List<AccountPf>> findAll(){
		List<AccountPf> accPf = service.findAll();
		return ResponseEntity.ok(accPf);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<AccountPf> findById(@PathVariable Long id){
		 AccountPf acc = service.findById(id);
		 return ResponseEntity.ok(acc);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<AccountPf> update(@PathVariable Long id, @RequestBody AccountPf pf){
		AccountPf acc = service.update(id, pf);
		return ResponseEntity.ok(acc);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> inativeAccount(@PathVariable Long id){
		service.inative(id);
		return ResponseEntity.noContent().build();
	}

}
