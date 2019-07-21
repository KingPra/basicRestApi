package com.kingpra.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kingpra.model.Pledge;

@RestController
public class PledgeController {
	//currently all data is stored in this list, we do not have a database for this one
	//for a real rest server, we would use something like mysql or nosql.
	private List<Pledge> pledges = new ArrayList<>();
	// this gives us auto generated id's. The other method we normally use is when we use @Entity on the model with AutoGeneerated ID's
	private AtomicLong nextId = new AtomicLong();

	@GetMapping("/hello")
	public String getPledge() {
		return "hello there";
	}
	
	@PostMapping("/pledges")
	// reponseStatus gives us the proper http code when a pledge is created instead of a standard 200.
	@ResponseStatus(HttpStatus.CREATED)
	public Pledge createAPledge(@RequestBody Pledge pledge) {
		// set pledge to have next id
		pledge.setId(nextId.incrementAndGet());
		pledges.add(pledge);
		return pledge;
	}
	
	@GetMapping("/pledges")
	public List<Pledge> getPledges() {
		return pledges;
	}
	@GetMapping("/pledges/{id}")
	public Pledge getPledgeById( @PathVariable("id") Long id) {
		for (Pledge pledge:pledges) {
			if (pledge.getId() == id) {
				return pledge;
			}
		}
		throw new IllegalArgumentException();
	}
	
	@PutMapping("/pledge/{id}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public Pledge editById(
			@PathVariable("id") long id, 
			@RequestBody Pledge newPledge) {
		for (Pledge pledge:pledges) {
			if (pledge.getId()== id) {
				newPledge.setId(id);
				pledges.remove(pledge);
				pledges.add(newPledge);
				return newPledge;
			}
		}
		throw new IllegalArgumentException();
	}
	
	// we create an exception handler that puts out the correct error code. Prior to this handler, IllegalArgumentException was putting out 500-server error.
	@ResponseStatus(value = HttpStatus.BAD_REQUEST, 
			reason = "Request ID not found.")
	@ExceptionHandler(IllegalArgumentException.class)
	public void badIdExceptionHandler() {
		//nothing to do 
	}
}
