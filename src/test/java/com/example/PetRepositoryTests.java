package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestTransaction
class PetRepositoryTests {
	@Inject
	PetRepository petRepository;

	private void setupTest() {
		// Delete everything from the repo
		this.petRepository.deleteAll();

		// Assert the repository is empty
		assertThat(this.petRepository.count())
				.isZero();
	}

	@Test
	void petsByKindFound() {
		// tag::petsByKindFound[]
		setupTest();

		// Persist a new pet to the repo
		this.petRepository.persist(new Pet("fluffy", "cat"));

		// Assert that finding the pets by kind returns the correct result
		assertThat(this.petRepository.findPetsByKind("cat"))
				.isNotNull()
				.singleElement()
				.extracting(Pet::getName, Pet::getKind)
				.containsExactly("fluffy", "cat");
		// end::petsByKindFound[]
	}

	@Test
	void noPetsByKindFound() {
		// tag::noPetsByKindFound[]
		setupTest();

		// Assert that finding the pets by kind returns empty
		assertThat(this.petRepository.findPetsByKind("cat"))
				.isNotNull()
				.isEmpty();
		// end::noPetsByKindFound[]
	}

	@Test
	void adoptFoundPet() {
		// tag::adoptFoundPet[]
		setupTest();

		// Persist some pets
		this.petRepository.persist(
				new Pet("fluffy", "cat"),
				new Pet("harry", "dog")
		);

		// Assert that adopting a found pet is correct
		assertThat(this.petRepository.adoptPetIfFound("cat", "Eric"))
				.isNotNull()
				.get()
				.extracting(Pet::getKind, Pet::getName, Pet::getAdoptedBy)
				.containsExactly("cat", "fluffy", "Eric");
		// end::adoptFoundPet[]
	}

	@Test
	void noAdoptablePetFound() {
		// tag::noAdoptablePetFound[]
		setupTest();

		// Persist some pets
		this.petRepository.persist(
				new Pet(null, "fluffy", "cat", "Eric"),
				new Pet("harry", "dog")
		);

		// Assert that no pet is found for adoption
		assertThat(this.petRepository.adoptPetIfFound("cat", "Eric"))
				.isNotNull()
				.isEmpty();
		// end::noAdoptablePetFound[]
	}
}