package com.gita.currencyconversorservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversorController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CurrencyExchangeServiceProxy proxy;

	@GetMapping("/currency-conversor/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversorBean convertCurrency(@PathVariable String from,
			@PathVariable String to, @PathVariable BigDecimal quantity) {
		
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		
		
		ResponseEntity<CurrencyConversorBean> responseEntity = 
				new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}/",
						CurrencyConversorBean.class, uriVariables);
		
		CurrencyConversorBean response = responseEntity.getBody();
		
		return new CurrencyConversorBean(response.getId(), from, to, 
				response.getConversionValue(), quantity, quantity.multiply(response.getConversionValue()),
				response.getPort());
	}
	
	
	@GetMapping("/currency-conversor-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversorBean convertCurrencyFeign(@PathVariable String from,
			@PathVariable String to, @PathVariable BigDecimal quantity) {
		
		CurrencyConversorBean response = proxy.retrieveExchangeValue(from, to);
		
		logger.info("{}", response);
		
		return new CurrencyConversorBean(response.getId(), from, to, 
				response.getConversionValue(), quantity, quantity.multiply(response.getConversionValue()),
				response.getPort());
	}
	
}
