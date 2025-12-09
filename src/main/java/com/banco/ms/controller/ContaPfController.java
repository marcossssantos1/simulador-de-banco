package com.banco.ms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banco.ms.dto.ContaPfReponseDto;
import com.banco.ms.dto.ContaPfRequestDto;
import com.banco.ms.dto.ContaPfUpdateRequestDto;
import com.banco.ms.dto.PixKeyRequestDto;
import com.banco.ms.dto.TransactionFilterDto;
import com.banco.ms.dto.TransactionRequestDto;
import com.banco.ms.dto.TransactionResponseDto;
import com.banco.ms.dto.TransferRequestDto;
import com.banco.ms.model.AccountPf;
import com.banco.ms.service.ContaPfService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/pf")
public class ContaPfController {
	
	@Autowired
	private ContaPfService service;
	
	@PostMapping("/criar-conta")
	public ResponseEntity<ContaPfReponseDto> create(@RequestBody @Valid ContaPfRequestDto pf){
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
	public ResponseEntity<ContaPfReponseDto> update(@PathVariable Long id, @RequestBody @Valid ContaPfUpdateRequestDto pf){
		AccountPf acc = service.update(id, pf);
		return ResponseEntity.ok(service.toDto(acc));
	}
	
	@DeleteMapping("/{id}/inativar")
	public ResponseEntity<Void> inativeAccount(@PathVariable Long id){
		service.inative(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/encerrar/{id}")
	public ResponseEntity<Void> deleteAccount(@PathVariable Long id){
		service.deleteAccount(id);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/{id}/depositar")
	public ResponseEntity<ContaPfReponseDto> deposit(@PathVariable Long id, @RequestBody @Valid TransactionRequestDto dto){
		return ResponseEntity.ok(service.deposit(id, dto));
	}
	
	@PostMapping("/{id}/sacar")
	public ResponseEntity<ContaPfReponseDto> sacar(@PathVariable Long id, 
	                                          @RequestBody @Valid TransactionRequestDto dto) {
	    return ResponseEntity.ok(service.withdraw(id, dto));
	}
	
	@GetMapping("/{id}/historico-geral")
	public ResponseEntity<Page<TransactionResponseDto>> history(
			@PathVariable Long id,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size){
		Page<TransactionResponseDto> result = service.getHistory(id,page,size);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/{id}/historico-filtrado")
	public ResponseEntity<Page<TransactionResponseDto>> getHistoryFiltered(
			@PathVariable Long id,
			@Valid @RequestBody TransactionFilterDto filter,
			@PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable
	) {
		Page<TransactionResponseDto> result = service.getHistoryFiltered(id,filter,pageable);
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/transferencia")
	public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequestDto dto){
		service.transfer(dto);
		return ResponseEntity.ok().build();
	}
	
	@PutMapping("/{id}/cadastrar-pix")
	public ResponseEntity<String> registerPixKey(@PathVariable Long id, @RequestBody @Valid PixKeyRequestDto dto){
		service.registerPixKey(id, dto);
		return ResponseEntity.ok("Chave PIX cadastrada com sucesso.");
	}


}
