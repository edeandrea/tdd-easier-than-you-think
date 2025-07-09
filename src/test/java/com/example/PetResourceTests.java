package com.example;

import static io.restassured.RestAssured.get;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import jakarta.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

import io.restassured.http.ContentType;

@QuarkusTest
class PetResourceTests {
	@InjectMock
	PetRepository petRepository;

	@Test
	void getAll() {
		// tag::getAll[]
		// Set up mock to return a pet when repo.listAll() is called
		when(this.petRepository.listAll())
				.thenReturn(List.of(new Pet(1L, "fluffy", "cat")));

		// Execute GET to /pets & assert
		get("/pets").then()
				.statusCode(Status.OK.getStatusCode())
				.contentType(ContentType.JSON)
				.body("$.size()", is(1))
				.body("[0].name", is("fluffy"))
				.body("[0].kind", is("cat"))
				.body("[0].adoptedBy", blankOrNullString());

		// Verify interactions
		verify(this.petRepository).listAll();
		verifyNoMoreInteractions(this.petRepository);
		// end::getAll[]
	}

	@Test
	void getByKind() {
		// Set up mock to return a pet when repo.findPetsByKind("cat") is called
		when(petRepository.findPetsByKind("cat"))
				.thenReturn(List.of(new Pet(1L, "fluffy", "cat", "Eric")));

		// Execute GET to /pets?kind=cat & assert
		get("/pets?kind={kind}", "cat").then()
				.contentType(ContentType.JSON)
				.statusCode(OK.getStatusCode())
				.body("$.size()", is(1))
				.body("[0].name", is("fluffy"))
				.body("[0].kind", is("cat"))
				.body("[0].adoptedBy", is("Eric"));

		// Verify interactions
		verify(this.petRepository).findPetsByKind("cat");
		verifyNoMoreInteractions(this.petRepository);
	}

	@Test
	void getByIdFound() {
		// Set up mock to return a pet when repo.findByIdOptional(1) is called
		when(petRepository.findByIdOptional(1L))
				.thenReturn(Optional.of(new Pet(1L, "fluffy", "cat", "Eric")));

		// Execute GET to /pets/1 & assert OK
		get("/pets/{id}", 1L).then()
				.contentType(ContentType.JSON)
				.statusCode(OK.getStatusCode())
				.body("name", is("fluffy"))
				.body("kind", is("cat"))
				.body("adoptedBy", is("Eric"));

		// Verify interactions
		verify(this.petRepository).findByIdOptional(1L);
		verifyNoMoreInteractions(this.petRepository);
	}

	@Test
	void getByIdNotFound() {
		// Set up mock to return empty when repo.findByIdOptional(1) is called
		when(petRepository.findByIdOptional(1L))
				.thenReturn(Optional.empty());

		// Execute GET to /pets/1 & assert NOT_FOUND
		get("/pets/{id}", 1L).then()
				.statusCode(NOT_FOUND.getStatusCode())
				.body(blankOrNullString());

		// Verify interactions
		verify(this.petRepository).findByIdOptional(1L);
		verifyNoMoreInteractions(this.petRepository);
	}
}