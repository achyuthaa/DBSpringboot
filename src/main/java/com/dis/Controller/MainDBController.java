package com.dis.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainDBController {

	@RequestMapping("/home")
	public String getStarted() {
		return "Hello";
	}

}
