package com.arslan_aziz.food_for_thought.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arslan_aziz.food_for_thought.service.NlpExtractionService;
import com.arslan_aziz.food_for_thought.service.QueryHandler;
import com.arslan_aziz.food_for_thought.service.dto.NlpExtractionDto;

@CrossOrigin(origins="*", allowedHeaders="*")
@RestController
public class QueryController {
	
	private NlpExtractionService nlpExtractionService;
	
	@Autowired
	public QueryController(QueryHandler queryHandler, NlpExtractionService nlpExtractionService) {
		this.nlpExtractionService = nlpExtractionService;
	}
	
	@PostMapping(value="/nlpextraction", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> requestSearch(@RequestParam("query") String query) {
		String queryKey = QueryHandler.normalizeQuery(query);

		nlpExtractionService.createNlpExtraction(queryKey);

		// return queryKey for client to use in follow-on request
		return ResponseEntity.ok(queryKey);
	}
	
	@GetMapping(value="/nlpextraction", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getSearchResult(@RequestParam("querykey") String querykey) {
		NlpExtractionDto nlpExtractionDto = nlpExtractionService.getNlpExtractionDtoByKey(querykey);
		if (nlpExtractionDto == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		else {
			return ResponseEntity.ok(nlpExtractionDto);
		}
	}

}
