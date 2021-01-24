package com.auction_system.entities.clients;

import lombok.Getter;

@Getter
public class Individual extends Client {

    private String birthDate;

    private Individual(String username) {
        super(username);
    }

    public static class IndividualBuilder extends Client.ClientBuilder {

        private final Individual individual;

        public IndividualBuilder(String username) {
            this.client = new Individual(username);
            this.individual = (Individual) client;
        }

        public IndividualBuilder withBirthDate(String birthDate) {
            individual.birthDate = birthDate;
            return this;
        }
    }
}
